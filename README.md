# musql

> **Mu**sic tags to **SQL** conversion

## About

Reads metadata from music files and inserts them into a database for further processing.

## Usage

Import files:

`java -jar musql*.jar music-file1.flac music-file2.mp3`

This will import the data into an embedded [H2 database](https://h2database.com/html/main.html) stored in the `./local` 
directory. Connection details can be
found in `src/main/resources/application.properties`.
