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
public class FileEntityService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileEntityService.class);

	private final FileEntityRepository fileEntityRepository;
	private final MetadataService metadataService;

	FileEntityService(FileEntityRepository fileEntityRepository, MetadataService metadataService) {
		this.fileEntityRepository = fileEntityRepository;
		this.metadataService = metadataService;
	}

	/**
	 * Imports the metadata of this file into the data source.
	 *
	 * @param file File to import.
	 * @throws IOException if I/O fails.
	 */
	public void importFile(Path file) throws IOException {
		Instant lastModified = Files.getLastModifiedTime(file).toInstant();

		if (fileEntityRepository.countCurrent(file, lastModified) == 1) {
			LOGGER.info("Skipping the file '{}' because a current entry was found.", file);
			return;
		}

		if (fileEntityRepository.deleteOutdated(file, lastModified) == 1) {
			LOGGER.info("Deleted an old entry for the file '{}'.", file);
		}

		Map<String, Set<String>> metadata = metadataService.parse(file);
		FileEntity fileEntity = new FileEntity(file, lastModified, metadata);
		fileEntityRepository.insert(fileEntity);
		LOGGER.info("Created an entry for the file '{}'.", file);
	}


}
