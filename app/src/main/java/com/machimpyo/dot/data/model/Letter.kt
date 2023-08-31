package com.machimpyo.dot.data.model

import com.google.gson.annotations.SerializedName
import com.machimpyo.dot.utils.extension.randomLocalDate
import com.machimpyo.dot.utils.extension.randomText
import java.time.LocalDate


//my_talk, related_talk 과 대응
/*
"myTalk":{
    "talk_title":"제목입니다",
    "talk_text":"내용입니다 테스트 from 민석",
    "letter_uid":18,
    "profile_img_url":null,
    "nickname":"ㅇㅊ퐇"
}
나중에 데이터 매퍼로 이부분을 해결해야할 것 같음
백에서 보내주는 키가 다른 경우, 근데 클래스를 같이 쓰는 경우
 */
data class Letter(
    @SerializedName(value="title") val title: String?,
    @SerializedName(value="content") val content: String?,
    @SerializedName(value="letter_design_uid") val letterDesignUid: Long?,
    @SerializedName(value="colorcode")val colorcode: String?,
    @SerializedName(value="letter_uid") val uid: Long?,
    @SerializedName(value="related_letter_uid")val relatedLetterUid: Long?,
    @SerializedName(value="sender_uid") val senderUid: String? = null,
    @SerializedName("profile_img_url")
    val profileUrl: String?,
    @SerializedName("nickname")
    val nickname: String?,
    var url: String?,
    ) {
    companion object {
        fun getMock(): Letter {
            val title = String.randomText()
            val contents = String.randomText(400)
            val uid = (1..1000000).random().toLong()
            val nickname = String.randomText(10)
            val profileUrl = "https://randomuser.me/api/portraits/women/2.jpg"
            return Letter(
                title = title,
                content = contents,
                uid = uid,
                relatedLetterUid = null,
                profileUrl = profileUrl,
                nickname = nickname,
                letterDesignUid = null,
                colorcode = null,
                url = null,
            )
        }
    }
}

data class LetterConfig(
    val contentMaxLength: Int = 500,
    val contentMaxLine: Int = 25,
)

data class Talk(
    @SerializedName("myTalk")
    val myTalk: Letter,
    @SerializedName("relatedTalk")
    val replyTalks: List<Letter>
) {
    companion object {
        fun getMock(): Talk {
            val myTalk = Letter.getMock()
            val replyTalks: MutableList<Letter> = mutableListOf()

            repeat(10) {
                replyTalks.add(
                    Letter.getMock()
                )
            }

            return Talk(
                myTalk, replyTalks
            )
        }
    }
}


data class LetterBoxItem(
    @SerializedName("talkList")
    val talks: List<Talk>,
    @SerializedName("company_name")
    val name: String,
    @SerializedName("company_uid")
    val uid: String,
    @SerializedName("endDate")
    val exitDate: LocalDate?,
    @SerializedName("recent_text")
    val recentLetterContents: String?,
    @SerializedName("companyImg")
    val photoUrl: Any?
) {
    companion object {
        fun getMock(): LetterBoxItem {
            val talks: MutableList<Talk> = mutableListOf()
            val photoUrl = "https://images.unsplash.com/photo-1688380692117-63178554d76d?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1740&q=80"
            val exitDate = randomLocalDate()
            val name = String.randomText(10)
            val recentLetterContents = String.randomText(200)

            repeat(10) {
                talks.add(
                    Talk.getMock()
                )
            }

            return LetterBoxItem(
                talks = talks,
                name = name,
                uid = String.randomText(40),
                exitDate = exitDate,
                recentLetterContents = recentLetterContents,
                photoUrl = photoUrl
            )
        }
    }
}
