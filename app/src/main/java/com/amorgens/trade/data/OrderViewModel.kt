package com.amorgens.trade.data

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.amorgens.trade.domain.BuyOrder
import com.amorgens.trade.domain.SellOrder
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
        Currency.USD,
        PaymentMethod.Bank,
        ""
    ))
    val createSellOrderReq = _createSellOrderReq.asStateFlow()

    private val _tradeUIState = MutableStateFlow(TradeUIState())
    val tradeUIState = _tradeUIState.asStateFlow()

    private val _mySellOrders = MutableStateFlow(listOf(SellOrder()))
    val mySellOrder = _mySellOrders.asStateFlow()

    private val _singleSellOrder = MutableStateFlow(SellOrder())
    val singleSellOrder = _singleSellOrder.asStateFlow()

    private val _myBuyOrders = MutableStateFlow(listOf(BuyOrder()))
    val myBuyOrders = _myBuyOrders.asStateFlow()

    private val _singleBuyOrder = MutableStateFlow(BuyOrder())
    val singleBuyOrder = _singleBuyOrder.asStateFlow()


    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()

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


    fun getMySellOrders(){
        _tradeUIState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
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
                if (token.isNullOrBlank()) {
                    _tradeUIState.update {
                        it.copy(
                            isSuccess = false,
                            isError = true,
                            errorMessage = "You are not authorized"
                        )
                    }
                    _tradeUIState.update { it.copy(isLoading = false) }
                    return@withContext
                }

                try {
                    val resp = apiService.getMySellOrders( mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        _tradeUIState.update { it.copy(isSuccess = true,
                            isError = false) }
                        val data = resp.body()?.data
                        if (data.isNullOrEmpty()){
                            _tradeUIState.update {
                                it.copy(
                                    isSuccess = false,
                                    isError = true,
                                    errorMessage = "No Orders"
                                )
                            }
                            _tradeUIState.update { it.copy(isLoading = false) }
                            return@withContext
                        }
                        _mySellOrders.value = data
                        Log.d("GET ORDERS XXXX", data.toString())
                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(isSuccess = false,
                            isError = true,
                            errorMessage = "You are not authorized"
                        ) }
                    }
                    else{
                        _tradeUIState.update { it.copy(isSuccess = false,
                            isError = true,
                            errorMessage = resp.body()?.message ?:""
                        ) }
                    }

                }catch (e:Exception){
                    _tradeUIState.update { it.copy(isSuccess = false,
                        isError = true,
                        errorMessage = "Network Error"
                    ) }
                    _tradeUIState.update { it.copy(isLoading = false) }
                }
                _tradeUIState.update { it.copy(isLoading = false) }
            }
        }
    }


    fun getMyBuyOrders(){
        _tradeUIState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
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
                if (token.isNullOrBlank()) {
                    _tradeUIState.update {
                        it.copy(
                            isSuccess = false,
                            isError = true,
                            errorMessage = "You are not authorized"
                        )
                    }
                    _tradeUIState.update { it.copy(isLoading = false) }
                    return@withContext
                }

                try {
                    val resp = apiService.getMyBuyOrders( mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        _tradeUIState.update { it.copy(isSuccess = true,
                            isError = false) }
                        val data = resp.body()?.data
                        if (data.isNullOrEmpty()){
//                            _tradeUIState.update {
//                                it.copy(
//                                    isSuccess = false,
//                                    isError = true,
//                                    errorMessage = "No Orders"
//                                )
//                            }
                            _tradeUIState.update { it.copy(isLoading = false) }
                            return@withContext
                        }
                        _myBuyOrders.value = data
                        Log.d("GET ORDERS XXXX", data.toString())
                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(isSuccess = false,
                            isError = true,
                            errorMessage = "You are not authorized"
                        ) }
                    }
                    else{
                        _tradeUIState.update { it.copy(isSuccess = false,
                            isError = true,
                            errorMessage = resp.body()?.message ?:""
                        ) }
                    }

                }catch (e:Exception){
                    _tradeUIState.update { it.copy(isSuccess = false,
                        isError = true,
                        errorMessage = "Network Error"
                    ) }
                    _tradeUIState.update { it.copy(isLoading = false) }
                }
                _tradeUIState.update { it.copy(isLoading = false) }
            }
        }
    }


    fun cancelSellOrder(id:String){
        _tradeUIState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
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
                if (token.isNullOrBlank()) {
                    _tradeUIState.update {
                        it.copy(
                            isSuccess = false,
                            isError = true,
                            errorMessage = "You are not authorized"
                        )
                    }
                    _tradeUIState.update { it.copy(isLoading = false) }
                    return@withContext
                }

                try {
                    val resp = apiService.cancelSellOrder(id, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        _tradeUIState.update { it.copy(isSuccess = true,
                            isError = false) }
//                        val data = resp.body()?.data
//                        if (data.isNullOrEmpty()){
//                            _tradeUIState.update {
//                                it.copy(
//                                    isSuccess = false,
//                                    isError = true,
//                                    errorMessage = "No Orders"
//                                )
//                            }
//                            _tradeUIState.update { it.copy(isLoading = false) }
//                            return@withContext
//                        }

                        //Log.d("GET ORDERS XXXX", data.toString())
                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(isSuccess = false,
                            isError = true,
                            errorMessage = "You are not authorized"
                        ) }
                    }
                    else{
                        Log.d("CANCEL SELL ORDER ERROR", resp.toString())
                        _tradeUIState.update { it.copy(isSuccess = false,
                            isError = true,
                            errorMessage = resp.body()?.message ?:""
                        ) }

                    }

                }catch (e:Exception){
                    _tradeUIState.update { it.copy(isSuccess = false,
                        isError = true,
                        errorMessage = "Network Error"
                    ) }
                    _tradeUIState.update { it.copy(isLoading = false) }
                }
                _tradeUIState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun getSingleSellOrders(id:String){
        _tradeUIState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
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
                if (token.isNullOrBlank()) {
                    _tradeUIState.update {
                        it.copy(
                            isSuccess = false,
                            isError = true,
                            errorMessage = "You are not authorized"
                        )
                    }
                    _tradeUIState.update { it.copy(isLoading = false) }
                    return@withContext
                }

                try {
                    val resp = apiService.getSingleSellOrder(id, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        _tradeUIState.update { it.copy(isSuccess = true,
                            isError = false) }
                        val data = resp.body()?.data ?: SellOrder()
                        if (data.created_at.isBlank()){
                            _tradeUIState.update {
                                it.copy(
                                    isSuccess = false,
                                    isError = true,
                                    errorMessage = "Not found"
                                )
                            }
                            _tradeUIState.update { it.copy(isLoading = false) }
                            return@withContext
                        }
                        _singleSellOrder.value = data
                        Log.d("GET ORDERS XXXX", data.toString())
                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(isSuccess = false,
                            isError = true,
                            errorMessage = "You are not authorized"
                        ) }
                    }
                    else{
                        _tradeUIState.update { it.copy(isSuccess = false,
                            isError = true,
                            errorMessage = resp.body()?.message ?:""
                        ) }
                        Log.d("SINGLE ORDER ERROR XXXX", resp.toString())
                    }

                }catch (e:Exception){
                    _tradeUIState.update { it.copy(isSuccess = false,
                        isError = true,
                        errorMessage = "Network Error"
                    ) }
                    _tradeUIState.update { it.copy(isLoading = false) }
                    Log.d("SINGLE ORDER ERROR XXXX", e.toString())
                }
                _tradeUIState.update { it.copy(isLoading = false) }
            }
        }
    }

    fun resetSuccessAndError(){
        _tradeUIState.update { it.copy(
            isSuccess = false,
            isError = false,
            errorMessage = "",
            successMessage = ""
        ) }
    }

    fun resetSingleOrderScreenState(){
        _tradeUIState.update {
            it.copy(
                isConfirmBuyOrderError = false,
                isConfirmBuyOrderSuccess = false,
                confirmBuyOrderErrorMessage = "",
                isConfirmBuyOrderButtonLoading = false
            )
        }
    }


    fun resetSuccessAndErrorCreateSellOrder(){
        _tradeUIState.update { it.copy(
            isCreateSellOrderSuccess = false,
            isCreateSellOrderError = false,
            createSellOrderErrorMessage = ""
        ) }
    }

    fun getSingleBuyOrder(buyOrderID:String){
        _tradeUIState.update { it.copy(isLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
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
                if (token.isNullOrBlank()) {
                    _tradeUIState.update {
                        it.copy(
                            isSuccess = false,
                            isError = true,
                            errorMessage = "You are not authorized"
                        )
                    }
                    _tradeUIState.update { it.copy(isLoading = false) }
                    return@withContext
                }

                try {
                    val resp = apiService.getSingleBuyOrder(buyOrderID, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        _tradeUIState.update { it.copy(isSuccess = true,
                            isError = false) }
                        val data = resp.body()?.data
                        if (data == null){
                            _tradeUIState.update {
                                it.copy(
                                    isSuccess = false,
                                    isError = true,
                                    errorMessage = "Order not found"
                                )
                            }
                            _tradeUIState.update { it.copy(isLoading = false) }
                            return@withContext
                        }
                        _singleBuyOrder.value = data
                        Log.d("GET ORDERS XXXX", data.toString())
                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(isSuccess = false,
                            isError = true,
                            errorMessage = "You are not authorized"
                        ) }
                    }
                    else{
                        _tradeUIState.update { it.copy(isSuccess = false,
                            isError = true,
                            errorMessage = resp.body()?.message ?:""
                        ) }
                    }

                }catch (e:Exception){
                    _tradeUIState.update { it.copy(isSuccess = false,
                        isError = true,
                        errorMessage = "Network Error"
                    ) }
                    _tradeUIState.update { it.copy(isLoading = false) }
                }
                _tradeUIState.update { it.copy(isLoading = false) }
            }
        }
    }


    fun buyerConfirmBuyOrder(buyOrderID:String){
        _tradeUIState.update { it.copy(isConfirmBuyOrderButtonLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
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
                if (token.isNullOrBlank()) {
                    _tradeUIState.update {
                        it.copy(
                            isConfirmBuyOrderSuccess = false,
                            isConfirmBuyOrderError = true,
                            confirmBuyOrderErrorMessage =  "Unauthorized"
                        )
                    }
                    _tradeUIState.update { it.copy(isConfirmBuyOrderButtonLoading = false) }

                    return@withContext
                }

                try {
                    val resp = apiService.buyerConfirmBuyOrder(buyOrderID, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        _tradeUIState.update { it.copy(
                            isConfirmBuyOrderSuccess = true,
                            isConfirmBuyOrderError = false,
                            confirmBuyOrderErrorMessage =  ""
                        ) }
                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(
                            isConfirmBuyOrderSuccess = false,
                            isConfirmBuyOrderError = true,
                            confirmBuyOrderErrorMessage = "You are not authorized"

                        ) }
                    }
                    else{
                        _tradeUIState.update { it.copy(
                            isConfirmBuyOrderSuccess = false,
                            isConfirmBuyOrderError = true,
                            confirmBuyOrderErrorMessage = resp.body()?.message ?:""

                        ) }
                        Log.d("XXX CONFIRM ORDER ERROR ", resp.body().toString())
                    }

                }catch (e:Exception){
                    _tradeUIState.update { it.copy(
                        isConfirmBuyOrderSuccess = false,
                        isConfirmBuyOrderError = true,
                        confirmBuyOrderErrorMessage =  "Network Error"
                    ) }
                    _tradeUIState.update { it.copy(isConfirmBuyOrderButtonLoading = false) }
                    Log.d("XXX CONFIRM ORDER ERROR ", e.toString())
                }
                _tradeUIState.update { it.copy(isConfirmBuyOrderButtonLoading = false) }
            }

        }
    }

    fun sellerConfirmBuyOrder(buyOrderID:String){
        _tradeUIState.update { it.copy(isConfirmBuyOrderButtonLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
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
                if (token.isNullOrBlank()) {
                    _tradeUIState.update {
                        it.copy(
                            isConfirmBuyOrderSuccess = false,
                            isConfirmBuyOrderError = true,
                            confirmBuyOrderErrorMessage = "You are not authorized"
                        )
                    }
                    _tradeUIState.update { it.copy(isConfirmBuyOrderButtonLoading = false) }

                    return@withContext
                }

                try {
                    val resp = apiService.sellerConfirmBuyOrder(buyOrderID, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        _tradeUIState.update { it.copy(
                            isConfirmBuyOrderSuccess = true,
                            isConfirmBuyOrderError = false,
                            confirmBuyOrderErrorMessage = ""
                        ) }
                    }else if (resp.code()==401){
                        _tradeUIState.update { it.copy(
                            isConfirmBuyOrderSuccess = false,
                            isConfirmBuyOrderError = true,
                            confirmBuyOrderErrorMessage = "You are not authorized"
                        ) }
                    }
                    else{
                        _tradeUIState.update { it.copy(
                            isConfirmBuyOrderSuccess = false,
                            isConfirmBuyOrderError = true,
                            confirmBuyOrderErrorMessage = resp.body()?.message ?: ""

                        ) }
                        Log.d("XXX CONFIRM ORDER ERROR ", resp.body().toString())
                    }

                }catch (e:Exception){
                    _tradeUIState.update { it.copy(
                        isConfirmBuyOrderSuccess = false,
                        isConfirmBuyOrderError = true,
                        confirmBuyOrderErrorMessage =  "Network Error"
                    ) }
                    _tradeUIState.update { it.copy(isConfirmBuyOrderButtonLoading = false) }
                    Log.d("XXX CONFIRM ORDER ERROR ", e.toString())
                }
                _tradeUIState.update { it.copy(isConfirmBuyOrderButtonLoading = false) }
            }

        }
    }
    fun login(){
        val context = application
        val mshared = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val mSharedEditor = mshared.edit()
        mSharedEditor.putString("user_name", "roland_case")
        mSharedEditor.apply()

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
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJyb2xlIjoiVXNlciIsImVtYWlsIjoicm9sYW5kQHguY29tIiwidXNlcl9uYW1lIjoicm9sYW5kX2Nhc2UiLCJleHAiOjE3NTUxNjIwMzl9.Pj6q31lTelU43ll8JlX9qH_ZeoSvTz4e2_-WmQDuyF8")
        editor.apply()
    }

    // get the current excahnge rate
    fun getExchangeValue(amount:BigDecimal):BigDecimal{
        val sharedPreferences = application.getSharedPreferences("exchange_rates", Context.MODE_PRIVATE)
        try {
            val rate = BigDecimal( sharedPreferences.getString("NGN", "0.5"))
            return rate.multiply(amount)
        }catch (e:Exception){
            return BigDecimal("0.00")
        }
    }

    // set excahnge rate
    fun setExchangeRate(key:String, value:String){
        val sharedPreferences = application.getSharedPreferences("exchange_rates", Context.MODE_PRIVATE)
        val edit = sharedPreferences.edit()
        edit.putString(key, value).apply()
    }

    fun getUserName(){
        val mshared = application.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        _userName.value = mshared.getString("user_name","").toString()
    }

}