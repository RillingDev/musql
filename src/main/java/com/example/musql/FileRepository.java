package com.example.musql;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

@Repository
public class FileRepository {

	private final JdbcTemplate jdbcTemplate;

	public FileRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public @NotNull Optional<FileEntity> loadByPath(@NotNull Path path) {
		FileEntity fileEntity = jdbcTemplate.query("SELECT id, sha256_hash FROM musql.file f WHERE f.path = ?", rs -> {
			if (rs.next()) {
				Long id = rs.getLong("id");
				byte[] sha256Hash = rs.getBytes("sha256_hash");
				return new FileEntity(id, path, sha256Hash, Map.of());
			}
			return null;
		}, path.toString());
		return Optional.ofNullable(fileEntity);
	}

	public void delete(@NotNull FileEntity fileEntity) {
		jdbcTemplate.execute("DELETE FROM musql.file f WHERE f.path = ?", (PreparedStatementCallback<?>) ps -> {
			ps.setString(1, fileEntity.path().toString());
			ps.execute();
			return ps;
		});
	}

	public void insert(@NotNull FileEntity fileEntity) {
		jdbcTemplate.execute("INSERT INTO musql.file (path, sha256_hash) VALUES (?, ?)",
			(PreparedStatementCallback<?>) ps -> {
				ps.setString(1, fileEntity.path().toString());
				ps.setBytes(2, fileEntity.sha256Hash());
				ps.execute();
				return ps;
			});
	}
}
