# musql

> **Mu**sic tags to **SQL** conversion

## About

Reads metadata from music files and inserts them into a relational database for further processing.

### Requirements

- Java Runtime Environment 21

## Usage

Either [PostgreSQL](https://www.postgresql.org/) or [H2](https://h2database.com/html/main.html) are supported as
database targets. Configure them by passing the fitting spring boot configuration parameters.

`java -Dspring.datasource.url=<JDBC URL> -Dspring.datasource.username=<username> -Dspring.datasource.password=<password> -jar musql*.jar [<path to directory or file>]`

### Example Using H2

H2 is an embedded database that does not require any external server to be set up.
This will import the data from all files in `./my_music_library/` into an H2 database stored in the `./local` directory.

`java -Dspring.datasource.url=jdbc:h2:./local/musql -Dspring.datasource.username=sa -Dspring.datasource.password="" -jar musql*.jar ./my_music_library/`

### Example Using PostgreSQL

Note that you will have to have a PostgreSQL server running for this.

`java -Dspring.datasource.url=jdbc:postgresql://<host>:<port>/<db_name> -Dspring.datasource.username=<username> -Dspring.datasource.password=<password> -jar musql*.jar ./my_music_library/`
