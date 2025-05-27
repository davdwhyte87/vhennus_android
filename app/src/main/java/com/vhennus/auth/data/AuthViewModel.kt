package com.vhennus.auth.data

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.vhennus.auth.domain.AuthUIState
import com.vhennus.auth.domain.LoginReq
import com.vhennus.auth.domain.SignupReq
import com.vhennus.general.data.APIService
import com.vhennus.general.domain.GenericResp
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vhennus.auth.domain.ChangePasswordReq
import com.vhennus.auth.domain.ConfirmAccountReq
import com.vhennus.auth.domain.GetResetPasswordCodeReq
import com.vhennus.auth.domain.LoginResp
import com.vhennus.auth.domain.ResendCodeReq
import com.vhennus.general.utils.CLog
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import javax.inject.Inject


@HiltViewModel
class AuthViewModel  @Inject constructor(
    private val apiService: APIService,
    private val application: Application,
    private val jsonService: Json
) : ViewModel() {

    private val _userToken = MutableStateFlow("")
    val userToken= _userToken.asStateFlow()

    private val _authUIState = MutableStateFlow(AuthUIState())
    val authUIState = _authUIState.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    private val _tempLoginEmail = MutableStateFlow("")
    val tempLoginEmail = _tempLoginEmail.asStateFlow()

    private val _loginResp = MutableStateFlow(LoginResp())
    val loginResp = _loginResp.asStateFlow()

    private val _tempLoginEmailConfirmed = MutableStateFlow(false)
    val tempLoginEmailConfirmed = _tempLoginEmailConfirmed.asStateFlow()

    private val _tempUserName = MutableStateFlow("")
    val tempUserName = _tempUserName.asStateFlow()


    fun resetUI(){
        _authUIState.value = AuthUIState()
    }
    fun signup(data: SignupReq){
        // start loading button
        _authUIState.update { it.copy(isSignupButtonLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                // send data to api
                try {
                    val resp = apiService.create_account(data)
                    CLog.debug("SIGNUP RESP", resp.body().toString())
                    if (resp.code() == 200){
                        _authUIState.update { it.copy(
                            isSignupButtonLoading = false,
                            isSignupSuccess = true,
                            isSignupError = false,
                            signupErrorMessage = ""
                        ) }

                    }else{
                        Log.d("SIGNUP ERROR !", resp.errorBody().toString())
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(resp.errorBody()?.string() , genericType)
                        _authUIState.update { it.copy(
                            isSignupButtonLoading = false,
                            isSignupSuccess = false,
                            isSignupError = true,
                            signupErrorMessage = errorResp.message
                        ) }

                        CLog.error("SIGNUP ERROR ", errorResp.message + " "+ errorResp.server_message)
                    }
                }catch (e:Exception){
                    _authUIState.update { it.copy(
                        isSignupButtonLoading = false,
                        isSignupSuccess = false,
                        isSignupError = true,
                        signupErrorMessage = "Network Error"
                    ) }
                    CLog.error("SIGNUP ERROR", e.toString())
                }
            }
        }
    }



    fun verifyAccount(data: ConfirmAccountReq){
        // start loading button
        _authUIState.update { it.copy(isVerifyAccountLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                // send data to api
                try {
                    val resp = apiService.confirm_account(data)
                    Log.d("VERIFY ACCOUNT RESP", resp.body().toString())
                    if (resp.code() == 200){
                        _authUIState.update { it.copy(
                            isVerifyAccountLoading = false,
                            isVerifyAccountSuccess = true,
                            isVerifyAccountError = false,
                            verifyAccountErrorMessage = ""
                        ) }

                    }else{
                        Log.d("VERIFY ACCOUNT ERROR !", resp.errorBody().toString())
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(resp.errorBody()?.string() , genericType)
                        _authUIState.update { it.copy(
                            isVerifyAccountLoading = false,
                            isVerifyAccountSuccess = false,
                            isVerifyAccountError = true,
                            verifyAccountErrorMessage = errorResp.message
                        ) }

                        CLog.error("VERIFY ACCOUNT ERROR ", errorResp.message + " "+ errorResp.server_message)
                    }
                }catch (e:Exception){
                    _authUIState.update { it.copy(
                        isVerifyAccountLoading = false,
                        isVerifyAccountSuccess = false,
                        isVerifyAccountError = true,
                        verifyAccountErrorMessage = "Network Error"
                    ) }
                    CLog.error("VERIFY ACCOUNT ERROR", e.toString())
                }


            }
        }
    }


    fun resendCode(data: ResendCodeReq){
        // start loading button
        _authUIState.update { it.copy(resendCodeLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                // send data to api
                try {
                    val resp = apiService.resend_code(data)
                    Log.d("Resend code RESP", resp.body().toString())
                    if (resp.code() == 200){
                        _authUIState.update { it.copy(
                            resendCodeLoading = false,
                            resendCodeSuccess = true,
                            resendCodeError = false,
                            resendCodeErrorMessage = ""
                        ) }

                    }else{
                        Log.d("Resend code ERROR !", resp.errorBody().toString())
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(resp.errorBody()?.string() , genericType)
                        _authUIState.update { it.copy(
                            resendCodeLoading = false,
                            resendCodeSuccess = false,
                            resendCodeError = true,
                            resendCodeErrorMessage = errorResp.message
                        ) }

                        CLog.error("resend code ERROR ", errorResp.message + " "+ errorResp.server_message)
                    }
                }catch (e:Exception){
                    _authUIState.update { it.copy(
                        resendCodeLoading = false,
                        resendCodeSuccess = false,
                        resendCodeError = true,
                        resendCodeErrorMessage = "Network Error"
                    ) }
                    CLog.error("resend code ERROR", e.toString())
                }


            }
        }
    }

    fun getSys(){

    }

    fun getSystemData(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try{

                    val resp = apiService.getSystemData()
                    CLog.debug("SYSTEM DA", resp.body().toString())
                    if (resp.isSuccessful){
                        val systemData = resp.body()?.data
                        if (systemData == null){
                            CLog.error("ERROR GETTING SYSTEM DATA", " Did not get any data")
                            return@withContext
                        }


                    }else{
                        val errData = resp.errorBody()?.string()
                        CLog.error("ERROR GETTING SYSTEM DATA", resp.code().toString()+"err data")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(errData, genericType)
                        CLog.error("ERROR GETTING SYSTEM DATA", errorResp.message)

                    }
                }catch (e:Exception){
                    CLog.error("ERROR GETTING SYSTEM DATA", e.toString())
                }

            }
        }
    }


    fun login(data: LoginReq){
        Log.d("LOGIN ****", "Starting " )
        // start loading button
        _authUIState.update { it.copy(isLoginButtonLoading = true) }
        CLog.debug("LOGIN ****", "loading " )
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                // send data to api
                try {
                    Log.d("LOGIN ****", "calling api " )
                    val resp = apiService.login2(data)
                    //Log.d("LOGIN ****", "done calling api " )
                    //Log.d("LOGIN ****", resp.body().toString())
                    if (resp.code() == 200){
                        _authUIState.update { it.copy(
                            isLoginButtonLoading = false,
                            isLoginSuccess = true,
                            isLoginError = false,
                            loginErrorMessage = ""
                        ) }
                        val respData = resp.body()?.data
                        if (respData != null) {
                            _loginResp.value = respData
//                            Log.d("LOGIN RESP", respData.toString())

                            if(loginResp.value.email_confirmed){
                                // save token to local
                                loginMani(loginResp.value.token)
                                // save username to device
                                saveUserName(data.user_name)
                            }
                        }

                    }else{
                        CLog.debug("LOGIN ERROR !", resp.errorBody().toString())
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(resp.errorBody()?.string() , genericType)
                        _authUIState.update { it.copy(
                            isLoginButtonLoading = false,
                            isLoginSuccess = false,
                            isLoginError = true,
                            loginErrorMessage = errorResp.message
                        ) }

                        CLog.error("LOGIN ERROR ", errorResp.message + " "+ errorResp.server_message)
                    }
                }catch (e:Exception){
                    _authUIState.update { it.copy(
                        isLoginButtonLoading = false,
                        isLoginSuccess = false,
                        isLoginError = true,
                        loginErrorMessage = "Network Error"
                    ) }
                    CLog.error("LOGIN ERROR", e.toString())
                }


            }
        }
    }

    fun sendResetPasswordCode(data: GetResetPasswordCodeReq) {

        _authUIState.update { it.copy(isSendResetPasswordCodeLoading = true) }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                // send data to api

                try {
                    var resp = apiService.getResetPasswordCode(data)
                    if (resp.isSuccessful){
                        _tempUserName.value = data.user_name

                        _authUIState.update { it.copy(
                            isSendResetPasswordCodeLoading = false,
                            isSendResetPasswordCodeError = false,
                            isSendResetPasswordCodeSuccess = true,
                            sendResetPasswordCodeErrorMessage = ""
                        ) }
                    }else{
                        val errorBodyString = resp.errorBody()?.string().toString()
                        CLog.error("ERROR SENDING RESET PASS CODE", errorBodyString)
                        val errorResp = jsonService.decodeFromString(GenericResp.serializer(String.serializer()), errorBodyString)
                        _authUIState.update { it.copy(
                            isSendResetPasswordCodeLoading = false,
                            isSendResetPasswordCodeError = true,
                            isSendResetPasswordCodeSuccess = false,
                            sendResetPasswordCodeErrorMessage = errorResp.message
                        ) }

                    }
                }catch (e: Exception){
                    _authUIState.update { it.copy(
                        isSendResetPasswordCodeLoading = false,
                        isSendResetPasswordCodeError = true,
                        isSendResetPasswordCodeSuccess = false,
                        sendResetPasswordCodeErrorMessage = "Error sending code"
                    ) }
                    CLog.error("ERROR SENDING RESET PASS CODE", e.toString())
                }
            }
        }
    }

    fun changePassword(data: ChangePasswordReq) {

        _authUIState.update { it.copy(isChangePasswordLoading = true) }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                try {
                    // send data to api
                    var resp = apiService.changePassword(data)
                    if (resp.isSuccessful){
//                    _tempUserName.value = data.user_name

                        _authUIState.update { it.copy(
                            isChangePasswordLoading = false,
                            isChangePasswordError = false,
                            isChangePasswordSuccess = true,
                            changePasswordErrorMessage = ""
                        ) }
                    }else{
                        val errorBodyString = resp.errorBody()?.string().toString()
                        CLog.error("ERROR SENDING RESET PASS CODE", errorBodyString)
                        val errorResp = jsonService.decodeFromString(GenericResp.serializer(String.serializer()), errorBodyString)
                        _authUIState.update { it.copy(
                            isChangePasswordLoading = false,
                            isChangePasswordError = true,
                            isChangePasswordSuccess = false,
                            changePasswordErrorMessage = errorResp.message
                        ) }

                    }
                }catch (e: Exception){
                    _authUIState.update { it.copy(
                        isChangePasswordLoading = false,
                        isChangePasswordError = true,
                        isChangePasswordSuccess = false,
                        changePasswordErrorMessage = "Error changing password"
                    ) }
                    CLog.error("ERROR SENDING RESET PASS CODE", e.toString())
                }
            }
        }
    }


    fun isLoggedIn():Boolean{
        if(getUserToken().isBlank()){
            return false
        }

        return true
    }

    fun getUserName(){
        val mshared = application.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        _userName.value = mshared.getString("user_name","").toString()
    }

    fun getUserToken():String{
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

        Log.d("GET TOKEN", token ?:"")
        return token ?: ""

    }

    fun loginMani(token:String){
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

        val editor = sharedPreferences.edit()
        editor.putString("auth_token", token)
        editor.apply()
    }
    fun logout(){
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

        val editor = sharedPreferences.edit()
        editor.remove("auth_token")
        editor.apply()

        // clear username
        val mshared = application.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val uNameeEdit = mshared.edit()
        uNameeEdit.remove("user_name")
        uNameeEdit.apply()
    }


    fun saveUserName(userName:String){
        val mshared = application.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        val edit = mshared.edit()
        edit.putString("user_name", userName)
        edit.apply()
    }

    fun resetSignupUIState(){
        _authUIState.update { it.copy(
            isSignupButtonLoading = false,
            isSignupSuccess = false,
            isSignupError = false,
            signupErrorMessage = ""
        ) }
    }

    fun resetLoginUIState(){
        _authUIState.update { it.copy(
            isLoginButtonLoading = false,
            isLoginError = false,
            isLoginSuccess = false,
            loginErrorMessage = ""
        ) }
    }
}