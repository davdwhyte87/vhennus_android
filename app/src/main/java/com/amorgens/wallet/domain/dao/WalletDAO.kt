package com.amorgens.wallet.domain.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amorgens.wallet.domain.Wallet
import kotlinx.coroutines.flow.Flow


@Dao
interface WalletDAO{
    @Query("SELECT * FROM wallet")
    fun getAllWallet() : Flow<List<Wallet>>
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWallet(wallet : Wallet)
    @Delete
    suspend fun deleteWallet(wallet : Wallet)
}