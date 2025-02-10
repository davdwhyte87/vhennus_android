package com.vhennus.di

import android.app.Application
import dagger.Module
import dagger.hilt.InstallIn


import android.content.Context
import com.vhennus.BuildConfig
import com.vhennus.general.data.AppDatabase
import com.vhennus.general.data.GetUserToken
import com.vhennus.general.data.WebSocketManager
import com.vhennus.general.utils.SoundVibratorHelper

import com.vhennus.wallet.data.WalletService
import com.vhennus.wallet.domain.dao.WalletDAO
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext

import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun getAppDatabase(@ApplicationContext context: Context):AppDatabase{
        return AppDatabase.getDatabase(context.applicationContext)
//        return Room.databaseBuilder(
//            context.applicationContext,
//            AppDatabase::class.java,
//            "app_database"
//        ).build()
    }

    @Provides
    @Singleton
    fun getWalletDAO(appDatabase: AppDatabase):WalletDAO{
        return appDatabase.walletDAO()
    }

    @Provides
    fun getWalletService():WalletService{
        return WalletService()
    }

    @Provides
    fun getUserToken(@ApplicationContext context: Context):GetUserToken{
        return GetUserToken(context)
    }

    @Provides
    fun getSoundManager(@ApplicationContext context: Context):SoundVibratorHelper{
        return SoundVibratorHelper(context)
    }

    @Provides
    @Named("webSocketUrl")
    fun provideWebSocketUrl(): String = BuildConfig.API_URL+"/api/v1/auth/chat/ws"

    @Provides
    @Singleton
    fun provideWebSocketManager(
        @Named("webSocketUrl") url: String,
        token: GetUserToken,

    ): WebSocketManager {

        return WebSocketManager(url, token)
    }

}