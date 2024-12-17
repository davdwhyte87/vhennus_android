package com.vhennus.profile.domain


data class UpdateProfileRequest(
    val image:String,
    val bio:String,
    val name:String
)