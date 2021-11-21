package com.example.musql;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class MusqlApplication implements CommandLineRunner {

	private final MetadataService metadataService;
	private final MusqlService musqlService;

	public static void main(String[] args) {
		SpringApplication.run(MusqlApplication.class, args);
	}

	public MusqlApplication(MetadataService metadataService, MusqlService musqlService) {
		this.metadataService = metadataService;
		this.musqlService = musqlService;
	}

	@Override
	public void run(String... args) throws Exception {
		Path a = Paths.get("./local/03 Things Happen.flac");
		System.out.println(a.toAbsolutePath());
		FileMetadata aM = metadataService.getMetadata(a);
		for (String name : aM.metadata().names()) {
			System.out.println(name + " - " + aM.metadata().get(name));
		}
		musqlService.save(aM);

		Path b = Paths.get("./local/11 Blueberry.mp3");
		System.out.println(b.toAbsolutePath());
		FileMetadata bM = metadataService.getMetadata(b);
		for (String name : bM.metadata().names()) {
			System.out.println(name + " - " + bM.metadata().get(name));
		}
		musqlService.save(aM);
	}
}
