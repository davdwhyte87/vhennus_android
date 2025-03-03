package com.vhennus.feed.presentation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.gson.Gson
import com.vhennus.R
import com.vhennus.chat.domain.CreateChatReq
import com.vhennus.chat.presentation.validateCreateChat
import com.vhennus.feed.data.FeedViewModel
import com.vhennus.feed.domain.Comment
import com.vhennus.feed.domain.CreateCommentReq
import com.vhennus.general.presentation.LoadImageWithPlaceholder
import com.vhennus.general.utils.CLog
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.ocpsoft.prettytime.PrettyTime
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun singlePostScreen(id:String, feedViewModel: FeedViewModel, navController: NavController){
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


    val post = feedViewModel.singlePost.collectAsState()
    val comment = remember { mutableStateOf("") }
    val feedUIState = feedViewModel.feedUIState.collectAsState().value
    val context = LocalContext.current
    val pullToRefreshState = rememberPullToRefreshState()
    var isRefresh = remember { mutableStateOf(false) }
    LaunchedEffect(feedUIState.isCreateCommentError) {
        if (feedUIState.isCreateCommentError){
            Toast.makeText(context, feedUIState.createCommentErrorMessage, Toast.LENGTH_SHORT).show()
            feedViewModel.clearUIData()
        }
    }

    LaunchedEffect(feedUIState.isCreateCommentSuccess) {
        if (feedUIState.isCreateCommentSuccess){
            Toast.makeText(context, "Sent!", Toast.LENGTH_SHORT).show()
            feedViewModel.clearUIData()
        }
    }
    LaunchedEffect(feedUIState.isGetSinglePostRefresh) {
        if(feedUIState.isGetSinglePostRefresh){
            feedViewModel.getSinglePosts(id)
        }
    }

    GeneralScaffold(
        {BackTopBar("Post", navController)}, {}
    ) {
        Box (
            modifier = Modifier
                .fillMaxSize()
                .pullToRefresh(feedUIState.isGetSinglePostRefresh, pullToRefreshState, onRefresh = {
                    CLog.debug("REFRESHING", "")
                    feedViewModel.updateSinglePOstRefresh(true)
            })
        ) {
            Column (
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())
                    .padding(bottom = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ){
                // refresh indicator
                if(feedUIState.isGetSinglePostRefresh){
                    AnimatedPreloader(modifier = Modifier.size(size = 60.dp), MaterialTheme.colorScheme.primary)
                }
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ){

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
                    if(post.value.profile.image.isEmpty() || post.value.profile.image.isBlank()){

                        Image(
                            painter = painter,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(40.dp).clip(CircleShape)
                        )
                    }else{
                        LoadImageWithPlaceholder(post.value.profile.image,
                            modifier = Modifier.size(40.dp)
                                .clip(CircleShape)
                        )
                    }

                    // actual post
                    Column (
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ){
                        Text(text = post.value.user_name, style= MaterialTheme.typography.titleLarge)
                        Text(text = getPrettyDate(post.value.created_at), style= MaterialTheme.typography.bodySmall)
                        Text(text = post.value.text, style= MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal))

                        if(post.value.image.isNotEmpty()){
                            LoadImageWithPlaceholder(post.value.image,
                                modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp, max = 300.dp)
                                    .clip(RoundedCornerShape(20.dp))
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.padding(top = 10.dp, bottom = 10.dp))
                Text(text = "Comments", style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
                )
                // llist of comments
//            LazyColumn {
//                items(post.value.comments){it->
//                    comment(it)
//                }
//            }

                for (comment in post.value.comments){
                    comment(comment)
                }
            }


            Box (
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .align(Alignment.BottomCenter)
            ){
                OutlinedTextField(value = comment.value,
                    onValueChange = {
                        comment.value = it
                    },
                    shape = RoundedCornerShape(30.dp),
                    placeholder = { Text(text = "Message") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .padding(end = 1.dp),
                    trailingIcon =  {
                        IconButton(
                            onClick = {
                                feedViewModel.createComment(id, CreateCommentReq(comment.value))
                                comment.value = ""
                                // refresh feed
                                feedViewModel.getSinglePosts(id)
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.surface,
                                containerColor = MaterialTheme.colorScheme.primary
                            )

                        ) {
                            if(feedUIState.isCreateCommentButtonLoading){
                                AnimatedPreloader(modifier = Modifier.size(size = 60.dp), MaterialTheme.colorScheme.surface)
                            }else{
                                Icon(Icons.AutoMirrored.Outlined.Send, "Send")
                            }

                        }
                    }
                )
            }




        }
    }

}

@Composable
fun comment(comment:Comment){
    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'UTC'", Locale.getDefault())
    var prettyPostDate = ""
    // Parse the string into a Date object
    try {
        val parsedDate = inputFormat.parse(comment.created_at)
        val prettyTime = PrettyTime()
        prettyPostDate = prettyTime.format(parsedDate)
    } catch (e: ParseException) {
        CLog.error("PRETTY DATE ERROR", e.toString())
    }

    // ui

    Row (
        modifier = Modifier.fillMaxWidth().padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // profile pic
        val images = listOf(
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
//        Image(
//            painter,
//            contentDescription = null,
//            modifier = Modifier
//                .size(40.dp)
//                .clip(CircleShape)
//        )

        Column (
                horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceEvenly
        ){

        Text(text = comment.user_name, style= MaterialTheme.typography.titleLarge)
        Text(text = prettyPostDate, style= MaterialTheme.typography.bodySmall)
        Text(text = comment.text, style= MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal))
    }
    }

}