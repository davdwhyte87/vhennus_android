package com.vhennus.wallet.domain

import kotlinx.serialization.Serializable


@Serializable
data class GetWalletReq(
    val address: String
)

@Serializable
data class AddWalletReq(
    val address:String,
    val message:String,
    val signature: String
    )