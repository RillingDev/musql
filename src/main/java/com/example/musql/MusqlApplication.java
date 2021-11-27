package com.example.musql;

import com.example.musql.core.FileEntity;
import com.example.musql.core.FileEntityService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;
import java.nio.file.Paths;

@SpringBootApplication
public class MusqlApplication implements CommandLineRunner {

	private final FileEntityService fileEntityService;

	public MusqlApplication(FileEntityService fileEntityService) {
		this.fileEntityService = fileEntityService;
	}

	public static void main(String[] args) {
		SpringApplication.run(MusqlApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Path a = Paths.get("./local/03 Things Happen.flac");
		System.out.println(a.toAbsolutePath());
		FileEntity aM = fileEntityService.loadFile(a);
		fileEntityService.save(aM);

		Path b = Paths.get("./local/11 Blueberry.mp3");
		System.out.println(b.toAbsolutePath());
		FileEntity bM = fileEntityService.loadFile(b);
		fileEntityService.save(bM);
	}
}
