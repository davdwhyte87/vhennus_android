package com.vhennus.trade.domain.response

import com.vhennus.trade.domain.BuyOrder
import com.vhennus.trade.domain.SellOrder


data class SingleSellOrderResp(
    val message:String,
    val data: SellOrder
)

data class GenericResp<T>(
    val message: String,
    val server_message: String?,
    val data:T?
)

data class CancelSellOrderResp(
    val message: String,
    val data:String
)

data class MyBuyOrdersResp(
    val message: String,
    val data:List<BuyOrder>
)

data class SingleBuyOrdersResp(
    val message: String,
    val data:BuyOrder
)

