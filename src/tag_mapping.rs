use std::collections::HashMap;
use std::sync::LazyLock;
use symphonia::core::meta::{StandardTag, Tag};

// Mapping table based on MusicBrainz Picard (`__translate_freetext` in `id3.py`).
// Some other mappings have been appended based on the tags in files written by Picard.
static NONSTANDARD_TAG_MAPPING: LazyLock<HashMap<String, String>> = LazyLock::new(|| {
	serde_json::from_str(include_str!("tag_mapping.json")).expect("Failed to parse tag mapping.")
});

pub fn map_tag(tag: &Tag) -> (String, String) {
	if let Some(std_tag) = &tag.std {
		map_std_tag(std_tag)
	} else {
		// TODO: some MP3 tags come in with just the key = "TXXX". Ignore them?
		// Remove any ID3 comment prefix.
		let key = tag.raw.key.replace("TXXX:", "");
		let mapped_key = NONSTANDARD_TAG_MAPPING
			.get(&key)
			.map_or(key, std::string::ToString::to_string);
		(mapped_key, tag.raw.value.to_string())
	}
}

#[allow(clippy::too_many_lines)]
fn map_std_tag(std_tag: &StandardTag) -> (String, String) {
	let x = match std_tag {
		StandardTag::AccurateRipCount(val) => ("AccurateRipCount", val.to_string()),
		StandardTag::AccurateRipCountAllOffsets(val) => {
			("AccurateRipCountAllOffsets", val.to_string())
		}
		StandardTag::AccurateRipCountWithOffset(val) => {
			("AccurateRipCountWithOffset", val.to_string())
		}
		StandardTag::AccurateRipCrc(val) => ("AccurateRipCrc", val.to_string()),
		StandardTag::AccurateRipDiscId(val) => ("AccurateRipDiscId", val.to_string()),
		StandardTag::AccurateRipId(val) => ("AccurateRipId", val.to_string()),
		StandardTag::AccurateRipOffset(val) => ("AccurateRipOffset", val.to_string()),
		StandardTag::AccurateRipResult(val) => ("AccurateRipResult", val.to_string()),
		StandardTag::AccurateRipTotal(val) => ("AccurateRipTotal", val.to_string()),
		StandardTag::AcoustIdFingerprint(val) => ("AcoustIdFingerprint", val.to_string()),
		StandardTag::AcoustIdId(val) => ("AcoustIdId", val.to_string()),
		StandardTag::Actor(val) => ("Actor", val.to_string()),
		StandardTag::Album(val) => ("Album", val.to_string()),
		StandardTag::AlbumArtist(val) => ("AlbumArtist", val.to_string()),
		StandardTag::Arranger(val) => ("Arranger", val.to_string()),
		StandardTag::ArtDirector(val) => ("ArtDirector", val.to_string()),
		StandardTag::Artist(val) => ("Artist", val.to_string()),
		StandardTag::AssistantDirector(val) => ("AssistantDirector", val.to_string()),
		StandardTag::Author(val) => ("Author", val.to_string()),
		StandardTag::Bpm(val) => ("Bpm", val.to_string()),
		StandardTag::CdToc(val) => ("CdToc", val.to_string()),
		StandardTag::CdTrackIndex(val) => ("CdTrackIndex", val.to_string()),
		StandardTag::ChapterTitle(val) => ("ChapterTitle", val.to_string()),
		StandardTag::Choregrapher(val) => ("Choregrapher", val.to_string()),
		StandardTag::Cinematographer(val) => ("Cinematographer", val.to_string()),
		StandardTag::CollectionTitle(val) => ("CollectionTitle", val.to_string()),
		StandardTag::Comment(val) => ("Comment", val.to_string()),
		StandardTag::CompilationFlag(val) => ("CompilationFlag", val.to_string()),
		StandardTag::Composer(val) => ("Composer", val.to_string()),
		StandardTag::Conductor(val) => ("Conductor", val.to_string()),
		StandardTag::ContentAdvisory(val) => (
			"ContentAdvisory",
			match val {
				symphonia::core::meta::ContentAdvisory::None => "none",
				symphonia::core::meta::ContentAdvisory::Explicit => "explicit",
				symphonia::core::meta::ContentAdvisory::Censored => "censored",
			}
			.to_string(),
		),
		StandardTag::ContentRating(val) => ("ContentRating", val.to_string()),
		StandardTag::ContentType(val) => ("ContentType", val.to_string()),
		StandardTag::Coproducer(val) => ("Coproducer", val.to_string()),
		StandardTag::Copyright(val) => ("Copyright", val.to_string()),
		StandardTag::CostumeDesigner(val) => ("CostumeDesigner", val.to_string()),
		StandardTag::CueToolsDbDiscConfidence(val) => ("CueToolsDbDiscConfidence", val.to_string()),
		StandardTag::CueToolsDbTrackConfidence(val) => {
			("CueToolsDbTrackConfidence", val.to_string())
		}
		StandardTag::Description(val) => ("Description", val.to_string()),
		StandardTag::DigitizedDate(val) => ("DigitizedDate", val.to_string()),
		StandardTag::Director(val) => ("Director", val.to_string()),
		StandardTag::DiscNumber(val) => ("DiscNumber", val.to_string()),
		StandardTag::DiscSubtitle(val) => ("DiscSubtitle", val.to_string()),
		StandardTag::DiscTotal(val) => ("DiscTotal", val.to_string()),
		StandardTag::Distributor(val) => ("Distributor", val.to_string()),
		StandardTag::EditedBy(val) => ("EditedBy", val.to_string()),
		StandardTag::EditionTitle(val) => ("EditionTitle", val.to_string()),
		StandardTag::EncodedBy(val) => ("EncodedBy", val.to_string()),
		StandardTag::Encoder(val) => ("Encoder", val.to_string()),
		StandardTag::EncoderSettings(val) => ("EncoderSettings", val.to_string()),
		StandardTag::EncodingDate(val) => ("EncodingDate", val.to_string()),
		StandardTag::Engineer(val) => ("Engineer", val.to_string()),
		StandardTag::Ensemble(val) => ("Ensemble", val.to_string()),
		StandardTag::ExecutiveProducer(val) => ("ExecutiveProducer", val.to_string()),
		StandardTag::Genre(val) => ("Genre", val.to_string()),
		StandardTag::Grouping(val) => ("Grouping", val.to_string()),
		StandardTag::IdentAsin(val) => ("IdentAsin", val.to_string()),
		StandardTag::IdentBarcode(val) => ("IdentBarcode", val.to_string()),
		StandardTag::IdentCatalogNumber(val) => ("IdentCatalogNumber", val.to_string()),
		StandardTag::IdentEanUpn(val) => ("IdentEanUpn", val.to_string()),
		StandardTag::IdentIsbn(val) => ("IdentIsbn", val.to_string()),
		StandardTag::IdentIsrc(val) => ("IdentIsrc", val.to_string()),
		StandardTag::IdentLccn(val) => ("IdentLccn", val.to_string()),
		StandardTag::IdentPn(val) => ("IdentPn", val.to_string()),
		StandardTag::IdentPodcast(val) => ("IdentPodcast", val.to_string()),
		StandardTag::IdentUpc(val) => ("IdentUpc", val.to_string()),
		StandardTag::ImdbTitleId(val) => ("ImdbTitleId", val.to_string()),
		StandardTag::InitialKey(val) => ("InitialKey", val.to_string()),
		StandardTag::InternetRadioName(val) => ("InternetRadioName", val.to_string()),
		StandardTag::InternetRadioOwner(val) => ("InternetRadioOwner", val.to_string()),
		StandardTag::Keywords(val) => ("Keywords", val.to_string()),
		StandardTag::Label(val) => ("Label", val.to_string()),
		StandardTag::LabelCode(val) => ("LabelCode", val.to_string()),
		StandardTag::Language(val) => ("Language", val.to_string()),
		StandardTag::License(val) => ("License", val.to_string()),
		StandardTag::Lyricist(val) => ("Lyricist", val.to_string()),
		StandardTag::Lyrics(val) => ("Lyrics", val.to_string()),
		StandardTag::Measure(val) => ("Measure", val.to_string()),
		StandardTag::MediaFormat(val) => ("MediaFormat", val.to_string()),
		StandardTag::MixDj(val) => ("MixDj", val.to_string()),
		StandardTag::MixEngineer(val) => ("MixEngineer", val.to_string()),
		StandardTag::Mood(val) => ("Mood", val.to_string()),
		StandardTag::MovementName(val) => ("MovementName", val.to_string()),
		StandardTag::MovementNumber(val) => ("MovementNumber", val.to_string()),
		StandardTag::MovementTotal(val) => ("MovementTotal", val.to_string()),
		StandardTag::MovieTitle(val) => ("MovieTitle", val.to_string()),
		StandardTag::Mp3GainAlbumMinMax(val) => ("Mp3GainAlbumMinMax", val.to_string()),
		StandardTag::Mp3GainMinMax(val) => ("Mp3GainMinMax", val.to_string()),
		StandardTag::Mp3GainUndo(val) => ("Mp3GainUndo", val.to_string()),
		StandardTag::MusicBrainzAlbumArtistId(val) => ("MusicBrainzAlbumArtistId", val.to_string()),
		StandardTag::MusicBrainzAlbumId(val) => ("MusicBrainzAlbumId", val.to_string()),
		StandardTag::MusicBrainzArtistId(val) => ("MusicBrainzArtistId", val.to_string()),
		StandardTag::MusicBrainzDiscId(val) => ("MusicBrainzDiscId", val.to_string()),
		StandardTag::MusicBrainzGenreId(val) => ("MusicBrainzGenreId", val.to_string()),
		StandardTag::MusicBrainzLabelId(val) => ("MusicBrainzLabelId", val.to_string()),
		StandardTag::MusicBrainzOriginalAlbumId(val) => {
			("MusicBrainzOriginalAlbumId", val.to_string())
		}
		StandardTag::MusicBrainzOriginalArtistId(val) => {
			("MusicBrainzOriginalArtistId", val.to_string())
		}
		StandardTag::MusicBrainzRecordingId(val) => ("MusicBrainzRecordingId", val.to_string()),
		StandardTag::MusicBrainzReleaseGroupId(val) => {
			("MusicBrainzReleaseGroupId", val.to_string())
		}
		StandardTag::MusicBrainzReleaseStatus(val) => ("MusicBrainzReleaseStatus", val.to_string()),
		StandardTag::MusicBrainzReleaseTrackId(val) => {
			("MusicBrainzReleaseTrackId", val.to_string())
		}
		StandardTag::MusicBrainzReleaseType(val) => ("MusicBrainzReleaseType", val.to_string()),
		StandardTag::MusicBrainzTrackId(val) => ("MusicBrainzTrackId", val.to_string()),
		StandardTag::MusicBrainzTrmId(val) => ("MusicBrainzTrmId", val.to_string()),
		StandardTag::MusicBrainzWorkId(val) => ("MusicBrainzWorkId", val.to_string()),
		StandardTag::Narrator(val) => ("Narrator", val.to_string()),
		StandardTag::Opus(val) => ("Opus", val.to_string()),
		StandardTag::OpusNumber(val) => ("OpusNumber", val.to_string()),
		StandardTag::OriginalAlbum(val) => ("OriginalAlbum", val.to_string()),
		StandardTag::OriginalArtist(val) => ("OriginalArtist", val.to_string()),
		StandardTag::OriginalFile(val) => ("OriginalFile", val.to_string()),
		StandardTag::OriginalLyricist(val) => ("OriginalLyricist", val.to_string()),
		StandardTag::OriginalRecordingDate(val) => ("OriginalRecordingDate", val.to_string()),
		StandardTag::OriginalRecordingTime(val) => ("OriginalRecordingTime", val.to_string()),
		StandardTag::OriginalRecordingYear(val) => ("OriginalRecordingYear", val.to_string()),
		StandardTag::OriginalReleaseDate(val) => ("OriginalReleaseDate", val.to_string()),
		StandardTag::OriginalReleaseTime(val) => ("OriginalReleaseTime", val.to_string()),
		StandardTag::OriginalReleaseYear(val) => ("OriginalReleaseYear", val.to_string()),
		StandardTag::OriginalWriter(val) => ("OriginalWriter", val.to_string()),
		StandardTag::Owner(val) => ("Owner", val.to_string()),
		StandardTag::Part(val) => ("Part", val.to_string()),
		StandardTag::PartNumber(val) => ("PartNumber", val.to_string()),
		StandardTag::PartTitle(val) => ("PartTitle", val.to_string()),
		StandardTag::PartTotal(val) => ("PartTotal", val.to_string()),
		StandardTag::Performer(val) => ("Performer", val.to_string()),
		StandardTag::Period(val) => ("Period", val.to_string()),
		StandardTag::PlayCounter(val) => ("PlayCounter", val.to_string()),
		StandardTag::PodcastCategory(val) => ("PodcastCategory", val.to_string()),
		StandardTag::PodcastDescription(val) => ("PodcastDescription", val.to_string()),
		StandardTag::PodcastFlag(val) => ("PodcastFlag", val.to_string()),
		StandardTag::PodcastKeywords(val) => ("PodcastKeywords", val.to_string()),
		StandardTag::Producer(val) => ("Producer", val.to_string()),
		StandardTag::ProductionCopyright(val) => ("ProductionCopyright", val.to_string()),
		StandardTag::ProductionDesigner(val) => ("ProductionDesigner", val.to_string()),
		StandardTag::ProductionStudio(val) => ("ProductionStudio", val.to_string()),
		StandardTag::PurchaseDate(val) => ("PurchaseDate", val.to_string()),
		StandardTag::Rating(val) => ("Rating", val.to_string()), // In PPM.
		StandardTag::RecordingDate(val) => ("RecordingDate", val.to_string()),
		StandardTag::RecordingLocation(val) => ("RecordingLocation", val.to_string()),
		StandardTag::RecordingTime(val) => ("RecordingTime", val.to_string()),
		StandardTag::RecordingYear(val) => ("RecordingYear", val.to_string()),
		StandardTag::ReleaseCountry(val) => ("ReleaseCountry", val.to_string()),
		StandardTag::ReleaseDate(val) => ("ReleaseDate", val.to_string()),
		StandardTag::ReleaseTime(val) => ("ReleaseTime", val.to_string()),
		StandardTag::ReleaseYear(val) => ("ReleaseYear", val.to_string()),
		StandardTag::Remixer(val) => ("Remixer", val.to_string()),
		StandardTag::ReplayGainAlbumGain(val) => ("ReplayGainAlbumGain", val.to_string()),
		StandardTag::ReplayGainAlbumPeak(val) => ("ReplayGainAlbumPeak", val.to_string()),
		StandardTag::ReplayGainAlbumRange(val) => ("ReplayGainAlbumRange", val.to_string()),
		StandardTag::ReplayGainReferenceLoudness(val) => {
			("ReplayGainReferenceLoudness", val.to_string())
		}
		StandardTag::ReplayGainTrackGain(val) => ("ReplayGainTrackGain", val.to_string()),
		StandardTag::ReplayGainTrackPeak(val) => ("ReplayGainTrackPeak", val.to_string()),
		StandardTag::ReplayGainTrackRange(val) => ("ReplayGainTrackRange", val.to_string()),
		StandardTag::ScreenplayAuthor(val) => ("ScreenplayAuthor", val.to_string()),
		StandardTag::Script(val) => ("Script", val.to_string()),
		StandardTag::Soloist(val) => ("Soloist", val.to_string()),
		StandardTag::SortAlbum(val) => ("SortAlbum", val.to_string()),
		StandardTag::SortAlbumArtist(val) => ("SortAlbumArtist", val.to_string()),
		StandardTag::SortArtist(val) => ("SortArtist", val.to_string()),
		StandardTag::SortCollectionTitle(val) => ("SortCollectionTitle", val.to_string()),
		StandardTag::SortComposer(val) => ("SortComposer", val.to_string()),
		StandardTag::SortEditionTitle(val) => ("SortEditionTitle", val.to_string()),
		StandardTag::SortMovieTitle(val) => ("SortMovieTitle", val.to_string()),
		StandardTag::SortOpusTitle(val) => ("SortOpusTitle", val.to_string()),
		StandardTag::SortPartTitle(val) => ("SortPartTitle", val.to_string()),
		StandardTag::SortTrackTitle(val) => ("SortTrackTitle", val.to_string()),
		StandardTag::SortTvEpisodeTitle(val) => ("SortTvEpisodeTitle", val.to_string()),
		StandardTag::SortTvSeasonTitle(val) => ("SortTvSeasonTitle", val.to_string()),
		StandardTag::SortTvSeriesTitle(val) => ("SortTvSeriesTitle", val.to_string()),
		StandardTag::SortVolumeTitle(val) => ("SortVolumeTitle", val.to_string()),
		StandardTag::Subject(val) => ("Subject", val.to_string()),
		StandardTag::Summary(val) => ("Summary", val.to_string()),
		StandardTag::Synopsis(val) => ("Synopsis", val.to_string()),
		StandardTag::TaggingDate(val) => ("TaggingDate", val.to_string()),
		StandardTag::TermsOfUse(val) => ("TermsOfUse", val.to_string()),
		StandardTag::Thanks(val) => ("Thanks", val.to_string()),
		StandardTag::TmdbMovieId(val) => ("TmdbMovieId", val.to_string()),
		StandardTag::TmdbSeriesId(val) => ("TmdbSeriesId", val.to_string()),
		StandardTag::TrackNumber(val) => ("TrackNumber", val.to_string()),
		StandardTag::TrackSubtitle(val) => ("TrackSubtitle", val.to_string()),
		StandardTag::TrackTitle(val) => ("TrackTitle", val.to_string()),
		StandardTag::TrackTotal(val) => ("TrackTotal", val.to_string()),
		StandardTag::Tuning(val) => ("Tuning", val.to_string()),
		StandardTag::TvdbEpisodeId(val) => ("TvdbEpisodeId", val.to_string()),
		StandardTag::TvdbMovieId(val) => ("TvdbMovieId", val.to_string()),
		StandardTag::TvdbSeriesId(val) => ("TvdbSeriesId", val.to_string()),
		StandardTag::TvEpisodeNumber(val) => ("TvEpisodeNumber", val.to_string()),
		StandardTag::TvEpisodeTitle(val) => ("TvEpisodeTitle", val.to_string()),
		StandardTag::TvEpisodeTotal(val) => ("TvEpisodeTotal", val.to_string()),
		StandardTag::TvNetwork(val) => ("TvNetwork", val.to_string()),
		StandardTag::TvSeasonNumber(val) => ("TvSeasonNumber", val.to_string()),
		StandardTag::TvSeasonTitle(val) => ("TvSeasonTitle", val.to_string()),
		StandardTag::TvSeasonTotal(val) => ("TvSeasonTotal", val.to_string()),
		StandardTag::TvSeriesTitle(val) => ("TvSeriesTitle", val.to_string()),
		StandardTag::Url(val) => ("Url", val.to_string()),
		StandardTag::UrlArtist(val) => ("UrlArtist", val.to_string()),
		StandardTag::UrlCopyright(val) => ("UrlCopyright", val.to_string()),
		StandardTag::UrlInternetRadio(val) => ("UrlInternetRadio", val.to_string()),
		StandardTag::UrlLabel(val) => ("UrlLabel", val.to_string()),
		StandardTag::UrlOfficial(val) => ("UrlOfficial", val.to_string()),
		StandardTag::UrlPayment(val) => ("UrlPayment", val.to_string()),
		StandardTag::UrlPodcast(val) => ("UrlPodcast", val.to_string()),
		StandardTag::UrlPurchase(val) => ("UrlPurchase", val.to_string()),
		StandardTag::UrlSource(val) => ("UrlSource", val.to_string()),
		StandardTag::Version(val) => ("Version", val.to_string()),
		StandardTag::VolumeNumber(val) => ("VolumeNumber", val.to_string()),
		StandardTag::VolumeTitle(val) => ("VolumeTitle", val.to_string()),
		StandardTag::VolumeTotal(val) => ("VolumeTotal", val.to_string()),
		StandardTag::Work(val) => ("Work", val.to_string()),
		StandardTag::Writer(val) => ("Writer", val.to_string()),
		StandardTag::WrittenDate(val) => ("WrittenDate", val.to_string()),
		_ => ("<unknown>", String::new()),
	};

	(x.0.to_string(), x.1)
}

#[cfg(test)]
mod tests {
	use symphonia::core::meta::RawTag;

	use super::*;

	#[test]
	fn standard_tag() {
		let tag = Tag::new_std(RawTag::new("Bpm", "120"), StandardTag::Bpm(120));
		assert_eq!(map_tag(&tag), ("Bpm".to_string(), "120".to_string()));
	}

	#[test]
	fn other_tag() {
		let tag = Tag::new(RawTag::new("ASIN", "SomeAsin"));
		assert_eq!(map_tag(&tag), ("Asin".to_string(), "SomeAsin".to_string()));
	}
}
