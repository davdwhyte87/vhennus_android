package com.vhennus.general.domain

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal


@Serializable
data class SystemData(
    val id: Int = 0,
    @Contextual val price: BigDecimal = BigDecimal("0.00"),
    val android_app_version:String = ""
)
