package com.machimpyo.dot.network.interceptor

import com.google.firebase.auth.FirebaseAuth
import com.machimpyo.dot.data.store.TokenSharedPreferences
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class FirebaseAuthIntercep업tor @Inject constructor(
    private val prefs: TokenSharedPreferences
)  : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        val token = prefs.getFirebaseIdToken()

        if (token != null) {
            val newRequest = request.newBuilder()
                .addHeader("firebase_token", token)
                .build()
            return chain.proceed(newRequest)
        }

        return chain.proceed(request)
    }
}
