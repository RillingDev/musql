use std::path::Path;

use crate::tag_key::CanonicalTagKey;
use anyhow::Result;
use log::{debug, warn};
use symphonia::core::formats::FormatOptions;
use symphonia::core::io::{MediaSourceStream, MediaSourceStreamOptions};
use symphonia::core::meta::{MetadataOptions, Tag};
use symphonia::core::probe::Hint;

// Note that we don't use a map, as tags that have multiple values are treated as individual entries with the same key.
pub type Tags = Vec<(String, String)>;

// Based on Symphonias `main.rs`.
pub fn read_tags(file_path: &Path) -> Result<Tags> {
	let src = std::fs::File::open(file_path)?;

	let mut hint = Hint::new();
	if let Some(extension) = file_path.extension().and_then(|a| a.to_str()) {
		hint.with_extension(extension);
	}

	let mss = MediaSourceStream::new(Box::new(src), MediaSourceStreamOptions::default());

	let mut probed = symphonia::default::get_probe().format(
		&hint,
		mss,
		&FormatOptions::default(),
		&MetadataOptions::default(),
	)?;

	// Based on https://github.com/pdeljanov/Symphonia/blob/6dbe658c2c64228c8b43e4f327264a83d9a1c499/symphonia-play/src/main.rs#L517
	if let Some(metadata_rev) = probed.format.metadata().current() {
		debug!("Using container format metadata.");

		if probed.metadata.get().as_ref().is_some() {
			warn!(
				"Tags that are part of the container format are preferentially used, ignoring other metadata."
			);
		}
		Ok(collect_tags(metadata_rev.tags()))
	} else if let Some(metadata_rev) = probed.metadata.get().as_ref().and_then(|m| m.current()) {
		debug!("Using probed metadata.");
		Ok(collect_tags(metadata_rev.tags()))
	} else {
		// Currently metadata that exists in the format (e.g. WAV) is ignored.
		warn!("No metadata found.");
		Ok(vec![])
	}
}

fn collect_tags(tags: &[Tag]) -> Tags {
	tags.iter()
		.map(|tag| {
			let key = match tag.std_key {
				Some(std_key) => std_key.canonical_tag_key(),
				None => tag.key.canonical_tag_key(),
			};
			(key, tag.value.to_string())
		})
		.collect()
}
