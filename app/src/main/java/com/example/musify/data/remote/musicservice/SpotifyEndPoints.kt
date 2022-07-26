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
    const val SPECIFIC_PLAYLIST_ENDPOINT = "v1/playlists/{playlist_id}"
    const val SEARCH_ENDPOINT = "v1/search"
    const val API_TOKEN_ENDPOINT = "api/token"
    const val RECOMMENDATIONS_ENDPOINT = "v1/recommendations"

    object Defaults {
        const val defaultPlaylistFields =
            "id,images,name,description,owner.display_name,tracks.items,followers.total"
        val defaultSearchQueryTypes = buildSearchQueryWithTypes(*SearchQueryType.values())
    }

}