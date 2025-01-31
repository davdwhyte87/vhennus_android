package com.vhennus.profile.domain

import kotlinx.serialization.Serializable


@Serializable
data class UpdateProfileRequest(
    val image:String?,
    val bio:String?,
    val name:String?
)



@Serializable
data class SendFriendRequest(
    val user_name:String
)