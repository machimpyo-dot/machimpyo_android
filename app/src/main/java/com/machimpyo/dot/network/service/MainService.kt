package com.machimpyo.dot.network.service

import com.machimpyo.dot.data.model.Letter
import com.machimpyo.dot.data.model.response.FirebaseCustomToken
import com.machimpyo.dot.data.model.request.ExitDateUpdate
import com.machimpyo.dot.data.model.request.KakaoAccessToken
import com.machimpyo.dot.data.model.response.UserInfo
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Query

/*
서버와의 API를 정의하는 인터페이스
 */
interface MainService {

    @POST("kakao/login")
    suspend fun createCustomToken(
        @Body kakaoAccessToken: KakaoAccessToken
    ): Response<FirebaseCustomToken>

    @GET("user/info")
    suspend fun getUserInfo(
    ): Response<UserInfo>

    @PUT("user/nickname")
    suspend fun updateUserNickname(
        @Body nickname: Map<String, String>
    ): Response<Map<String, Boolean>>

    @PUT("user/company")
    suspend fun updateUserCompany(
        @Body companyName: Map<String, String>
    ): Response<Map<String, String?>>

    @PUT("user/enddate")
    suspend fun updateUserExitDate(
        @Body exitDate: ExitDateUpdate
    ): Response<Map<String, Boolean>>

    @GET("letter/design")
    suspend fun getLetterDesign(
    ): Response<List<String>>

    @POST("letter/store")
    suspend fun createLetter(
        @Body letter: Letter,
    ): Response<Map<String, Long>>

    @GET("letter/get_one")
    suspend fun getLetter(
        @Query("letter_uid") letterUid: Long,
    ): Response<Letter>

}