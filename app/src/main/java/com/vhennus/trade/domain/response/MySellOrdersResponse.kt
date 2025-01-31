package com.vhennus.trade.domain.response

import com.vhennus.trade.domain.SellOrder
import kotlinx.serialization.Serializable


@Serializable
data class MySellOrdersResponse(
    val message:String ,
    val data: List<SellOrder>
)



