package com.machimpyo.dot.di

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import com.google.gson.GsonBuilder
import com.kakao.sdk.user.UserApiClient
import com.machimpyo.dot.data.store.AuthDataStore
import com.machimpyo.dot.data.store.TokenSharedPreferences
import com.machimpyo.dot.network.adapter.LocalDateAdapter
import com.machimpyo.dot.network.endpoint.MAIN_SERVICE_BASE_URL
import com.machimpyo.dot.network.interceptor.FirebaseAuthInterceptor
import com.machimpyo.dot.network.service.MainService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /*
    데이터 스토어
     */
    @Provides
    @Singleton
    fun provideTokenDataStore(
        @ApplicationContext context: Context
    ): AuthDataStore {
        return AuthDataStore(
            context
        )
    }


    /*
    Token을 헤더에 붙이기 위한 Interceptor
     */
    @Provides
    @Singleton
    fun provideFirebaseAuthInterceptor(
        prefs: TokenSharedPreferences,
        dataStore: AuthDataStore
    ): FirebaseAuthInterceptor {
        return FirebaseAuthInterceptor(prefs, dataStore)
    }

    /*
    OkHttpClient 의존성 주입
     */
    @Provides
    @Singleton
    fun provideHttpClient(
        firebaseAuthInterceptor: FirebaseAuthInterceptor
    ): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.MINUTES)
            .connectTimeout(10, TimeUnit.MINUTES)
            .addInterceptor(firebaseAuthInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    /*
    Retrofit 객체 의존성 주입
     */
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {

        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .create()

        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(MAIN_SERVICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /*
    MainService 로 만든 서비스 객체
     */
    @Singleton
    @Provides
    fun provideMainService(
        retrofit: Retrofit
    ): MainService = retrofit.create(MainService::class.java)


    @Singleton
    @Provides
    fun provideFirebaseAuth(
        prefs: TokenSharedPreferences,
        dataStore: AuthDataStore
    ): FirebaseAuth {

        val firebaseAuth = FirebaseAuth.getInstance()

        val idTokenListener = FirebaseAuth.IdTokenListener {
            Log.e("TAG", "idToken 변경 감지!!!!")
            if (it.currentUser == null) {
//                prefs.putFirebaseIdToken(null)
                dataStore.updateIdToken(null)
                return@IdTokenListener
            }

            it.currentUser?.getIdToken(false)?.addOnSuccessListener { tokenResult ->
//                prefs.putFirebaseIdToken(tokenResult?.token)
                dataStore.updateIdToken(tokenResult?.token)
                return@addOnSuccessListener
            }

//            if(it.currentUser == null) {
//
//                return@IdTokenListener
//            }
//
//            it.currentUser?.getIdToken(false)?.addOnSuccessListener { tokenResult->
//
//            }
        }

        val authStateListener = AuthStateListener {
            if(it.currentUser == null) {
                Log.e("TAG", "커런트 유저 null 감지 !!!")
                dataStore.updateUserInfo(null)
                dataStore.updateIdToken(null)
            }
        }
        firebaseAuth.addIdTokenListener(idTokenListener)
//        firebaseAuth.addAuthStateListener(authStateListener)
        return firebaseAuth

    }

    @Singleton
    @Provides
    fun providesKakaoAuth(): UserApiClient {
        return UserApiClient.instance
    }

    /*
    토큰을 캐싱해두기 위한 TokenSharedPreferences
     */
    @Singleton
    @Provides
    fun provideTokenPreferences(
        @ApplicationContext context: Context
    ): TokenSharedPreferences {
        return TokenSharedPreferences(context)
    }

}