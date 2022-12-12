package dev.rilling.musql.core;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public record FileEntity(@NotNull Path path, @NotNull Instant lastModified,
						 @NotNull Map<String, Set<String>> metadata) {
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		FileEntity that = (FileEntity) o;
		return path.equals(that.path) && lastModified.equals(that.lastModified) && metadata.equals(that.metadata);
	}

	@Override
	public int hashCode() {
		return Objects.hash(path, lastModified, metadata);
	}
}
