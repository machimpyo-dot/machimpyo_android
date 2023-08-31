package com.machimpyo.dot.data.model

import com.google.gson.annotations.SerializedName
import com.machimpyo.dot.utils.extension.randomLocalDate
import com.machimpyo.dot.utils.extension.randomText
import java.time.LocalDate


//my_talk, related_talk 과 대응
data class Letter(
    @SerializedName(value="title") val title: String,
    @SerializedName(value="content") val content: String,
    @SerializedName(value="letter_design_uid") val letterDesignUid: Long?,
    @SerializedName(value="colorcode")val colorcode: String?,
    @SerializedName(value="letter_uid") val uid: Long?,
    @SerializedName(value="related_letter_uid")val relatedLetterUid: Long?,
    @SerializedName(value="sender_uid") val senderUid: String? = null,
    val profileUrl: String?,
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
    val myTalk: Letter,
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

data class Company(
    val name: String,
    val uid: String,
    val exitDate: LocalDate?,
    val recentLetterContents: String?,
    val photoUrl: Any?
) {
    companion object {
        fun getMock(): Company {
            val photoUrl = "https://images.unsplash.com/photo-1688380692117-63178554d76d?ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D&auto=format&fit=crop&w=1740&q=80"
            val exitDate = randomLocalDate()
            val name = String.randomText(10)
            val recentLetterContents = String.randomText(200)
            return Company(
                name = name,
                uid = String.randomText(40),
                exitDate = exitDate,
                recentLetterContents = recentLetterContents,
                photoUrl = photoUrl
            )
        }
    }
}

data class LetterBoxItem(
    val company: Company,
    val talks: List<Talk>
) {
    companion object {
        fun getMock(): LetterBoxItem {
            val company = Company.getMock()
            val talks: MutableList<Talk> = mutableListOf()

            repeat(10) {
                talks.add(
                    Talk.getMock()
                )
            }

            return LetterBoxItem(
                company, talks
            )
        }
    }
}

data class LetterBox(
    val letterBoxItems: List<LetterBoxItem>
) {
    companion object {
        fun getMock(): LetterBox {
            val result: MutableList<LetterBoxItem> = mutableListOf()

            repeat(10) {
                result.add(
                    LetterBoxItem.getMock()
                )
            }
            return LetterBox(result)
        }
    }
}