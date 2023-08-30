package com.machimpyo.dot.data.model.response


import com.google.gson.GsonBuilder
import com.google.gson.annotations.SerializedName
import com.google.protobuf.StringValue
import com.machimpyo.dot.UserInfoProto
import com.machimpyo.dot.network.adapter.LocalDateAdapter
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
) {
    fun toProto(): UserInfoProto {
        val builder = UserInfoProto.newBuilder()

        builder.apply {
            this@UserInfo.uid?.let {
                this@apply.uid = StringValue.of(it)
            }
            this@UserInfo.nickName?.let {
                this@apply.nickName = StringValue.of(it)
            }
            this@UserInfo.profileImg?.let {
                this@apply.profileImg = StringValue.of(it)
            }
            this@UserInfo.company?.let {
                this@apply.company = StringValue.of(it)
            }
            this@UserInfo.exitDate?.let {
                try {
                    val gson = GsonBuilder()
                        .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
                        .create()
                    val jsonString = gson.toJson(it)
                    this@apply.exitDate = StringValue.of(jsonString)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return builder.build()
    }
    companion object {

    }
}
fun UserInfoProto.toUserInfo(): UserInfo? {
    val exitDate = try {
        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .create()
        gson.fromJson(this.exitDate?.value, LocalDate::class.java)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    val uid = if(uid?.value.isNullOrEmpty()) {
        return null
    } else {
        uid?.value
    }

    val nickName = if(nickName?.value.isNullOrEmpty()) {
        null
    } else {
        nickName?.value
    }

    val profileImg = if(profileImg?.value.isNullOrEmpty()) {
        null
    } else {
        profileImg?.value
    }

    val company = if(company?.value.isNullOrEmpty()){
        null
    } else {
        company?.value
    }

    return UserInfo(
        uid = uid,
        nickName = nickName,
        profileImg = profileImg,
        company = company,
        exitDate = exitDate
    )
}

//fun fromProto(userInfoProto: UserInfoProto): UserInfo {
//    val exitDate = try {
//        val gson = GsonBuilder()
//            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
//            .create()
//        gson.fromJson(userInfoProto.exitDate?.value, LocalDate::class.java)
//    } catch (e: Exception) {
//        e.printStackTrace()
//        null
//    }
//
//    return UserInfo(
//        uid = userInfoProto.uid?.value,
//        nickName = userInfoProto.nickName?.value,
//        profileImg = userInfoProto.profileImg?.value,
//        company = userInfoProto.company?.value,
//        exitDate = exitDate
//    )
//}