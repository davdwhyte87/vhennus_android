package com.vhennus.general.data


import com.vhennus.auth.domain.LoginReq
import com.vhennus.auth.domain.SignupReq
import com.vhennus.chat.domain.Chat
import com.vhennus.chat.domain.ChatPair
import com.vhennus.chat.domain.CreateChatReq
import com.vhennus.feed.domain.Comment
import com.vhennus.feed.domain.CreateCommentReq
import com.vhennus.feed.domain.CreatePostReq
import com.vhennus.feed.domain.Post
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
import com.vhennus.trade.domain.response.GenericResp
import com.vhennus.trade.domain.response.MyBuyOrdersResp
import com.vhennus.trade.domain.response.MySellOrdersResponse
import com.vhennus.trade.domain.response.SingleBuyOrdersResp
import com.vhennus.trade.domain.response.SingleSellOrderResp
import com.vhennus.trivia.domain.TriviaGame
import com.vhennus.trivia.domain.TriviaGameReq
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
    suspend fun getAllPosts(@HeaderMap header:Map<String,String> ):Response<GenericResp<List<Post>>>

    @GET("api/v1/auth/post/allmy")
    suspend fun getAllMyPosts(@HeaderMap header:Map<String,String> ):Response<GenericResp<List<Post>>>


    @GET("api/v1/auth/post/single/{id}")
    suspend fun getSinglePost(@Path("id") id:String, @HeaderMap header:Map<String,String> ):Response<GenericResp<Post>>


    @POST("api/v1/auth/post/{id}/comment/create")
    suspend fun createComment(@Path("id") id:String,@Body data:CreateCommentReq, @HeaderMap header:Map<String,String> ):Response<GenericResp<Comment>>


    @GET("api/v1/auth/post/like/{id}")
    suspend fun likePost(@Path("id") id:String, @HeaderMap header:Map<String,String> ):Response<GenericResp<Post>>
    // system data
    @GET("get_system_data")
    suspend fun getSystemData():Response<GenericResp<SystemData>>


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


    // profile
    @GET("api/v1/auth/profile/get")
    suspend fun getMyProfile( @HeaderMap header:Map<String,String> ):Response<GenericResp<Profile>>

    @GET("api/v1/auth/profile/get/{username}")
    suspend fun getUserProfile(@Path("username") username:String,@HeaderMap header:Map<String,String> ):Response<GenericResp<Profile>>

    @GET("api/v1/auth/chat/find_chat_pair/{username}")
    suspend fun findChatPair(@Path("username") username:String,@HeaderMap header:Map<String,String> ):Response<GenericResp<ChatPair>>

    @GET("api/v1/auth/chat/get_my_chat_pairs")
    suspend fun getMyChatPairs(@HeaderMap header:Map<String,String> ):Response<GenericResp<List<ChatPair>>>

    @POST("api/v1/auth/profile/update")
    suspend fun updateProfile(@Body data:UpdateProfileRequest, @HeaderMap header:Map<String,String> ):Response<GenericResp<Profile>>


    //Friend request
    @GET("api/v1/auth/user/friend_requests")
    suspend fun getMyFriendRequests( @HeaderMap header:Map<String,String> ):Response<GenericResp<List<FriendRequest>>>

    @GET("api/v1/auth/user/friend_request/accept/{id}")
    suspend fun acceptFriendRequest(@Path("id") id:String, @HeaderMap header:Map<String,String>):Response<GenericResp<String>>

    @GET("api/v1/auth/user/friend_request/reject/{id}")
    suspend fun rejectFriendRequest(@Path("id") id:String, @HeaderMap header:Map<String,String>):Response<GenericResp<String>>

    @POST("api/v1/auth/user/friend_request/send")
    suspend fun sendFriendRequest(@Body data:SendFriendRequest, @HeaderMap header:Map<String,String> ):Response<GenericResp<FriendRequest>>

    // search
    @GET("api/v1/auth/profile/search/{data}")
    suspend fun searchProfile(@Path("data") data:String, @HeaderMap header:Map<String,String> ):Response<GenericResp<List<Profile>>>


}