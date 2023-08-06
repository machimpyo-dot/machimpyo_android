package com.machimpyo.dot.di

import com.machimpyo.dot.repository.AuthRepository
import com.machimpyo.dot.repository.MainRepository
import com.machimpyo.dot.repository.impl.AuthRepositoryImpl
import com.machimpyo.dot.repository.impl.MainRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindMainRepository(
        mainRepositoryImpl: MainRepositoryImpl
    ): MainRepository


    @Singleton
    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

}