package com.amorgens.trade.domain.response

import com.amorgens.trade.domain.SellOrder


data class SingleSellOrderResp(
    val message:String,
    val data: SellOrder
)