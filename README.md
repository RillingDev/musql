# musql

> **Mu**sic tags to **SQL** conversion

## About

Reads metadata from music files and inserts them into a database for further processing.

## Usage

Before starting, set up a PostgreSQL 14 DB locally (Docker works well).

Import music:

`java -Dspring.datasource.url=jdbc:postgresql://127.0.0.1:5432/<musql_db> -Dspring.datasource.username=<username>
-Dspring.datasource.password=<password> -jar musql*.jar music-file1.flac music-file2.mp3`
