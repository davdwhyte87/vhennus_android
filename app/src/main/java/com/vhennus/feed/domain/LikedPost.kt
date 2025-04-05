package com.vhennus.feed.domain

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Serializable
@Entity(tableName = "liked_posts")
data class LikedPost(
    @PrimaryKey val postId: String
)
