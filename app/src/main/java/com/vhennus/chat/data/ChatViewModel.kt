package com.vhennus.chat.data


import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.vhennus.Application
import com.vhennus.chat.domain.Chat
import com.vhennus.chat.domain.ChatPair
import com.vhennus.chat.domain.ChatUIState
import com.vhennus.chat.domain.CreateChatReq
import com.vhennus.general.data.APIService
import com.vhennus.general.data.GetUserToken
import com.vhennus.general.data.WebSocketManager
import com.vhennus.general.utils.CLog
import com.vhennus.profile.domain.Profile
import com.vhennus.general.domain.GenericResp
import com.vhennus.general.utils.SoundVibratorHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject


@HiltViewModel
class ChatViewModel @Inject constructor(
   private val apiService: APIService,
    private val getUserToken: GetUserToken,
    private val webSocketManager: WebSocketManager,
    private val application: android.app.Application,
    private val getSoundVibratorHelper: SoundVibratorHelper
):ViewModel() {

    private val _chatPairs = MutableStateFlow<List<ChatPair>>(emptyList())
    val chatPairs = _chatPairs.asStateFlow()

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats = _chats.asStateFlow()

    private val _allChats = MutableStateFlow<List<Chat>>(emptyList())
    val allChats = _allChats.asStateFlow()

    private val _allChatPairs = MutableStateFlow<List<ChatPair>>(emptyList())
    val allChatPairs = _allChatPairs.asStateFlow()

    private val _singleChatPair = MutableStateFlow(ChatPair())
    val singleChatPair = _singleChatPair.asStateFlow()

    private val _chatsUIState = MutableStateFlow(ChatUIState())
    val chatsUIState = _chatsUIState.asStateFlow()

    private val _singleChatReceiverProfile = MutableStateFlow(Profile())
    val singleChatReceiverProfile = _singleChatReceiverProfile.asStateFlow()

    private val _isUnreadMessage = MutableStateFlow(false)
    val isUnreadMessage = _isUnreadMessage.asStateFlow()


    fun setSingleChatReceiverProfile(profile:Profile){
        _singleChatReceiverProfile.value = profile
    }

    fun singleChatScreenDispose(){
        _singleChatReceiverProfile.value = Profile()
        _chats.value = emptyList()
        _singleChatPair.value = ChatPair()

    }

    fun resetChatUIState(){
        _chatsUIState.value = ChatUIState()
    }

    fun updateUnreadMessageFlag(data: Boolean){
        _isUnreadMessage.value = data
    }


    init {
        CLog.debug("INIT","ChatViewModel")
        //connectToChatWS()

        // get all chat pairs
        getAllMyChatPairs()
    }


    fun getAllChats(){
        _chatsUIState.update { it.copy(isGetAllChatsLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val token = getUserToken.getUserToken()
                try{
                    val resp= apiService.getAllChatPairs( mapOf("Authorization" to token) )

                    if(resp.code() == 200){
                        val data = resp.body()?.data
                        if(data !=null){
                            _allChatPairs.value = data
                        }
                        CLog.debug("GET_ALL_CHATS_RESP", data.toString())
                        _chatsUIState.update { it.copy(
                            isGetAllChatsLoading = false,
                            isGetAllChatsSuccess = true,
                            isGetAllChatsError = false,
                            isGetAllChatsErrorMessage = ""
                        ) }

                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("GET_ALL_CHATS_RESP", respString +" ")
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
                    CLog.debug("GET_ALL_CHATS_RESP", e.toString())
                }
            }
        }
    }

    fun checkForUnreadMessages(){
        // determine unread messages and save in state
        var isNMessage  = false
        for(chatPiar in _allChatPairs.value){
            val lastMessage = getLastMessage(chatPiar.id)
            if(lastMessage != null){
                if(chatPiar.last_message != lastMessage){
                    isNMessage = true

                }
            }else{
                // this is most likely a new message from a new chat pair
                isNMessage = true
            }
        }

        if(isNMessage){
            _isUnreadMessage.value = true

        }else{
            _isUnreadMessage.value = false
        }
    }
    fun getAllMyChatPairs(){
        _chatsUIState.update { it.copy(isGetAllChatsLoading = true) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                val token = getUserToken.getUserToken()
                try{
                    val resp= apiService.getMyChatPairs( mapOf("Authorization" to token) )

                    if(resp.code() == 200){
                        val data = resp.body()?.data

                        if(data !=null){
                            // sort so that the latest chat pair shows on top
                            val formatter = DateTimeFormatter.ISO_DATE_TIME
                            val sortedData = data.sortedByDescending {
                                LocalDateTime.parse(it.updated_at, formatter)
                            }
                            _allChatPairs.value = sortedData
                        }
                        CLog.debug("GET_ALL_CHAT_PAIRS_RESP", data.toString())
                        _chatsUIState.update { it.copy(
                            isGetAllChatsLoading = false,
                            isGetAllChatsSuccess = true,
                            isGetAllChatsError = false,
                            isGetAllChatsErrorMessage = ""
                        ) }

                            // check for unread messages
                        checkForUnreadMessages()



                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("GET_ALL_CHAT_PAIRS ERROR", respString +" ")
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
                    CLog.debug("GET_ALL_CHAT_PAIRS ERROR", e.toString())
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
                        // store last message locally
                        val lastMessage = _chats.value.last()
                        saveLastMessage(lastMessage.pair_id, lastMessage.message)


                        //CLog.debug("GET_CHATS_BY_PAIR_RESP", lastMessage.toString())
                        _chatsUIState.update { it.copy(
                           isGetChatsLoading = false,
                            isGetChatsSuccess = true,
                            isGetChatsError = false,
                            getChatsErrorMessage = ""
                        ) }

                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("GET_CHATS_BY_PAIR_RESP", respString +" ")
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
                    CLog.debug("GET_CHATS_BY_PAIR_RESP", e.toString())
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

                        CLog.debug("CREATE_CHAT_RESP", data.toString())
                        _chatsUIState.update { it.copy(
                            isCreateChatLoading = false,
                            isCreateChatSuccess = true,
                            isCreateChatError = false,
                            createChatErrorMessage = ""
                        ) }

                        // refresh to get chat pair after the first chat
                        if(_chatsUIState.value.isChatPairNull){
                            findChatPair(chat.receiver)
                        }

                        // get all chats
                        getAllChatsByPair(chat.pair_id, true)

                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("CREATE_CHAT_RESP", respString +" ")
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
                    CLog.debug("CREATE_CHAT_RESP", e.toString())
                }
            }
        }
    }



    // find chat pair between app user and the supplied userName
    fun findChatPair(userName:String){
        _chatsUIState.update { it.copy(
            isFindChatPairLoading = true
        ) }
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                try {
                    val token = getUserToken.getUserToken()
                    val resp = apiService.findChatPair(userName, mapOf("Authorization" to token))
                    if (resp.code() == 200){
                        val data = resp.body()?.data
                        CLog.debug("FIND CHAT PAIR", data.toString())
                        if(data !=null){
                            _chatsUIState.update { it.copy(
                                isFindChatPairLoading = false,
                                isFindChatPairSuccess = true,
                                isFindChatPairError = false,
                                findChatPairErrorMessage = "",
                                isChatPairNull = false
                            ) }
                            _singleChatPair.value = data

                        }else{
                            _chatsUIState.update { it.copy(
                                isFindChatPairLoading = false,
                                isFindChatPairSuccess = true,
                                isFindChatPairError = false,
                                findChatPairErrorMessage = "",
                                isChatPairNull = true
                            ) }

                        }

                    }else{
                        val respString = resp.errorBody()?.string()
                        CLog.error("FIND CHAT PAIR ERROR", respString +" ")
                        val gson = Gson()
                        val genericType = object : TypeToken<GenericResp<String>>() {}.type
                        val errorResp: GenericResp<String> = gson.fromJson(respString ?:"" , genericType)
                        _chatsUIState.update { it.copy(
                            isFindChatPairLoading = false,
                            isFindChatPairSuccess = false,
                            isFindChatPairError = true,
                            findChatPairErrorMessage = errorResp.message,
                            isChatPairNull = true
                        ) }
                    }

                }catch (e:Exception){
                    _chatsUIState.update { it.copy(
                        isFindChatPairLoading = false,
                        isFindChatPairSuccess = false,
                        isFindChatPairError = true,
                        findChatPairErrorMessage = "Error",
                        isChatPairNull = true
                    ) }
                    CLog.error("FIND CHAT PAIR ERROR", e.toString() +" ")
                }
            }
        }
    }

    fun connectToChatWS(){
        CLog.debug("WS MESSAGE", "Connecting .......  ....")
        webSocketManager.connect(
            onMessageReceived = { text->
                // convert text to chat model
                val json =  Json { coerceInputValues = true }
                val message = json.decodeFromString<Chat>(text)
                // save last message locally
                //saveLastMessage(message.pair_id, message.message)
                addMessageWS(message)
                // check if the current chat is open
                if(_singleChatPair.value.id == message.pair_id){
                    // play in chat sound
                    getSoundVibratorHelper.playLowBel()
                }else{
                    //play vibrate sound. this means user is not in active chat with the pair
                    // notify the user with sound
                    val soundHelper = getSoundVibratorHelper
                    soundHelper.playSoundAndVibrate()
                }
                CLog.debug("WS MESSAGE", message.toString()) },
            onFailure = {error ->
                CLog.debug("WS MESSAGE ERROR", error.toString())
            },
        )

    }

    fun disconnectWS(){
        webSocketManager.disconnect()
    }
    fun sendMessageToWS(createChatReq: CreateChatReq, userName:String){
        // check if websocket manager is connected if not connect
        if(!webSocketManager.isConnected()){
            connectToChatWS()
        }

        CLog.debug("WS SEND MESSAGE", "starting send")
        val gson = Gson()
        val text = Json.encodeToString(CreateChatReq.serializer(), createChatReq)
        CLog.debug("WS SEND MESSAGE", "text ... ${text}")


        if(!webSocketManager.sendMessage(text)){
            CLog.debug("WS SEND MESSAGE", "error sending ")
            _chatsUIState.update { it.copy(
                isCreateChatLoading = false,
                isCreateChatError = true,
                createChatErrorMessage = "Error sending message"
            ) }
        }
        // refresh to get chat pair after the first chat
        if(_chatsUIState.value.isChatPairNull){
            findChatPair(createChatReq.receiver)
        }

        // get new chat

        val temp = _chats.value.toMutableList()
        temp.add(
            Chat(
            pair_id = createChatReq.pair_id,
            receiver = createChatReq.receiver,
            message = createChatReq.message,
            image = createChatReq.image,
            sender = userName
        )
        )
        _chats.value = temp.toList()
        // get all chats
        //getAllChatsByPair(createChatReq.pair_id, true)
    }

    fun addMessageWS(message:Chat){
        // add message to current
        // check if th chats have the same pair ID
        if (_chats.value.isEmpty()){
            _chats.value.toMutableList().add(message)
            CLog.debug("NO SINGLECHATS OPEN","")
        }else{
            if (_chats.value.get(0).pair_id == message.pair_id){
                // add to the list
                val tempChats = _chats.value.toMutableList()
                tempChats.add(message)
                _chats.value = tempChats.toList()

                CLog.debug("ADDED NEW CHATS TO CHAT", "")

            }else{

            }
        }

        var pairFound = false
        // find the chat pair it belongs to and add last message
        val cpairs = _allChatPairs.value.toMutableList()
        cpairs.forEachIndexed { index, chatPair ->
            CLog.debug("FINDING PAIR", "chatpair ${chatPair.id} - messagepair ${message.pair_id}")
            if (message.pair_id == chatPair.id){
                val updatedChatPair = chatPair.copy(last_message = message.message)
                cpairs[index] = updatedChatPair
                pairFound = true
//                    var newChatPair = _chatPairs.value.last()
//                    newChatPair.last_message = message.message
                CLog.debug("NEW CHAT PAIR", chatPair.toString())

            }

        }
        _allChatPairs.value = cpairs

        if(!pairFound){
            // if no pair was found, get all chat pairs from the sever
            CLog.debug("NO PAIR FOUND", "")

            getAllMyChatPairs()
        }

        // check for unread messges
        checkForUnreadMessages()


    }

    // save last message to shared preference

    fun saveLastMessage(chatPairId:String, lastMessage:String ){
        val mshared = application.getSharedPreferences("last_messages", Context.MODE_PRIVATE)
        val edit = mshared.edit()
        edit.putString(chatPairId, lastMessage)
        edit.apply()
    }


    fun getLastMessage(chatPairId:String):String?{
        val mshared = application.getSharedPreferences("last_messages", Context.MODE_PRIVATE)
        val lastMessageId = mshared.getString(chatPairId, null)
        return lastMessageId
    }

}