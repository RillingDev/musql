use std::collections::HashMap;
use std::sync::LazyLock;
use symphonia::core::meta::Tag;

// Mapping table based on MusicBrainz Picard (`__translate_freetext` in `id3.py`).
// Some other mappings have been appended based on the tags in files written by Picard.
static NONSTANDARD_TAG_MAPPING: LazyLock<HashMap<String, String>> = LazyLock::new(|| {
	serde_json::from_str(include_str!("tag_key_mapping.json"))
		.expect("Failed to parse tag mapping.")
});

pub fn map_tag(tag: &Tag) -> (String, String) {
	// TODO: std tags
	// Remove any ID3 comment prefix.
	let key = tag.raw.key.replace("TXXX:", "");
	let mapped_key = NONSTANDARD_TAG_MAPPING
		.get(&key)
		.map_or(key, std::string::ToString::to_string);
	(mapped_key, tag.raw.value.to_string())
}
