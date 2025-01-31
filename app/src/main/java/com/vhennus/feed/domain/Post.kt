package com.vhennus.feed.domain

import com.vhennus.profile.domain.Profile
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id:String = "",
    val profile_image:String ="",
    val user_name: String ="",
    val created_at:String ="",
    val text:String ="",
    val image:String ="",
    val likes:List<String> = emptyList(),
    val comments:List<Comment> = emptyList(),
    val number_of_views:Int = 0,
    val profile:Profile = Profile()
)

@Serializable
data class Comment(
    val text: String,
    val id:String,
    val user_name: String,
    val created_at: String
)