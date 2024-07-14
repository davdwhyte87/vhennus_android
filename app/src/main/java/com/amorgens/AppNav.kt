package com.amorgens

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.amorgens.home.presentation.HomeScreen
import com.amorgens.wallet.data.WalletViewModel
import com.amorgens.wallet.presentation.AddWalletScreen
import com.amorgens.wallet.presentation.NewWalletScreen
import com.amorgens.wallet.presentation.SingleWalletScreen
import com.amorgens.wallet.presentation.TransferScreen
import com.amorgens.wallet.presentation.WalletScreen

@Composable
fun AppNav(navController:NavHostController, walletViewModel: WalletViewModel){

    NavHost(navController = navController, startDestination = NavScreen.HomeScreen.route) {
        composable(route=NavScreen.HomeScreen.route){
            HomeScreen(navController)
        }
        composable(route=NavScreen.WalletScreen.route){
            WalletScreen(navController, walletViewModel)
        }
        composable(route=NavScreen.SingleWalletScreen.route+"/{address}"){navBackStack->
            val address = navBackStack.arguments?.getString("address")
            if (address != null){
                SingleWalletScreen(address, navController)
            }
        }
        composable(route=NavScreen.TransferScreen.route+"/{address}"){ navBackStack->
            val address = navBackStack.arguments?.getString("address")
            if (address != null){
                TransferScreen( navController, address)
            }
        }
        
        composable(route=NavScreen.NewWalletScreen.route){
            NewWalletScreen(navController = navController, walletViewModel)
        }


        composable(route=NavScreen.AddWalletScreen.route){
            AddWalletScreen(navController = navController, walletViewModel)
        }

    }
}