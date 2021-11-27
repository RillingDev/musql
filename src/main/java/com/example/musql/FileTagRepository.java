package com.example.musql;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Repository
public class FileTagRepository {

	private final JdbcTemplate jdbcTemplate;

	public FileTagRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public Map<String, String> loadByFile(@NotNull FileEntity fileEntity) {
		Objects.requireNonNull(fileEntity.id());

		return jdbcTemplate.query("SELECT key, value FROM musql.file_tag f WHERE f.file_id = ?", rs -> {
			Map<String, String> map = new HashMap<>(15);
			while (rs.next()) {
				String key = rs.getString("key");
				String value = rs.getString("value");
				map.put(key, value);
			}
			return Collections.unmodifiableMap(map);
		}, fileEntity.id());
	}

	public void insert(@NotNull FileEntity fileEntity, @NotNull String key, @NotNull String value) {
		Objects.requireNonNull(fileEntity.id());

		jdbcTemplate.execute("INSERT INTO musql.file_tag (file_id, key, value) VALUES (?, ?, ?)",
			(PreparedStatementCallback<?>) ps -> {
				ps.setLong(1, fileEntity.id());
				ps.setString(2, key);
				ps.setString(3, value);
				ps.execute();
				return null;
			});
	}
}
