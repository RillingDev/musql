use std::path::Path;

use anyhow::Result;
use clap::Parser;
use log::{debug, info, warn, LevelFilter};
use rusqlite::Connection;
use walkdir::WalkDir;

mod sql;
mod tag;
mod tag_key;

#[derive(Parser, Debug)]
#[command(version, about, long_about = None)]
struct Args {
	/// Name of the person to greet
	#[arg(
		required = true,
		help = "File path to scan. If a directory is specified, all contents will be scanned recursively."
	)]
	base_path: String,

	/// Number of times to greet
	#[arg(
		short = 'o',
		long,
		required = false,
		default_value = "./musql.db3",
		help = "Path for the SQLite database that will be written to. It will be created if it does not exist."
	)]
	database_path: String,
}

fn main() -> Result<()> {
	env_logger::builder().filter_level(LevelFilter::Info).init();

	let args = Args::parse();

	musql(&args.base_path, &args.database_path)
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
