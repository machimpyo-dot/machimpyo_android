package com.machimpyo.dot.data.model

import com.google.gson.annotations.SerializedName
import com.machimpyo.dot.utils.extension.randomText

data class LetterName(
    @SerializedName("letter_uid")
    val letterUid: Long?,
    @SerializedName("user_nickname")
    val nickName: String?
) {
    companion object {
        fun getMock(): LetterName {
            return LetterName(
                letterUid = (1..100000).random().toLong(),
                nickName = String.randomText(10)
            )
        }
    }
}