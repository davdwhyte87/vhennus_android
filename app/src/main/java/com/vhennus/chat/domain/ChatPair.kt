package com.vhennus.chat.domain

import com.vhennus.profile.domain.Profile

data class ChatPair(
    val id:String = "",
    val user_name:String = "",
    val users_ids:List<String> = emptyList(),
    val users:List<Profile> = emptyList(),
    val created_at:String = "",
    val updaed_at:String = "",
    var last_message:String = ""
)

data class MUser(
    val userName:String = "",
    val image:String = ""
)
