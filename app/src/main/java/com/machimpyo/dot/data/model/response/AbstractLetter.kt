package com.machimpyo.dot.data.model.response

import com.google.gson.annotations.SerializedName

data class AbstractLetter(
    @SerializedName("letter_uid")
    val letterUid: String,
    @SerializedName("sender_img")
    val senderProfileUrl: String
)
