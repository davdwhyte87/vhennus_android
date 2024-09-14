package com.vhennus.trade.domain.response

import com.vhennus.trade.domain.SellOrder

data class MySellOrdersResponse(
    val message:String ,
    val data: List<SellOrder>
)



