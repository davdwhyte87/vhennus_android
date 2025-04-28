package com.vhennus

import androidx.navigation.Navigator

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

    object ShopCoinsScreen:NavScreen(
        route = "shop_coins",
        title = "Shop Coins"
    )

    object  SingleOrderScreen: NavScreen(
        route = "single_buy_order",
        title = "Buy Order"
    )

    object CreateSellOrderScreen:NavScreen(
        route = "create_sell_order",
        title = "Create Sell Order"
    )

    object SingleSellOrderScreen:NavScreen(
        route = "single_sell_order",
        title = "Sell Order"
    )

    object MyOrdersScreen:NavScreen(
        route = "my_orders",
        title = "My Orders"
    )

    object PreLoginScreen:NavScreen(
        route = "pre_login",
        title = "Pre Login"
    )

    object LoginScreen:NavScreen(
        route = "login",
        title = "Login"
    )
    object SignupScreen:NavScreen(
        route = "signup",
        title = "Signup"
    )
    object LogoutScreen:NavScreen(
        route = "logout",
        title = "Logout"
    )

    object MyPaymentMethodsScreen:NavScreen(
        route = "my_payment_method",
        title = "My Payment Method"
    )

    object CreatePaymentMethodScreen:NavScreen(
        route = "create_payment_method",
        title = "Create Payment Method"
    )

    object CreatePostScreen:NavScreen(
        route = "create_post",
        title = "Create Post"
    )

    object CreateCommentScreen:NavScreen(
        route = "create_comment",
        title = "Create Comment"
    )

    object SinglePost:NavScreen(
        route = "single_post",
        title = "Single Post"
    )

    object TriviaScreen:NavScreen(
        route = "trivia",
        title = "Trivia"
    )

    object PostTriviaPage:NavScreen(
        route = "post_trivia",
        title = "Post Trivia"
    )

    object AllChatsScreen:NavScreen(
        route = "all_chats",
        title = "All Chats"
    )

    object SingleChatScreen: NavScreen(
        route = "single_chat",
        title = "Single Chat"
    )

    object EditProfilePage:NavScreen(
        route = "edit_profile",
        title = "Edit Profile"
    )

    object MyFriendsPage:NavScreen(
        route = "my_friends",
        title = "My Friends"
    )

    object FriendRequestPage:NavScreen(
        route = "friend_request",
        title = "Friend Request"
    )

    object SearchPage:NavScreen(
        route = "search",
        title = "Search Page"
    )

    object SettingsPage:NavScreen(
        route = "settings",
        title = "Settings"
    )

    object OtherUserProfileScreen:NavScreen(
        route = "other_user_profile",
        title = "User Profile"
    )

    object VerifyAccount: NavScreen(
        route = "verify_account",
        title = "Verify Account"
    )

    object SendForgotPasswordCodeScreen: NavScreen(
        route = "send_forgot_password_code",
        title = "Forgot Password"
    )

    object ResetPasswordScreen: NavScreen(
        route = "reset_password",
        title = "Forgot Password"
    )

}
