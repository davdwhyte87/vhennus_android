package com.vhennus.trivia.domain

data class TriviaUIState(
    val isQuestionLoading:Boolean = false,
    val isGetQuestionSuccess:Boolean = false,
    val isGetQuestionError:Boolean = false,
    val getQuestionError:String = "",

    val isGameLoading:Boolean = false,
    val isGetGameSuccess:Boolean = false,
    val isGetGameError:Boolean = false,
    val getGameError:String = "",

    val isPlayGameLoading:Boolean = false,
    val isPlayGameError:Boolean = false,
    val isPlayGameSuccess:Boolean = false,
    val playGameErrorMessage:String = ""
)
