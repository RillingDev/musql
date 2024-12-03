use std::collections::HashMap;

use lazy_static::lazy_static;
use symphonia::core::meta::StandardTagKey;
pub trait CanonicalTagKey {
	fn canonical_tag_key(&self) -> &str;
}

lazy_static! {
	static ref NONSTANDARD_TAG_MAPPING: HashMap<String, String> =
		serde_json::from_str(include_str!("tag_key_mapping.json"))
			.expect("Failed to parse tag mapping.");
}
impl CanonicalTagKey for str {
	fn canonical_tag_key(&self) -> &str {
		NONSTANDARD_TAG_MAPPING
			.get(self)
			.map_or(self, |s| s.as_str())
	}
}

impl CanonicalTagKey for StandardTagKey {
	fn canonical_tag_key(&self) -> &str {
		match self {
			StandardTagKey::AcoustidFingerprint => "AcoustidFingerprint",
			StandardTagKey::AcoustidId => "AcoustidId",
			StandardTagKey::Album => "Album",
			StandardTagKey::AlbumArtist => "AlbumArtist",
			StandardTagKey::Arranger => "Arranger",
			StandardTagKey::Artist => "Artist",
			StandardTagKey::Bpm => "Bpm",
			StandardTagKey::Comment => "Comment",
			StandardTagKey::Compilation => "Compilation",
			StandardTagKey::Composer => "Composer",
			StandardTagKey::Conductor => "Conductor",
			StandardTagKey::ContentGroup => "ContentGroup",
			StandardTagKey::Copyright => "Copyright",
			StandardTagKey::Date => "Date",
			StandardTagKey::Description => "Description",
			StandardTagKey::DiscNumber => "DiscNumber",
			StandardTagKey::DiscSubtitle => "DiscSubtitle",
			StandardTagKey::DiscTotal => "DiscTotal",
			StandardTagKey::EncodedBy => "EncodedBy",
			StandardTagKey::Encoder => "Encoder",
			StandardTagKey::EncoderSettings => "EncoderSettings",
			StandardTagKey::EncodingDate => "EncodingDate",
			StandardTagKey::Engineer => "Engineer",
			StandardTagKey::Ensemble => "Ensemble",
			StandardTagKey::Genre => "Genre",
			StandardTagKey::IdentAsin => "IdentAsin",
			StandardTagKey::IdentBarcode => "IdentBarcode",
			StandardTagKey::IdentCatalogNumber => "IdentCatalogNumber",
			StandardTagKey::IdentEanUpn => "IdentEanUpn",
			StandardTagKey::IdentIsrc => "IdentIsrc",
			StandardTagKey::IdentPn => "IdentPn",
			StandardTagKey::IdentPodcast => "IdentPodcast",
			StandardTagKey::IdentUpc => "IdentUpc",
			StandardTagKey::Label => "Label",
			StandardTagKey::Language => "Language",
			StandardTagKey::License => "License",
			StandardTagKey::Lyricist => "Lyricist",
			StandardTagKey::Lyrics => "Lyrics",
			StandardTagKey::MediaFormat => "MediaFormat",
			StandardTagKey::MixDj => "MixDj",
			StandardTagKey::MixEngineer => "MixEngineer",
			StandardTagKey::Mood => "Mood",
			StandardTagKey::MovementName => "MovementName",
			StandardTagKey::MovementNumber => "MovementNumber",
			StandardTagKey::MusicBrainzAlbumArtistId => "MusicBrainzAlbumArtistId",
			StandardTagKey::MusicBrainzAlbumId => "MusicBrainzAlbumId",
			StandardTagKey::MusicBrainzArtistId => "MusicBrainzArtistId",
			StandardTagKey::MusicBrainzDiscId => "MusicBrainzDiscId",
			StandardTagKey::MusicBrainzGenreId => "MusicBrainzGenreId",
			StandardTagKey::MusicBrainzLabelId => "MusicBrainzLabelId",
			StandardTagKey::MusicBrainzOriginalAlbumId => "MusicBrainzOriginalAlbumId",
			StandardTagKey::MusicBrainzOriginalArtistId => "MusicBrainzOriginalArtistId",
			StandardTagKey::MusicBrainzRecordingId => "MusicBrainzRecordingId",
			StandardTagKey::MusicBrainzReleaseGroupId => "MusicBrainzReleaseGroupId",
			StandardTagKey::MusicBrainzReleaseStatus => "MusicBrainzReleaseStatus",
			StandardTagKey::MusicBrainzReleaseTrackId => "MusicBrainzReleaseTrackId",
			StandardTagKey::MusicBrainzReleaseType => "MusicBrainzReleaseType",
			StandardTagKey::MusicBrainzTrackId => "MusicBrainzTrackId",
			StandardTagKey::MusicBrainzWorkId => "MusicBrainzWorkId",
			StandardTagKey::Opus => "Opus",
			StandardTagKey::OriginalAlbum => "OriginalAlbum",
			StandardTagKey::OriginalArtist => "OriginalArtist",
			StandardTagKey::OriginalDate => "OriginalDate",
			StandardTagKey::OriginalFile => "OriginalFile",
			StandardTagKey::OriginalWriter => "OriginalWriter",
			StandardTagKey::Owner => "Owner",
			StandardTagKey::Part => "Part",
			StandardTagKey::PartTotal => "PartTotal",
			StandardTagKey::Performer => "Performer",
			StandardTagKey::Podcast => "Podcast",
			StandardTagKey::PodcastCategory => "PodcastCategory",
			StandardTagKey::PodcastDescription => "PodcastDescription",
			StandardTagKey::PodcastKeywords => "PodcastKeywords",
			StandardTagKey::Producer => "Producer",
			StandardTagKey::PurchaseDate => "PurchaseDate",
			StandardTagKey::Rating => "Rating",
			StandardTagKey::ReleaseCountry => "ReleaseCountry",
			StandardTagKey::ReleaseDate => "ReleaseDate",
			StandardTagKey::Remixer => "Remixer",
			StandardTagKey::ReplayGainAlbumGain => "ReplayGainAlbumGain",
			StandardTagKey::ReplayGainAlbumPeak => "ReplayGainAlbumPeak",
			StandardTagKey::ReplayGainTrackGain => "ReplayGainTrackGain",
			StandardTagKey::ReplayGainTrackPeak => "ReplayGainTrackPeak",
			StandardTagKey::Script => "Script",
			StandardTagKey::SortAlbum => "SortAlbum",
			StandardTagKey::SortAlbumArtist => "SortAlbumArtist",
			StandardTagKey::SortArtist => "SortArtist",
			StandardTagKey::SortComposer => "SortComposer",
			StandardTagKey::SortTrackTitle => "SortTrackTitle",
			StandardTagKey::TaggingDate => "TaggingDate",
			StandardTagKey::TrackNumber => "TrackNumber",
			StandardTagKey::TrackSubtitle => "TrackSubtitle",
			StandardTagKey::TrackTitle => "TrackTitle",
			StandardTagKey::TrackTotal => "TrackTotal",
			StandardTagKey::TvEpisode => "TvEpisode",
			StandardTagKey::TvEpisodeTitle => "TvEpisodeTitle",
			StandardTagKey::TvNetwork => "TvNetwork",
			StandardTagKey::TvSeason => "TvSeason",
			StandardTagKey::TvShowTitle => "TvShowTitle",
			StandardTagKey::Url => "Url",
			StandardTagKey::UrlArtist => "UrlArtist",
			StandardTagKey::UrlCopyright => "UrlCopyright",
			StandardTagKey::UrlInternetRadio => "UrlInternetRadio",
			StandardTagKey::UrlLabel => "UrlLabel",
			StandardTagKey::UrlOfficial => "UrlOfficial",
			StandardTagKey::UrlPayment => "UrlPayment",
			StandardTagKey::UrlPodcast => "UrlPodcast",
			StandardTagKey::UrlPurchase => "UrlPurchase",
			StandardTagKey::UrlSource => "UrlSource",
			StandardTagKey::Version => "Version",
			StandardTagKey::Writer => "Writer",
		}
	}
}
