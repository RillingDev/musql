package dev.rilling.musql.core;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.io.Serial;
import java.nio.file.Path;
import java.sql.*;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Set;

@Repository
class FileRepository {

	private final DataSource dataSource;

	FileRepository(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * Checks if an entity exists by its file path.
	 *
	 * @param path Path to load by.
	 * @return if it exists.
	 */
	public boolean hasByPath(@NotNull Path path) {
		try (Connection con = dataSource.getConnection(); PreparedStatement ps = con.prepareStatement(
			"SELECT COUNT(*) FROM musql.file f WHERE f.path = ?")) {
			ps.setString(1, serializePath(path));
			ps.execute();

			try (ResultSet rs = ps.getResultSet()) {
				rs.next();
				return rs.getLong(1) > 0;
			}
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}

	/**
	 * Deletes an already persisted entity if its last modified date is older.
	 *
	 * @param path         Path to load by.
	 * @param lastModified Last modified date.
	 * @return if an entry was deleted.
	 */
	public boolean deleteOutdatedByPath(@NotNull Path path, @NotNull Instant lastModified) {
		try (Connection con = dataSource.getConnection(); PreparedStatement ps = con.prepareStatement(
			"DELETE FROM musql.file f WHERE f.path = ? AND f.last_modified < ?")) {
			ps.setString(1, serializePath(path));
			ps.setTimestamp(2, serializeInstant(lastModified));
			return ps.executeUpdate() > 0;
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
		return Timestamp.from(instant.truncatedTo(ChronoUnit.SECONDS)); // Truncating makes comparisons easier
	}

	private static @NotNull String serializePath(@NotNull Path path) {
		return path.toString();
	}

	static class PersistenceException extends RuntimeException {

		@Serial
		private static final long serialVersionUID = 7717725742104778161L;

		PersistenceException(Throwable cause) {
			super(cause);
		}
	}
}
