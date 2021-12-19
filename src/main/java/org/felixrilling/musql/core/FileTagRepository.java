package org.felixrilling.musql.core;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Repository
class FileTagRepository {

	private final DataSource dataSource;

	FileTagRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public @NotNull Map<String, Set<String>> loadByFileId(long fileId) {
		try (Connection con = dataSource.getConnection(); PreparedStatement ps = con.prepareStatement(
			"SELECT key, value FROM musql.file_tag f WHERE f.file_id = ?")) {
			ps.setLong(1, fileId);
			ps.execute();
			Map<String, Set<String>> map = new HashMap<>(15);
			try (ResultSet rs = ps.getResultSet()) {
				while (rs.next()) {
					String key = rs.getString(1);
					String value = rs.getString(2);
					map.computeIfAbsent(key, (ignored) -> new HashSet<>(1)).add(value);
				}
			}
			return MetadataUtils.createUnmodifiableMetadata(map);
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}

	public void insert(long fileId, @NotNull Map<String, Set<String>> tags) {
		try (Connection con = dataSource.getConnection()) {
			con.setAutoCommit(false);
			try (PreparedStatement ps = con.prepareStatement(
				"INSERT INTO musql.file_tag (file_id, key, value) VALUES (?, ?, ?)")) {
				for (Map.Entry<String, Set<String>> entry : tags.entrySet()) {
					String key = entry.getKey();
					for (String value : entry.getValue()) {
						ps.setLong(1, fileId);
						ps.setString(2, key);
						ps.setString(3, value);
						ps.addBatch();
					}
				}
				ps.executeBatch();
			}
			con.commit();
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}
}
