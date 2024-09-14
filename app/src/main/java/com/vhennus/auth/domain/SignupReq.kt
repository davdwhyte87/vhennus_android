package com.vhennus.auth.domain

data class SignupReq(
    val user_name:String,
    val password:String
)

data class LoginReq(
    val user_name:String,
    val password:String
)
