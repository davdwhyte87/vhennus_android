package com.vhennus.wallet.domain

import kotlinx.serialization.Serializable


@Serializable
data class TransferReq(
    val sender:String,
    val receiver:String,
    val amount:String,
    val timestamp: Long,
    val id: String,
    val signature:String
)