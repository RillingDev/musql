package dev.rilling.musql.core;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = {"musql.key-mapping.xmpDM\\:artist=artist", "musql.key-mapping.dc\\:title="})
@ContextConfiguration(classes = MetadataService.class)
class MetadataServiceTest {

	@Autowired
	MetadataService metadataService;

	@ParameterizedTest
	@CsvSource(value = {"test.flac", "test.mp3"})
	@DisplayName("maps keys.")
	void mapsKeys(String fileName) throws IOException {
		Path testFile = Paths.get("src", "test", "resources", fileName);

		Map<String, Set<String>> metadata = metadataService.parse(testFile);

		assertThat(metadata)
			.as("maps to a new key").containsEntry("artist", Set.of("test-artist"))
			.as("drops the key for a blank mapping").doesNotContainKey("title");
	}

	@Test
	@DisplayName("throws for unsupported.")
	void name() {
		Path testFile = Paths.get("src", "test", "resources", "test.txt");

		assertThatThrownBy(() -> metadataService.parse(testFile)).isNotNull();
	}
}
