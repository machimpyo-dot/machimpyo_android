package com.machimpyo.dot.repository.impl

import android.content.Context
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.machimpyo.dot.network.service.MainService
import com.machimpyo.dot.repository.AuthRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val kakaoAuth: UserApiClient,
    private val mainService: MainService,
    @ApplicationContext private val context: Context
): AuthRepository {

    override val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    override suspend fun signInWithKakao(callback: (Result<FirebaseUser>) -> Unit) {
        try {
            Log.e("TAG", "레포지토리 옴!!!")
            if(kakaoAuth.isKakaoTalkLoginAvailable(context)) {
                //카카오톡이 설치되어있는 경우
                kakaoAuth.loginWithKakaoTalk(context) { token, error ->
                    error?.let {
                        callback(Result.failure(it))
                        return@loginWithKakaoTalk
                    }

                    token?.apply {
                        accessToken //액세스 토큰
                        idToken //아이디 토큰
                        refreshToken //리프래시 토큰
                        //위의 정보를 가지고 서버에 커스텀 토큰 생성 요청

                        //firebaseAuth.signInWithCustomToken()

                        //TODO(아래 코드 삭제해야함 )
                        firebaseAuth.signInAnonymously()
                    }
                }

            } else {
                //카카오톡이 설치되어있지 않은 경우
                kakaoAuth.loginWithKakaoAccount(context) { token, error ->
                    error?.let {
                        callback(Result.failure(it))
                        return@loginWithKakaoAccount
                    }

                    token?.apply {
                        accessToken //액세스 토큰
                        idToken //아이디 토큰
                        refreshToken //리프래시 토큰
                        //위의 정보를 가지고 서버에 커스텀 토큰 생성 요청

                        //firebaseAuth.signInWithCustomToken()
                        firebaseAuth.signInAnonymously()
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