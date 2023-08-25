package com.machimpyo.dot.repository

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import com.kakao.sdk.auth.model.OAuthToken
import com.machimpyo.dot.data.model.response.UserInfo

interface AuthRepository {

    val currentUser: FirebaseUser?
    suspend fun getUserInfo(
//        idToken: String
    ): Result<UserInfo>
    suspend fun createFirebaseCustomTokenAndSignIn(token: OAuthToken, callback: (Result<FirebaseUser>)->Unit)
//    suspend fun logInWithKakaoCustomToken(customToken: String): Result<FirebaseUser>

    suspend fun signInWithKakao(context: Context, callback: (Result<FirebaseUser>) -> Unit)
    fun logOut()
}