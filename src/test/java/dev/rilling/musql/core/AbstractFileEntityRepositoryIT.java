package dev.rilling.musql.core;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

abstract class AbstractFileEntityRepositoryIT {

	@Autowired
	FileEntityRepository fileEntityRepository;

	@Autowired
	JdbcClient jdbcClient;

	@AfterEach
	void tearDown() {
		jdbcClient.sql("DELETE FROM musql.file").update();
	}

	@Test
	@DisplayName("inserts.")
	void insert() {
		FileEntity fileEntity = new FileEntity(
			Path.of("./test.mp3"),
			Instant.EPOCH,
			Map.of(
				"single_value", Set.of("fizz"),
				"multi_value", Set.of("foo", "bar")
			)
		);

		fileEntityRepository.insert(fileEntity);

		assertThat(jdbcClient.sql("SELECT path FROM musql.file").query(String.class).single()).isEqualTo("./test.mp3");
		assertThat(jdbcClient.sql("SELECT last_modified FROM musql.file").query(Instant.class).single()).isEqualTo(Instant.EPOCH);
		assertThat(jdbcClient.sql("SELECT val FROM musql.file_tag WHERE name = 'single_value'").query(String.class).set()).containsExactlyInAnyOrder("fizz");
		assertThat(jdbcClient.sql("SELECT val FROM musql.file_tag WHERE name = 'multi_value'").query(String.class).set()).containsExactlyInAnyOrder("foo", "bar");
	}


	@Test
	@DisplayName("counts current")
	void countsCurrent() {
		FileEntity fileEntity = new FileEntity(
			Path.of("./test.mp3"),
			Instant.EPOCH,
			Map.of(
				"single_value", Set.of("fizz")
			)
		);
		fileEntityRepository.insert(fileEntity);

		assertThat(fileEntityRepository.countCurrent(fileEntity.path(), fileEntity.lastModified())).isOne();
	}


	@Test
	@DisplayName("deletes outdated")
	void deleteOutdated() {
		FileEntity fileEntity = new FileEntity(
			Path.of("./test.mp3"),
			Instant.EPOCH,
			Map.of(
				"single_value", Set.of("fizz")
			)
		);
		fileEntityRepository.insert(fileEntity);

		assertThat(fileEntityRepository.deleteOutdated(fileEntity.path(), fileEntity.lastModified())).isZero();
		assertThat(jdbcClient.sql("SELECT COUNT(*) FROM musql.file").query(Integer.class).single()).isOne();

		assertThat(fileEntityRepository.deleteOutdated(fileEntity.path(), fileEntity.lastModified().plusSeconds(1))).isOne();
		assertThat(jdbcClient.sql("SELECT COUNT(*) FROM musql.file").query(Integer.class).single()).isZero();
	}
}
