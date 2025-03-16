package com.vhennus.chat.domain

import com.vhennus.profile.domain.Profile
import kotlinx.serialization.Serializable


@Serializable
data class ChatPair(
    val id:String = "",
    val user2:String = "",
    val user1:String = "",
    val created_at:String = "",
    val updated_at:String = "",
    var last_message:String = "",
    val all_read: Boolean = false,
    val user1_image:String = "",
    val user2_image:String = ""
)


data class MUser(
    val userName:String = "",
    val image:String = ""
)
