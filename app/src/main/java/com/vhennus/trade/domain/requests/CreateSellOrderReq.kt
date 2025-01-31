package com.vhennus.trade.domain.requests

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal


@Serializable
data class CreateSellOrderReq(
    @Contextual val amount:BigDecimal,
    @Contextual val min_amount:BigDecimal,
    //val max_amount:BigDecimal,
    val currency:Currency,
    val payment_method:PaymentMethod,
    val payment_method_id:String,
    val wallet_address:String,
    val phone_number:String,
    val password:String
)


@Serializable
enum class Currency{
    NGN,
    USD
}


@Serializable
enum class PaymentMethod {
    Bank,
    Paypal
}

