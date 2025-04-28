package com.vhennus.wallet.domain

import kotlinx.serialization.Serializable


@Serializable
data class CreateWalletReq (
    val address:String,
    val wallet_name:String,
    val public_key:String
)