package com.vhennus.chat.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.airbnb.lottie.model.content.CircleShape
import com.vhennus.chat.data.ChatViewModel
import com.vhennus.chat.domain.Chat
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.GeneralScaffold


@Composable
fun SingleChatScreen(
    navController: NavController,
    chatViewModel: ChatViewModel
){

    val newMessage = remember {
        mutableStateOf("")
    }
//    val chats = listOf(
//        Chat(sender = "jubello", receiver = "cammello", message = "Why are you so dumb seenat"),
//        Chat(sender = "rexienelly", receiver = "temz", message = "We need to make this work o"),
//        Chat(sender = "greengoo", receiver = "remi", message = "Let us go on a fucking heist")
//    )

    val chats = chatViewModel.chats.collectAsState().value
    val chatsUIState = chatViewModel.chatsUIState.collectAsState().value

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(true) {
        val observer = LifecycleEventObserver{_,event->
            if(event == Lifecycle.Event.ON_RESUME){
                chatViewModel.getAllChatsByPair("iow9838usose")
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {

        }
    }
    GeneralScaffold(
        { ChatTopBar(navController) },
        floatingActionButton = {  },
    ) {
      Column(
          modifier = Modifier.fillMaxSize(),
          horizontalAlignment = Alignment.CenterHorizontally
      ){
          val scrollState = rememberScrollState()
          if(chatsUIState.isGetChatsLoading){
              AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.primary)
          }

          LazyColumn(
              modifier = Modifier.weight(1f)
                  .fillMaxWidth()
                  .padding(top = 40.dp)
              ,
              verticalArrangement = Arrangement.spacedBy(10.dp),
              reverseLayout = true
          ) {
              items(chats){chat->
                  Card (
                      modifier = Modifier
                          .fillMaxWidth(0.8f)
                          .align(if(chat.sender == "dodoman") Alignment.End else Alignment.Start),
                      colors = CardDefaults.cardColors(containerColor =if(chat.sender == "dodoman") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary)
                  ) {
                      Text(text = chat.message,
                          color = if(chat.sender == "dodoman") MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.secondary ,
                          style = MaterialTheme.typography.bodyMedium,
                          modifier = Modifier.padding(15.dp)
                      )
                  }

              }
          }

          // input
          Row {
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
                  onClick = {  },
                  shape = CircleShape, // Makes the button round
                  modifier = Modifier.size(65.dp).padding(0.dp),
                  colors = ButtonDefaults.buttonColors(
                      containerColor = MaterialTheme.colorScheme.primary
                  )
              ) {
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