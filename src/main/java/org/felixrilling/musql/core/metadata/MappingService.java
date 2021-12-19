package org.felixrilling.musql.core.metadata;

import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@Service
class MappingService {

	private Map<String, String> keyMapping;

	@PostConstruct
	void init() throws IOException {
		Properties properties = new Properties();
		Path path = Paths.get("key.mapping.properties");
		try (InputStream inStream = Files.newInputStream(path)) {
			properties.load(inStream);
		}
		Map<String, String> map = stringMapForProperties(properties);
		keyMapping = Collections.unmodifiableMap(map);
	}

	/**
	 * Maps the given key to a key ready for output.
	 *
	 * @param originalKey The original key.
	 * @return The key to use. If empty, key can be omitted.
	 */
	public @NotNull Optional<String> mapKey(@NotNull String originalKey) {
		if (keyMapping.containsKey(originalKey)) {
			String newKey = keyMapping.get(originalKey);
			if (StringUtils.isBlank(newKey)) {
				return Optional.empty();
			}
			return Optional.of(newKey);
		}

		return Optional.of(originalKey);
	}

	@NotNull
	private Map<String, String> stringMapForProperties(@NotNull Properties properties) {
		Map<String, String> map = new HashMap<>(properties.size());
		for (String propertyName : properties.stringPropertyNames()) {
			map.put(propertyName, properties.getProperty(propertyName));
		}
		return map;
	}
}
