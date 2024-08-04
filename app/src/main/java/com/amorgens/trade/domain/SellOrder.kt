package com.amorgens.trade.domain

import java.math.BigDecimal

data class SellOrder(
    val id:String,
    val userName:String,
    val minAmount:BigDecimal,
    val maxAmount:BigDecimal,
    val amount:BigDecimal
)
