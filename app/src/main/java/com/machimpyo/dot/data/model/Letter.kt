package com.machimpyo.dot.data.model

import com.machimpyo.dot.R
import com.machimpyo.dot.utils.extension.randomLocalDate
import com.machimpyo.dot.utils.extension.randomText
import java.time.LocalDate


//my_talk, related_talk 과 대응
data class Letter(
    val title: String,
    val contents: String,
    val uid: Long,
    val profileUrl: String?,
    val nickname: String,
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
                contents = contents,
                uid = uid,
                profileUrl = profileUrl,
                nickname = nickname,
                url = null
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
    val photoUrl: Any? = R.drawable.dot_icon
) {
    companion object {
        fun getMock(): Company {
            val photoUrl = R.drawable.dot_icon
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