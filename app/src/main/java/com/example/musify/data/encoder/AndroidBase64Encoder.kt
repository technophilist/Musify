package com.example.musify.data.encoder

import android.util.Base64

class AndroidBase64Encoder : Base64Encoder {
    override fun encodeToString(
        input: ByteArray
    ): String = Base64.encodeToString(input, Base64.NO_WRAP)
}