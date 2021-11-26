package com.example.musql;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;

@Service
public class MusqlService {

	private static final Logger LOGGER = LoggerFactory.getLogger(MusqlService.class);

	private final FileRepository fileRepository;

	public MusqlService(FileRepository fileRepository) {
		this.fileRepository = fileRepository;
	}

	public void save(@NotNull FileEntity fileEntity) {
		Path path = fileEntity.path();

		Optional<FileEntity> existing = fileRepository.loadByPath(path);
		if (existing.isPresent()) {
			FileEntity existingEntity = existing.get();
			if (Arrays.equals(existingEntity.sha256Hash(), fileEntity.sha256Hash())) {
				LOGGER.info("For path '{}' an entry was found and its hash matches, not changing anything.", path);
				return;
			}

			LOGGER.info("For path '{}' an entry was found, but with a different hash, replacing it.", path);
			fileRepository.delete(existingEntity);
		} else {
			LOGGER.info("For path '{}' no entry was found, creating it.", path);
		}

		fileRepository.insert(fileEntity);
	}
}
