package com.vhennus.auth.domain

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class SignupReq(
    val user_name:String,
    val password:String,
    val user_type:USER_TYPE,
    val email:String,
    val referral: String? = null
)


@Serializable
data class LoginReq(
    val user_name:String,
    val password:String,
)

@Serializable
data class GetResetPasswordCodeReq(
    val user_name:String,
)

@Serializable
data class ChangePasswordReq(
    val user_name:String,
    val code: String,
    val password: String
)



@Serializable
data class ConfirmAccountReq(
    val code:String,
    val email:String
)

@Serializable
data class ResendCodeReq(
    val email:String
)

@Serializable
enum class USER_TYPE{
    User,
    Admin
}


@Serializable
class SignupResp{}