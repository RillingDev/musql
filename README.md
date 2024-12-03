# musql

> **Mu**sic tags to **SQL** conversion

## About

Reads metadata from music files and inserts them into a relational database for further processing.

### Requirements

-   [SQLite](https://www.sqlite.org/)

## Usage

`./musql <path to directory or file>`

SQLite is used as database backend. By default, the database is created at `./musql.db3`. This can be configured with the parameter `-o`.

`./musql -o ~/musql.db3 <path to directory or file>`
