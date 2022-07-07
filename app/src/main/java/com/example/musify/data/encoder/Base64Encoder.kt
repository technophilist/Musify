package com.example.musify.data.encoder

fun interface Base64Encoder {
    fun encodeToString(input: ByteArray): String
}