package com.example.musify.domain

import android.text.Spanned

/**
 * A domain object that represents a PodcastShow.
 */
data class PodcastShow(
    val id: String,
    val name:String,
    val imageUrlString: String,
    val nameOfPublisher:String,
    val htmlDescription:Spanned
)
