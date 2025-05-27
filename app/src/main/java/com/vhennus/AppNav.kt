package com.vhennus

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.vhennus.auth.data.AuthViewModel
import com.vhennus.auth.presentation.ForgotPasswordScreen
import com.vhennus.auth.presentation.ResetPasswordScreen
import com.vhennus.auth.presentation.VerifyScreen
import com.vhennus.auth.presentation.loginScreen
import com.vhennus.auth.presentation.logoutScreen
import com.vhennus.auth.presentation.preLoginScreen
import com.vhennus.auth.presentation.signUpScreen
import com.vhennus.chat.data.ChatViewModel
import com.vhennus.chat.presentation.AllChatsScreen
import com.vhennus.chat.presentation.SingleChatScreen
import com.vhennus.earnings.data.EarningsViewModel
import com.vhennus.earnings.presentation.EarningsScreen
import com.vhennus.earnings.presentation.SelectWalletScreen
import com.vhennus.feed.data.FeedViewModel

import com.vhennus.feed.presentation.createCommentScreen
import com.vhennus.feed.presentation.createPostScreen
import com.vhennus.feed.presentation.singlePostScreen
import com.vhennus.general.data.GeneralViewModel
import com.vhennus.home.presentation.HomeScreen
import com.vhennus.profile.data.ProfileViewModel
import com.vhennus.profile.domain.Profile
import com.vhennus.profile.presentation.FriendRequestsPage
import com.vhennus.profile.presentation.OtherUserProfile
import com.vhennus.profile.presentation.editProfilePage
import com.vhennus.profile.presentation.myFriendsPage
import com.vhennus.profile.presentation.profilePage
import com.vhennus.search.presentation.SearchPage
import com.vhennus.settings.presentation.SettingsPage
import com.vhennus.trade.data.OrderViewModel
import com.vhennus.trade.presentation.ShopCoinsScreen
import com.vhennus.trade.presentation.addPaymentMethodScreen
import com.vhennus.trade.presentation.createSellOrderScreen
import com.vhennus.trade.presentation.myOrdersScreen
import com.vhennus.trade.presentation.paymentOptionScreen
import com.vhennus.trade.presentation.singleOrderScreen
import com.vhennus.trade.presentation.singleSellOrderScreen
import com.vhennus.trivia.data.TriviaViewModel
import com.vhennus.trivia.presentation.postTriviaPlayPage
import com.vhennus.trivia.presentation.triviaPage
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
    feedViewModel: FeedViewModel,
    triviaViewModel: TriviaViewModel,
    chatViewModel: ChatViewModel,
    profileViewModel:ProfileViewModel,
    generalViewModel: GeneralViewModel,
    earningsViewModel: EarningsViewModel
){

    NavHost(navController = navController, startDestination = NavScreen.HomeScreen.route) {
//        composable(route=NavScreen.HomeScreen.route){
//            HomeScreen(navController)
//        }
        composable(route=NavScreen.WalletScreen.route){
            WalletScreen(navController, walletViewModel, profileViewModel, generalViewModel)
        }
        composable(route=NavScreen.SingleWalletScreen.route+"/{address}"){navBackStack->
            val address = navBackStack.arguments?.getString("address")
            if (address != null){
                SingleWalletScreen(address, navController, walletViewModel, generalViewModel)
            }
        }
        composable(route=NavScreen.TransferScreen.route+"/{address}"){ navBackStack->
            val address = navBackStack.arguments?.getString("address")
            if (address != null){
                TransferScreen( navController, address, walletViewModel, generalViewModel)
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

        composable(route=NavScreen.TriviaScreen.route){
            triviaPage(navController,triviaViewModel, walletViewModel)
        }
        composable(route=NavScreen.PostTriviaPage.route){
            postTriviaPlayPage(navController, triviaViewModel)
        }

        composable(route=NavScreen.EditProfilePage.route){
            editProfilePage(navController, profileViewModel)
        }
        composable(route= NavScreen.SendForgotPasswordCodeScreen.route){
            ForgotPasswordScreen(navController, authViewModel)
        }
        composable(route= NavScreen.ResetPasswordScreen.route){
            ResetPasswordScreen(authViewModel, navController)
        }

        composable(route=NavScreen.FriendRequestPage.route){
            FriendRequestsPage(navController, profileViewModel)
        }
        composable(route = NavScreen.SearchPage.route) {
            SearchPage(navController, profileViewModel)
        }

//        composable(route=NavScreen.CreateSellOrderScreen.route){
//            createSellOrderScreen(navController, orderViewModel)
//        }


        composable(route=NavScreen.AddWalletScreen.route){
            AddWalletScreen(navController = navController, walletViewModel)
        }

        composable(NavScreen.MyFriendsPage.route){
            myFriendsPage(navController, profileViewModel, chatViewModel)
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

        composable(NavScreen.AllChatsScreen.route){
            AllChatsScreen(navController, chatViewModel, authViewModel)
        }
        composable(NavScreen.SingleChatScreen.route+"/{userName}",
            arguments = listOf(navArgument("userName"){type= NavType.StringType}),
            deepLinks = listOf(navDeepLink { uriPattern = "https://vhennus.com/single_chat/{userName}" })
        ){backStackEntry->
            val userNameParam = backStackEntry.arguments?.getString("userName")
//            val chatsPairIDParam = backStackEntry.arguments?.getString("chatPairID")

            val userName = if(userNameParam=="null") null else userNameParam
//            val chatsPairID = if(chatsPairIDParam == "null") null else chatsPairIDParam


            SingleChatScreen(navController, chatViewModel,profileViewModel, authViewModel, userName)
        }

        composable(NavScreen.OtherUserProfileScreen.route+"/{userName}"){backStackEntry->
            val userName = backStackEntry.arguments?.getString("userName")
            if(userName!=null){
                authViewModel.getUserName()
                val myUserName = authViewModel.userName.collectAsState().value
                if(myUserName == userName){
                    profilePage(navController,profileViewModel,feedViewModel, true)
                }else{
                    OtherUserProfile(navController,profileViewModel,feedViewModel,userName)
                }

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
        composable(route= NavScreen.EarningsScreen.route){
            EarningsScreen(navController, profileViewModel,earningsViewModel )
        }

        composable(route= NavScreen.SelectEarningsScreen.route+"/{walletsString}"){backStackEntry->
            val walletsString = backStackEntry.arguments?.getString("walletsString")
            if(walletsString!=null){
                SelectWalletScreen(navController,
                    walletViewModel,
                    walletsString,
                    profileViewModel,
                    earningsViewModel,
                    walletViewModel
                )
            }

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

        composable(NavScreen.CreateCommentScreen.route+"/{id}"){
            val id = it.arguments?.getString("id")
            if(id!=null) {
                createCommentScreen(navController, feedViewModel, id)
            }

        }

        composable(NavScreen.SinglePost.route+"/{id}"){
            val id = it.arguments?.getString("id")
            if(id!=null) {
                singlePostScreen(id, feedViewModel, navController)
            }
        }

        composable(NavScreen.VerifyAccount.route+"/{email}"){
            val email = it.arguments?.getString("email")
            if(email!=null) {
                VerifyScreen(authViewModel, navController, email)
            }else{
                VerifyScreen(authViewModel, navController, "x@x.com")
            }
        }


        composable(NavScreen.SettingsPage.route){
            SettingsPage(navController,profileViewModel, authViewModel)
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
            HomeScreen(
                navController,
                feedViewModel,
                chatViewModel,
                authViewModel,
                profileViewModel,
                generalViewModel,
                earningsViewModel
                )
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
            navController.navigate(NavScreen.LoginScreen.route) {
                popUpTo(NavScreen.HomeScreen.route) { inclusive = false }
            }
        }
    }
}