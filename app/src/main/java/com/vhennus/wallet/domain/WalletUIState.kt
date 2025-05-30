package com.vhennus.wallet.domain

data class WalletUIState (
    val isCreateWalletButtonLoading:Boolean = false,
    val isCreateWalletDone: Boolean = false,
    val createWalletScreenNavigateBack:Boolean = false,
    val createWalletSuccess:Boolean = false,
    val createWalletSuccessMessage: String ="",
    val createWalletError: Boolean = false,
    val createWalletErrorMessage:String ="",
    val isError:Boolean = false,
    val isSuccess:Boolean = false,
    val errorMessage:String ="",
    val successMessage:String ="",

    val isAddWalletDone:Boolean = false,
    val isSingleWalletPageLoading: Boolean = false,
    val isSyncingLocalWallet:Boolean = false,

    val isAddWalletButtonLoading:Boolean = false,
    val isAddWalletSuccess:Boolean = false,
    val isAddWalletError:Boolean = false,
    val addWalletErrorMessage:String="",

    val isTransferSuccessful:Boolean = false,
    val isTransferError:Boolean = false,
    val isTransferButtonLoading:Boolean = false,
    val transferErrorMessage:String = " ",

    val isGetAllWalletsLoading: Boolean = false,
    val isGetAllWalletsSuccess: Boolean = false,
    val isGetAllWalletsError: Boolean = false,
    val getAllWalletsErrorMessage:String = "",

    val isGetSingleWalletLoading: Boolean = false,
    val isGetSingleWalletSuccess: Boolean = false,
    val isGetSingleWalletError: Boolean = false,
    val getSingleWalletErrorMessage:String = "",


    val isGetSingleWalletTransactionsLoading: Boolean = false,
    val isGetSingleWalletTransactionsSuccess: Boolean = false,
    val isGetSingleWalletTransactionsError: Boolean = false,
    val getSingleWalletTransactionsErrorMessage:String = ""

)