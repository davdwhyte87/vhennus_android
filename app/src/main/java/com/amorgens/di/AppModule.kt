package com.amorgens.di

import dagger.Module
import dagger.hilt.InstallIn


import android.app.Application
import android.content.Context
import com.amorgens.wallet.data.WalletService
import dagger.Provides

import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    fun getWalletService():WalletService{
        return WalletService()
    }
}