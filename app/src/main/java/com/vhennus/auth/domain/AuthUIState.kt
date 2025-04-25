package com.vhennus.auth.domain

data class AuthUIState(
    val isSignupButtonLoading:Boolean = false,
    val isSignupSuccess:Boolean = false,
    val isSignupError:Boolean = false,
    val signupErrorMessage:String = "",

    val isLoginButtonLoading:Boolean = false,
    val isLoginSuccess:Boolean = false,
    val isLoginError:Boolean = false,
    val loginErrorMessage:String = ""


)
