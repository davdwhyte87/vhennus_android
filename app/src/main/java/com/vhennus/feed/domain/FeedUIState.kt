package com.vhennus.feed.domain

import android.text.BoringLayout
import androidx.collection.floatSetOf

data class FeedUIState(
    val isGetSystemDataSuccess:Boolean = false,

    val isFeedLoading:Boolean = false,
    val isFeedLoadingError:Boolean = false,
    val isFeedLoadingSuccess:Boolean = false,
    val getFeedErrorMessage:String = "",
    val isCreatePostLoading:Boolean = false,
    val isCreatePostError:Boolean=false,
    val isCreatePostSuccess:Boolean = false,
    val createPostErrorMessage:String ="",
    val isCreatePostImage:Boolean = false,

    val isGetSinglePostLoading:Boolean = false,
    val isGetSinglePostSuccess:Boolean = false,
    val isGetSinglePostError:Boolean= false,
    val getSinglePostErrorMessage:String = "",
    val isGetSinglePostRefresh:Boolean = false,

    val isCreateCommentButtonLoading:Boolean = false,
    val isCreateCommentSuccess:Boolean = false,
    val isCreateCommentError:Boolean = false,
    val createCommentErrorMessage:String = "",

    val isScrollToFeedTop: Boolean = false,

    val isLikePostError:Boolean = false,
    val isLikePostSuccess:Boolean = false,
    val likePostErrorMessage:String = "",

    val isGetAllMyPostsLoading:Boolean = false,
    val isGetAllMyPostsSuccess:Boolean = false,
    val isGetAllMyPostsError:Boolean = false,
    val getAllMyPostsErrorMessage:String  =""

)
