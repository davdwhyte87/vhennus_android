package com.vhennus.trade.domain

import com.vhennus.trade.domain.requests.Currency
import com.vhennus.trade.domain.requests.PaymentMethod
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

import java.math.BigDecimal

//data class SellOrder(
//    val id:String,
//    val userName:String,
//    val minAmount:BigDecimal,
//    val maxAmount:BigDecimal,
//    val amount:BigDecimal
//)


@Serializable
data class SellOrder(
    val id:String = "",
    val user_name:String = "",
    val buy_orders_id:List<String> = listOf(""),
    val buy_orders:List<BuyOrder> = listOf(BuyOrder()),
    @Contextual val amount: BigDecimal = BigDecimal("0.0"),
    @Contextual val min_amount:BigDecimal= BigDecimal("0.00"),
    @Contextual val max_amount:BigDecimal= BigDecimal("0.00"),
    val is_closed:Boolean= false,
    val currency: Currency = Currency.NGN,
    val created_at:String = "",
    val updated_at:String= "",
    val payment_method: PaymentMethod= PaymentMethod.Bank,
    val payment_method_id:String= "",
    val payment_method_data: PaymentMethodData? = PaymentMethodData(),
    val wallet_address:String = "",
    val phone_number:String = ""
)