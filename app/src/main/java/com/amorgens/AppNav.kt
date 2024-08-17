package com.amorgens

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.amorgens.home.presentation.HomeScreen
import com.amorgens.trade.data.OrderViewModel
import com.amorgens.trade.presentation.ShopCoinsScreen
import com.amorgens.trade.presentation.createSellOrderScreen
import com.amorgens.trade.presentation.myOrdersScreen
import com.amorgens.trade.presentation.singleOrderScreen
import com.amorgens.trade.presentation.singleSellOrderScreen
import com.amorgens.wallet.data.WalletViewModel
import com.amorgens.wallet.presentation.AddWalletScreen
import com.amorgens.wallet.presentation.NewWalletScreen
import com.amorgens.wallet.presentation.SingleWalletScreen
import com.amorgens.wallet.presentation.TransferScreen
import com.amorgens.wallet.presentation.WalletScreen

@Composable
fun AppNav(
    navController:NavHostController,
    walletViewModel: WalletViewModel,
    orderViewModel: OrderViewModel
){

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
                SingleWalletScreen(address, navController, walletViewModel)
            }
        }
        composable(route=NavScreen.TransferScreen.route+"/{address}"){ navBackStack->
            val address = navBackStack.arguments?.getString("address")
            if (address != null){
                TransferScreen( navController, address, walletViewModel)
            }
        }

        composable(route=NavScreen.SingleOrderScreen.route+"/{id}"){navBackStack->
            val id = navBackStack.arguments?.getString("id")
            if(id!=null){
                singleOrderScreen(navController, orderViewModel, id)
            }
        }
        
        composable(route=NavScreen.NewWalletScreen.route){
            NewWalletScreen(navController = navController, walletViewModel)
        }

        composable(route=NavScreen.CreateSellOrderScreen.route){
            createSellOrderScreen(navController, orderViewModel)
        }


        composable(route=NavScreen.AddWalletScreen.route){
            AddWalletScreen(navController = navController, walletViewModel)
        }

        // jut him kin mute turn the
        composable(route=NavScreen.ShopCoinsScreen.route){
            ShopCoinsScreen(navController,orderViewModel )
        }

        composable(route=NavScreen.SingleSellOrderScreen.route+ "/{id}"){navBackStack->
            val id = navBackStack.arguments?.getString("id")
            if(id!=null) {
                singleSellOrderScreen(navController,orderViewModel, id)
            }
        }
        composable(route=NavScreen.MyOrdersScreen.route){
            myOrdersScreen(navController,orderViewModel )
        }

        
    }
}
fun NavGraphBuilder.protectedComposable(
    navController: NavHostController,
    isLoggedIn: Boolean,
    route: String,
    content: @Composable () -> Unit
){
    composable(route) {
        if (isLoggedIn) {
            content()
        } else {
            navController.navigate("login") {
                popUpTo("home") { inclusive = false }
            }
        }
    }
}