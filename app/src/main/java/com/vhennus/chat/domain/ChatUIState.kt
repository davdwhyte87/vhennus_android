package com.vhennus.chat.domain

data class ChatUIState(
    val isGetChatsSuccess:Boolean = false,
    val isGetChatsError:Boolean = false,
    val getChatsErrorMessage:String = "",
    val isGetChatsLoading:Boolean = false,

    val isCreateChatLoading: Boolean = false,
    val isCreateChatSuccess:Boolean = false,
    val isCreateChatError:Boolean = false,
    val createChatErrorMessage:String = "",

    val isGetAllChatsSuccess:Boolean = false,
    val isGetAllChatsError:Boolean = false,
    val isGetAllChatsErrorMessage:String = "",
    val isGetAllChatsLoading:Boolean = false
)
