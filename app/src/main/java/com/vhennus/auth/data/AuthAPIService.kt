package com.vhennus.auth.data

import com.vhennus.auth.domain.SignupReq
import com.vhennus.general.domain.GenericResp
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthAPIService {

    @POST("user/kura_signup")
    suspend fun signup(@Body data:SignupReq):GenericResp<String>


}