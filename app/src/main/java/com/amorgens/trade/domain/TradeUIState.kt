package com.amorgens.trade.domain

data class TradeUIState(
    val isCreateSellOrderSuccess:Boolean = false,
    val isCreateSellOrderError:Boolean = false,
    val isCreateSellOrderButtonLoading:Boolean = false,
    val createSellOrderErrorMessage:String ="",
    val isError:Boolean = false,
    val isSuccess:Boolean = false,
    val errorMessage:String = "",
    val successMessage:String = "",
    val isLoading:Boolean = false,
    val isLoadingGetSingleBuyOrderPage:Boolean = false,
    val isGetBuyOrderPageError:Boolean = false,
    val isGetBuyOrderPageSuccess:Boolean = false,
    val getBuyOrderPageErrorMessage:String = "",


    val isConfirmBuyOrderButtonLoading:Boolean = false,
    val isConfirmBuyOrderSuccess: Boolean = false,
    val isConfirmBuyOrderError: Boolean = false,
    val confirmBuyOrderErrorMessage:String = ""
)
