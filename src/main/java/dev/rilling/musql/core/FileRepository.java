package dev.rilling.musql.core;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Repository
class FileRepository {

	private final JdbcTemplate jdbcTemplate;
	private final JdbcClient jdbcClient;

	FileRepository(JdbcTemplate jdbcTemplate, JdbcClient jdbcClient) {
		this.jdbcTemplate = jdbcTemplate;
		this.jdbcClient = jdbcClient;
	}

	/**
	 * Counts the entities with the provided path and last modified date.
	 *
	 * @param path         Path of the file.
	 * @param lastModified Last modified date.
	 * @return how many entries exist.
	 */
	public int countCurrent(Path path, Instant lastModified) {
		return jdbcClient.sql("SELECT COUNT(*) FROM file f WHERE f.path = ? AND last_modified = ?").params(serializePath(path), serializeInstant(lastModified)).query(Integer.class).single();
	}

	/**
	 * Deletes an already persisted entity if its last modified date is older than the provided.
	 *
	 * @param path         Path of the file.
	 * @param lastModified Last modified date.
	 * @return how many entries were deleted.
	 */
	public int deleteOutdated(Path path, Instant lastModified) {
		return jdbcClient.sql("DELETE FROM file f WHERE f.path = ? AND f.last_modified < ?").params(serializePath(path), serializeInstant(lastModified)).update();
	}

	/**
	 * Inserts a new entity.
	 *
	 * @param path         Path of the file.
	 * @param lastModified Last modified date.
	 * @param metadata     Metadata of the file.
	 */
	@Transactional
	public void insert(Path path, Instant lastModified, Map<String, Set<String>> metadata) {
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcClient.sql("INSERT INTO file (path, last_modified) VALUES (?, ?)").params(serializePath(path), serializeInstant(lastModified)).update(keyHolder,"id");
		long fileId = Objects.requireNonNull(keyHolder.getKeyAs(Long.class));

		List<Map.Entry<String, String>> flattenedMetadata = flattenMetadata(metadata);
		jdbcTemplate.batchUpdate("INSERT INTO file_tag (file_id, name, val) VALUES (?, ?, ?)", new BatchPreparedStatementSetter() {
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				Map.Entry<String, String> entry = flattenedMetadata.get(i);
				ps.setLong(1, fileId);
				ps.setString(2, entry.getKey());
				ps.setString(3, entry.getValue());
			}

			public int getBatchSize() {
				return flattenedMetadata.size();
			}
		});
	}

	private static List<Map.Entry<String, String>> flattenMetadata(Map<String, Set<String>> metadata) {
		List<Map.Entry<String, String>> list = new ArrayList<>((int) (metadata.size() * 1.5));
		metadata.forEach((key, values) -> values.forEach(value -> list.add(Map.entry(key, value))));
		return list;
	}

	private static Timestamp serializeInstant(Instant instant) {
		return Timestamp.from(instant.truncatedTo(ChronoUnit.SECONDS)); // Truncating makes comparisons easier
	}

	private static String serializePath(Path path) {
		return path.toString();
	}

}
