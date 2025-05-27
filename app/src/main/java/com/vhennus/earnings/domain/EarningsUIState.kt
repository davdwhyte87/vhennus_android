package com.vhennus.earnings.domain

data class EarningsUIState(
    val isActivateEarningsLoading: Boolean = false,
    val isActivateEarningsSuccess: Boolean = false,
    val isActivateEarningsError: Boolean = false,
    val activateEarningsErrorMessage:String = "",

    val isGetWalletsLoading: Boolean = false,
    val isCashoutLoading: Boolean=false,
    val isCashoutSuccess: Boolean=false,
    val isCashoutError: Boolean=false,
    val cashoutErrorMessage: String =""
)
