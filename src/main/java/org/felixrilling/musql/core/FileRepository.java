package org.felixrilling.musql.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Repository
class FileRepository {

	private final DataSource dataSource;

	FileRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public @NotNull Optional<FileEntity> loadByPath(@NotNull Path path) {
		try (Connection con = dataSource.getConnection(); PreparedStatement ps = con.prepareStatement(
			"SELECT id, sha256_hash, tags FROM musql.file f WHERE f.path = ?")) {
			ps.setString(1, serializePath(path));
			ps.execute();
			try (ResultSet rs = ps.getResultSet()) {
				if (rs.next()) {
					long id = rs.getLong(1);
					byte[] sha256Hash = rs.getBytes(2);
					String tagsJson = rs.getString(3);
					ObjectNode tags = deserializeJson(tagsJson);
					return Optional.of(new FileEntity(id, path, sha256Hash, tags));
				}
				return Optional.empty();
			}
		} catch (SQLException | IOException e) {
			throw new PersistenceException(e);
		}
	}

	public void delete(@NotNull FileEntity fileEntity) {
		try (Connection con = dataSource.getConnection(); PreparedStatement ps = con.prepareStatement(
			"DELETE FROM musql.file f WHERE f.path = ?")) {
			ps.setString(1, serializePath(fileEntity.path()));
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}

	public void insert(@NotNull FileEntity fileEntity) {
		try (Connection con = dataSource.getConnection(); PreparedStatement ps = con.prepareStatement(
			"INSERT INTO musql.file (path, sha256_hash, tags) VALUES (?, ?, ?::JSONB)")) {
			ps.setString(1, serializePath(fileEntity.path()));
			ps.setBytes(2, fileEntity.sha256Hash());
			ps.setString(3, serializeJson(fileEntity.tags()));
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}

	private @NotNull String serializePath(@NotNull Path path) {
		return path.toString();
	}

	private @NotNull String serializeJson(@NotNull ObjectNode tags) {
		return tags.toString();
	}

	private @NotNull ObjectNode deserializeJson(@NotNull String tagsJson) throws JsonProcessingException {
		return new ObjectMapper().readValue(tagsJson, ObjectNode.class);
	}

}
