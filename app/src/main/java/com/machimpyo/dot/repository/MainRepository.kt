package com.machimpyo.dot.repository

import com.airbnb.lottie.animation.content.Content
import com.machimpyo.dot.data.model.ColorList
import com.machimpyo.dot.data.model.ContentInfo
import com.machimpyo.dot.data.model.Letter
import com.machimpyo.dot.data.model.LetterBoxItem
import com.machimpyo.dot.data.model.LetterName
import com.machimpyo.dot.data.model.response.AbstractLetter
import java.time.LocalDate

/*
개발자 둘이서 이 레포지토리 수정하면,
깃허브 충돌나니까 따로 관리하는게 좋을 것 같아요
 */
interface MainRepository {
    /*
    예시 코드
    suspend fun getInterviewScore(hostUUID: String): Result<InterviewScore>
     */

    /*
    민석 부분 아래로 쓰고 - 깃허브 관리시 헷갈릴 수 있기 때문에 나눠서 작성
     */
    suspend fun updateUserNickname(nickname: String)

    suspend fun updateUserCompany(company: String)

    suspend fun updateUserExitDate(exitDate: LocalDate?, isExitDateDetermined: Boolean)

    suspend fun getContentInfo(contentUid: Int): Result<ContentInfo>

    suspend fun getLetterBox(): Result<List<LetterBoxItem>>

    suspend fun getLetterNames(): Result<List<LetterName>>

    suspend fun getAbstractLetters(): Result<List<AbstractLetter>>


    /*
    나경 부분 아래로 쓰고
     */

//    suspend fun updateTempLetter(letter: Letter)
//
    suspend fun createLetter(letter: Letter): Result<Long>

    suspend fun getLetterDesign(): Result<ColorList>

    suspend fun getLetter(uid: Long): Result<Letter>


}