use std::{
	path::Path,
	time::{SystemTime, UNIX_EPOCH},
};

use crate::tag::Tags;
use anyhow::Result;
use rusqlite::Connection;

pub fn init_schema(conn: &Connection) -> Result<()> {
	conn.pragma_update(None, "foreign_keys", "ON")?;
	conn.execute_batch(include_str!("schema.sql"))?;
	Ok(())
}

pub fn insert(
	conn: &mut Connection,
	path: &Path,
	last_modified: &SystemTime,
	tags: Tags,
) -> Result<()> {
	let path_str = path.to_str().expect("Could not convert path to string.");
	let last_modified_int: u64 = last_modified.duration_since(UNIX_EPOCH)?.as_secs();

	let tx = conn.transaction()?;
	tx.execute(
		"INSERT INTO file (path, last_modified) VALUES (?1, ?2)",
		(path_str, last_modified_int),
	)?;
	for (key, value) in tags {
		tx.execute(
			"INSERT INTO file_tag (file_path, name, val) VALUES (?1, ?2, ?3)",
			(path_str, key, value),
		)?;
	}
	tx.commit()?;
	Ok(())
}
