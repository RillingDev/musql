use std::path::Path;

use anyhow::Result;
use clap::{Arg, ArgAction, Command};
use log::{debug, info, warn, LevelFilter};
use rusqlite::Connection;
use walkdir::WalkDir;

mod sql;
mod tag;
mod tag_key;

fn main() -> Result<()> {
	let matches = Command::new("musql")
		.arg(
			Arg::new("file-path")
				.required(true)
				.action(ArgAction::Set)
				.help("File path to scan. If a directory is specified, all contents including other directories will be scanned."),
		)
		.arg(
			Arg::new("database-path")
				.long("database-path")
				.short('o')
				.required(false)
				.default_value("./musql.db3")
				.action(ArgAction::Set)
				.help("Path for the SQLite database that will be written to. It will be created if it does not exist."),
		)
		.get_matches();

	env_logger::builder().filter_level(LevelFilter::Info).init();

	musql(
		matches.get_one::<String>("file-path").unwrap(),
		matches.get_one::<String>("database-path").unwrap(),
	)
}

fn musql(file_path: &str, database_path: &str) -> Result<()> {
	info!("Initializing database.");
	let mut conn = Connection::open(database_path)?;
	sql::init_schema(&conn)?;

	info!("Importing from base path {:?}.", file_path);
	for entry in WalkDir::new(file_path) {
		let dir_entry = entry?;
		if dir_entry.file_type().is_file() {
			info!("Reading file {:?}.", dir_entry.path());
			if let Err(err) = import_file(&mut conn, dir_entry.path()) {
				warn!("Could not process file {:?}: {}.", dir_entry.path(), err);
			};
		}
	}

	info!("Done.");
	Ok(())
}

fn import_file(conn: &mut Connection, path: &Path) -> Result<()> {
	let last_modified = path.metadata()?.modified()?;
	let tags = tag::read_tags(path)?;
	debug!(
		"Read tags from {:?} ({:?}): {:#?}.",
		path, last_modified, tags
	);

	sql::insert(conn, path, &last_modified, tags)
}
