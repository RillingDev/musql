package org.felixrilling.musql.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
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
import java.util.Set;

@Service
public class MetadataService {

	private static final Set<String> IGNORED_NAMES = Set.of(TikaCoreProperties.TIKA_PARSED_BY.getName());

	public @NotNull ObjectNode parse(@NotNull Path file) throws IOException {
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

		for (String ignoredName : IGNORED_NAMES) {
			metadata.remove(ignoredName);
		}

		return metaDataAsJson(metadata);
	}

	private @NotNull ObjectNode metaDataAsJson(@NotNull Metadata metadata) {
		ObjectMapper mapper = new ObjectMapper();
		ObjectNode node = mapper.createObjectNode();
		for (String name : metadata.names()) {
			if (metadata.isMultiValued(name)) {
				ArrayNode array = node.putArray(name);
				String[] values = metadata.getValues(name);
				for (String arrayItem : values) {
					array.add(arrayItem);
				}
			} else {
				String value = metadata.get(name);
				node.put(name, value);
			}
		}

		return node;
	}

}
