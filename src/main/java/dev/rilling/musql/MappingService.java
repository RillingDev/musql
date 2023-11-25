package dev.rilling.musql;

import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
class MappingService {

	private Map<String, String> keyMapping;

	@PostConstruct
	void init() throws IOException {
		keyMapping = loadKeyMapping();
	}

	/**
	 * Maps the given key to a key ready for output.
	 *
	 * @param originalKey The original key.
	 * @return The key to use. If empty, key can be omitted.
	 */
	public Optional<String> mapKey(String originalKey) {
		if (keyMapping.containsKey(originalKey)) {
			String newKey = keyMapping.get(originalKey);
			if (newKey.isBlank()) {
				return Optional.empty();
			}
			return Optional.of(newKey);
		}

		return Optional.of(originalKey);
	}

	@NotNull
	private Map<String, String> loadKeyMapping() throws IOException {
		Properties properties = new Properties();
		try (InputStream inStream = getClass().getClassLoader().getResourceAsStream("key.mapping.properties")) {
			properties.load(inStream);
		}
		return Collections.unmodifiableMap(stringMapForProperties(properties));
	}

	@NotNull
	private Map<String, String> stringMapForProperties(Properties properties) {
		Map<String, String> map = new HashMap<>(properties.size());
		for (String propertyName : properties.stringPropertyNames()) {
			map.put(propertyName, properties.getProperty(propertyName));
		}
		return map;
	}

}
