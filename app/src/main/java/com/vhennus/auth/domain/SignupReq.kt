package com.vhennus.auth.domain

data class SignupReq(
    val user_name:String,
    val password:String,
    val user_type:USER_TYPE
)

data class LoginReq(
    val user_name:String,
    val password:String
)

enum class USER_TYPE{
    User,
    Admin
}
