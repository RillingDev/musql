package com.example.musql;

import com.example.musql.core.FileEntity;
import com.example.musql.core.FileEntityService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

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
		Set<Path> paths = Arrays.stream(args).map(Paths::get).collect(Collectors.toSet());
		for (Path path : paths) {
			FileEntity fileEntity = fileEntityService.loadFile(path);
			fileEntityService.save(fileEntity);
		}
	}
}
