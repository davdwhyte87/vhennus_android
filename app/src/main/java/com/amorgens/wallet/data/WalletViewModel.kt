package com.amorgens.wallet.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amorgens.wallet.domain.CreateWalletReq
import com.amorgens.wallet.domain.Wallet
import com.amorgens.wallet.domain.WalletUIState
import com.amorgens.wallet.presentation.formatter
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject


@HiltViewModel
class WalletViewModel @Inject constructor(private val walletService: WalletService) : ViewModel(){
    val defaultSingleNote = Wallet(
        "",
        "",
        "",
        ""
    )
    val _singleWallet = MutableStateFlow(defaultSingleNote)
    val singleWallet = _singleWallet.asStateFlow()

    val _walletUIState = MutableStateFlow(WalletUIState(
        isCreateWalletButtonLoading = false,
        isError = false,
        isCreateWalletDone = false,
        createWalletScreenNavigateBack = false,
        createWalletSuccess = false,
        createWalletError = false,
        createWalletErrorMessage = "",
        createWalletSuccessMessage = ""
    ))
    val walletUIState = _walletUIState.asStateFlow()





    fun updateSingleWallet(wallet:Wallet){
        _singleWallet.value = wallet
    }
    fun createWallet(createWalletRequest:CreateWalletReq){

        val gson= Gson()
        val reqString = gson.toJson(createWalletRequest)
        val message = formatter("CreateWallet", reqString)

        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val resp = walletService.sendData(message)
                    Log.d("RESPDATA XXXX", resp)
                    // seperate response string
                    val respPack = resp.split("\n")
                    if(respPack.get(0) == "1"){
                        // success
                        _walletUIState.update { it.copy(createWalletSuccess = true, createWalletSuccessMessage = respPack.get(1)) }

                    }else{
                        _walletUIState.update { it.copy(createWalletError = true, createWalletErrorMessage = respPack.get(1)) }
                    }
                }catch (exception:IOException){
                    println("ERROR XXXX"+ exception.toString())
                    Log.d("ERROR XXXX", exception.toString())
                }
            }
            updateIsCreateWalletButtonLoading(false)
            updateCreateWalletScreenNavigateBack(true)
        }


    }

    fun updateUIStateData(data:WalletUIState){
        _walletUIState.value = data
    }

    fun updateIsCreateWalletButtonLoading(data:Boolean){
        _walletUIState.update { it.copy(isCreateWalletButtonLoading = data) }
    }
    fun updateCreateWalletScreenNavigateBack(data:Boolean){
        _walletUIState.update { it.copy(createWalletScreenNavigateBack = data) }
    }

    fun clearSuccessData(){
        _walletUIState.update { it.copy(createWalletSuccess = false, createWalletSuccessMessage = "") }
    }
    fun clearErrorData(){
        _walletUIState.update { it.copy(createWalletError = false, createWalletErrorMessage = "") }
    }


}


sealed class WalletUIEvent{
    object CreateWalletScreenNavigateBack:WalletUIEvent()
}
fun onEvent(event: WalletUIEvent){
    when(event){
        WalletUIEvent.CreateWalletScreenNavigateBack->{

        }
    }
}


