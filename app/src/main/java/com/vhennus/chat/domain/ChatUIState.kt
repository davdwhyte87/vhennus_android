package com.vhennus.chat.domain

data class ChatUIState(
    val isGetChatsSuccess:Boolean = false,
    val isGetChatsError:Boolean = false,
    val getChatsErrorMessage:String = "",
    val isGetChatsLoading:Boolean = false
)
