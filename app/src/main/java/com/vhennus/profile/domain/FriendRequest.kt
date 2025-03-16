package com.vhennus.profile.domain

import kotlinx.serialization.Serializable
import java.io.Serial


@Serializable
data class FriendRequest(
    val id:String = "",
    val user_name:String = "",
    val requester:String ="",
    val status:String ="",
    val created_at:String = "",
    val updated_at:String = "",
)

@Serializable
data class FriendRequestWithProfile(
    val id:String = "",
    val user_name:String = "",
    val requester:String ="",
    val status: String = "", // PENDING, ACCEPTED, REJECTED
    val created_at:String = "",
    val bio:String = "",
    val name:String = "",
    val image:String = ""
)



@Serializable
enum class FriendRequestStatus{
    PENDING,
    ACCEPTED,
    DECLINED
}
