package com.amorgens.wallet.data

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amorgens.wallet.domain.Block
import com.amorgens.wallet.domain.Chain
import com.amorgens.wallet.domain.CreateWalletReq
import com.amorgens.wallet.domain.GetBalanceReq
import com.amorgens.wallet.domain.GetWalletReq
import com.amorgens.wallet.domain.TransferReq
import com.amorgens.wallet.domain.Wallet
import com.amorgens.wallet.domain.WalletC
import com.amorgens.wallet.domain.WalletUIState
import com.amorgens.wallet.presentation.formatter
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletService: WalletService,
    private val walletRepository: WalletRepository
) : ViewModel(){
    val defaultSingleWallet = Wallet(
        "",
        "",
        "",
        "0.00"
    )
    val _singleWallet = MutableStateFlow(defaultSingleWallet)
    val singleWallet = _singleWallet.asStateFlow()
    val _singleWalletC = MutableStateFlow(WalletC(
        "",
        "",
        "",
        "",
        "",
        "",
        false,
        Chain(listOf(
            Block(
            "",
                "",
                "",
                "",
                "",
                "",
                "",
                BigDecimal("0.0"),
                "", BigDecimal("0.00"),
                "",
            ))),
    ))
    val singleWalletC = _singleWalletC.asStateFlow()
    private val _allWallets = MutableStateFlow(listOf(defaultSingleWallet))
    val allWallets = _allWallets.asStateFlow()

    val defaultUIState = WalletUIState(
        isCreateWalletButtonLoading = false,
        isError = false,
        isCreateWalletDone = false,
        createWalletScreenNavigateBack = false,
        createWalletSuccess = false,
        createWalletError = false,
        createWalletErrorMessage = "",
        createWalletSuccessMessage = "",
        isSuccess = false,
        errorMessage = "",
        successMessage = "",
        isAddWalletButtonLoading = false,
        isAddWalletDone = false,
        isSingleWalletPageLoading = false,
        isTransferButtonLoading = false,
        isSyncingLocalWallet = false
    )
    val _walletUIState = MutableStateFlow(defaultUIState)
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
                        _walletUIState.update { it.copy(createWalletError = false, createWalletErrorMessage = "" )}
                        // add wallet locally
                        // save wallet data to database
                        val wallet = Wallet("",createWalletRequest.wallet_name,createWalletRequest.address, BigDecimal("0.0").toString())
                        walletRepository.insertWallet(wallet)

                    }else{
                        _walletUIState.update { it.copy(createWalletError = true, createWalletErrorMessage = respPack.get(1)) }
                        clearSuccess()
                        _walletUIState.update { it.copy(createWalletSuccess = false, createWalletSuccessMessage = "" )}
                    }
                }catch (exception:IOException){
                    println("ERROR XXXX"+ exception.toString())
                    Log.d("ERROR XXXX", exception.toString())
                    _walletUIState.update { it.copy(createWalletError = true, createWalletErrorMessage = exception.toString()) }
                    clearSuccess()
                    _walletUIState.update { it.copy(createWalletSuccess = false, createWalletSuccessMessage = "" )}
                }
            }
            updateIsCreateWalletButtonLoading(false)
            updateCreateWalletScreenNavigateBack(true)
        }


    }

    fun getBalanceRemote(getWalletReq:GetBalanceReq){
        val gson= Gson()
        val reqString = gson.toJson(getWalletReq)
        val message = formatter("GetBalance", reqString)

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try{
                    val resp = walletService.sendData(message)
                    val respPack = resp.split("\n")
                    Log.d("XXX RESPONSE", resp)
                    if(respPack[0] == "1"){
                        // success
                        _walletUIState.update { it.copy(isSuccess = true, successMessage = respPack[1]) }

                        // save wallet data to database
                        val wallet = Wallet(UUID.randomUUID().toString(),getWalletReq.address, getWalletReq.address, respPack[2])
                        walletRepository.insertWallet(wallet)

                        // trigger back navigation
                        updateIsAddWalletDone(true)

                    }else{
                        // error
                        _walletUIState.update { it.copy(isError = true, errorMessage = respPack.get(1)) }
                    }
                }catch (exception:Exception){
                    Log.d("XXX Yola", exception.toString())
                    _walletUIState.update { it.copy(isError = true, errorMessage ="Network Error") }

                }

            }

            updateIsAddWalletButtonLoading(false)

        }
    }

    fun addWallet(getWalletReq:GetWalletReq){
        val gson= Gson()
        val reqString = gson.toJson(getWalletReq)
        val message = formatter("GetWalletData", reqString)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try{
                    val resp = walletService.sendData(message)
                    val respPack = resp.split("\n")
                    Log.d("XXX RESPONSE", resp)
                    if(respPack[0] == "1"){
                        // success
                        _walletUIState.update { it.copy(isSuccess = true, successMessage = respPack[1]) }
                        clearError()
                        // convert response data to object
                        val walletC = gson.fromJson(respPack[2], WalletC::class.java)
                        Log.d("Converted OBJ XXX", walletC.toString())

                        // update single wallet variable
                        //_singleWalletC.value = walletC
                        // trigger back navigation
                        // save wallet data to database
                        val wallet = Wallet(walletC.id,walletC.address, getWalletReq.address, walletC.chain.chain.last().balance.toString())
                        walletRepository.insertWallet(wallet)

                        // trigger back navigation
                        updateIsAddWalletDone(true)

                    }else{
                        // error
                        _walletUIState.update { it.copy(isError = true, errorMessage = respPack.get(1)) }
                        clearSuccess()
                    }
                }catch (exception:Exception){
                    Log.d("XXX Yola", exception.toString())
                    _walletUIState.update { it.copy(isError = true, errorMessage ="Network Error") }
                    clearSuccess()
                }
            }
        }
    }

    fun getWalletRemote(getWalletReq:GetWalletReq){
        // change loading state
        _walletUIState.update { it.copy(isSingleWalletPageLoading = true) }

        val gson= Gson()
        val reqString = gson.toJson(getWalletReq)
        val message = formatter("GetWalletData", reqString)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try{
                    val resp = walletService.sendData(message)
                    val respPack = resp.split("\n")
                    Log.d("XXX RESPONSE", resp)
                    if(respPack[0] == "1"){
                        _walletUIState.update { it.copy(isSingleWalletPageLoading = false) }
                        // success
                        //_walletUIState.update { it.copy(isSuccess = true, successMessage = respPack[1]) }
                        // clear error
                        clearError()
                        // convert response data to object
                        val walletC = gson.fromJson(respPack[2], WalletC::class.java)
                        Log.d("Converted OBJ XXX", walletC.toString())

                        // update single wallet variable
                        _singleWalletC.value = walletC


                        // update wallet data locally
                        val wallet = Wallet(walletC.id,walletC.address, getWalletReq.address, walletC.chain.chain.last().balance.toString())
                        walletRepository.updateWallet(wallet)

                        // trigger back navigation
                        updateIsAddWalletDone(true)


                    }else{
                        _walletUIState.update { it.copy(isSingleWalletPageLoading = false) }
                        // error
                        _walletUIState.update { it.copy(isError = true, errorMessage = respPack.get(1)) }
                        clearSuccess()
                    }
                }catch (exception:Exception){
                    _walletUIState.update { it.copy(isSingleWalletPageLoading = false) }
                    Log.d("XXX Yola", exception.toString())
                    _walletUIState.update { it.copy(isError = true, errorMessage ="Network Error") }
                    clearSuccess()
                }
            }
        }
    }

    fun transfer(transferReq: TransferReq){
        // change loading state
        _walletUIState.update { it.copy(isTransferButtonLoading = true) }

        val gson= Gson()
        val reqString = gson.toJson(transferReq)
        val message = formatter("Transfer", reqString)
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try{
                    val resp = walletService.sendData(message)
                    val respPack = resp.split("\n")
                    Log.d("XXX RESPONSE", resp)
                    if(respPack[0] == "1"){
                        _walletUIState.update { it.copy(isTransferButtonLoading = false) }
                        // success
                        _walletUIState.update { it.copy(isSuccess = true, successMessage = respPack[1]) }
                        // clear error
                        clearError()
                        // we do nothing with the data part of the response


                        // trigger back navigation



                    }else{
                        _walletUIState.update { it.copy(isTransferButtonLoading = false) }
                        // error
                        _walletUIState.update { it.copy(isError = true, errorMessage = respPack[2]) }
                        clearSuccess()
                    }
                }catch (exception:Exception){
                    _walletUIState.update { it.copy(isTransferButtonLoading = false) }
                    Log.d("XXX Yola", exception.toString())
                    _walletUIState.update { it.copy(isError = true, errorMessage ="Network Error") }
                    clearSuccess()
                }
            }
        }
    }


    // run through a list of users wallets and get the wallet address data remotely
    fun updateWalletsLocal(addresses: List<String>){
        _walletUIState.update { it.copy(isSyncingLocalWallet = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                addresses.forEachIndexed { index, address ->
                    val gson= Gson()
                    val reqString = gson.toJson(GetWalletReq(address))
                    val message = formatter("GetWalletData", reqString)
                    try{
                        val resp = walletService.sendData(message)
                        val respPack = resp.split("\n")
                        Log.d("XXX RESPONSE", resp)
                        if(respPack[0] == "1"){

                            _walletUIState.update { it.copy(isSyncingLocalWallet = false) }
                            // success
                            //_walletUIState.update { it.copy(isSuccess = true, successMessage = respPack[1]) }
                            // clear error
                            clearError()

                            // convert response data to object
                            val walletC = gson.fromJson(respPack[2], WalletC::class.java)
                            Log.d("Converted OBJ XXX", walletC.toString())

                            // update wallet data locally
                            val wallet = Wallet(walletC.id,walletC.address, address, walletC.chain.chain.last().balance.toString())
                            walletRepository.updateWallet(wallet)

                        }else{
                            _walletUIState.update { it.copy(isSyncingLocalWallet = false) }

                            // error
                            _walletUIState.update { it.copy(isError = true, errorMessage = respPack.get(1)) }
                            clearSuccess()
                        }
                    }catch (exception:Exception) {
                        _walletUIState.update { it.copy(isSyncingLocalWallet = false) }
                        Log.d("XXX Yola", exception.toString())
                        _walletUIState.update {
                            it.copy(
                                isError = true,
                                errorMessage = "Network Error"
                            )
                        }
                        clearSuccess()
                    }

                }
            }
        }
    }


    // get all wallets in local db
    fun getAllWallets(){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
               _allWallets.value= walletRepository.getAllWallets().first()
            }
        }
    }

    fun updateUIStateData(data:WalletUIState){
        _walletUIState.value = data
    }

    fun updateIsCreateWalletButtonLoading(data:Boolean){
        _walletUIState.update { it.copy(isCreateWalletButtonLoading = data) }
    }
    fun updateIsAddWalletButtonLoading(data:Boolean){
        _walletUIState.update { it.copy(isAddWalletButtonLoading = data)}
    }
    fun updateCreateWalletScreenNavigateBack(data:Boolean){
        _walletUIState.update { it.copy(createWalletScreenNavigateBack = data) }
    }

    fun updateIsAddWalletDone(data:Boolean){
        _walletUIState.update { it.copy(isAddWalletDone = data) }
    }

    fun resetUIState(){
        Log.d("RESET UI XXXXX","yes")
        _walletUIState.value =  defaultUIState
    }

    fun clearCreateWalletSuccessData(){
        _walletUIState.update { it.copy(createWalletSuccess = false, createWalletSuccessMessage = "") }
    }
    fun clearCreateWalletErrorData(){
        _walletUIState.update { it.copy(createWalletError = false, createWalletErrorMessage = "") }
    }

    fun clearSuccess(){
        _walletUIState.update { it.copy(isSuccess = false, successMessage = "") }
    }
    fun clearError(){
        _walletUIState.update { it.copy(isError = false, errorMessage = "") }
    }


}





