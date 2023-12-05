package dev.rilling.musql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

@SpringBootApplication
public class MusqlApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(MusqlApplication.class);

	private final MusqlService musqlService;

	public MusqlApplication(MusqlService musqlService) {
		this.musqlService = musqlService;
	}

	public static void main(String[] args) {
		SpringApplication.run(MusqlApplication.class, args);
	}

	@Override
	public void run(String... args) {
		if (args.length != 1) {
			throw new IllegalArgumentException("Expected exactly one argument.");
		}
		Path path = Paths.get(args[0]);
		if (!Files.exists(path)) {
			throw new IllegalArgumentException("Path '%s' does not exist.".formatted(path));
		}

		LOGGER.debug("Starting import for '{}'.", path);

		musqlService.processRecursively(Set.of(path));

		LOGGER.info("Import complete.");
	}
}
