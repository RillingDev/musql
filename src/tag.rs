use std::path::Path;

use crate::tag_normalization::{noop_normalize, normalize};
use anyhow::Result;
use log::{debug, warn};
use symphonia::core::formats::FormatOptions;
use symphonia::core::formats::probe::Hint;
use symphonia::core::io::{MediaSourceStream, MediaSourceStreamOptions};
use symphonia::core::meta::MetadataOptions;

// Note that we don't use a map, as tags that have multiple values are treated as individual entries with the same key.
pub type Tags = Vec<(String, String)>;

// Based on Symphonias `main.rs`.
pub fn read_tags(file_path: &Path, skip_normalization: bool) -> Result<Tags> {
	let src = std::fs::File::open(file_path)?;

	let mut hint = Hint::new();
	if let Some(extension) = file_path.extension().and_then(|a| a.to_str()) {
		hint.with_extension(extension);
	}

	let mss = MediaSourceStream::new(Box::new(src), MediaSourceStreamOptions::default());

	let mut probed = symphonia::default::get_probe().probe(
		&hint,
		mss,
		FormatOptions::default(),
		MetadataOptions::default(),
	)?;

	// Based on https://github.com/pdeljanov/Symphonia/blob/main/symphonia-play/src/main.rs
	// We ignore older and per-track metadata
	if let Some(metadata_rev) = probed.metadata().skip_to_latest() {
		debug!("Using container format metadata.");
		Ok(metadata_rev
			.media
			.tags
			.iter()
			.map(|tag| {
				if skip_normalization {
					noop_normalize(tag)
				} else {
					normalize(tag)
				}
			})
			.collect())
	} else {
		warn!("No metadata found.");
		Ok(vec![])
	}
}
