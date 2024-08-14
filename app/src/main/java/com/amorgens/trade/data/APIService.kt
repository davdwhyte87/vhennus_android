package com.amorgens.trade.data


import com.amorgens.trade.domain.RequestHeader
import com.amorgens.trade.domain.requests.CreateSellOrderReq
import com.amorgens.trade.domain.response.CancelSellOrderResp
import com.amorgens.trade.domain.response.GenericResp
import com.amorgens.trade.domain.response.MyBuyOrdersResp
import com.amorgens.trade.domain.response.MySellOrdersResponse
import com.amorgens.trade.domain.response.SingleBuyOrdersResp
import com.amorgens.trade.domain.response.SingleSellOrderResp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST
import retrofit2.http.Path

interface APIService {
    @GET("/davido")
    suspend fun sayHello():Response<String>

    @POST("/api/v1/auth/sell_order/")
    suspend fun createSellOrder(@Body data:CreateSellOrderReq, @HeaderMap header:Map<String,String> ):Response<GenericResp<String>>

    @GET("api/v1/auth/sell_order/my_orders")
    suspend fun getMySellOrders(@HeaderMap header:Map<String,String>):Response<MySellOrdersResponse>

    @GET("api/v1/auth/sell_order/{id}")
    suspend fun getSingleSellOrder(@Path("id") id:String, @HeaderMap header:Map<String,String> ):Response<SingleSellOrderResp>

    @GET("api/v1/auth/buy_order/{id}")
    suspend fun getSingleBuyOrder(@Path("id") id:String, @HeaderMap header:Map<String,String>):Response<SingleBuyOrdersResp>
    @GET("api/v1/auth/buy_order/my_orders")
    suspend fun getMyBuyOrders(@HeaderMap header:Map<String,String>):Response<MyBuyOrdersResp>

    @GET("api/v1/auth/sell_order/cancel/{id}")
    suspend fun cancelSellOrder(@Path("id") id:String, @HeaderMap header:Map<String,String> ):Response<CancelSellOrderResp>
    @GET("api/v1/auth/sell_order/cancel/{id}")
    suspend fun cancelBuyOrder(@Path("id") id:String, @HeaderMap header:Map<String,String> ):Response<SingleSellOrderResp>

    @GET("api/v1/auth/buy_order/{id}/buyer_confirmed")
    suspend fun buyerConfirmBuyOrder(@Path("id") id:String,@HeaderMap header:Map<String,String> ):Response<GenericResp<String>>

    @GET("api/v1/auth/buy_order/{id}/seller_confirmed")
    suspend fun sellerConfirmBuyOrder(@Path("id") id:String,@HeaderMap header:Map<String,String> ):Response<GenericResp<String>>
}