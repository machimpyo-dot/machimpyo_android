package com.machimpyo.dot.data.model.response

import com.google.gson.annotations.SerializedName



data class FirebaseCustomToken(
    @SerializedName("firebase_custom_token")
    val customToken: String
)