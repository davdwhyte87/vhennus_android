package com.vhennus.auth.domain

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class SignupReq(
    val user_name:String,
    val password:String,
    val user_type:USER_TYPE
)


@Serializable
data class LoginReq(
    val user_name:String,
    val password:String
)


@Serializable
enum class USER_TYPE{
    User,
    Admin
}


@Serializable
class SignupResp{}