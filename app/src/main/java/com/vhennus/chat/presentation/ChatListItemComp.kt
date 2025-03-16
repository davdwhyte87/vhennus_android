package com.vhennus.chat.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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

    LaunchedEffect(true) {
        // get profile of chat receiver

    }
    // set receiver image
    var receiverImage = ""
    var receiverUserName =""
    if (chat.user1 == userName){
        receiverImage = chat.user2_image.toString()
        receiverUserName = chat.user2
    }else{
        receiverImage = chat.user1_image.toString()
        receiverUserName = chat.user1
    }



    // handle last messages
    var newMessage:Boolean = false
    val lastMessage = chatViewModel.getLastMessage(chat.id)
    if(lastMessage != null){
        if(chat.last_message != lastMessage){
            newMessage = true
        }
    }else{
        // this is most likely a new message from a new chat pair
        newMessage = true
    }

    // set badge flaf
    if(newMessage){
        chatViewModel.updateUnreadMessageFlag(true)
    }

    CLog.debug("NEW MESSAGE FIL", lastMessage.toString())
    // ui start
    Row  (
        modifier = Modifier.fillMaxWidth().clickable(onClick = {
            // pass in the profile of the person you want to chat with
            //chatViewModel.setSingleChatReceiverProfile(receiverProfile)
            navController.navigate(NavScreen.SingleChatScreen.route+"/${receiverUserName}")
        }),
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
            modifier = Modifier.weight(1f)
        ) {
            Text(receiverUserName, style = MaterialTheme.typography.titleMedium)
            Text(chat.last_message,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = if(newMessage) MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodyMedium,
                modifier = Modifier.widthIn(max=700.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(prettyDate2(chat.updated_at), style = MaterialTheme.typography.bodyMedium)
//            Box(
//                contentAlignment = Alignment.Center,
//                modifier = Modifier.clip(CircleShape)
//                    .size(20.dp)
//                    .background(MaterialTheme.colorScheme.primary)
//            ){
//                Text(
//                    text = "3",
//                    color = MaterialTheme.colorScheme.surface,
//                    style = MaterialTheme.typography.bodySmall,
//                    fontWeight = FontWeight.Bold
//                )
//            }
        }
    }
}