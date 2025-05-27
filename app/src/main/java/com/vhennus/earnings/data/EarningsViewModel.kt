package com.vhennus.earnings.data

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vhennus.earnings.domain.EarningsUIState
import com.vhennus.general.data.APIService
import com.vhennus.general.data.GetUserToken
import com.vhennus.general.domain.GenericResp
import com.vhennus.general.utils.CLog
import com.vhennus.profile.domain.UpdateEarnings
import com.vhennus.profile.domain.UpdateProfileRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import javax.inject.Inject


@HiltViewModel
class EarningsViewModel @Inject constructor(
    private val apiService: APIService,
    private val getUserToken: GetUserToken,
    private val application: Application,
) : ViewModel() {

    private val _earningsUIState = MutableStateFlow(EarningsUIState())
    val earningsUIState = _earningsUIState.asStateFlow()


    fun resetUIState(){
        _earningsUIState.value = EarningsUIState()
    }

    fun activateEarnings(){
        _earningsUIState.update { it.copy(
            isActivateEarningsLoading = true,
        ) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val token = getUserToken.getUserToken()
                    val resp = apiService.activateEarnings(mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        CLog.debug("ACTIVATE EARNINGS ERROR ", resp.body().toString() +" ")
                        _earningsUIState.update { it.copy(
                            isActivateEarningsLoading = false,
                            isActivateEarningsSuccess = true,
                            isActivateEarningsError = false,
                            activateEarningsErrorMessage = ""
                        ) }
                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.debug("ACTIVATE EARNINGS ERROR ", respString +" ")
                        val json =  Json { coerceInputValues = true }
                        val errorResp = json.decodeFromString<GenericResp<String>>(respString.toString())
                        _earningsUIState.update { it.copy(
                            isActivateEarningsLoading = false,
                            isActivateEarningsSuccess = false,
                            isActivateEarningsError = true,
                            activateEarningsErrorMessage = errorResp.message
                        ) }
                    }
                }catch (e: Exception){
                    _earningsUIState.update { it.copy(
                        isActivateEarningsLoading = false,
                        isActivateEarningsSuccess = false,
                        isActivateEarningsError = true,
                        activateEarningsErrorMessage = "Network Error"
                    ) }
                    CLog.error("ACTIVATE EARNINGS ERROR", e.toString() +" ")
                }
            }
        }
    }

    fun cashOut(){
        _earningsUIState.update { it.copy(
            isCashoutLoading = true,
        ) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val token = getUserToken.getUserToken()
                    val resp = apiService.cashoutEarnings(mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        CLog.debug("CASHOUT EARNINGS ", resp.body().toString() +" ")
                        _earningsUIState.update { it.copy(
                            isCashoutLoading = false,
                            isCashoutSuccess = true,
                            isCashoutError = false,
                            cashoutErrorMessage = ""
                        ) }
                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.debug("CASHOUT EARNINGS ERROR ", respString +" ")
                        val json =  Json { coerceInputValues = true }
                        val errorResp = json.decodeFromString<GenericResp<String>>(respString.toString())
                        _earningsUIState.update { it.copy(
                            isCashoutLoading = false,
                            isCashoutSuccess = false,
                            isCashoutError = true,
                            cashoutErrorMessage = errorResp.message
                        ) }
                    }
                }catch (e: Exception){
                    _earningsUIState.update { it.copy(
                        isCashoutLoading = false,
                        isCashoutSuccess = false,
                        isCashoutError = true,
                        cashoutErrorMessage = "Network Error"
                    ) }
                    CLog.error("CASHOUT EARNINGS ERROR", e.toString() +" ")
                }
            }
        }
    }

    fun postEarnings(earning: BigDecimal){
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    CLog.debug("POST EARNINGS REQ",earning.toString() +" ")
                    val token = getUserToken.getUserToken()
                    val resp = apiService.postEarnings(UpdateProfileRequest(
                        image = null,
                        bio = null,
                        name = null,
                        app_f_token = null,
                        earnings_wallet = null,
                        new_earning = earning.toString()
                    ), mapOf("Authorization" to token))

                    if (resp.code() == 200){
                        CLog.debug("POST EARNINGS ", resp.body().toString() +" ")
                        val prefs = application.getSharedPreferences("app", Context.MODE_PRIVATE)
                        prefs.edit().putLong("time_spent", 0L).apply()

                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.debug("POST EARNINGS ERROR ", respString +" ")
                        val json =  Json { coerceInputValues = true }
                        val errorResp = json.decodeFromString<GenericResp<String>>(respString.toString())

                    }
                }catch (e: Exception){

                    CLog.error("POST EARNINGS ERROR", e.toString() +" ")
                }
            }
        }
    }
}
