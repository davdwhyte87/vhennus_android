package com.vhennus.profile.domain

import com.vhennus.general.utils.BigDecimalSerializer
import com.vhennus.profile.data.ProfileViewModel
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.math.BigDecimal

@Serializable
data class Profile(
    val id:String = "",
    val user_name:String = "",
    val bio:String = "",
    val image:String = "",
    val name:String = "",
    val created_at :String = "",
    val updated_at:String = "",
    val app_f_token: String = "",
    val wallets:String ="",
    @Serializable(with = BigDecimalSerializer::class)
    @Contextual val unclaimed_earnings: BigDecimal = BigDecimal.ZERO,
    val is_earnings_activated: Boolean = false,
    val referred_users: List<String> = emptyList<String>(),
    val earnings_wallet: String = ""
)

@Serializable
data class MiniProfile(
    val user_name: String = "",
    val name:String = "",
    val bio:String = "",
    val image:String  =""
)

@Serializable
data class ProfileWithFriends(
    val profile: Profile = Profile(),
    val friends: List<MiniProfile> = emptyList<MiniProfile>()
)