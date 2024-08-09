package com.amorgens.trade.domain

data class TradeUIState(
    val isCreateSellOrderSuccess:Boolean = false,
    val isCreateSellOrderError:Boolean = false,
    val isCreateSellOrderButtonLoading:Boolean = false,
    val createSellOrderErrorMessage:String =""
)
