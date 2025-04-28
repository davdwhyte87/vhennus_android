package com.vhennus.wallet.domain

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal


@Serializable
data class Transaction(
    val sender: String,
    val receiver: String,
    @Contextual val amount: BigDecimal,
    val nonce: Int,
    val signature: String,
    )