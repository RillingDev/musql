use std::path::Path;

use crate::tag_key::CanonicalTagKey;
use anyhow::Result;
use log::{debug, info, warn};
use symphonia::core::formats::FormatOptions;
use symphonia::core::io::MediaSourceStream;
use symphonia::core::meta::{MetadataOptions, Tag};
use symphonia::core::probe::Hint;

// Note that we don't use a map, as tags that have multiple values are treated as individual entries with the same key.
pub type Tags = Vec<(String, String)>;

// Based on Symphonias `main.rs``
pub fn read_tags(path: &Path) -> Result<Tags> {
	let src = std::fs::File::open(path)?;

	// Create a probe hint using the file's extension. [Optional]
	let mut hint = Hint::new();
	if let Some(extension) = path.extension().and_then(|a| a.to_str()) {
		hint.with_extension(extension);
	}

	let mss = MediaSourceStream::new(Box::new(src), Default::default());

	let meta_opts: MetadataOptions = Default::default();
	let fmt_opts: FormatOptions = Default::default();
	let mut probed = symphonia::default::get_probe().format(&hint, mss, &fmt_opts, &meta_opts)?;

	if let Some(metadata_rev) = probed.format.metadata().current() {
		debug!("Using container format metadata.");

		if probed.metadata.get().as_ref().is_some() {
			info!("tags that are part of the container format are preferentially printed.");
			info!("not printing additional tags that were found while probing.");
		}
		Ok(collect_tags(metadata_rev.tags()))
	} else if let Some(metadata_rev) = probed.metadata.get().as_ref().and_then(|m| m.current()) {
		debug!("Using probed metadata.");
		Ok(collect_tags(metadata_rev.tags()))
	} else {
		warn!("No metadata found.");
		Ok(vec![])
	}
}

fn collect_tags(tags: &[Tag]) -> Tags {
	tags.iter()
		.map(|tag| {
			let key = match tag.std_key {
				Some(std_key) => std_key.canonical_tag_key().to_string(),
				None => tag.key.canonical_tag_key().to_string(),
			};
			(key, tag.value.to_string())
		})
		.collect()
}
