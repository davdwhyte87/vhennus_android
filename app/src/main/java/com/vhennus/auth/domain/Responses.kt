package com.vhennus.auth.domain

import kotlinx.serialization.Serializable

@Serializable
data class LoginResp(
    val token:String ="",
    val email_confirmed: Boolean =false,
    val email:String = ""
)