package com.vhennus.chat.domain

data class Chat(
    val id:String = "",
    val pair_id:String = "",
    val sender:String = "",
    val receiver:String = "",
    val message:String = "",
    val image:String = "",
    val created_at:String="",
    val updated_at:String = ""
)


