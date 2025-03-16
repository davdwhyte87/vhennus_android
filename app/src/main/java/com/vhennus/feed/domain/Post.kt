package com.vhennus.feed.domain

import com.vhennus.profile.domain.Profile
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val id:String = "",
    val profile_image:String ="",
    val user_name: String ="",
    val name: String="",
    val created_at:String ="",
    val text:String ="",
    val image:String ="",
    val like_count: Int =0,
    val comment_count: Int= 0,
)

@Serializable
data class PostFeed(
    val id:String = "",
    val profile_image:String ="",
    val user_name: String ="",
    val name: String="",
    val created_at:String ="",
    val text:String ="",
    val image:String ="",
    val like_count: Int =0,
    val comment_count: Int= 0,
)

@Serializable
data class PostWithComments(
    val post: PostFeed = PostFeed(),
    val comments: List<Comment> = emptyList<Comment>()
)



@Serializable
data class Comment(
    val text: String,
    val id:String,
    val user_name: String,
    val created_at: String
)