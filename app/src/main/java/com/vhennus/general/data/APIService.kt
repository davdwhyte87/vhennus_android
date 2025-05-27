package com.vhennus.general.data


import com.vhennus.auth.domain.ChangePasswordReq
import com.vhennus.auth.domain.ConfirmAccountReq
import com.vhennus.auth.domain.GetResetPasswordCodeReq
import com.vhennus.auth.domain.LoginReq
import com.vhennus.auth.domain.LoginResp
import com.vhennus.auth.domain.ResendCodeReq
import com.vhennus.auth.domain.SignupReq
import com.vhennus.auth.domain.SignupResp
import com.vhennus.chat.domain.Chat
import com.vhennus.chat.domain.ChatPair
import com.vhennus.chat.domain.CreateChatReq
import com.vhennus.feed.domain.Comment
import com.vhennus.feed.domain.CreateCommentReq
import com.vhennus.feed.domain.CreatePostReq
import com.vhennus.feed.domain.Post
import com.vhennus.feed.domain.PostFeed
import com.vhennus.feed.domain.PostWithComments
import com.vhennus.general.domain.SystemData
import com.vhennus.profile.domain.FriendRequest
import com.vhennus.profile.domain.Profile
import com.vhennus.profile.domain.SendFriendRequest
import com.vhennus.profile.domain.UpdateProfileRequest
import com.vhennus.trade.domain.BuyOrder
import com.vhennus.trade.domain.OrderMessage
import com.vhennus.trade.domain.PaymentMethodData
import com.vhennus.trade.domain.SellOrder
import com.vhennus.trade.domain.requests.CreateBuyOrderReq
import com.vhennus.trade.domain.requests.CreateOrderMessageReq
import com.vhennus.trade.domain.requests.CreatePaymentMethod
import com.vhennus.trade.domain.requests.CreateSellOrderReq
import com.vhennus.general.domain.GenericResp
//import com.vhennus.general.domain.LoginResp
import com.vhennus.general.domain.MyBuyOrdersResp
import com.vhennus.trade.domain.response.MySellOrdersResponse
import com.vhennus.general.domain.SingleBuyOrdersResp
import com.vhennus.general.domain.SingleSellOrderResp
import com.vhennus.profile.domain.FriendRequestWithProfile
import com.vhennus.profile.domain.MiniProfile
import com.vhennus.profile.domain.ProfileWithFriends
import com.vhennus.profile.domain.UpdateEarnings
import com.vhennus.trivia.domain.TriviaGame
import com.vhennus.trivia.domain.TriviaGameReq
import com.vhennus.wallet.domain.Account
import com.vhennus.wallet.domain.AddWalletReq
import com.vhennus.wallet.domain.CreateWalletReq
import com.vhennus.wallet.domain.GetWalletReq
import com.vhennus.wallet.domain.TransferReq
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {
    @GET("/davido")
    suspend fun sayHello():Response<String>

    @POST("/api/v1/auth/sell_order/sell")
    suspend fun createSellOrder(@Body data:CreateSellOrderReq, @HeaderMap header:Map<String,String> ):Response<GenericResp<SellOrder>>

    @GET("api/v1/auth/sell_order/my_orders")
    suspend fun getMySellOrders(@HeaderMap header:Map<String,String>):Response<MySellOrdersResponse>

    @GET("api/v1/auth/sell_order/single/{id}")
    suspend fun getSingleSellOrder(@Path("id") id:String, @HeaderMap header:Map<String,String> ):Response<SingleSellOrderResp>

    @GET("api/v1/auth/buy_order/single/{id}")
    suspend fun getSingleBuyOrder(@Path("id") id:String, @HeaderMap header:Map<String,String>):Response<SingleBuyOrdersResp>

    @GET("api/v1/auth/buy_order/my_orders")
    suspend fun getMyBuyOrders(@HeaderMap header:Map<String,String>):Response<MyBuyOrdersResp>

    @GET("api/v1/auth/sell_order/cancel/{id}")
    suspend fun cancelSellOrder(@Path("id") id:String, @HeaderMap header:Map<String,String> ):Response<GenericResp<SellOrder>>

    @GET("api/v1/auth/buy_order/cancel/{id}")
    suspend fun cancelBuyOrder(@Path("id") id:String, @HeaderMap header:Map<String,String> ):Response<GenericResp<BuyOrder>>

    @GET("api/v1/auth/buy_order/buyer_confirmed/{id}")
    suspend fun buyerConfirmBuyOrder(@Path("id") id:String,@HeaderMap header:Map<String,String> ):Response<GenericResp<String>>

    @GET("api/v1/auth/buy_order/seller_confirmed/{id}")
    suspend fun sellerConfirmBuyOrder(@Path("id") id:String,@HeaderMap header:Map<String,String> ):Response<GenericResp<String>>

    @GET("api/v1/auth/sell_order/open_orders")
    suspend fun getOpenSellOrders(@HeaderMap header:Map<String,String> ):Response<GenericResp<List<SellOrder>>>

    @POST("api/v1/auth/buy_order/buy")
    suspend fun createBuyOrder(@Body order:CreateBuyOrderReq,  @HeaderMap header:Map<String,String> ):Response<GenericResp<BuyOrder>>


    @POST("api/v1/auth/order_message/post")
    suspend fun createOrderMessage(@Body order:CreateOrderMessageReq,  @HeaderMap header:Map<String,String> ):Response<GenericResp<OrderMessage>>

    @GET("api/v1/auth/order_message/get_all/{id}")
    suspend fun getAllOrderMessage(@Path("id") id:String,  @HeaderMap header:Map<String,String> ):Response<GenericResp<List<OrderMessage>>>


    // auth
    @POST("user/kura_signup")
    suspend fun signup(@Body data: SignupReq):Response<GenericResp<String>>

    @POST("user/kura_login")
    suspend fun login(@Body data: LoginReq):Response<GenericResp<String>>

    @POST("/create_account")
    suspend fun create_account(@Body data: SignupReq):Response<GenericResp<String>>

    @POST("/confirm_account")
    suspend fun confirm_account(@Body data: ConfirmAccountReq):Response<GenericResp<String>>

    @POST("/resend_code")
    suspend fun resend_code(@Body data: ResendCodeReq):Response<GenericResp<String>>

    @POST("/login")
    suspend fun login2(@Body data: LoginReq):Response<GenericResp<LoginResp>>

    @POST("/get_reset_password_code")
    suspend fun getResetPasswordCode(@Body data: GetResetPasswordCodeReq): Response<GenericResp<String>>

    @POST("/change_password")
    suspend fun changePassword(@Body data: ChangePasswordReq): Response<GenericResp<String>>


    // payment method
    @GET("api/v1/auth/payment_method/my_payment_methods")
    suspend fun getMyPaymentMethods( @HeaderMap header:Map<String,String> ):Response<GenericResp<List<PaymentMethodData>>>

    @POST("api/v1/auth/payment_method/create")
    suspend fun addPaymentMethods(@Body data:CreatePaymentMethod, @HeaderMap header:Map<String,String> ):Response<GenericResp<PaymentMethodData>>

    @GET("api/v1/auth/payment_method/delete/{id}")
    suspend fun deletePaymentMethod(@Path("id") id:String, @HeaderMap header:Map<String,String>):Response<GenericResp<String>>


    // posts --------------------------- ----------------------------- ------------------

    @POST("api/v1/auth/post/create")
    suspend fun createPost(@Body data:CreatePostReq, @HeaderMap header:Map<String,String> ):Response<GenericResp<Post>>

    @GET("api/v1/auth/post/all")
    suspend fun getAllPosts(@HeaderMap header:Map<String,String> ):Response<GenericResp<List<PostFeed>>>

    @GET("api/v1/auth/post/all/{userName}")
    suspend fun getAllUserPosts(@Path("userName") userName: String, @HeaderMap header:Map<String,String> ):Response<GenericResp<List<PostFeed>>>


    @GET("api/v1/auth/post/allmy")
    suspend fun getAllMyPosts(@HeaderMap header:Map<String,String> ):Response<GenericResp<List<PostFeed>>>


    @GET("api/v1/auth/post/single/{id}")
    suspend fun getSinglePost(@Path("id") id:String, @HeaderMap header:Map<String,String> ):Response<GenericResp<PostWithComments>>


    @POST("api/v1/auth/post/{id}/comment/create")
    suspend fun createComment(@Path("id") id:String,@Body data:CreateCommentReq, @HeaderMap header:Map<String,String> ):Response<GenericResp<Comment>>


    @GET("api/v1/auth/post/like/{id}")
    suspend fun likePost(@Path("id") id:String, @HeaderMap header:Map<String,String> ):Response<GenericResp<Post>>
    // system data
    @GET("get_system_data")
    suspend fun getSystemData():Response< GenericResp<SystemData>>


    // trivia
    @GET("api/v1/auth/trivia/todays_game")
    suspend fun getTriviaGame( @HeaderMap header:Map<String,String> ):Response<GenericResp<TriviaGame>>

    @POST("api/v1/auth/trivia/play")
    suspend fun playTriviaGame(@Body data:TriviaGameReq, @HeaderMap header:Map<String,String> ):Response<GenericResp<String>>


    // chats
    @GET("api/v1/auth/chat/get_pair/{id}")
    suspend fun getChatsByPair(@Path("id") id:String, @HeaderMap header:Map<String,String> ):Response<GenericResp<List<Chat>>>

    @GET("api/v1/auth/chat/get_all_chats")
    suspend fun getAllChats( @HeaderMap header:Map<String,String> ):Response<GenericResp<List<Chat>>>

    @POST("api/v1/auth/chat/create")
    suspend fun createChat(@Body data:CreateChatReq, @HeaderMap header:Map<String,String> ):Response<GenericResp<String>>

    @GET("api/v1/auth/chat/get_my_chat_pairs")
    suspend fun getAllChatPairs( @HeaderMap header:Map<String,String> ):Response<GenericResp<List<ChatPair>>>

    @GET("api/v1/auth/chat/find_chat_pair/{username}")
    suspend fun findChatPair(@Path("username") username:String,@HeaderMap header:Map<String,String> ):Response<GenericResp<ChatPair>>

    @GET("api/v1/auth/chat/get_my_chat_pairs")
    suspend fun getMyChatPairs(@HeaderMap header:Map<String,String> ):Response<GenericResp<List<ChatPair>>>


    // profile
    @GET("api/v1/auth/profile/get")
    suspend fun getMyProfile( @HeaderMap header:Map<String,String> ):Response<GenericResp<ProfileWithFriends>>

    @GET("api/v1/auth/profile/get/{username}")
    suspend fun getUserProfile(@Path("username") username:String,@HeaderMap header:Map<String,String> ):Response<GenericResp<ProfileWithFriends>>

    @POST("api/v1/auth/profile/update")
    suspend fun updateProfile(@Body data:UpdateProfileRequest, @HeaderMap header:Map<String,String> ):Response<GenericResp<Profile>>

    @POST("api/v1/auth/profile/add_wallet")
    suspend fun addWallet(@Body data: AddWalletReq, @HeaderMap header:Map<String,String> ):Response<GenericResp<String>>

    @GET("api/v1/auth/profile/cashout_earnings")
    suspend fun cashoutEarnings(@HeaderMap header:Map<String,String> ):Response<GenericResp<String>>

    @GET("api/v1/auth/profile/activate_earnings")
    suspend fun activateEarnings(@HeaderMap header:Map<String,String> ):Response<GenericResp<String>>

    @POST("api/v1/auth/profile/update_earnings")
    suspend fun postEarnings(@Body data: UpdateProfileRequest, @HeaderMap header:Map<String,String> ):Response<GenericResp<String>>


    //Friend request
    @GET("api/v1/auth/user/friend_requests")
    suspend fun getMyFriendRequests( @HeaderMap header:Map<String,String> ):Response<GenericResp<List<FriendRequestWithProfile>>>

    @GET("api/v1/auth/user/friend_request/accept/{id}")
    suspend fun acceptFriendRequest(@Path("id") id:String, @HeaderMap header:Map<String,String>):Response<GenericResp<String>>

    @GET("api/v1/auth/user/friend_request/reject/{id}")
    suspend fun rejectFriendRequest(@Path("id") id:String, @HeaderMap header:Map<String,String>):Response<GenericResp<String>>

    @POST("api/v1/auth/user/friend_request/send")
    suspend fun sendFriendRequest(@Body data:SendFriendRequest, @HeaderMap header:Map<String,String> ):Response<GenericResp<FriendRequest>>

    // search
    @GET("api/v1/auth/profile/search/{data}")
    suspend fun searchProfile(@Path("data") data:String, @HeaderMap header:Map<String,String> ):Response<GenericResp<List<MiniProfile>>>

    // delete account
    @GET("api/v1/auth/user/delete")
    suspend fun deleteAccount( @HeaderMap header:Map<String,String> ):Response<GenericResp<String>>


    // blockchain
    @POST("wallet/create_wallet")
    suspend fun createWallet(@Body data: CreateWalletReq):Response<GenericResp<String>>
    @POST("wallet/verify_account")
    suspend fun verifyAccount(@Body data: AddWalletReq):Response<GenericResp<String>>
    @POST("wallet/get_account")
    suspend fun getAccount(@Body data: GetWalletReq):Response<GenericResp<Account>>
    @POST("wallet/transfer")
    suspend fun transfer(@Body data: TransferReq):Response<GenericResp<String>>


}