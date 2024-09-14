package com.vhennus

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.vhennus.auth.data.AuthViewModel
import com.vhennus.auth.presentation.loginScreen
import com.vhennus.auth.presentation.logoutScreen
import com.vhennus.auth.presentation.preLoginScreen
import com.vhennus.auth.presentation.signUpScreen
import com.vhennus.feed.data.FeedViewModel
import com.vhennus.feed.presentation.createPostScreen
import com.vhennus.home.presentation.HomeScreen
import com.vhennus.trade.data.OrderViewModel
import com.vhennus.trade.presentation.ShopCoinsScreen
import com.vhennus.trade.presentation.addPaymentMethodScreen
import com.vhennus.trade.presentation.createSellOrderScreen
import com.vhennus.trade.presentation.myOrdersScreen
import com.vhennus.trade.presentation.paymentOptionScreen
import com.vhennus.trade.presentation.singleOrderScreen
import com.vhennus.trade.presentation.singleSellOrderScreen
import com.vhennus.wallet.data.WalletViewModel
import com.vhennus.wallet.presentation.AddWalletScreen
import com.vhennus.wallet.presentation.NewWalletScreen
import com.vhennus.wallet.presentation.SingleWalletScreen
import com.vhennus.wallet.presentation.TransferScreen
import com.vhennus.wallet.presentation.WalletScreen

@Composable
fun AppNav(
    navController:NavHostController,
    walletViewModel: WalletViewModel,
    orderViewModel: OrderViewModel,
    authViewModel: AuthViewModel,
    feedViewModel: FeedViewModel
){

    NavHost(navController = navController, startDestination = NavScreen.HomeScreen.route) {
//        composable(route=NavScreen.HomeScreen.route){
//            HomeScreen(navController)
//        }
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

        composable(route=NavScreen.CreatePostScreen.route){
            createPostScreen(navController, feedViewModel)
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

//        composable(route=NavScreen.CreateSellOrderScreen.route){
//            createSellOrderScreen(navController, orderViewModel)
//        }


        composable(route=NavScreen.AddWalletScreen.route){
            AddWalletScreen(navController = navController, walletViewModel)
        }

        // jut him kin mute turn the
        composable(route=NavScreen.ShopCoinsScreen.route+"/{address}"){navBackStack->
            val address = navBackStack.arguments?.getString("address")
            if (address !=null){
                ShopCoinsScreen(navController,orderViewModel, address )
            }
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

        composable(route=NavScreen.PreLoginScreen.route){
            preLoginScreen(navController)
        }

        composable(route=NavScreen.LoginScreen.route){
            loginScreen(navController, authViewModel)
        }
        composable(route=NavScreen.SignupScreen.route){
            signUpScreen(authViewModel,navController)
        }
        
        composable(NavScreen.LogoutScreen.route){
            logoutScreen(navHostController = navController, authViewModel)
        }

        composable(NavScreen.MyPaymentMethodsScreen.route){
            paymentOptionScreen(orderViewModel, navController)
        }

        composable(NavScreen.CreatePaymentMethodScreen.route){
            addPaymentMethodScreen(navController, orderViewModel)
        }

        protectedComposable(
            navController,
            false,
            NavScreen.CreateSellOrderScreen.route+"/{address}",
            authViewModel
        ) {
            val address = it.arguments?.getString("address")
            if(address!=null) {
                createSellOrderScreen(navController, orderViewModel, address)
            }
        }

        protectedComposable(
            navController,
            false,
            NavScreen.HomeScreen.route,
            authViewModel
        ) {
            HomeScreen(navController, feedViewModel)
        }

    }
}
fun NavGraphBuilder.protectedComposable(
    navController: NavController,
    isLoggedIn: Boolean,
    route: String,
    authViewModel: AuthViewModel,
    content: @Composable (androidx.navigation.NavBackStackEntry) -> Unit
){
    composable(route) {backStackEntry->

//        if (!authViewModel.isLoggedIn()){
//            navController.navigate("pre_login") {
//                popUpTo(NavScreen.HomeScreen.route) { inclusive = false }
//            }
//        }
        if (authViewModel.isLoggedIn()) {
            content(backStackEntry)
        } else {
            navController.navigate("pre_login") {
                popUpTo(NavScreen.HomeScreen.route) { inclusive = false }
            }
        }
    }
}