package com.amorgens.wallet.domain

data class WalletUIState (
    val isCreateWalletButtonLoading:Boolean,
    val isCreateWalletDone: Boolean,
    val createWalletScreenNavigateBack:Boolean,
    val createWalletSuccess:Boolean,
    val createWalletSuccessMessage: String,
    val createWalletError: Boolean,
    val createWalletErrorMessage:String,
    val isError:Boolean,
    val isSuccess:Boolean,
    val errorMessage:String,
    val successMessage:String,
    val isAddWalletButtonLoading:Boolean,
    val isAddWalletDone:Boolean,
    val isSingleWalletPageLoading: Boolean,
)