package org.felixrilling.musql.core;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.TikaCoreProperties;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class MetadataService {

	public @NotNull Map<String, String> parse(@NotNull Path file) throws IOException {
		org.apache.tika.metadata.Metadata metadata = new org.apache.tika.metadata.Metadata();
		Parser parser = new AutoDetectParser();
		ContentHandler handler = new DefaultHandler();
		ParseContext context = new ParseContext();
		try (InputStream fsStream = Files.newInputStream(file); InputStream inputStream = new BufferedInputStream(
			fsStream)) {
			parser.parse(inputStream, handler, metadata, context);
		} catch (TikaException | SAXException e) {
			throw new IOException("Could not parse file.", e);
		}

		Map<String, String> map = new HashMap<>(metadata.size());
		for (String name : metadata.names()) {
			if (!name.equals(TikaCoreProperties.TIKA_PARSED_BY.getName())) {
				map.put(name, metadata.get(name));
			}
		}
		return Collections.unmodifiableMap(map);
	}

}
