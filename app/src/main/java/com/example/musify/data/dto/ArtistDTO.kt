package com.example.musify.data.dto

import com.example.musify.data.utils.MapperImageSize
import com.example.musify.data.utils.getImageDtoForImageSize
import com.example.musify.domain.MusicSummary
import java.net.URL


/**
 * A DTO object that contains information about an Artist.
 */
data class ArtistDTO(
    val id: String,
    val name: String,
    val images: List<ImageDTO>,
    val followers: Followers
) {
    /**
     * A DTO class that holds the number of followers that follow
     * an Artist.
     */
    data class Followers(val total: String)
}

/**
 * A mapper function used to map an instance of [ArtistDTO] to
 * an instance of [MusicSummary.ArtistSummary]. The [imageSize]
 * parameter describes the size of image to be used for the
 * [MusicSummary.ArtistSummary] instance.
 */
fun ArtistDTO.toArtistSummary(imageSize: MapperImageSize) = MusicSummary.ArtistSummary(
    id = id,
    name = name,
    associatedImageUrl = URL(images.getImageDtoForImageSize(imageSize).url)
)
