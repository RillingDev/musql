package dev.rilling.musql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

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
		List<Path> files = new ArrayList<>(args.length);
		for (String arg : args) {
			Path path = Paths.get(arg);
			if (!Files.exists(path)) {
				throw new IllegalArgumentException("Path '%s' does not exist.".formatted(path));
			}
			files.add(path);
		}

		LOGGER.debug("Starting import for {} entry point(s).", files.size());

		musqlService.processRecursively(files);

		LOGGER.info("Import complete.");
	}
}
