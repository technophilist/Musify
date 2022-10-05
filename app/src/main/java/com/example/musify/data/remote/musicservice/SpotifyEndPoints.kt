package com.example.musify.data.remote.musicservice

/**
 * An object that contains the different end points
 * used by [SpotifyService]. It also contains certain defaults for the
 * api calls made by [SpotifyService].
 */
object SpotifyEndPoints {
    const val SPECIFIC_ARTIST_ENDPOINT = "v1/artists/{id}"
    const val SPECIFIC_ARTIST_ALBUMS_ENDPOINT = "v1/artists/{id}/albums"
    const val SPECIFIC_ALBUM_ENDPOINT = "v1/albums/{id}"
    const val TOP_TRACKS_ENDPOINT = "v1/artists/{id}/top-tracks"
    const val SEARCH_ENDPOINT = "v1/search"
    const val API_TOKEN_ENDPOINT = "api/token"
    const val RECOMMENDATIONS_ENDPOINT = "v1/recommendations"
    const val PLAYLIST_TRACKS_ENDPOINT = "v1/playlists/{playlist_id}/tracks"
    const val NEW_RELEASES_ENDPOINT = "v1/browse/new-releases"
    const val FEATURED_PLAYLISTS = "v1/browse/featured-playlists"

    object Defaults {
        const val defaultPlaylistFields =
            "id,images,name,description,owner.display_name,tracks.items,followers.total"
        val defaultSearchQueryTypes = buildSearchQueryWithTypes(*SearchQueryType.values())
    }

}