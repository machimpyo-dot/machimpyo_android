package com.machimpyo.dot.repository.impl

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.machimpyo.dot.data.model.request.KakaoAccessToken
import com.machimpyo.dot.data.model.response.UserInfo
import com.machimpyo.dot.network.service.MainService
import com.machimpyo.dot.repository.AuthRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val kakaoAuth: UserApiClient,
    private val mainService: MainService,
//    @ActivityContext private val context: Context
) : AuthRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun getUserInfo(): Result<UserInfo> {
        return try {
            val response = mainService.getUserInfo()
            if (!response.isSuccessful) {
                throw Exception("유저 정보 가져오기 실패")
            }

            val userInfo = response.body() ?: throw Exception("유저 정보 가져오기 실패")

            Result.success(userInfo)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun createFirebaseCustomTokenAndSignIn(
        token: OAuthToken,
        callback: (Result<FirebaseUser>) -> Unit
    ) {
        try {
            val kakaoAccessToken = KakaoAccessToken(token.accessToken)

            val response = mainService.createCustomToken(kakaoAccessToken)

            if (!response.isSuccessful) {
                throw Exception("파이어베이스 커스텀 토큰 생성 오류")
            }

            val result = response.body() ?: throw Exception("파이어베이스 커스텀 토큰 생성 오류")

            firebaseAuth.signInWithCustomToken(result.customToken).addOnSuccessListener {
                it.user?.let { user ->
                    callback(Result.success(user))
                } ?: callback(Result.failure(Exception("user is null")))
            }.addOnFailureListener {
                callback(Result.failure(it))
            }

        } catch (e: Exception) {
            callback(Result.failure(e))
        }
    }

    override suspend fun signInWithKakao(
        context: Context,
        callback: (Result<FirebaseUser>) -> Unit
    ) {
        try {
            Log.e("TAG", "레포지토리 옴!!!")
            if (kakaoAuth.isKakaoTalkLoginAvailable(context)) {
                //카카오톡이 설치되어있는 경우
                kakaoAuth.loginWithKakaoTalk(context) { token, error ->
                    error?.let {
                        callback(Result.failure(it))
                        return@loginWithKakaoTalk
                    }

                    if (token == null) {
                        callback(Result.failure(Exception("카카오 토큰 생성 오류")))
                        return@loginWithKakaoTalk
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        createFirebaseCustomTokenAndSignIn(token, callback)
                    }
                }

            } else {
                //카카오톡이 설치되어있지 않은 경우
                kakaoAuth.loginWithKakaoAccount(context) { token, error ->
                    error?.let {
                        callback(Result.failure(it))
                        return@loginWithKakaoAccount
                    }

                    if (token == null) {
                        callback(Result.failure(Exception("카카오 토큰 생성 오류")))
                        return@loginWithKakaoAccount
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        createFirebaseCustomTokenAndSignIn(token, callback)
                    }

                }
            }

        } catch (e: Exception) {
            callback(Result.failure(e))
        }
    }

    override fun logOut() {
        firebaseAuth.signOut()
    }


}