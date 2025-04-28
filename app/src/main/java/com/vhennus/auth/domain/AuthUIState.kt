package com.vhennus.auth.domain

data class AuthUIState(
    val isSignupButtonLoading:Boolean = false,
    val isSignupSuccess:Boolean = false,
    val isSignupError:Boolean = false,
    val signupErrorMessage:String = "",

    val isLoginButtonLoading:Boolean = false,
    val isLoginSuccess:Boolean = false,
    val isLoginError:Boolean = false,
    val loginErrorMessage:String = "",

    val isSendResetPasswordCodeLoading:Boolean = false,
    val isSendResetPasswordCodeSuccess:Boolean = false,
    val isSendResetPasswordCodeError:Boolean = false,
    val sendResetPasswordCodeErrorMessage:String = "",

    val isVerifyAccountLoading: Boolean = false,
    val isVerifyAccountSuccess: Boolean = false,
    val isVerifyAccountError: Boolean = false,
    val verifyAccountErrorMessage:String = "",


    val resendCodeLoading: Boolean = false,
    val resendCodeSuccess: Boolean = false,
    val resendCodeError: Boolean = false,
    val resendCodeErrorMessage:String = "",




    )
