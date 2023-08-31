package com.machimpyo.dot.network.interceptor

import android.util.Log
import com.machimpyo.dot.data.store.AuthDataStore
import com.machimpyo.dot.data.store.TokenSharedPreferences
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class FirebaseAuthInterceptor @Inject constructor(
    private val prefs: TokenSharedPreferences,
    private val dataStore: AuthDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {


        val request = chain.request()

//            val prefs = dataStore.getIdTokenFlow().first()

//            val token = prefs[AuthDataStore.ID_TOKEN]

        val token = prefs.getFirebaseIdToken()

        Log.e("TAG", "인터셉터 !!! $token")

        return if (token != null) {
            val newRequest = request.newBuilder()
                .addHeader("firebase_token", token)
                .build()
            chain.proceed(newRequest)
        } else {
            chain.proceed(request)
        }

    }


}