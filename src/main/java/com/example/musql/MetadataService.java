package com.example.musql;

import org.apache.commons.codec.digest.DigestUtils;
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

@Service
public class MetadataService {

	public @NotNull FileMetadata getMetadata(@NotNull Path file) throws IOException, SAXException, TikaException {
		org.apache.tika.metadata.Metadata metadata = parse(file);
		metadata.remove(TikaCoreProperties.TIKA_PARSED_BY.getName());

		byte[] sha256Hash = calcSha256Hash(file);

		return new FileMetadata(file, metadata, sha256Hash);
	}

	private byte[] calcSha256Hash(@NotNull Path file) throws IOException {
		try (InputStream inputStream = Files.newInputStream(file)) {
			return DigestUtils.sha256(inputStream);
		}
	}

	private @NotNull org.apache.tika.metadata.Metadata parse(@NotNull Path file)
		throws IOException, SAXException, TikaException {
		org.apache.tika.metadata.Metadata metadata = new org.apache.tika.metadata.Metadata();
		Parser parser = new AutoDetectParser();
		ContentHandler handler = new DefaultHandler();
		ParseContext context = new ParseContext();
		try (InputStream fsStream = Files.newInputStream(file); InputStream inputStream = new BufferedInputStream(
			fsStream)) {
			parser.parse(inputStream, handler, metadata, context);
		}
		return metadata;
	}

}
