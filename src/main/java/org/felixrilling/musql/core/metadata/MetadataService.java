package org.felixrilling.musql.core.metadata;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.xml.sax.ContentHandler;
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

	public @NotNull Map<String, Set<String>> parse(@NotNull Path file) throws IOException {
		Metadata metadata = new Metadata();
		Parser parser = new AutoDetectParser();
		ContentHandler handler = new DefaultHandler();
		ParseContext context = new ParseContext();
		try (InputStream fsStream = Files.newInputStream(file); InputStream inputStream = new BufferedInputStream(
			fsStream)) {
			parser.parse(inputStream, handler, metadata, context);
		} catch (TikaException | SAXException e) {
			throw new IOException("Could not parse file.", e);
		}
		return convertMetadata(metadata);
	}

	private @NotNull Map<String, Set<String>> convertMetadata(@NotNull Metadata metadata) {
		Map<String, Set<String>> hashMap = new HashMap<>(metadata.size());
		for (String name : metadata.names()) {
			Optional<String> keyOpt = mappingService.mapKey(name);
			if (keyOpt.isEmpty()) {
				continue;
			}
			String key = keyOpt.get();

			if (metadata.isMultiValued(name)) {
				String[] values = metadata.getValues(name);
				hashMap.put(key, Set.copyOf(Arrays.asList(values)));
			} else {
				String value = metadata.get(name);
				hashMap.put(key, Set.of(value));
			}
		}
		return MetadataUtils.createUnmodifiableMetadata(hashMap);
	}

}
