package com.machimpyo.dot.repository

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import com.kakao.sdk.auth.model.OAuthToken

interface AuthRepository {

    val currentUser: FirebaseUser?

//    suspend fun createKakaoCustomToken(token: OAuthToken): Result<String>
//    suspend fun logInWithKakaoCustomToken(customToken: String): Result<FirebaseUser>

    suspend fun signInWithKakao(context: Context, callback: (Result<FirebaseUser>) -> Unit)
    fun logOut()
}