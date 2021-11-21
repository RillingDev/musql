package com.example.musql;

import org.apache.tika.metadata.Metadata;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Service;

import java.nio.file.Path;

@Service
public class MusqlService {
	private final JdbcTemplate jdbcTemplate;

	MusqlService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void save(@NotNull FileMetadata fileMetadata) {
		Path path = fileMetadata.file().toAbsolutePath().normalize();

		Metadata metadata = fileMetadata.metadata();
		byte[] sha256Hash = fileMetadata.sha256Hash();

		Long count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM musql.files f WHERE f.path = ?",
			Long.class,
			path.toString());
		if (count > 0) {

		} else {
			jdbcTemplate.execute("INSERT INTO musql.files (path, sha256_hash, tags) VALUES (?,?,CAST(? AS JSON))",
				(PreparedStatementCallback<Object>) ps -> {
					ps.setString(1, path.toString());
					ps.setBytes(2, sha256Hash);
					ps.setObject(3, "{}");
					ps.execute();
					return ps;
				});
		}

	}
}
