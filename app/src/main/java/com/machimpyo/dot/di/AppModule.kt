package com.machimpyo.dot.di

import com.machimpyo.dot.network.endpoint.MAIN_SERVICE_BASE_URL
import com.machimpyo.dot.network.service.MainService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttp
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /*
    OkHttpClient 의존성 주입
     */
    @Provides
    @Singleton
    fun provideHttpClient(): OkHttpClient {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.MINUTES)
            .connectTimeout(10, TimeUnit.MINUTES)
            .addInterceptor(httpLoggingInterceptor)
            .build()
    }

    /*
    Retrofit 객체 의존성 주입
     */
    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(MAIN_SERVICE_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
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
}