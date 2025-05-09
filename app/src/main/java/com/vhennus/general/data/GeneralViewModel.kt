package com.vhennus.general.data

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vhennus.general.domain.GenericResp
import com.vhennus.general.domain.SystemData
import com.vhennus.general.utils.CLog
import com.vhennus.wallet.data.WalletViewModel
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
class GeneralViewModel @Inject constructor(
private val apiService: APIService,
private val application: Application,
    private val jsonService: Json
): ViewModel() {
    private val _systemData = MutableStateFlow(SystemData())
    val systemData = _systemData.asStateFlow()


    fun savePrice(p: String){
        val sharedPreferences = application.getSharedPreferences("app", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("ngn_price", p).apply()
    }

    fun getNgn(p: String){
        val sharedPreferences = application.getSharedPreferences("app", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("ngn_price", p).apply()
    }
    fun getSystemData(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try{

                    val resp = apiService.getSystemData()
                    if (resp.isSuccessful){
                        val systemData = resp.body()?.data
                        if (systemData == null){
                            CLog.error("ERROR GETTING SYSTEM DATA", " Did not get any data")
                            return@withContext
                        }
                        CLog.debug("SYSTEM DATA", systemData.toString())
                        _systemData.value = systemData
                        savePrice(systemData.ngn.toString())

                    }else{
                        val errorResp = jsonService.decodeFromString(GenericResp.serializer(
                            SystemData.serializer()),
                            resp.errorBody()?.string() ?: ""
                        )

                    }
                }catch (e:Exception){
                    CLog.error("ERROR GETTING SYSTEM DATA", e.toString())
                }

            }
        }
    }
}