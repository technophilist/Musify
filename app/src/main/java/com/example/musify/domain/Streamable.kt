package com.example.musify.domain

/**
 * Indicates a class that contains a streamable url. It is possible
 * that a class may contain a null [streamUrl] because the
 * link might not be available for that specific instance.
 * For example, an API might return a list of tracks with a nullable
 * preview url, where the preview url for certain tracks might be null.
 */
sealed interface Streamable {
    val streamUrl: String?
}