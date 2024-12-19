package com.vhennus.profile.domain

data class FriendRequest(
    val id:String = "",
    val user_name:String = "",
    val requester:String ="",
    val status:FriendRequestStatus = FriendRequestStatus.PENDING,
    val created_at:String = "",
    val updated_at:String = "",
    val requester_profile:Profile = Profile()
)

enum class FriendRequestStatus{
    PENDING,
    ACCEPTED,
    DECLINED
}
