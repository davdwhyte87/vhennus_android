package com.vhennus.general.domain

import kotlinx.serialization.Serializable


@Serializable
data class SystemData(
    val id:String = "",
    val price:String = "",
    val android_app_version:String = ""
)
