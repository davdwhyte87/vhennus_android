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
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.vhennus.auth.data.AuthViewModel
import com.vhennus.chat.data.ChatViewModel
import com.vhennus.chat.domain.Chat
import com.vhennus.chat.domain.ChatPair
import com.vhennus.chat.domain.MUser
import com.vhennus.ui.GeneralScaffold
import com.vhennus.ui.GeneralTopBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllChatsScreen(
    navController: NavController,
    chatViewModel: ChatViewModel,
    authViewModel: AuthViewModel
    ){

    val chats = listOf(
        ChatPair(
            "iow9838usose",
            listOf(MUser("Daniel", "james"))
        )
    )

    val lifecycleOwner = LocalLifecycleOwner.current
    val userName = authViewModel.userName.collectAsState().value

    DisposableEffect(true) {
        val observer = LifecycleEventObserver{_,event->
            if(event == Lifecycle.Event.ON_RESUME){
                authViewModel.getUserName()
                chatViewModel.getAllChats()
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

                ChatListItem(chat, navController, userName)
            }
        }
    }
}