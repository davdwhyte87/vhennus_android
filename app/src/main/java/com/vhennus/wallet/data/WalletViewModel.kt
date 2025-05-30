package com.vhennus.wallet.data

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vhennus.general.utils.CLog
import com.vhennus.wallet.domain.CreateWalletReq
import com.vhennus.wallet.domain.GetBalanceReq
import com.vhennus.wallet.domain.GetWalletReq
import com.vhennus.wallet.domain.TransferReq
import com.vhennus.wallet.domain.Wallet
import com.vhennus.wallet.domain.WalletC
import com.vhennus.wallet.domain.WalletUIState
import com.vhennus.wallet.presentation.formatter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vhennus.di.NetworkModule
import com.vhennus.general.data.APIService
import com.vhennus.general.data.GetUserToken
import com.vhennus.general.domain.GenericResp
import com.vhennus.general.utils.KeyGenerator
import com.vhennus.general.utils.signMessage
import com.vhennus.general.utils.signMessage2
import com.vhennus.wallet.domain.Account

import com.vhennus.wallet.domain.AddWalletReq
import com.vhennus.wallet.domain.BlockchainRequest
import com.vhennus.wallet.domain.BlockchainResp
import com.vhennus.wallet.domain.GetWalletTransactionsReq
import com.vhennus.wallet.domain.MicroAccount
import com.vhennus.wallet.domain.Transaction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import java.io.IOException
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class WalletViewModel @Inject constructor(
    private val walletService: WalletService,
    private val walletRepository: WalletRepository,
    private val application: Application,
    private val apiService: APIService,
    private val jsonService: Json,
    private val tokenService: GetUserToken,
    @NetworkModule.BlockchainAPIService private val blockchainAPIService: APIService
) : ViewModel(){
    val defaultSingleWallet = Wallet()

    private val _singleWallet = MutableStateFlow(defaultSingleWallet)
    val singleWallet = _singleWallet.asStateFlow()

    private val _singleWalletC = MutableStateFlow(Account())
    val singleWalletC = _singleWalletC.asStateFlow()

    private val _allWallets = MutableStateFlow(emptyList<Account>())
    val allWallets = _allWallets.asStateFlow()

    private val _userName = MutableStateFlow("")
    val userName = _userName.asStateFlow()

    private val _singleWalletTransactions = MutableStateFlow(emptyList<Transaction>())
    val singleWalletTransactions = _singleWalletTransactions.asStateFlow()


    val defaultUIState = WalletUIState()
    val _walletUIState = MutableStateFlow(defaultUIState)
    val walletUIState = _walletUIState.asStateFlow()

    private val _selectedCurrency = MutableStateFlow("NGN")
    val selectedCurrency = _selectedCurrency.asStateFlow()

    private val _isBalanceHidden = MutableStateFlow("0")
    val isBalanceHidden = _isBalanceHidden.asStateFlow()



    fun clearModelData(){
        _walletUIState.value = WalletUIState()

        _allWallets.value = emptyList()
        _singleWallet.value = Wallet()

    }

    fun resetUI(){
        _walletUIState.value = WalletUIState()
    }


    fun updateSingleWallet(wallet:Wallet){
        _singleWallet.value = wallet
    }


    fun getSelectedCurrency(){
        val sharedPreferences = application.getSharedPreferences("app", Context.MODE_PRIVATE)
        val value = sharedPreferences.getString("currency", null)
        _selectedCurrency.value = value?: "NGN"
    }

    fun getIsBalanceHidden(){
        val sharedPreferences = application.getSharedPreferences("app", Context.MODE_PRIVATE)
        val value = sharedPreferences.getString("is_balance_hidden", null)
        _isBalanceHidden.value = value?: "0"
    }

    fun saveSelectedCurrency(cur: String){
        val sharedPreferences = application.getSharedPreferences("app", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("currency", cur).apply()

        getSelectedCurrency()
    }

    fun saveIsBalanceHidden(data: String){
        val sharedPreferences = application.getSharedPreferences("app", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("is_balance_hidden", data).apply()

        getIsBalanceHidden()
    }
    fun createWallet(createWalletRequest:CreateWalletReq, priv:String){
        _walletUIState.update { it.copy(
            isCreateWalletButtonLoading = true,
        ) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val resp = blockchainAPIService.createWallet(createWalletRequest)

                    CLog.debug("CREATE WALLET RESP", resp.toString())
                    if (resp.code() == 200){
                        _walletUIState.update { it.copy(
                            isCreateWalletButtonLoading = false,
                            createWalletSuccess = true,
                            createWalletError = false,
                            createWalletErrorMessage = ""
                        ) }

                        val smessage = "north pole"
                        val sig = signMessage2(smessage,priv)
                        val addReq = AddWalletReq(
                            address = createWalletRequest.address,
                            message = smessage,
                            signature = sig
                        )
                        addWallet(addReq)
                    }else{
                        val errorResp = jsonService.decodeFromString(GenericResp.serializer(String.serializer()),
                            resp.errorBody()?.string() ?: ""
                        )
                        _walletUIState.update { it.copy(
                            isCreateWalletButtonLoading = false,
                            createWalletSuccess = false,
                            createWalletError = true,
                            createWalletErrorMessage = errorResp.message
                        ) }
                    }
                }catch (exception:IOException){
                    CLog.error("CREATE WALLET ERROR ", exception.toString())

                    _walletUIState.update { it.copy(
                        isCreateWalletButtonLoading = false,
                        createWalletSuccess = false,
                        createWalletError = true,
                        createWalletErrorMessage = exception.toString()
                    ) }
                }
            }
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
                    CLog.error("XXX RESPONSE", resp)
                    if(respPack[0] == "1"){
                        // success
                        _walletUIState.update { it.copy(isSuccess = true, successMessage = respPack[1]) }

                        // save wallet data to database
                        val wallet = Wallet(UUID.randomUUID().toString(),getWalletReq.address, getWalletReq.address,BigDecimal(respPack[2]),getUserName(application) )
                        walletRepository.insertWallet(wallet)

                        // trigger back navigation
                        updateIsAddWalletDone(true)

                    }else{
                        // error
                        _walletUIState.update { it.copy(isError = true, errorMessage = respPack.get(1)) }
                    }
                }catch (exception:Exception){
                    CLog.error("XXX Yola", exception.toString())
                    _walletUIState.update { it.copy(isError = true, errorMessage ="Network Error") }

                }

            }

            updateIsAddWalletButtonLoading(false)

        }
    }

    fun getUserName(application:Context):String{
        val mshared = application.getSharedPreferences("user_data", Context.MODE_PRIVATE)
        _userName.value = mshared.getString("user_name","").toString()
        return  _userName.value
    }

    fun addWallet(req: AddWalletReq){
        _walletUIState.update { it.copy(isAddWalletButtonLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try{
                    val token = tokenService.getUserToken()
                    CLog.debug("ADD WALLET REQUEST ",req.toString())
                    val resp = apiService.addWallet(req, mapOf("Authorization" to token))


                    if (resp.isSuccessful){
                        _walletUIState.update { it.copy(
                            isAddWalletButtonLoading = false,
                            isAddWalletSuccess = true,
                            isAddWalletError = false,
                            isAddWalletDone = true,
                            addWalletErrorMessage =  ""
                        ) }

                        CLog.error("ADD WALLET RESP ",resp.toString())

                    }else{
                        val errorResp = jsonService.decodeFromString(GenericResp.serializer(String.serializer()),
                            resp.errorBody()?.string() ?: ""
                        )
                        val errMessage = ""

                        _walletUIState.update { it.copy(
                            isAddWalletButtonLoading = false,
                            isAddWalletSuccess = false,
                            isAddWalletError = true,
                            isAddWalletDone = false,
                            addWalletErrorMessage =  errorResp.message
                        ) }
                        CLog.error("ADD WALLET ERROR ",errorResp.toString())
                    }
                    CLog.debug("ADD WALLET RESPONSE ",resp.toString())

                }catch (exception:Exception){
                    CLog.error("XXX Yola", exception.toString())
                    _walletUIState.update { it.copy(
                        isAddWalletButtonLoading = false,
                        isAddWalletSuccess = false,
                        isAddWalletError = true,
                        isAddWalletDone = false,
                        addWalletErrorMessage =  exception.toString()
                    ) }
                }
            }
        }
    }

    fun getWalletFromBlockchain(getWalletReq:GetWalletReq){
        // change loading state
        _walletUIState.update { it.copy(isGetSingleWalletLoading = true) }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {

                try {
                    val resp = blockchainAPIService.getAccount(getWalletReq)

                    if (resp.code() == 200) {
                        val account = resp.body()?.data
                        if (account != null) {
                            _singleWalletC.value =account
                        }
                        _walletUIState.update { it.copy(
                            isGetSingleWalletLoading = false,
                            isGetSingleWalletSuccess = true,
                            isGetSingleWalletError = false,
                            getSingleWalletErrorMessage = ""
                        ) }
                    }else{
                        val errorResp = jsonService.decodeFromString(GenericResp.serializer(Account.serializer()),
                            resp.errorBody()?.string() ?: ""
                        )
                        _walletUIState.update { it.copy(
                            isGetSingleWalletLoading = false,
                            isGetSingleWalletSuccess = false,
                            isGetSingleWalletError = true,
                            getSingleWalletErrorMessage = errorResp.message
                        ) }
                    }
                    CLog.debug("GET WALLET RESP", resp.toString())
                } catch (exception:Exception){
                    _walletUIState.update { it.copy(
                        isGetSingleWalletLoading = false,
                        isGetSingleWalletSuccess = false,
                        isGetSingleWalletError = true,
                        getSingleWalletErrorMessage = "A network error occurred"
                    ) }
                }
            }
        }
    }

    fun getWalletTransactionsFromBlockchain(req: GetWalletTransactionsReq){
        // change loading state
        _walletUIState.update { it.copy(isGetSingleWalletTransactionsLoading = true) }

        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                val req = BlockchainRequest<GetWalletTransactionsReq>(
                    action = "get_user_transactions",
                    data = GetWalletTransactionsReq(address = req.address)
                )
                try {
                    val json =  jsonService
                    val reqString = json.encodeToString(BlockchainRequest.serializer(
                        GetWalletTransactionsReq.serializer()), req)
                    val resp = walletService.sendData(reqString)
                    CLog.debug("GET WALLET TRANSACTIONS RESP", resp)
                    val respData = json.decodeFromString(BlockchainResp.serializer(ListSerializer(
                        Transaction.serializer())), resp)
                    CLog.debug("GET WALLET TRANSACTIONS RESP Json", respData.toString())
                    if (respData.status == 1) {
                        if (respData.data != null) {
                            _singleWalletTransactions.value = respData.data
                        }
                        _walletUIState.update { it.copy(
                            isGetSingleWalletTransactionsLoading  = false,
                            isGetSingleWalletTransactionsSuccess = true,
                            isGetSingleWalletTransactionsError = false,
                            getSingleWalletTransactionsErrorMessage = ""
                        ) }
                    }else{
                        CLog.debug("GET WALLET TRANSACTIONS ERROR ", respData.message)
                        _walletUIState.update { it.copy(
                            isGetSingleWalletTransactionsLoading  = false,
                            isGetSingleWalletTransactionsSuccess = false,
                            isGetSingleWalletTransactionsError = true,
                            getSingleWalletTransactionsErrorMessage = respData.message
                        ) }
                    }
                } catch (exception:Exception){
                    _walletUIState.update { it.copy(
                        isGetSingleWalletTransactionsLoading  = false,
                        isGetSingleWalletTransactionsSuccess = false,
                        isGetSingleWalletTransactionsError = true,
                        getSingleWalletTransactionsErrorMessage = "Network error"
                    ) }
                    CLog.debug("GET WALLET TRANSACTIONS ERROR ", exception.toString())
                }
            }
        }
    }

    fun transfer(transferReq: TransferReq){
        // change loading state
        _walletUIState.update { it.copy(isTransferButtonLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                try {

                    val resp = blockchainAPIService.transfer(transferReq)
                    if (resp.code() == 200) {
                        _walletUIState.update { it.copy(
                            isTransferButtonLoading  = false,
                            isTransferSuccessful = true,
                            isTransferError = false,
                            transferErrorMessage = ""
                        ) }
                    }else{
                        val errorResp = jsonService.decodeFromString(GenericResp.serializer(String.serializer()),
                            resp.errorBody()?.string() ?: ""
                        )
                        _walletUIState.update { it.copy(
                            isTransferButtonLoading  = false,
                            isTransferSuccessful = false,
                            isTransferError = true,
                            transferErrorMessage = errorResp.message
                        ) }

                    }
                    CLog.debug("TRANSFER RESP", resp.toString())
                } catch (exception:Exception){
                    _walletUIState.update { it.copy(
                        isTransferButtonLoading  = false,
                        isTransferSuccessful = false,
                        isTransferError = true,
                        transferErrorMessage = "Network error"
                    ) }
                    CLog.error("TRANSFER ERROR ", exception.toString())
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
                        CLog.error("XXX RESPONSE", resp)
                        if(respPack[0] == "1"){

                            _walletUIState.update { it.copy(isSyncingLocalWallet = false) }
                            // success
                            //_walletUIState.update { it.copy(isSuccess = true, successMessage = respPack[1]) }
                            // clear error
                            clearError()

                            // convert response data to object
                            val walletC = gson.fromJson(respPack[2], WalletC::class.java)
                            CLog.error("Converted OBJ XXX", walletC.toString())

                            // update wallet data locally
                            val wallet = Wallet(walletC.id,walletC.address, address, walletC.chain.chain.last().balance, getUserName(application))
                            walletRepository.updateWallet(wallet)

                        }else{
                            _walletUIState.update { it.copy(isSyncingLocalWallet = false) }

                            // error
                            _walletUIState.update { it.copy(isError = true, errorMessage = respPack.get(1)) }
                            clearSuccess()
                        }
                    }catch (exception:Exception) {
                        _walletUIState.update { it.copy(isSyncingLocalWallet = false) }
                        CLog.error("XXX Yola", exception.toString())
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


    // get all wallets in local db for the logged in user
    fun getAllWallets(walletsString: String){
        _walletUIState.update { it.copy(
            isGetAllWalletsLoading = true,
        ) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val walletAddresses =  walletsString.split(",")
                // get wallet from blockchain api
                val accounts = mutableListOf<Account>()
                walletAddresses.forEach{it->
                    CLog.debug("WL", it)
                    try {
                        val resp = blockchainAPIService.getAccount(GetWalletReq(address = it))

                        if (resp.code() == 200){
                            val account = resp.body()?.data
                            if(account != null){
                                accounts.add(account)
                            }

//                            _walletUIState.update { it.copy(
//                                isGetAllWalletsLoading = false,
//                                isGetAllWalletsSuccess = true,
//                                isGetAllWalletsError = false,
//                                getAllWalletsErrorMessage = ""
//                            ) }
                        }else{
                            val errorResp = jsonService.decodeFromString(GenericResp.serializer(
                                Account.serializer()),
                                resp.errorBody()?.string() ?: ""
                            )
                            CLog.error("GET ALL WALLETS", errorResp.message)
//                            _walletUIState.update { it.copy(
//                                isGetAllWalletsLoading = false,
//                                isGetAllWalletsSuccess = false,
//                                isGetAllWalletsError = true,
//                                getAllWalletsErrorMessage = respData.message
//                            ) }
                        }
                        CLog.debug("GET ALL WALLETS RESP", resp.toString())

                    }catch (e: Exception){
                        CLog.error("GET ALL WALLETS", e.toString())
                        _walletUIState.update { it.copy(
                            isGetAllWalletsLoading = false,
                            isGetAllWalletsSuccess = false,
                            isGetAllWalletsError = true,
                            getAllWalletsErrorMessage = "Error getting Wallet"
                        ) }
                    }

                }

                _walletUIState.update { it.copy(
                                isGetAllWalletsLoading = false,
                                isGetAllWalletsSuccess = true,
                                isGetAllWalletsError = false,
                                getAllWalletsErrorMessage = ""
                            ) }

                _allWallets.value = accounts
            }
        }
    }


    fun getExchangeRate(){
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

                        val sharedPreferences = application.getSharedPreferences("exchange_rates", Context.MODE_PRIVATE)
                        val edit = sharedPreferences.edit()
                        edit.putString("NGN", systemData.price.toString()).apply()
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


    // amara101_sigil
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





