package com.example.musql;

import java.nio.file.Path;

record FileMetadata(Path file, org.apache.tika.metadata.Metadata metadata,byte[] sha256Hash) {
}
