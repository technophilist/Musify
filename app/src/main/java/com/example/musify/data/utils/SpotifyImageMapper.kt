package com.example.musify.data.utils

import com.example.musify.data.dto.ImageResponse

/**
 * A helper a function that is used to get an instance of [ImageResponse]
 * corresponding to the [imageSize]. Meant to be specifically used
 * with the Spotify API. The Spotify API will return 3 images, where
 * the first image will always be the widest. If the list doesn't have
 * three items, an [IllegalStateException] will be thrown.
 */
fun List<ImageResponse>.getImageDtoForImageSize(imageSize: MapperImageSize): ImageResponse {
    if (this.size < 3) throw IllegalStateException("This list must contain at least 3 items")
    return when (imageSize) {
        MapperImageSize.LARGE -> this[0]
        MapperImageSize.MEDIUM -> this[1]
        MapperImageSize.SMALL -> this[2]
    }
}