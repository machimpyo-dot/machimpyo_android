package com.machimpyo.dot.data.model.response

import com.google.gson.annotations.SerializedName
import java.time.LocalDate

data class UserInfo(
    @SerializedName("user_uid")
    val uid: String?,
    @SerializedName("nickname")
    val nickName: String?,
    @SerializedName("profileImg")
    val profileImg: String?,
    @SerializedName("company")
    val company: String?,
    @SerializedName("enddate")
    val exitDate: LocalDate?
)