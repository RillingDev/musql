package dev.rilling.musql.core.metadata;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.metadata.TikaCoreProperties;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.jetbrains.annotations.NotNull;
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
	private final MappingService mappingService;

	MetadataService(MappingService mappingService) {
		this.mappingService = mappingService;
	}

	public @NotNull Optional<Map<String, Set<String>>> parse(@NotNull Path file) throws IOException {
		Metadata metadata = new Metadata();
		Parser parser = new AutoDetectParser();
		try (InputStream fsStream = Files.newInputStream(file); InputStream inputStream = new BufferedInputStream(
			fsStream)) {
			parser.parse(inputStream, new DefaultHandler(), metadata, new ParseContext());
		} catch (TikaException | SAXException e) {
			throw new IOException("Could not parse file.", e);
		}

		if ("org.apache.tika.parser.EmptyParser".equals(metadata.get(TikaCoreProperties.TIKA_PARSED_BY))) {
			return Optional.empty();
		}

		return Optional.of(convertMetadata(metadata));
	}

	private @NotNull Map<String, Set<String>> convertMetadata(@NotNull Metadata metadata) {
		Map<String, Set<String>> mappedMetadata = new HashMap<>(metadata.size());
		for (String name : metadata.names()) {
			// Only map names for which a mapping exists
			mappingService.mapKey(name).ifPresent(mappedKey -> {
				String[] values = metadata.getValues(name);
				mappedMetadata.put(mappedKey, Set.copyOf(Arrays.asList(values)));
			});
		}
		return Collections.unmodifiableMap(mappedMetadata);
	}

}
