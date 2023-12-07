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
		FileEntity fileEntity = loadFile(file);

		if (fileEntityRepository.hasByPath(fileEntity.path())) {
			if (fileEntityRepository.deleteOutdatedByPath(fileEntity.path(), fileEntity.lastModified())) {
				LOGGER.info("For path '{}' an outdated entry was found, replacing it.", fileEntity.path());
				fileEntityRepository.insert(fileEntity);
			} else {
				// TODO: avoid metadata analysis if we end up here.
				LOGGER.info("For path '{}' a current entry was found, skipping it.", fileEntity.path());
			}
		} else {
			LOGGER.info("For path '{}' no entry was found, creating it.", fileEntity.path());
			fileEntityRepository.insert(fileEntity);
		}
	}


	private FileEntity loadFile(Path file) throws IOException {
		Instant lastModified = Files.getLastModifiedTime(file).toInstant();

		Map<String, Set<String>> metadata = metadataService.parse(file);

		return new FileEntity(file, lastModified, metadata);
	}

}
