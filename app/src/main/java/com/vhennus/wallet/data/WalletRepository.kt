package com.vhennus.wallet.data

import com.vhennus.wallet.domain.Wallet
import com.vhennus.wallet.domain.dao.WalletDAO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WalletRepository @Inject constructor(private val walletDAO: WalletDAO) {
    suspend fun insertWallet(wallet:Wallet){
        return walletDAO.insertWallet(wallet)
    }

    suspend fun updateWallet(wallet: Wallet){
        return walletDAO.updateWallet(wallet)
    }

    suspend fun getAllWallets():Flow<List<Wallet>>{
        return walletDAO.getAllWallet()
    }
    suspend fun getAllWallets2(userName:String):Flow<List<Wallet>>{
        return walletDAO.getAllWallet2(userName)
    }
}