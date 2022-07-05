package com.example.musify.data.remote.musicservice

/**
 * An object that contains the different end points
 * used by [SpotifyService]. It also contains certain defaults for the
 * api calls made by [SpotifyService].
 */
// TODO use screaming case convention
object SpotifyEndPoints {
    const val SpecificArtistEndPoint = "v1/artists/{id}"
    const val SpecificArtistAlbumsEndPoint = "v1/artists/{id}/albums"
    const val SpecificAlbumEndPoint = "v1/albums/{id}"
    const val TopTracksEndPoint = "v1/artists/{id}/top-tracks"
    const val SpecificPlaylistEndPoint = "v1/playlists/{playlist_id}"
    const val SearchEndPoint = "v1/search"
    const val ApiTokenEndPoint = "api/token"

    object Defaults {
        const val defaultPlaylistFields =
            "id,images,name,description,owner.display_name,tracks.items,followers.total"
        val defaultSearchQueryTypes = buildSearchQueryWithTypes(*SearchQueryType.values())
    }

}