package com.amorgens.trade.domain.requests

import java.math.BigDecimal

data class CreateBuyOrderReq(
    val amount:BigDecimal,
    val sell_order_id:String
)

data class CreateOrderMessageReq(
    val receiver_user_name: String,
    val buy_order_id:String,
    val text:String,
    val image:String
)