package com.vhennus.wallet.domain

import kotlinx.serialization.Serializable


@Serializable
data class BlockchainResp<T>(
    val status: Int,
    val message: String,
    val data: T? = null
)