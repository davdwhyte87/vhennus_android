package com.vhennus.chat.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.airbnb.lottie.model.content.CircleShape
import com.vhennus.auth.data.AuthViewModel
import com.vhennus.chat.data.ChatViewModel
import com.vhennus.chat.domain.Chat
import com.vhennus.chat.domain.ChatPair
import com.vhennus.chat.domain.CreateChatReq
import com.vhennus.chat.domain.MUser
import com.vhennus.general.utils.CLog
import com.vhennus.profile.data.ProfileViewModel
import com.vhennus.profile.domain.Profile
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.GeneralScaffold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@Composable
fun SingleChatScreen(
    navController: NavController,
    chatViewModel: ChatViewModel,
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel,
    receiverUsername:String?,
){

    val newMessage = remember {
        mutableStateOf("")
    }
    val context = LocalContext.current
//    val chats = listOf(
//        Chat(sender = "jubello", receiver = "cammello", message = "Why are you so dumb seenat"),
//        Chat(sender = "rexienelly", receiver = "temz", message = "We need to make this work o"),
//        Chat(sender = "greengoo", receiver = "remi", message = "Let us go on a fucking heist")
//    )



    val chats = chatViewModel.chats.collectAsState().value
    val chatsUIState = chatViewModel.chatsUIState.collectAsState().value
    val userName= authViewModel.userName.collectAsState().value
    val collectAsState = chatViewModel.singleChatPair.collectAsState().value
    var chatPair = chatViewModel.singleChatPair.collectAsState().value
    val listState = rememberLazyListState()
    val profile = profileViewModel.profile.collectAsState().value
    val singleChatPair = chatViewModel.singleChatPair.collectAsState().value

    val lifecycleOwner = LocalLifecycleOwner.current
    val scrollState = rememberScrollState()
    DisposableEffect(true) {
        val observer = LifecycleEventObserver{_,event->
            if(event == Lifecycle.Event.ON_RESUME){
//                if(chatPairID != null){
//                    chatViewModel.getAllChatsByPair(chatPairID)
//                }

                authViewModel.getUserName()
                if(receiverUsername != null){
                    // get the persons profile
                    CLog.debug("RECEIVER_USERNAME_FROM_ROUTE", receiverUsername)
                    chatViewModel.findChatPair(receiverUsername)
                }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            //profileViewModel.resetModelData()
            chatViewModel.resetChatUIState()
            chatViewModel.singleChatScreenDispose()
        }
    }
    val receiverProfile = chatViewModel.singleChatReceiverProfile.collectAsState().value

    LaunchedEffect(chatsUIState.isFindChatPairSuccess) {
        if(chatsUIState.isFindChatPairSuccess && !chatsUIState.isChatPairNull){
            // get chats if a chat pair exists
            CLog.debug("CHAT_PAIR_ID", singleChatPair.id)
            chatViewModel.getAllChatsByPair(singleChatPair.id)
        }

        if(chatsUIState.isFindChatPairSuccess && chatsUIState.isChatPairNull){
            chatPair = ChatPair(
                user_name = userName,
                users_ids = listOf(userName, receiverUsername!!)

            )
        }
    }
    GeneralScaffold(
        { ChatTopBar(navController, receiverProfile) },
        floatingActionButton = {  },
    ) {
      Column(
          modifier = Modifier.fillMaxSize(),
          horizontalAlignment = Alignment.CenterHorizontally
      ){

          if(chatsUIState.isGetChatsLoading){
              AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.primary)
          }

          LazyColumn(
              modifier = Modifier.weight(1f)
                  .fillMaxWidth()
                  .padding(top = 40.dp)
              ,
              state = listState ,
              verticalArrangement = Arrangement.spacedBy(10.dp),
              reverseLayout = true
          ) {
//              .align(if(chat.sender == userName) Alignment.End else Alignment.Start),
              items(chats.reversed()){chat->
                  Row(

                      horizontalArrangement = if(chat.sender == userName) Arrangement.End else Arrangement.Start,
                      modifier = Modifier.fillMaxWidth()
                  ) {
                      Card (
                          modifier = Modifier
                              .fillMaxWidth(0.8f),
                          colors = CardDefaults.cardColors(containerColor =if(chat.sender == userName) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary)
                      ) {
                          Text(text = chat.message,
                              color = if(chat.sender == userName) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.secondary ,
                              style = MaterialTheme.typography.bodyMedium,
                              modifier = Modifier.padding(15.dp)
                          )
                      }
                  }


              }
          }

          // input
          Row (
              modifier = Modifier.fillMaxWidth()
          ){
              OutlinedTextField(value = newMessage.value,
                  onValueChange = {
                      newMessage.value = it
                  },
                  shape = RoundedCornerShape(20.dp),
                  placeholder = { Text(text = "Message") },
                  modifier = Modifier
                      .weight(1f)
                      .padding(end = 16.dp)
              )
              Button(
                  onClick = {
                      if(!validateCreateChat(context, newMessage.value)){
                          return@Button
                      }

                      val createChatReq = CreateChatReq(
                          pair_id = chatPair.id,
                          receiver = receiverUsername!!,
                          message = newMessage.value,
                          image = ""
                      )
                      chatViewModel.createChat(createChatReq)
                      newMessage.value = ""

                      // Scroll to the bottom (newest message)
                      CoroutineScope(Dispatchers.Main).launch {
                          listState.scrollToItem(0)
                      }
                  },
                  shape = CircleShape, // Makes the button round
                  modifier = Modifier.size(width = 65.dp, height = 65.dp).padding(0.dp),
                  colors = ButtonDefaults.buttonColors(
                      containerColor = MaterialTheme.colorScheme.primary
                  )
              ) {
                  if(chatsUIState.isCreateChatLoading){
                      AnimatedPreloader(modifier = Modifier.size(size = 60.dp), MaterialTheme.colorScheme.surface)
                  }else{
                      Icon(
                          imageVector = Icons.AutoMirrored.Filled.Send,
                          contentDescription = "Favorite",
                          tint = MaterialTheme.colorScheme.surface,
                          modifier = Modifier.size(30.dp).padding(0.dp)
                      )
                  }
              }
          }
      }
    }
}

fun validateCreateChat(context: Context, message:String):Boolean {
    if (message.isBlank() || message.isEmpty()) {
        Toast.makeText(context, "Empty message", Toast.LENGTH_SHORT).show()
        return false
    }
    return true
}