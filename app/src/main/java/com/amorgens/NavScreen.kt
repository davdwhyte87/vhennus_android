package com.amorgens

sealed class NavScreen(
    val route: String,
    val title: String
){
    object HomeScreen: NavScreen(
        route = "home",
        title = "Home"
    )
    object WalletScreen: NavScreen(
        route = "wallet",
        title = "Wallet"
    )

    object SingleWalletScreen: NavScreen(
        route = "single_wallet",
        title = "Single Wallet"
    )

    object  TransferScreen: NavScreen(
        route = "transfer",
        title = "Transfer"
    )

    object AddWalletScreen:NavScreen(
        route ="add_wallet",
        title = "Add Wallet"
    )

    object NewWalletScreen:NavScreen(
        route = "new_wallet",
        title = "New Wallet"
    )
}
