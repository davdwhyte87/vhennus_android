package com.vhennus.feed.domain

import kotlinx.serialization.Serializable


@Serializable
data class CreatePostReq(
    val text:String,
    val image:String?
)

@Serializable
data class CreateCommentReq(
    val text:String
)