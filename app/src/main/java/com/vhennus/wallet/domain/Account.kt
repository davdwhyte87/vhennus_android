package com.vhennus.wallet.domain

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.math.BigInteger


@Serializable
data class Account(
    val id: String = "",
    val address:String = "",
    val chain: List<TBlock> = emptyList<TBlock>(),
    @Contextual val balance: BigDecimal = BigDecimal.ZERO,
    val created_at: String ="",
    val public_key: String = ""
)

@Serializable
data class MicroAccount(
    val address:String = "",
    @Contextual val balance: BigDecimal = BigDecimal.ZERO,
)


@Serializable
data class TBlock(
    val id: String = "",
    val sender:String = "",
    val receiver:String ="",
    val timestamp: ULong = 0UL,
    @Contextual val amount: BigDecimal = BigDecimal.ZERO
)
