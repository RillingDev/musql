DROP INDEX file_tag_file_ix;

CREATE INDEX file_tag_key_ix ON musql.file_tag (key);

CREATE VIEW musql.genre_popularity AS
SELECT t.value, COUNT(t.value)
FROM musql.file_tag t
WHERE t.key = 'genre'
GROUP BY t.value
ORDER BY COUNT(t.value) DESC;

CREATE VIEW musql.tracks_from_cd AS
SELECT f.path
FROM musql.file f
WHERE f.id IN
	  (
		  SELECT t.file_id
		  FROM file_tag t
		  WHERE t.key = 'media'
			AND t.value = 'CD'
	  )
