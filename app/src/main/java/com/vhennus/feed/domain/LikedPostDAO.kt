package com.vhennus.feed.domain

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface LikedPostDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLikedPost(likedPost: LikedPost)

    @Query("SELECT COUNT(*) FROM liked_posts WHERE postId = :postId")
    suspend fun isPostLiked(postId: String): Int

    @Query("SELECT postId FROM liked_posts")
    suspend fun getAllLikedPosts(): List<String>

    @Delete
    suspend fun removeLikedPost(likedPost: LikedPost)
}