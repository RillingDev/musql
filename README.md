# musql

> **Mu**sic tags to **SQL** conversion

## About

Reads metadata from music files and inserts them into a database for further processing.

## Usage

Either [PostgreSQL](https://www.postgresql.org/) or [H2](https://h2database.com/html/main.html) are supported as
database targets. Configure them by passing the fitting spring boot configuration parameters.

Usage:

`java -Dspring.datasource.url=<jdbc_url> -Dspring.datasource.username=<username> -Dspring.datasource.password=<password> -jar musql*.jar ./file1.flac ./file2.mp3 ./folder/`

### Example

Example using H2 database:
`java -Dspring.datasource.url=jdbc:h2:./local/musql -Dspring.datasource.username=sa -Dspring.datasource.password=""
-jar musql*.jar ./music_library/`

This will import the data from all files in `./music_library/` into an embedded H2 database stored in the `./local`
directory.
