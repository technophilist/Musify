package com.example.musify.data.remote.musicservice

/**
 * An enum that contains the different applicable values
 * for the 'type' query parameter for performing a
 * search operation using [SpotifyService.search]
 */
enum class SearchQueryType(val value: String) {
    ALBUM("album"),
    ARTIST("artist"),
    PLAYLIST("playlist"),
    TRACK("track"),
    SHOW("show")
}

/**
 * A utility function that is used to build the 'type' query
 * parameter for performing a search operation using [SpotifyService.search].
 * The query string is generated using the provided [types] param.
 */
fun buildSearchQueryWithTypes(vararg types: SearchQueryType): String {
    if (types.isEmpty()) throw IllegalArgumentException("The list cannot be empty")
    var query = types.first().value
    if (types.size == 1) return query
    // start with index one because the query string is initialized
    // with the value of the first item in the array
    for (i in 1..types.lastIndex) {
        query += ",${types[i].value}"
    }
    return query
}