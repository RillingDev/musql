package com.example.musql.core;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Repository
class FileTagRepository {

	private final DataSource dataSource;

	FileTagRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Map<String, String> loadByFileId(long fileId) {
		try (Connection con = dataSource.getConnection(); PreparedStatement ps = con.prepareStatement(
			"SELECT key, value FROM musql.file_tag f WHERE f.file_id = ?")) {
			ps.setLong(1, fileId);
			ps.execute();
			Map<String, String> map = new HashMap<>(15);
			try (ResultSet rs = ps.getResultSet()) {
				while (rs.next()) {
					String key = rs.getString(1);
					String value = rs.getString(2);
					map.put(key, value);
				}
			}
			return Collections.unmodifiableMap(map);
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}

	public void insert(long fileId, @NotNull String key, @NotNull String value) {
		try (Connection con = dataSource.getConnection(); PreparedStatement ps = con.prepareStatement(
			"INSERT INTO musql.file_tag (file_id, key, value) VALUES (?, ?, ?)")) {
			ps.setLong(1, fileId);
			ps.setString(2, key);
			ps.setString(3, value);
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}
}
