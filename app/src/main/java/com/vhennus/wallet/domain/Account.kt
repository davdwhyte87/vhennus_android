package com.vhennus.wallet.domain

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal


@Serializable
data class Account(
    val id: String = "",
    val address:String = "",
    val wallet_name:String = "",
    val nonce: Int = 0,
    @Contextual val balance: BigDecimal = BigDecimal.ZERO,
    val created_at: String ="" ,
    val public_key: String = ""
)
