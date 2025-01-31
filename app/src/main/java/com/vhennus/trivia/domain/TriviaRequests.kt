package com.vhennus.trivia.domain

import kotlinx.serialization.Serializable


@Serializable
data class TriviaGameReq(
    val answer:String,
    val wallet_address:String
)