package com.amorgens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.amorgens.home.presentation.HomeScreen
import com.amorgens.wallet.presentation.WalletScreen

@Composable
fun AppNav(navController:NavHostController){
    NavHost(navController = navController, startDestination = NavScreen.HomeScreen.route) {
        composable(route=NavScreen.HomeScreen.route){
            HomeScreen(navController)
        }
        composable(route=NavScreen.WalletScreen.route){
            WalletScreen()
        }
    }
}