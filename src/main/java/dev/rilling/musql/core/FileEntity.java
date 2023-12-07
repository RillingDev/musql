package dev.rilling.musql.core;

import java.nio.file.Path;
import java.time.Instant;
import java.util.Map;
import java.util.Set;

record FileEntity(Path path, Instant lastModified,
						 Map<String, Set<String>> metadata) {
}
