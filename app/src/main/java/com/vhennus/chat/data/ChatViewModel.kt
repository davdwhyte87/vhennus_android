package com.vhennus.chat.data

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vhennus.chat.domain.Chat
import com.vhennus.chat.domain.ChatPair
import com.vhennus.chat.domain.ChatUIState
import com.vhennus.chat.domain.CreateChatReq
import com.vhennus.general.data.APIService
import com.vhennus.general.data.GetUserToken
import com.vhennus.general.utils.CLog
import com.vhennus.trade.domain.response.GenericResp
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
   private val apiService: APIService,
    private val getUserToken: GetUserToken
):ViewModel() {

    private val _chatPairs = MutableStateFlow<List<ChatPair>>(emptyList())
    val chatPairs = _chatPairs.asStateFlow()

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats = _chats.asStateFlow()

    private val _allChats = MutableStateFlow<List<Chat>>(emptyList())
    val allChats = _allChats.asStateFlow()

    private val _chatsUIState = MutableStateFlow(ChatUIState())
    val chatsUIState = _chatsUIState.asStateFlow()


    fun getAllChats(){
        _chatsUIState.update { it.copy(isGetAllChatsLoading = true) }


        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val token = getUserToken.getUserToken()
                try{
                    val resp= apiService.getAllChats( mapOf("Authorization" to token) )

                    if(resp.code() == 200){
                        val data = resp.body()?.data
                        if(data !=null){
                            _allChats.value = data
                        }
                        CLog.debug("CHATS_RESP", data.toString())
                        _chatsUIState.update { it.copy(
                            isGetAllChatsLoading = false,
                            isGetAllChatsSuccess = true,
                            isGetAllChatsError = false,
                            isGetAllChatsErrorMessage = ""
                        ) }

                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("CREATE ORDER RESPONSE", respString +" ")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(respString ?:"" , genericType)
                        _chatsUIState.update { it.copy(
                            isGetAllChatsLoading = false,
                            isGetAllChatsSuccess = false,
                            isGetAllChatsError = true,
                            isGetAllChatsErrorMessage = errorResp.message
                        ) }
                    }

                }catch (e:Exception){
                    _chatsUIState.update { it.copy(
                        isGetAllChatsLoading = false,
                        isGetAllChatsSuccess = false,
                        isGetAllChatsError = true,
                        isGetAllChatsErrorMessage = "Network Error"
                    ) }
                    CLog.debug("CHATS_RESP", e.toString())
                }
            }
        }
    }



    fun getAllChatsByPair(pairID:String, silentLoad:Boolean = false){
        if(!silentLoad){
            _chatsUIState.update { it.copy(isGetChatsLoading = true) }
        }

        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val token = getUserToken.getUserToken()
                try{
                    val resp= apiService.getChatsByPair(pairID, mapOf("Authorization" to token) )

                    if(resp.code() == 200){
                        val data = resp.body()?.data
                        if(data !=null){
                            _chats.value = data
                        }
                        CLog.debug("CHATS_RESP", data.toString())
                        _chatsUIState.update { it.copy(
                           isGetChatsLoading = false,
                            isGetChatsSuccess = true,
                            isGetChatsError = false,
                            getChatsErrorMessage = ""
                        ) }

                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("CREATE ORDER RESPONSE", respString +" ")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(respString ?:"" , genericType)
                        _chatsUIState.update { it.copy(
                            isGetChatsLoading = false,
                            isGetChatsSuccess = false,
                            isGetChatsError = true,
                            getChatsErrorMessage = errorResp.message
                        ) }
                    }

                }catch (e:Exception){
                    _chatsUIState.update { it.copy(
                        isGetChatsLoading = false,
                        isGetChatsSuccess = false,
                        isGetChatsError = true,
                        getChatsErrorMessage = "Network Error"
                    ) }
                    CLog.debug("CHATS_RESP", e.toString())
                }
            }
        }
    }


    fun createChat(chat: CreateChatReq){
        _chatsUIState.update { it.copy(isCreateChatLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val token = getUserToken.getUserToken()
                try{
                    val resp= apiService.createChat(chat, mapOf("Authorization" to token) )

                    if(resp.code() == 200){
                        val data = resp.body()?.data

                        CLog.debug("CHATS_RESP", data.toString())
                        _chatsUIState.update { it.copy(
                            isCreateChatLoading = false,
                            isCreateChatSuccess = true,
                            isCreateChatError = false,
                            createChatErrorMessage = ""
                        ) }

                        // get all chats
                        getAllChatsByPair(chat.pair_id, true)

                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("CREATE ORDER RESPONSE", respString +" ")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(respString ?:"" , genericType)
                        _chatsUIState.update { it.copy(
                            isCreateChatLoading = false,
                            isCreateChatSuccess = true,
                            isCreateChatError = false,
                            createChatErrorMessage = errorResp.message
                        ) }
                    }

                }catch (e:Exception){
                    _chatsUIState.update { it.copy(
                        isCreateChatLoading = false,
                        isCreateChatSuccess = true,
                        isCreateChatError = false,
                        createChatErrorMessage = "Network Error"
                    ) }
                    CLog.debug("CHATS_RESP", e.toString())
                }
            }
        }
    }
}