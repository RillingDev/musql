/* This schema is built to work in PostgreSQL and H2 */

CREATE SCHEMA IF NOT EXISTS musql;

CREATE TABLE musql.file
(
	id          BIGINT  NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	path        VARCHAR NOT NULL UNIQUE,
	sha256_hash BYTEA   NOT NULL
);

CREATE TABLE musql.file_tag
(
	id      BIGINT  NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	file_id BIGINT  NOT NULL,
	key     VARCHAR NOT NULL,
	value   VARCHAR NOT NULL, /* Maybe use TEXT instead, but h2 does not support indexing it. */
	CONSTRAINT file_tag_file_fk FOREIGN KEY (file_id) REFERENCES musql.file (id) ON DELETE CASCADE
);

/* Useful for most queries operating on tags as they almost always go by key, and often go by value */
CREATE INDEX file_tag_key_value_ix ON musql.file_tag (key, value);



CREATE VIEW musql.demo_genre_popularity AS
SELECT t.value, COUNT(t.value)
FROM musql.file_tag t
WHERE t.key = 'genre'
GROUP BY t.value
ORDER BY COUNT(t.value) DESC;

CREATE VIEW musql.demo_tracks_from_cd AS
SELECT f.path
FROM musql.file f
WHERE f.id IN
	  (
		  SELECT t.file_id
		  FROM musql.file_tag t
		  WHERE t.key = 'media'
			AND t.value = 'CD'
	  )
