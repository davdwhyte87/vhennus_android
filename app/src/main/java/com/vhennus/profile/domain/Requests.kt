package com.vhennus.profile.domain

import kotlinx.serialization.Serializable


@Serializable
data class UpdateProfileRequest(
    val image:String?,
    val bio:String?,
    val name:String?,
    val app_f_token: String?
)



@Serializable
data class SendFriendRequest(
    val user_name:String
)