package com.vhennus.feed.domain

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "liked_posts")
data class LikedPost(
    @PrimaryKey val postId: String
)
