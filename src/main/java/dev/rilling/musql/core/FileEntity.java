package dev.rilling.musql.core;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

public record FileEntity(@NotNull Path path, @NotNull Instant lastModified,
						 @NotNull Map<String, Set<String>> metadata) {
}
