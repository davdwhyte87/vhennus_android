package com.vhennus.chat.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vhennus.NavScreen
import com.vhennus.R
import com.vhennus.chat.data.ChatViewModel
import com.vhennus.chat.domain.ChatPair
import com.vhennus.feed.presentation.TruncatedText
import com.vhennus.feed.presentation.TruncatedTextChatList
import com.vhennus.feed.presentation.getPrettyDate
import com.vhennus.general.presentation.LoadImageWithPlaceholder
import com.vhennus.general.utils.CLog

import com.vhennus.general.utils.prettyDate2
import com.vhennus.profile.domain.Profile
import org.ocpsoft.prettytime.PrettyTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale


@Composable
fun ChatListItem(
    chat:ChatPair,
    navController: NavController,
    userName:String,
    chatViewModel: ChatViewModel
){
    var receiver =""
    var receiverProfile = Profile()
    val chatListUIState = chatViewModel.chatsUIState.collectAsState().value

    LaunchedEffect(true) {
        // get profile of chat receiver

    }
    // set receiver image
    var receiverImage = ""
    var receiverUserName =""
    var receiverName = ""
    if (chat.user1 == userName){
        receiverImage = chat.user2_image.toString()
        receiverUserName = chat.user2
    }else{
        receiverImage = chat.user1_image.toString()
        receiverUserName = chat.user1
    }



    // handle last messages
    var newMessage = remember{ mutableStateOf(false) }




    LaunchedEffect(true) {
        val lastMessage = chatViewModel.getLastMessage(chat.id)
        if(lastMessage != null){
            if(chat.last_message != lastMessage){
                newMessage.value = true
            }
        }else{
            // this is most likely a new message from a new chat pair
            newMessage.value = true
        }

        // set badge flaf
//        if(newMessage){
//            chatViewModel.updateUnreadMessageFlag(true)
//        }

    }
    // ui start
    Row  (
        modifier = Modifier.fillMaxWidth().clickable(onClick = {
            // pass in the profile of the person you want to chat with
            //chatViewModel.setSingleChatReceiverProfile(receiverProfile)
            navController.navigate(NavScreen.SingleChatScreen.route+"/${receiverUserName}")
        }).padding(top = 8.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if(receiverImage.isEmpty() || receiverImage.isBlank()){
            Image(
                painter = painterResource(R.drawable.p1),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
        }else{
            LoadImageWithPlaceholder(receiverImage,
                modifier = Modifier.size(40.dp)
                    .clip(CircleShape)
            )
        }
        Column (
            modifier = Modifier.weight(1f).fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row {
//                Text(receiverUserName, style = MaterialTheme.typography.titleSmall)
                Text(receiverUserName, style = MaterialTheme.typography.titleSmall)
            }

//            Text(chat.last_message,
//                maxLines = 1,
//                overflow = TextOverflow.Ellipsis,
//                style = if(newMessage) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodyMedium,
//                modifier = Modifier.widthIn(max=700.dp)
//            )
            TruncatedTextChatList(chat.last_message,  Modifier,  if(newMessage.value) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodyMedium )
        }
//        Spacer(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(getPrettyDate(chat.updated_at), style = MaterialTheme.typography.bodyMedium)
            if(newMessage.value){
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.clip(CircleShape)
                        .size(8.dp)
                        .background(MaterialTheme.colorScheme.primary)
                ){

                }
            }

        }
    }
}