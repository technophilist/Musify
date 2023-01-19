package com.example.musify.usecases.getCurrentlyPlayingStreamableUseCase

import com.example.musify.domain.Streamable
import kotlinx.coroutines.flow.Flow

interface GetCurrentlyPlayingStreamableUseCase {
    val currentlyPlayingStreamableStream: Flow<Streamable>
}