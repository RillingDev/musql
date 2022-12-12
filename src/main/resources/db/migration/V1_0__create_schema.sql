-- This schema is built to work in both PostgreSQL and H2.

CREATE SCHEMA musql;

CREATE TABLE musql.file
(
	id            BIGINT    NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	path          VARCHAR   NOT NULL UNIQUE,
	last_modified TIMESTAMP NOT NULL -- Timezone-less file timestamp
);

-- Note that even for a single combination of `file_id` and `name`,
-- multiple rows may exist in the case of multi-valued tags.
CREATE TABLE musql.file_tag
(
	id      BIGINT  NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	file_id BIGINT  NOT NULL,
	name    VARCHAR NOT NULL,
	-- Maybe use TEXT instead, but H2 does not support indexing it.
	-- Name is shorted to `val instead of `value` as the later is a reserved word in SQL2016.
	val     VARCHAR NOT NULL,
	CONSTRAINT file_tag_file_fk FOREIGN KEY (file_id) REFERENCES musql.file (id) ON DELETE CASCADE
);

-- Useful for most queries operating on tags as they almost always go by name, and often go by value.
CREATE INDEX file_tag_name_value_ix ON musql.file_tag (name, val);



CREATE VIEW musql.demo_genre_popularity AS
SELECT t.val, COUNT(t.val)
FROM musql.file_tag t
WHERE t.name = 'genre'
GROUP BY t.val
ORDER BY COUNT(t.val) DESC;

CREATE VIEW musql.demo_year_popularity AS
SELECT t.val, COUNT(t.val)
FROM musql.file_tag t
WHERE t.name = 'originalyear'
GROUP BY t.val
ORDER BY t.val;

CREATE VIEW musql.demo_tracks_from_cd AS
SELECT f.path
FROM musql.file f
		 LEFT JOIN musql.file_tag t ON f.id = t.file_id
WHERE t.name = 'media'
  AND t.val = 'CD'
ORDER BY f.path;

CREATE VIEW musql.demo_tracks_missing_genres AS
SELECT f.path
FROM musql.file f
WHERE f.id NOT IN
	  (SELECT t.file_id
	   FROM musql.file_tag t
	   WHERE t.name = 'genre')
ORDER BY f.path


