package dev.rilling.musql;

import org.apache.commons.io.file.AccumulatorPathVisitor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Stream;

@Service
public class MusqlService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MusqlService.class);

	private final FileEntityService fileEntityService;

	public MusqlService(FileEntityService fileEntityService) {
		this.fileEntityService = fileEntityService;
	}

	/**
	 * Recursively searches for files to process in the given root paths.
	 *
	 * @param entryPoints Files or directories to parse. Must exist.
	 */
	public void processRecursively(Collection<Path> entryPoints) {
		entryPoints.stream().flatMap(this::findFiles).forEach(this::processFile);
	}

	private void processFile(Path file) {
		LOGGER.info("Starting import of file '{}'.", file);
		try {
			FileEntity fileEntity = fileEntityService.loadFile(file);
			LOGGER.debug("Read file '{}'.", file);
			fileEntityService.save(fileEntity);
			LOGGER.info("Completed import of file '{}'.", file);
		} catch (IOException e) {
			LOGGER.warn("Could not read/import '{}'.", file, e);
		}
	}

	private Stream<Path> findFiles(Path path) {
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
		throw new IllegalArgumentException("Path '%s' is neither a directory nor a file.".formatted(path));
	}
}
