package com.vhennus.trivia.domain

import kotlinx.serialization.Serializable


@Serializable
data class TriviaQuestion(
    val id:String = "",
    val question:String = "",
    val options: List<String> = emptyList(),
    val answer:String = "",
    val is_used:Boolean = false
)
