package org.felixrilling.musql;

import org.felixrilling.musql.core.FileEntity;
import org.felixrilling.musql.core.FileEntityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

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
	public void run(String... args) {
		Set<Path> paths = Arrays.stream(args).map(Paths::get).collect(Collectors.toSet());
		LOGGER.info("Starting import of {} files.", paths.size());
		for (Path path : paths) {
			try {
				FileEntity fileEntity = fileEntityService.loadFile(path);
				LOGGER.info("Read file '{}'.", path);
				fileEntityService.save(fileEntity);
				LOGGER.info("Imported file '{}'.", path);
			} catch (IOException e) {
				LOGGER.error("Could not read/import '{}'.", path, e);
			}
		}
	}
}
