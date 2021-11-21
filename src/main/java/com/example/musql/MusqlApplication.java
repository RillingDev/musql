package com.example.musql;

import org.apache.tika.metadata.Metadata;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class MusqlApplication implements CommandLineRunner {

	private final MetadataService metadataService;

	public static void main(String[] args) {
		SpringApplication.run(MusqlApplication.class, args);
	}

	public MusqlApplication(MetadataService metadataService) {
		this.metadataService = metadataService;
	}

	@Override
	public void run(String... args) throws Exception {
		Path a = Paths.get("./local/03 Things Happen.flac");
		System.out.println(a.toAbsolutePath());
		Metadata metadata = metadataService.getMetadata(a).metadata();
		for (String name : metadata.names()) {
			System.out.println(name+" - "+metadata.get(name));
		}

		Path b = Paths.get("./local/11 Blueberry.mp3");
		System.out.println(b.toAbsolutePath());
		Metadata metadata1 = metadataService.getMetadata(b).metadata();
		for (String name : metadata1.names()) {
			System.out.println(name+" - "+metadata1.get(name));
		}
	}
}
