package com.vhennus.chat.domain

data class ChatPair(
    val id:String = "",
    val users:List<MUser> = emptyList(),
    val dateCreated:String = "",
    val lastMessage:String = ""
)

data class MUser(
    val userName:String = "",
    val image:String = ""
)
