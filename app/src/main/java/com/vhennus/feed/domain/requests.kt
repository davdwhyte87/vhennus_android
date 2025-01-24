package com.vhennus.feed.domain



data class CreatePostReq(
    val text:String,
    val image:String?
)


data class CreateCommentReq(
    val text:String
)