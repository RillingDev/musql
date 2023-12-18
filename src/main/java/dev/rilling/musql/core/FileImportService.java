package dev.rilling.musql.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

@Service
public class FileImportService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileImportService.class);

	private final FileRepository fileRepository;
	private final MetadataService metadataService;

	FileImportService(FileRepository fileRepository, MetadataService metadataService) {
		this.fileRepository = fileRepository;
		this.metadataService = metadataService;
	}

	/**
	 * Imports the metadata of this file into the data source.
	 *
	 * @param path File to import.
	 * @throws IOException if I/O fails.
	 */
	public void importFile(Path path) throws IOException {
		Instant lastModified = Files.getLastModifiedTime(path).toInstant();

		if (fileRepository.countCurrent(path, lastModified) == 1) {
			LOGGER.info("Skipping the path '{}' because a current entry was found.", path);
			return;
		}

		if (fileRepository.deleteOutdated(path, lastModified) == 1) {
			LOGGER.info("Deleted an old entry for the path '{}'.", path);
		}

		Map<String, Set<String>> metadata = metadataService.parse(path);
		fileRepository.insert(path, lastModified, metadata);
		LOGGER.info("Created an entry for the path '{}'.", path);
	}


}
