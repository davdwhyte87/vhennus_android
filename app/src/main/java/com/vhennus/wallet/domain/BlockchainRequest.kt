package com.vhennus.wallet.domain

import kotlinx.serialization.Serializable


@Serializable
data class BlockchainRequest <T>(
    val action: String,
    val data:T,
)
