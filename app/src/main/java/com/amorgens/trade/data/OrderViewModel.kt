package com.amorgens.trade.data

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.amorgens.trade.domain.TradeUIState
import com.amorgens.trade.domain.requests.CreateSellOrderReq
import com.amorgens.trade.domain.requests.Currency
import com.amorgens.trade.domain.requests.PaymentMethod
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.math.BigDecimal
import javax.inject.Inject


@HiltViewModel
class OrderViewModel @Inject constructor(private val apiService: APIService,
    private val application: Application
    ) :ViewModel(){

    val _createSellOrderReq = MutableStateFlow(CreateSellOrderReq(
        BigDecimal("0.00"),
        BigDecimal("0.00"),
        BigDecimal("0.00"),
        Currency.USD,
        PaymentMethod.Bank,
        ""
    ))
    val createSellOrderReq = _createSellOrderReq.asStateFlow()

    private val _tradeUIState = MutableStateFlow(TradeUIState())
    val tradeUIState = _tradeUIState.asStateFlow()
    fun sayHello(){
        viewModelScope.launch {
            try{
                val resp = apiService.sayHello();
                Log.d("RESP HELLO XXXX", resp.toString())
            }catch (e: Exception){
                Log.d("Exception XXXX", e.toString())
            }

        }
    }

    fun createSellOrder(data:CreateSellOrderReq){
        _tradeUIState.update { it.copy(isCreateSellOrderButtonLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val masterKey = MasterKey.Builder(application)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build()

                val sharedPreferences = EncryptedSharedPreferences.create(
                    application,
                    "secure_prefs",
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
                )

                val token = sharedPreferences.getString("auth_token", null)
                if (token.isNullOrBlank()){
                    _tradeUIState.update { it.copy(isCreateSellOrderSuccess = false,
                        isCreateSellOrderError = true,
                        createSellOrderErrorMessage = "You are not authorized"
                    ) }
                    _tradeUIState.update { it.copy(isCreateSellOrderButtonLoading = false) }
                    return@withContext
                }
                try {

                    val resp = apiService.createSellOrder(data, mapOf("Authorization" to token))
                    if (resp.code() == 200 || resp.code()==201){
                        _tradeUIState.update { it.copy(isCreateSellOrderSuccess = true,
                            isCreateSellOrderError = false) }
                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(isCreateSellOrderSuccess = false,
                            isCreateSellOrderError = true,
                            createSellOrderErrorMessage = "You are not authorized"
                        ) }
                    }
                    else{
                        _tradeUIState.update { it.copy(isCreateSellOrderSuccess = false,
                            isCreateSellOrderError = true,
                            createSellOrderErrorMessage = resp.body()?.message ?:""
                        ) }
                    }
                }catch (e:Exception){
                    _tradeUIState.update { it.copy(isCreateSellOrderButtonLoading = false) }
                    _tradeUIState.update { it.copy(isCreateSellOrderSuccess = false,
                        isCreateSellOrderError = true, createSellOrderErrorMessage = "Network Error") }
                }

                _tradeUIState.update { it.copy(isCreateSellOrderButtonLoading = false) }
            }
        }


    }

    fun resetSuccessAndError(){
        _tradeUIState.update { it.copy(
            isCreateSellOrderSuccess = false,
            isCreateSellOrderError = false,
            createSellOrderErrorMessage = ""
        ) }
    }

    fun login(context:Context){
        val masterKey = MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

        val sharedPreferences = EncryptedSharedPreferences.create(
            context,
            "secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )

        val editor = sharedPreferences.edit()
        editor.putString("auth_token",
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVXNlciIsImVtYWlsIjoicm9sYW5kQHguY29tIiwidXNlcl9uYW1lIjoicm9sYW5kIGNvc2UiLCJleHAiOjE3NTMyMTIyMjB9.bwaKbzQJaVMaR0kAqfZp4NfJdgRbCOA4jGQGUGciwP4")
        editor.apply()
    }

}