package com.amorgens.trade.data


import com.amorgens.trade.domain.RequestHeader
import com.amorgens.trade.domain.requests.CreateSellOrderReq
import com.amorgens.trade.domain.response.GenericResp
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.HeaderMap
import retrofit2.http.POST

interface APIService {
    @GET("/davido")
    suspend fun sayHello():Response<String>

    @POST("/api/v1/auth/sell_order/")
    suspend fun createSellOrder(@Body data:CreateSellOrderReq, @HeaderMap header:Map<String,String> ):Response<GenericResp>
}