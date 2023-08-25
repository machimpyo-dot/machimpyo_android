package com.machimpyo.dot.data.model.request

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class ExitDateUpdate(
    @SerializedName("date")
    val date: LocalDate?,
    @SerializedName("vaild")
    val valid: Boolean
)

