package com.machimpyo.dot.data.model

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class SendResult(
    @SerializedName("letter_uid") val letterUid: Long?,
    @SerializedName("date") val date: LocalDate?,
    @SerializedName("error") val error: String?,
)