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
import com.vhennus.chat.domain.Chat
import com.vhennus.chat.domain.ChatPair


@Composable
fun ChatListItem(chat:ChatPair, navController: NavController, userName:String){
    Row  (
        modifier = Modifier.fillMaxWidth().clickable(onClick = {
            navController.navigate(NavScreen.SingleChatScreen.route)
        }),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Image(
            painter = painterResource(R.drawable.dp1),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(60.dp).clip(CircleShape)
        )
        Column {
            Text(if (chat.users[0].user_name==userName) chat.users[1].user_name else chat.users[0].user_name , style = MaterialTheme.typography.titleMedium)
            Text(chat.last_message,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.widthIn(max=500.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text("17:03", style = MaterialTheme.typography.bodyMedium)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.clip(CircleShape)
                    .size(20.dp)
                    .background(MaterialTheme.colorScheme.primary)
            ){
                Text(
                    text = "3",
                    color = MaterialTheme.colorScheme.surface,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}