package com.vhennus.feed.presentation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.sharp.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.vhennus.NavScreen
import com.vhennus.R
import com.vhennus.feed.data.FeedViewModel
import com.vhennus.feed.domain.CreateCommentReq
import com.vhennus.feed.domain.CreatePostReq
import com.vhennus.general.utils.CLog
import com.vhennus.ui.GeneralScaffold
import org.ocpsoft.prettytime.PrettyTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random

@Composable
fun createCommentScreen(
    navController:NavController,
    feedViewModel: FeedViewModel,
    id:String
){

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(true) {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                feedViewModel.getSinglePosts(id)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            feedViewModel.clearModelData()
        }
    }

    val commentText = remember {
        mutableStateOf("")
    }



    val post = feedViewModel.singlePost.collectAsState()
    val feedUIState = feedViewModel.feedUIState.collectAsState()

    if (feedUIState.value.isCreateCommentError){
        Toast.makeText(LocalContext.current, feedUIState.value.createCommentErrorMessage, Toast.LENGTH_SHORT).show()
    }

    GeneralScaffold(
        topBar = { createCommentNav(navController, {
            feedViewModel.createComment(id, CreateCommentReq(commentText.value))
        }, feedViewModel) }, floatingActionButton = { /*TODO*/ }) {

        Column {
            Row (
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ){

                val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'UTC'", Locale.getDefault())


                var prettyPostDate = ""
                // Parse the string into a Date object
                try {
                    val parsedDate = inputFormat.parse(post.value.created_at)
                    val prettyTime = PrettyTime()
                    prettyPostDate = prettyTime.format(parsedDate)
                } catch (e: ParseException) {
                    CLog.error("PRETTY DATE ERROR", e.toString())
                }


                // Format the parsed date using PrettyTime

                // profile pic
                val images =listOf(
                    R.drawable.p1,
                    R.drawable.p2,
                    R.drawable.p3,
                    R.drawable.p4,
                    R.drawable.p5
                )
                var randomImage = images[0]
                LaunchedEffect(true) {
                    randomImage = images[Random.nextInt(images.size)]
                }

                val painter = painterResource(id = randomImage)
                Image(
                    painter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                )
                // actual post
                Column (
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.SpaceEvenly
                ){
                    Text(text = post.value.user_name, style=MaterialTheme.typography.titleLarge)
                    Text(text = prettyPostDate, style=MaterialTheme.typography.bodySmall)
                    Text(text = post.value.text, style=MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal))
                }
            }

            TextField(value = commentText.value,
                onValueChange = { commentText.value = it},
                maxLines = 20,
                modifier = Modifier.fillMaxSize(),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                placeholder = { Text(
                    text = "Post your reply ...",
                    color = Color.Gray,
                )
                }
            )
        }
    }
}