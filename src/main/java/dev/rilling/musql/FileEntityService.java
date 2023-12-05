package dev.rilling.musql;

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

	private final FileRepository fileRepository;
	private final MetadataService metadataService;

	FileEntityService(FileRepository fileRepository, MetadataService metadataService) {
		this.fileRepository = fileRepository;
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

		if (fileRepository.hasByPath(fileEntity.path())) {
			if (fileRepository.deleteOutdatedByPath(fileEntity.path(), fileEntity.lastModified())) {
				LOGGER.info("For path '{}' an outdated entry was found, replacing it.", fileEntity.path());
				fileRepository.insert(fileEntity);
			} else {
				// TODO: avoid metadata analysis if we end up here.
				LOGGER.info("For path '{}' a current entry was found, skipping it.", fileEntity.path());
			}
		} else {
			LOGGER.info("For path '{}' no entry was found, creating it.", fileEntity.path());
			fileRepository.insert(fileEntity);
		}
	}


	private FileEntity loadFile(Path file) throws IOException {
		Instant lastModified = Files.getLastModifiedTime(file).toInstant();

		Map<String, Set<String>> metadata = metadataService.parse(file);

		return new FileEntity(file, lastModified, metadata);
	}

}
