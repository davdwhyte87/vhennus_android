package com.amorgens.trade.data


import retrofit2.Response
import retrofit2.http.GET

interface APIService {
    @GET("/davido")
    suspend fun sayHello():Response<String>
}