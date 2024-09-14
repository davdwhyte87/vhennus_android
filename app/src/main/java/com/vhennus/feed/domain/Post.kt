package com.vhennus.feed.domain

data class Post(
    val profile_image:String ="",
    val user_name: String ="",
    val created_at:String ="",
    val text:String ="",
    val image:String ="",
    val likes:List<String> = emptyList(),
    val comments:List<Comment> = emptyList(),
    val number_of_views:Int = 0
)

data class Comment(
    val text: String
)