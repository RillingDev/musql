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

abstract class AbstractFileRepositoryIT {

	@Autowired
	FileRepository fileRepository;

	@Autowired
	JdbcClient jdbcClient;

	@AfterEach
	void tearDown() {
		jdbcClient.sql("DELETE FROM musql.file").update();
	}

	@Test
	@DisplayName("inserts.")
	void insert() {
		Path file = Path.of("./test.mp3");
		Instant lastModified = Instant.EPOCH;
		Map<String, Set<String>> metadata = Map.of(
			"single_value", Set.of("fizz"),
			"multi_value", Set.of("foo", "bar")
		);

		fileRepository.insert(file, lastModified, metadata);

		assertThat(jdbcClient.sql("SELECT path FROM musql.file").query(String.class).single()).isEqualTo("./test.mp3");
		assertThat(jdbcClient.sql("SELECT last_modified FROM musql.file").query(Instant.class).single()).isEqualTo(lastModified);
		assertThat(jdbcClient.sql("SELECT val FROM musql.file_tag WHERE name = 'single_value'").query(String.class).set()).containsExactlyInAnyOrder("fizz");
		assertThat(jdbcClient.sql("SELECT val FROM musql.file_tag WHERE name = 'multi_value'").query(String.class).set()).containsExactlyInAnyOrder("foo", "bar");
	}


	@Test
	@DisplayName("counts current")
	void countsCurrent() {
		Path file = Path.of("./test.mp3");
		Instant lastModified = Instant.EPOCH;
		Map<String, Set<String>> metadata = Map.of(
			"single_value", Set.of("fizz"),
			"multi_value", Set.of("foo", "bar")
		);

		fileRepository.insert(file, lastModified, metadata);

		assertThat(fileRepository.countCurrent(file, lastModified)).isOne();
	}


	@Test
	@DisplayName("deletes outdated")
	void deleteOutdated() {
		Path file = Path.of("./test.mp3");
		Instant lastModified = Instant.EPOCH;
		Map<String, Set<String>> metadata = Map.of(
			"single_value", Set.of("fizz"),
			"multi_value", Set.of("foo", "bar")
		);

		fileRepository.insert(file, lastModified, metadata);

		assertThat(fileRepository.deleteOutdated(file, lastModified)).isZero();
		assertThat(jdbcClient.sql("SELECT COUNT(*) FROM musql.file").query(Integer.class).single()).isOne();

		assertThat(fileRepository.deleteOutdated(file, lastModified.plusSeconds(1))).isOne();
		assertThat(jdbcClient.sql("SELECT COUNT(*) FROM musql.file").query(Integer.class).single()).isZero();
	}
}
