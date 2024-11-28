package com.vhennus.chat.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.vhennus.chat.data.ChatViewModel
import com.vhennus.chat.domain.Chat
import com.vhennus.ui.GeneralScaffold
import com.vhennus.ui.GeneralTopBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllChatsScreen(
    navController: NavController,
    chatViewModel: ChatViewModel
    ){
    val chats = listOf(
        Chat(sender = "jubello", receiver = "cammello", message = "Why are you so dumb seenat"),
        Chat(sender = "rexienelly", receiver = "temz", message = "We need to make this work o"),
        Chat(sender = "greengoo", receiver = "remi", message = "Let us go on a fucking heist")
    )
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(true) {
        val observer = LifecycleEventObserver{_,event->
            if(event == Lifecycle.Event.ON_RESUME){
                chatViewModel.getAllChatsByPair("8937b13c-3034-4405-96be-c7a7d41fb3f3")
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {

        }
    }

    GeneralScaffold(
        { GeneralTopBar() },
        floatingActionButton = {},
    ) {

        LazyColumn (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(chats){chat->
                ChatListItem(chat, navController)
            }
        }
    }
}