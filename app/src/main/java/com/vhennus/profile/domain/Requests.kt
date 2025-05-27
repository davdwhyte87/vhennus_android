package com.vhennus.profile.domain

import com.vhennus.general.utils.BigDecimalSerializer
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal


@Serializable
data class UpdateProfileRequest(
    val image:String?,
    val bio:String?,
    val name:String?,
    val app_f_token: String?,
    val earnings_wallet:String?,
    val new_earning:String? = null
)


@Serializable
data class UpdateEarnings(
    @Serializable(with = BigDecimalSerializer::class)
    @Contextual val new_earning: BigDecimal
)



@Serializable
data class SendFriendRequest(
    val user_name:String
)