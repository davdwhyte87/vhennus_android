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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vhennus.NavScreen
import com.vhennus.R
import com.vhennus.chat.data.ChatViewModel
import com.vhennus.chat.domain.Chat
import com.vhennus.chat.domain.ChatPair
import com.vhennus.general.presentation.LoadImageWithPlaceholder
import com.vhennus.general.utils.CLog
import com.vhennus.profile.data.ProfileViewModel
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
    if(userName == chat.users_ids[0]){
        receiver = chat.users_ids[1]
    }else{
        receiver = chat.users_ids[0]
    }

    if(userName == chat.users[0].user_name){
        receiverProfile = chat.users[1]
    }else{
        receiverProfile = chat.users[0]
    }

    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'UTC'", Locale.getDefault())
    val dinputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'UTC'", Locale.getDefault())
    val doutputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    var prettyPostDate = ""
    var chatDate = ""
    // Parse the string into a Date object
    try {
//        val parsedDate = inputFormat.parse(chat.created_at)
//        chatDate = dinputFormat.parse(chat.created_at)?.toString() ?: ""

        val parsedDate = dinputFormat.parse(chat.created_at)
        chatDate = parsedDate?.let { doutputFormat.format(it) } ?: ""

        val prettyTime = PrettyTime()
        prettyPostDate = prettyTime.format(parsedDate)
    } catch (e: ParseException) {
        CLog.error("PRETTY DATE ERROR", e.toString())
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

    CLog.debug("NEW MESSAGE FIL", lastMessage.toString())
    // ui start
    Row  (
        modifier = Modifier.fillMaxWidth().clickable(onClick = {
            // pass in the profile of the person you want to chat with
            chatViewModel.setSingleChatReceiverProfile(receiverProfile)
            navController.navigate(NavScreen.SingleChatScreen.route+"/${receiver}")
        }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        if(receiverProfile.image.isEmpty() || receiverProfile.image.isBlank()){
            Image(
                painter = painterResource(R.drawable.p1),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
        }else{
            LoadImageWithPlaceholder(receiverProfile.image,
                modifier = Modifier.size(40.dp)
                    .clip(CircleShape)
            )
        }
        Column (
            modifier = Modifier.weight(1f)
        ) {
            Text(if (chat.users[0].user_name==userName) chat.users[1].user_name else chat.users[0].user_name , style = MaterialTheme.typography.titleMedium)
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
            Text(chatDate, style = MaterialTheme.typography.bodyMedium)
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