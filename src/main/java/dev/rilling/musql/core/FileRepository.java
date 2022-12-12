package dev.rilling.musql.core;

import dev.rilling.musql.core.metadata.MetadataUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.sql.*;
import java.time.Instant;
import java.util.*;

@Repository
class FileRepository {

	private final DataSource dataSource;

	FileRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Loads an existing entity by its file path.
	 *
	 * @param path Path to load by.
	 * @return existing entity or empty if none exists for this path.
	 */
	public @NotNull Optional<FileEntity> loadByPath(@NotNull Path path) {
		try (Connection con = dataSource.getConnection(); PreparedStatement ps = con.prepareStatement(
			"SELECT id, last_modified FROM musql.file f WHERE f.path = ?")) {
			ps.setString(1, serializePath(path));
			ps.execute();

			long id;
			Instant lastModified;
			try (ResultSet rs = ps.getResultSet()) {
				if (rs.next()) {
					id = rs.getLong(1);
					lastModified = rs.getTimestamp(2).toInstant();
				} else {
					return Optional.empty();
				}
			}

			Map<String, Set<String>> metadata = loadMetadataByFileId(con, id);
			return Optional.of(new FileEntity(id, path, lastModified, metadata));
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}

	/**
	 * Inserts a new entity.
	 *
	 * @param fileEntity Entity to persist.
	 */
	public void insert(@NotNull FileEntity fileEntity) {
		try (Connection con = dataSource.getConnection()) {
			con.setAutoCommit(false);
			try {
				doInsert(con, fileEntity);
				con.commit();
			} catch (SQLException e) {
				con.rollback();
				throw new PersistenceException(e);
			}
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}

	private void doInsert(@NotNull Connection con, @NotNull FileEntity fileEntity) throws SQLException {
		try (PreparedStatement ps = con.prepareStatement("INSERT INTO musql.file (path, last_modified) VALUES (?, ?)",
			Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, serializePath(fileEntity.path()));
			ps.setTimestamp(2, serializeInstant(fileEntity.lastModified()));
			ps.executeUpdate();

			long id;
			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				id = keys.getLong(1);
			}
			insertMetadata(con, id, fileEntity.metadata());
		}
	}

	/**
	 * Deletes an already persisted entity.
	 *
	 * @param fileEntity Entity to delete.
	 */
	public void delete(@NotNull FileEntity fileEntity) {
		try (Connection con = dataSource.getConnection(); PreparedStatement ps = con.prepareStatement(
			"DELETE FROM musql.file f WHERE f.path = ?")) {
			ps.setString(1, serializePath(fileEntity.path()));
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}

	private @NotNull Map<String, Set<String>> loadMetadataByFileId(@NotNull Connection con, long fileId)
		throws SQLException {
		try (PreparedStatement ps = con.prepareStatement("SELECT name, val FROM musql.file_tag f WHERE f.file_id = ?")) {
			ps.setLong(1, fileId);
			ps.execute();
			Map<String, Set<String>> map = new HashMap<>(15);
			try (ResultSet rs = ps.getResultSet()) {
				while (rs.next()) {
					String name = rs.getString(1);
					String value = rs.getString(2);
					map.computeIfAbsent(name, (ignored) -> new HashSet<>(1)).add(value);
				}
			}
			return MetadataUtils.createUnmodifiableMetadata(map);
		}
	}

	private void insertMetadata(@NotNull Connection connection, long fileId, @NotNull Map<String, Set<String>> metadata)
		throws SQLException {
		try (PreparedStatement ps = connection.prepareStatement(
			"INSERT INTO musql.file_tag (file_id, name, val) VALUES (?, ?, ?)")) {
			for (Map.Entry<String, Set<String>> entry : metadata.entrySet()) {
				String name = entry.getKey();
				for (String value : entry.getValue()) {
					ps.setLong(1, fileId);
					ps.setString(2, name);
					ps.setString(3, value);
					ps.addBatch();
				}
			}
			ps.executeBatch();
		}
	}

	private static @NotNull Timestamp serializeInstant(@NotNull Instant instant) {
		return Timestamp.from(instant);
	}

	private static @NotNull String serializePath(@NotNull Path path) {
		return path.toString();
	}

}
