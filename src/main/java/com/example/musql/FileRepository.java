package com.example.musql;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.nio.file.Path;
import java.sql.*;
import java.util.Map;
import java.util.Optional;

@Repository
public class FileRepository {

	private final DataSource dataSource;
	private final FileTagRepository fileTagRepository;

	public FileRepository(DataSource dataSource, FileTagRepository fileTagRepository) {
		this.dataSource = dataSource;
		this.fileTagRepository = fileTagRepository;
	}

	public @NotNull Optional<FileEntity> loadByPath(@NotNull Path path) {
		try (Connection con = dataSource.getConnection(); PreparedStatement ps = con.prepareStatement(
			"SELECT id, sha256_hash FROM musql.file f WHERE f.path = ?")) {
			ps.setString(1, path.toString());
			ps.execute();
			try (ResultSet rs = ps.getResultSet()) {
				if (rs.next()) {
					long id = rs.getLong(1);
					byte[] sha256Hash = rs.getBytes(2);
					Map<String, String> tags = fileTagRepository.loadByFileId(id);
					return Optional.of(new FileEntity(id, path, sha256Hash, tags));
				}
				return Optional.empty();
			}
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}

	public void delete(@NotNull FileEntity fileEntity) {
		try (Connection con = dataSource.getConnection(); PreparedStatement ps = con.prepareStatement(
			"DELETE FROM musql.file f WHERE f.path = ?")) {
			ps.setString(1, fileEntity.path().toString());
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}

	public void insert(@NotNull FileEntity fileEntity) {
		try (Connection con = dataSource.getConnection(); PreparedStatement ps = con.prepareStatement(
			"INSERT INTO musql.file (path, sha256_hash) VALUES (?, ?)",
			Statement.RETURN_GENERATED_KEYS)) {
			ps.setString(1, fileEntity.path().toString());
			ps.setBytes(2, fileEntity.sha256Hash());
			ps.executeUpdate();

			long id;
			try (ResultSet keys = ps.getGeneratedKeys()) {
				keys.next();
				id = keys.getLong(1);
			}
			for (Map.Entry<String, String> entry : fileEntity.tags().entrySet()) {
				fileTagRepository.insert(id, entry.getKey(), entry.getValue());
			}
		} catch (SQLException e) {
			throw new PersistenceException(e);
		}
	}

}
