package com.vhennus.trivia.domain

data class TriviaGame(
    val id:String = "",
    val trivia_question_id:String ="",
    val winner_user_name:String? = "",
    val date:String = "",
    val is_ended:Boolean = false,
    val trivia_question:TriviaQuestion = TriviaQuestion()
)
