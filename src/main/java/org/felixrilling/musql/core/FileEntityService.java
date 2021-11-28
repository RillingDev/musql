package org.felixrilling.musql.core;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.commons.codec.digest.DigestUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Optional;

@Service
public class FileEntityService {

	private static final Logger LOGGER = LoggerFactory.getLogger(FileEntityService.class);

	private final FileRepository fileRepository;
	private final MetadataService metadataService;

	public FileEntityService(FileRepository fileRepository, MetadataService metadataService) {
		this.fileRepository = fileRepository;
		this.metadataService = metadataService;
	}

	public @NotNull FileEntity loadFile(@NotNull Path file) throws IOException {
		if (!Files.exists(file)) {
			throw new IllegalArgumentException(MessageFormat.format("Path ''{0}'' does not exist.", file));
		}
		if (!Files.isRegularFile(file)) {
			throw new IllegalArgumentException(MessageFormat.format("Path ''{0}'' is not a regular file.", file));
		}

		@NotNull ObjectNode metadata = metadataService.parse(file);
		byte[] sha256Hash = calcSha256Hash(file);

		return new FileEntity(null, file.normalize(), sha256Hash, metadata);
	}

	private byte[] calcSha256Hash(@NotNull Path file) throws IOException {
		try (InputStream inputStream = Files.newInputStream(file)) {
			return DigestUtils.sha256(inputStream);
		}
	}

	public void save(@NotNull FileEntity fileEntity) {
		Optional<FileEntity> existing = fileRepository.loadByPath(fileEntity.path());
		if (existing.isPresent()) {
			FileEntity existingEntity = existing.get();
			if (Arrays.equals(existingEntity.sha256Hash(), fileEntity.sha256Hash())) {
				LOGGER.info("For path '{}' an entry was found and its hash matches, not changing anything.",
					fileEntity.path());
				return;
			}

			LOGGER.info("For path '{}' an entry was found, but with a different hash, replacing it.",
				fileEntity.path());
			fileRepository.delete(existingEntity);
		} else {
			LOGGER.info("For path '{}' no entry was found, creating it.", fileEntity.path());
		}

		fileRepository.insert(fileEntity);
	}
}
