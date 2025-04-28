package com.vhennus.chat.domain

import kotlinx.serialization.Serializable

@Serializable
data class CreateChatReq(
    val pair_id:String = "",
    val receiver:String = "",
    val message:String = "",
    val image:String = ""
)