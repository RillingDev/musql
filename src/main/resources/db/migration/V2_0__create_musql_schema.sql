ALTER TABLE musql.file
	DROP COLUMN tags;

CREATE TABLE musql.file_tag
(
	id      BIGINT  NOT NULL GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	file_id BIGINT  NOT NULL,
	key     VARCHAR NOT NULL,
	value   TEXT    NOT NULL,
	CONSTRAINT file_tag_file_fk FOREIGN KEY (file_id) REFERENCES musql.file (id) ON DELETE CASCADE
);
/* Not a unique index as a key may appear multiple times if multi-valued */
CREATE INDEX file_tag_file_ix ON musql.file_tag (file_id, key);
