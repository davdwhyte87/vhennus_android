package com.vhennus.profile.domain

data class ProfileUIState(
    val isGetProfileLoading:Boolean = false,
    val isGetProfileSuccess:Boolean = false,
    val isGetProfileError:Boolean = false,
    val isGetProfileErrorMessage:String = "",

    val isGetUserProfileLoading:Boolean = false,
    val isGetUserProfileSuccess:Boolean = false,
    val isGetUserProfileError:Boolean = false,
    val getUserProfileErrorMessage:String = "",


    val isUpdateProfileLoading:Boolean = false,
    val isUpdateProfileSuccess:Boolean = false,
    val isUpdateProfileError:Boolean = false,
    val updateProfileErrorMessage:String = "",

    val isGetFriendRequestsLoading:Boolean = false,
    val isGetFriendRequestsSuccess:Boolean = false,
    val isGetFriendRequestsError:Boolean = false,
    val getFrienRequestsErrorMessage:String = "",

    val isAcceptRequestLoading:Boolean = false,
    val isAcceptRequestSuccess:Boolean = false,
    val isAcceptRequestError:Boolean = false,
    val acceptRequestErrorMessage:String = "",

    val isRejectRequestLoading:Boolean = false,
    val isRejectRequestSuccess:Boolean = false,
    val isRejectRequestError:Boolean = false,
    val rejectRequestErrorMessage:String = ""
)
