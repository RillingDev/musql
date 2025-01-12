CREATE TABLE IF NOT EXISTS file (
    path TEXT NOT NULL PRIMARY KEY,
    -- last modified file time as seconds since unix epoch
    last_modified INTEGER NOT NULL
) STRICT;
--
-- Note that even for a single combination of `file_path` and `name`,
-- multiple rows may exist in the case of multi-valued tags.
CREATE TABLE IF NOT EXISTS file_tag (
    file_path TEXT NOT NULL REFERENCES file (path) ON DELETE CASCADE,
    name TEXT NOT NULL,
    val TEXT NOT NULL
) STRICT;
-- Useful for most queries operating on tags as they almost always go by name.
CREATE INDEX IF NOT EXISTS file_tag_name_ix ON file_tag (name, file_path);
----------------
-- Demo views --
----------------
CREATE VIEW IF NOT EXISTS demo_album_artist_popularity AS
SELECT t.val,
    COUNT(t.val)
FROM file_tag t
WHERE t.name = 'AlbumArtist'
GROUP BY t.val
ORDER BY COUNT(t.val) DESC;
--
CREATE VIEW IF NOT EXISTS demo_genre_popularity AS
SELECT t.val,
    COUNT(t.val)
FROM file_tag t
WHERE t.name = 'Genre'
GROUP BY t.val
ORDER BY COUNT(t.val) DESC;
--
CREATE VIEW IF NOT EXISTS demo_year_popularity AS
SELECT t.val,
    COUNT(t.val)
FROM file_tag t
WHERE t.name = 'OriginalYear'
GROUP BY t.val
ORDER BY t.val;
--
CREATE VIEW IF NOT EXISTS demo_tracks_from_cd AS
SELECT f.path
FROM file f
    LEFT JOIN file_tag t ON f.path = t.file_path
WHERE t.name = 'MediaFormat'
    AND t.val = 'CD'
ORDER BY f.path;
--
CREATE VIEW IF NOT EXISTS demo_tracks_missing_genres AS
SELECT f.path
FROM file f
    LEFT JOIN file_tag t ON f.path = t.file_path
    AND t.name = 'Genre'
WHERE t.file_path IS NULL
ORDER BY f.path;
--
CREATE VIEW IF NOT EXISTS demo_all_tag_names AS
SELECT t.name
FROM file_tag t
GROUP BY t.name
ORDER BY t.name;
--
CREATE VIEW IF NOT EXISTS demo_all_artists AS
SELECT DISTINCT(val)
FROM file_tag
WHERE name = 'AlbumArtist';