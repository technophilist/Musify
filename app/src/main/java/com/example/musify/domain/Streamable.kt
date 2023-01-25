package com.example.musify.domain

/**
 * Indicates a class that represents something that can be streamed over
 * a network.
 */
sealed interface Streamable {
    val streamInfo: StreamInfo
}

/**
 * A class that contains contains information about a specific [Streamable].
 * Note that it is possible that a class may contain a null [streamUrl] because
 * the link might not be available for that specific instance.
 * For example, an API might return a list of tracks with a nullable
 * preview url, where the preview url for certain tracks might be null.
 */
data class StreamInfo(
    val streamUrl: String?,
    @Deprecated("Use imageUrls property")
    val imageUrl: String,
    val title: String,
    val subtitle: String,
    val imageUrls: ImageUrls? = null
) {
    /**
     * A data class that contains image urls for an instance of [StreamInfo]
     * class.
     */
    data class ImageUrls(val smallImage: String, val largeImage: String)
}






