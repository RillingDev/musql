package dev.rilling.musql;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@Service
public class MetadataService {
	private final Environment environment;

	MetadataService(Environment environment) {
		this.environment = environment;
	}

	/**
	 * Extracts the metadata for this file.
	 * <p>
	 * Mapping may be configured as described in {@link MusqlProperties}.
	 *
	 * @param file File to extract metadata from.
	 * @return Mapped metadata.
	 * @throws IOException if the file cannot be parsed.
	 */
	public Map<String, Set<String>> parse(Path file) throws IOException {
		Metadata metadata = new Metadata();
		Parser parser = new AutoDetectParser();
		try (InputStream fsStream = Files.newInputStream(file); InputStream inputStream = new BufferedInputStream(fsStream)) {
			parser.parse(inputStream, new DefaultHandler(), metadata, new ParseContext());
		} catch (TikaException | SAXException e) {
			throw new IOException("Could not parse file.", e);
		}

		if ("org.apache.tika.parser.EmptyParser".equals(metadata.get(TikaCoreProperties.TIKA_PARSED_BY))) {
			throw new IOException("No parser supports this file type.");
		}

		return convertMetadata(metadata);
	}

	private Map<String, Set<String>> convertMetadata(Metadata metadata) {
		Map<String, Set<String>> mappedMetadata = new HashMap<>(metadata.size());
		for (String name : metadata.names()) {
			// Only map names for which a mapping exists
			mapKey(name).ifPresent(mappedKey -> {
				String[] values = metadata.getValues(name);
				mappedMetadata.put(mappedKey, Set.copyOf(Arrays.asList(values)));
			});
		}
		return Collections.unmodifiableMap(mappedMetadata);
	}

	private Optional<String> mapKey(String originalKey) {
		String propertyName = "musql.key-mapping." + originalKey;

		String mappedKey = environment.getProperty(propertyName);
		if (mappedKey == null) {
			return Optional.of(originalKey);
		}
		if (mappedKey.isBlank()) {
			return Optional.empty();
		}
		return Optional.of(mappedKey);
	}

}
