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
}
