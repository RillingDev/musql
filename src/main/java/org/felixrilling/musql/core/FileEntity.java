package org.felixrilling.musql.core;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public record FileEntity(Long id, @NotNull Path path, byte[] sha256Hash, @NotNull Map<String, Set<String>> metadata) {
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		FileEntity that = (FileEntity) obj;
		return path.equals(that.path);
	}

	@Override
	public int hashCode() {
		return Objects.hash(path);
	}
}
