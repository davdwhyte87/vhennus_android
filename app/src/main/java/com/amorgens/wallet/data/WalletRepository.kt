package com.amorgens.wallet.data

import com.amorgens.wallet.domain.Wallet
import com.amorgens.wallet.domain.dao.WalletDAO
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WalletRepository @Inject constructor(private val walletDAO: WalletDAO) {
    suspend fun insertWallet(wallet:Wallet){
        return walletDAO.insertWallet(wallet)
    }

    suspend fun getAllWallets():Flow<List<Wallet>>{
        return walletDAO.getAllWallet()
    }
}