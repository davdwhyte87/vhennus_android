package com.vhennus.chat.domain

data class CreateChatReq(
    val pair_id:String = "",
    val receiver:String = "",
    val message:String = "",
    val image:String = ""
)