package com.vhennus.settings.domain

data class SettingsUIState(
    val isDeleteAccountLoading:Boolean = false,
    val isDeleteAccountSuccess:Boolean = false,
    val isDeleteAccountError:Boolean = false,
    val deleteAccountErrorMessage:String = ""
)
