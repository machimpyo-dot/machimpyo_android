package com.machimpyo.dot.data.model.request

import com.google.gson.annotations.SerializedName

data class KakaoAccessToken(
    @SerializedName("access_token")
    val accessToken: String
)