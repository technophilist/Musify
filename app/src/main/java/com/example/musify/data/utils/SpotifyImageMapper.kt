package com.example.musify.data.utils

import com.example.musify.data.remote.response.ImageResponse

/**
 * A helper a function that is used to get an instance of [ImageResponse]
 * corresponding to the [imageSize]. Meant to be specifically used
 * with the Spotify API. The Spotify API will return 3 images, where
 * the first image will always be the widest. If the list doesn't have
 * three items, then this method will return the last element in the list.
 * If the list is empty, then this method throws [IllegalStateException]
 * @throws IllegalStateException if the list is empty.
 */
fun List<ImageResponse>.getImageResponseForImageSize(imageSize: MapperImageSize): ImageResponse {
    if (this.isEmpty()) throw IllegalStateException("The list of images is empty!")
    if (this.size < 3) return this.last()
    return when (imageSize) {
        MapperImageSize.LARGE -> this[0]
        MapperImageSize.MEDIUM -> this[1]
        MapperImageSize.SMALL -> this[2]
    }
}