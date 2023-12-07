package dev.rilling.musql;

import dev.rilling.musql.core.FileEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Stream;

@SpringBootApplication
public class MusqlApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(MusqlApplication.class);

	private final FileEntityService fileEntityService;

	public MusqlApplication(FileEntityService fileEntityService) {
		this.fileEntityService = fileEntityService;
	}

	public static void main(String[] args) {
		SpringApplication.run(MusqlApplication.class, args);
	}

	@Override
	public void run(String... args) throws IOException {
		if (args.length != 1) {
			throw new IllegalArgumentException("Expected exactly one argument.");
		}
		Path entryPath = Paths.get(args[0]);

		LOGGER.info("Starting import.");
		try (Stream<Path> stream = Files.walk(entryPath)) {
			stream.forEach(path -> {
				if (Files.isRegularFile(path)) {
					processFile(path);
				}
			});
		}
		LOGGER.info("Completed import.");
	}

	private void processFile(Path file) {
		LOGGER.info("Starting import of file '{}'.", file);
		try {
			fileEntityService.importFile(file);
			LOGGER.info("Completed import of file '{}'.", file);
		} catch (IOException e) {
			LOGGER.warn("Could not read/import '{}'.", file, e);
		}
	}
}
