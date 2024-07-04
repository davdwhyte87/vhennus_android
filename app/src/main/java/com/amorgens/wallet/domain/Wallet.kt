package com.amorgens.wallet.domain

data class Wallet(
    val id: String,
    val walletName:String,
    val walletAddress:String,
    val balance:String
    )