package org.felixrilling.musql;

import org.apache.commons.io.file.AccumulatorPathVisitor;
import org.felixrilling.musql.core.FileEntity;
import org.felixrilling.musql.core.FileEntityService;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
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
	public void run(String... args) {
		List<Path> files = Arrays.stream(args).map(Paths::get).flatMap(this::findFiles).toList();
		LOGGER.info("Found {} files.", files.size());

		for (Path file : files) {
			LOGGER.info("Starting import of file '{}'.", file);
			try {
				FileEntity fileEntity = fileEntityService.loadFile(file);
				LOGGER.info("Read file '{}'.", file);
				fileEntityService.save(fileEntity);
				LOGGER.info("Completed file '{}'.", file);
			} catch (Exception e) {
				LOGGER.error("Could not read/import '{}'.", file, e);
			}
		}
	}

	private @NotNull Stream<Path> findFiles(@NotNull Path path) {
		if (Files.isRegularFile(path)) {
			return Stream.of(path);
		}
		if (Files.isDirectory(path)) {
			try {
				AccumulatorPathVisitor accumulatorPathVisitor = new AccumulatorPathVisitor();
				Files.walkFileTree(path, accumulatorPathVisitor);
				return accumulatorPathVisitor.getFileList().stream();
			} catch (IOException e) {
				LOGGER.error("Could not walk directory '{}'.", path, e);
			}
		}
		throw new IllegalArgumentException("Unexpected path type for path '%s'.".formatted(path));
	}
}
