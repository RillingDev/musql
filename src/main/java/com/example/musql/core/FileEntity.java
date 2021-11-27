package com.example.musql.core;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;

public record FileEntity(Long id, @NotNull Path path, byte[] sha256Hash, @NotNull Map<String, String> tags) {
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FileEntity that = (FileEntity) o;
		return path.equals(that.path);
	}

	@Override
	public int hashCode() {
		return Objects.hash(path);
	}
}
