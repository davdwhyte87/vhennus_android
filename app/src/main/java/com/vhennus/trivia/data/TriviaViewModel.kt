package com.vhennus.trivia.data

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vhennus.general.data.APIService
import com.vhennus.general.data.GetUserToken
import com.vhennus.general.utils.CLog
import com.vhennus.trade.domain.response.GenericResp
import com.vhennus.trivia.domain.TriviaGame
import com.vhennus.trivia.domain.TriviaGameReq
import com.vhennus.trivia.domain.TriviaUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject



@HiltViewModel
class TriviaViewModel @Inject constructor(
    private val apiService: APIService,
    private val getUserToken: GetUserToken,
    private val application: Application,
) :ViewModel() {
    val _triviaUiState = MutableStateFlow(TriviaUIState())
    val triviaUIState = _triviaUiState.asStateFlow()

    val _triviaGame = MutableStateFlow(TriviaGame())
    val trivaGame = _triviaGame.asStateFlow()
    val _gameResult = MutableStateFlow("")
    val gameResult = _gameResult.asStateFlow()


    fun resetUiState(){
        _triviaUiState.value = TriviaUIState()
        //_gameResult.value = ""
    }
    fun resetUIE(data:TriviaUIState){
        _triviaUiState.value = data
    }
    fun resetUIError(){
        _triviaUiState.value = _triviaUiState.value.copy(
            playGameErrorMessage = "",
            getQuestionError = "",
            getGameError = ""
        )
    }
    fun resetResultState(){
        _gameResult.value = ""
    }
    fun getTriviaGame(){
        _triviaUiState.update { it.copy(isGameLoading = true) }
        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                try{
                    val token = getUserToken.getUserToken()
                    val resp = apiService.getTriviaGame(mapOf("Authorization" to token))
                    if (resp.code() == 200 || resp.code()==201){
                        _triviaUiState.update { it.copy(
                            isGameLoading = false,
                            isGetGameSuccess = true,
                            isGetGameError = false,
                            getGameError = ""
                        ) }
                        val data =  resp.body()?.data
                        if (data !=null){
                            _triviaGame.value = data
                        }

                    }else if (resp.code()==401){
                        _triviaUiState.update { it.copy(
                            isGameLoading = false,
                            isGetGameSuccess = false,
                            isGetGameError = true,
                            getGameError = "You are not authorized"
                        ) }
                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("GET TRIVIA GAME ERROR", respString +" ")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(respString ?:"" , genericType)
                        _triviaUiState.update {
                            it.copy(
                                isGameLoading = false,
                                isGetGameSuccess = false,
                                isGetGameError = true,
                                getGameError = errorResp.message
                            )
                        }
                    }
                }catch (e:Exception){
                  _triviaUiState.update { it.copy(
                      isGameLoading = false,
                      isGetGameSuccess = false,
                      isGetGameError = true,
                      getGameError = "Internal Error"
                  ) }

                  CLog.error("GET TRIVIA GAME ERROR", e.toString())
                }
            }
        }
    }

    fun storePlayLocally(){
        // store the play so that we know this user has played for today
        val date = LocalDate.now() // current date
        val formattedDate = formatDateWithDateTimeFormatter(date)

        val sharedPref: SharedPreferences = application.getSharedPreferences("Trivia", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString(formattedDate, "1")
            apply()
        }
    }
    fun playTriviaGame(req:TriviaGameReq){
        _triviaUiState.update { it.copy(isPlayGameLoading = true) }
        viewModelScope.launch {

            withContext(Dispatchers.IO) {
                try{
                    val token = getUserToken.getUserToken()
                    val resp = apiService.playTriviaGame(req, mapOf("Authorization" to token))
                    if (resp.code() == 200 || resp.code()==201){
                        _triviaUiState.update { it.copy(
                            isPlayGameLoading = false,
                            isPlayGameSuccess = true,
                            isPlayGameError = false,
                            playGameErrorMessage = ""
                        ) }
                        val data =  resp.body()?.data
                        if (data !=null){
                            _gameResult.value = data
                        }
                        // keep a record that the user has played
                        storePlayLocally()

                    }else if (resp.code()==401){
                        _triviaUiState.update { it.copy(
                            isPlayGameLoading = false,
                            isPlayGameSuccess = false,
                            isPlayGameError = true,
                            playGameErrorMessage = "You are not authorized"
                        ) }
                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("Play TRIVIA GAME ERROR", respString +" ")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(respString ?:"" , genericType)
                        _triviaUiState.update {
                            it.copy(
                                isPlayGameLoading = false,
                                isPlayGameSuccess = false,
                                isPlayGameError = true,
                                playGameErrorMessage = errorResp.message
                            )
                        }
                    }
                }catch (e:Exception){
                    _triviaUiState.update { it.copy(
                        isPlayGameLoading = false,
                        isPlayGameSuccess = false,
                        isPlayGameError = true,
                        playGameErrorMessage = "Internal Error"
                    ) }

                    CLog.error("Play TRIVIA GAME ERROR", e.toString())
                }
            }
        }
    }

    fun hasPlayedTriviaToday(){
        // get current date and check if it exists in the shared pref store
        val date = LocalDate.now() // current date
        val formattedDate = formatDateWithDateTimeFormatter(date)
        val sharedPref: SharedPreferences = application.getSharedPreferences("Trivia", Context.MODE_PRIVATE)
        val res =  sharedPref.getString(formattedDate, "0") ?: "0"
        if (res == "1"){
            _triviaUiState.value = triviaUIState.value.copy(hasPlayedTriviaToday = true)
        }

        if(res == "0"){
            _triviaUiState.value = triviaUIState.value.copy(hasPlayedTriviaToday = false)
        }
    }
}




fun formatDateWithDateTimeFormatter(date: LocalDate): String {
    val formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy")
    return date.format(formatter)
}