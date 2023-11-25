package dev.rilling.musql;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

public record FileEntity(Path path, Instant lastModified,
						 Map<String, Set<String>> metadata) {
}
