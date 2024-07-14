package com.amorgens.wallet.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.amorgens.wallet.domain.Wallet
import com.amorgens.wallet.domain.dao.WalletDAO


@Database(entities = [Wallet::class], version = 1, exportSchema = false)
abstract  class AppDatabase:RoomDatabase(){
    abstract fun walletDAO():WalletDAO

    companion object{
        @Volatile
        private var INSTANCE:AppDatabase? = null

        fun getDatabase(context:Context): AppDatabase{
            return INSTANCE?: synchronized(this){
                val instance =Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}