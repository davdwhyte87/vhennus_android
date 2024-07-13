package com.amorgens.wallet.domain

data class WalletUIState (
    val isCreateWalletButtonLoading:Boolean,
    val isError:Boolean,
    val isCreateWalletDone: Boolean,
    val createWalletScreenNavigateBack:Boolean,
    val createWalletSuccess:Boolean,
    val createWalletSuccessMessage: String,
    val createWalletError: Boolean,
    val createWalletErrorMessage:String
)