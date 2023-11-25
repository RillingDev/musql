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
	 * Loads file data as a {@link FileEntity}.
	 *
	 * @param file File to parse. Must be a regular, existing file.
	 * @return File entity with parsed data.
	 * @throws IOException if IO fails.
	 */
	public FileEntity loadFile(Path file) throws IOException {
		if (!Files.exists(file)) {
			throw new IllegalArgumentException("File '%s' does not exist.".formatted(file));
		}
		if (!Files.isRegularFile(file)) {
			throw new IllegalArgumentException("File '%s' is not a regular file.".formatted(file));
		}

		Instant lastModified = Files.getLastModifiedTime(file).toInstant();

		Map<String, Set<String>> metadata = metadataService.parse(file)
			.orElseThrow(() -> new IOException("File '%s' metadata could not be extracted.".formatted(file)));

		return new FileEntity(file, lastModified, metadata);
	}

	/**
	 * Persists this entity.
	 *
	 * @param fileEntity File entity to persist.
	 */
	public void save(FileEntity fileEntity) {
		if (fileRepository.hasByPath(fileEntity.path())) {
			if (fileRepository.deleteOutdatedByPath(fileEntity.path(), fileEntity.lastModified())) {
				LOGGER.info("For path '{}' an outdated entry was found, replacing it.", fileEntity.path());
				fileRepository.insert(fileEntity);
			} else {
				LOGGER.info("For path '{}' a current entry was found, skipping it.", fileEntity.path());
			}
		} else {
			LOGGER.info("For path '{}' no entry was found, creating it.", fileEntity.path());
			fileRepository.insert(fileEntity);
		}
	}
}
