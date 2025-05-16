use std::path::{Path, PathBuf};

use anyhow::Result;
use clap::Parser;
use log::{debug, info, warn};
use rusqlite::Connection;
use walkdir::WalkDir;
mod sql;
mod tag;
mod tag_key;

#[derive(Parser, Debug)]
#[command(version, about, long_about = None)]
struct Args {
	#[arg(
		required = true,
		help = "File path to scan. If a directory is specified, all contents will be scanned recursively"
	)]
	base_path: PathBuf,

	#[arg(
		short = 'o',
		long,
		required = false,
		default_value = "./musql.db3",
		help = "Path for the SQLite database that will be written to. It will be created if it does not exist"
	)]
	database_path: PathBuf,

	#[command(flatten)]
	verbosity: clap_verbosity_flag::Verbosity<clap_verbosity_flag::InfoLevel>,
}

fn main() -> Result<()> {
	let args = Args::parse();

	env_logger::builder()
		.filter_level(args.verbosity.into())
		// Logs a lot of general details on info which are not needed
		.filter_module("symphonia_core", log::LevelFilter::Warn)
		.init();

	musql(&args.base_path, &args.database_path)
}

fn musql(base_path: &Path, database_path: &Path) -> Result<()> {
	info!("Initializing database.");
	let mut conn = Connection::open(database_path)?;
	sql::init_schema(&conn)?;

	info!("Importing from base path {}.", base_path.display());
	for entry in WalkDir::new(base_path) {
		let dir_entry = entry?;
		if dir_entry.file_type().is_file() {
			info!("Reading file {}.", dir_entry.path().display());
			if let Err(err) = import_file(&mut conn, dir_entry.path()) {
				warn!(
					"Could not process file {}: {}.",
					dir_entry.path().display(),
					err
				);
			}
		}
	}

	info!("Done.");
	Ok(())
}

fn import_file(conn: &mut Connection, file_path: &Path) -> Result<()> {
	let last_modified = file_path.metadata()?.modified()?;
	let tags = tag::read_tags(file_path)?;
	debug!(
		"Read tags from {} ({:?}): {:#?}.",
		file_path.display(),
		last_modified,
		tags
	);

	sql::insert(conn, file_path, &last_modified, &tags)
}
