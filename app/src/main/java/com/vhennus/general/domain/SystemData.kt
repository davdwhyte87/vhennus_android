package com.vhennus.general.domain

import com.vhennus.general.utils.BigDecimalSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal


@Serializable
data class SystemData(
    val id: Int = 0,
    @Serializable(with = BigDecimalSerializer::class)
    @Contextual val price: BigDecimal = BigDecimal("0.00"),
    val android_app_version:String = "",
    val apk_link: String = "",
    @Serializable(with = BigDecimalSerializer::class)
    @Contextual val ngn:BigDecimal = BigDecimal("0.00"),
    @Serializable(with = BigDecimalSerializer::class)
    @Contextual val price_per_min: BigDecimal = BigDecimal.ZERO,
    @Serializable(with = BigDecimalSerializer::class)
    @Contextual val ref_amount: BigDecimal = BigDecimal.ZERO
)
