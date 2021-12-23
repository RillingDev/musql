package dev.rilling.musql.core.metadata;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public final class MetadataUtils {

	private MetadataUtils() {
	}

	public static @NotNull Map<String, Set<String>> createUnmodifiableMetadata(@NotNull Map<String, Set<String>> metadata) {
		return metadata.entrySet()
			.stream()
			.map(e -> Map.entry(e.getKey(), Collections.unmodifiableSet(e.getValue())))
			.collect(Collectors.toUnmodifiableMap(Map.Entry::getKey, Map.Entry::getValue));
	}
}
