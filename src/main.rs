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
			Arg::new("base-path")
				.required(true)
				.action(ArgAction::Set)
				.help("File path to scan. If a directory is specified, all contents will be scanned recursively."),
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
		matches.get_one::<String>("base-path").unwrap(),
		matches.get_one::<String>("database-path").unwrap(),
	)
}

fn musql(base_path: &str, database_path: &str) -> Result<()> {
	info!("Initializing database.");
	let mut conn = Connection::open(database_path)?;
	sql::init_schema(&conn)?;

	info!("Importing from base path {:?}.", base_path);
	for entry in WalkDir::new(base_path) {
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

fn import_file(conn: &mut Connection, file_path: &Path) -> Result<()> {
	let last_modified = file_path.metadata()?.modified()?;
	let tags = tag::read_tags(file_path)?;
	debug!(
		"Read tags from {:?} ({:?}): {:#?}.",
		file_path, last_modified, tags
	);

	sql::insert(conn, file_path, &last_modified, &tags)
}
