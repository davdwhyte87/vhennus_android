package com.vhennus.profile.domain

import com.vhennus.profile.data.ProfileViewModel
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val id:String = "",
    val user_name:String = "",
    val bio:String = "",
    val image:String = "",
    val name:String = "",
    val created_at :String = "",
    val updated_at:String = "",
    val app_f_token: String = ""
)

@Serializable
data class MiniProfile(
    val user_name: String = "",
    val name:String = "",
    val bio:String = "",
    val image:String  =""
)

@Serializable
data class ProfileWithFriends(
    val profile: Profile = Profile(),
    val friends: List<MiniProfile> = emptyList<MiniProfile>()
)