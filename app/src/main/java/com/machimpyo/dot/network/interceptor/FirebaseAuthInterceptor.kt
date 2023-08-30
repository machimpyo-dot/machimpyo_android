package com.machimpyo.dot.network.interceptor

import android.util.Log
import com.machimpyo.dot.data.store.AuthDataStore
import com.machimpyo.dot.data.store.TokenSharedPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class FirebaseAuthInterceptor @Inject constructor(
    private val prefs: TokenSharedPreferences,
    private val dataStore: AuthDataStore
)  : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {

        return runBlocking {
            val request = chain.request()

//            val prefs = dataStore.context.tokenDataStore.data.first()
//            val token = prefs[ID_TOKEN]

//            val token = dataStore.idToken.value

            val prefs = dataStore.getIdTokenFlow().first()

            val token = prefs[AuthDataStore.ID_TOKEN]

            Log.e("TAG", "인터셉터 !!! $token")

            if (token != null) {
                val newRequest = request.newBuilder()
                    .addHeader("firebase_token", token)
                    .build()
                chain.proceed(newRequest)
            } else {
                chain.proceed(request)
            }

        }

    }
}
