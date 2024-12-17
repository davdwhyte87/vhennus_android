package com.vhennus.profile.domain

data class ProfileUIState(
    val isGetProfileLoading:Boolean = false,
    val isGetProfileSuccess:Boolean = false,
    val isGetProfileError:Boolean = false,
    val isGetProfileErrorMessage:String = "",

    val isUpdateProfileLoading:Boolean = false,
    val isUpdateProfileSuccess:Boolean = false,
    val isUpdateProfileError:Boolean = false,
    val updateProfileErrorMessage:String = "",

    val isGetFriendRequestsLoading:Boolean = false,
    val isGetFriendRequestsSuccess:Boolean = false,
    val isGetFriendRequestsError:Boolean = false,
    val getFrienRequestsErrorMessage:String = ""
)
