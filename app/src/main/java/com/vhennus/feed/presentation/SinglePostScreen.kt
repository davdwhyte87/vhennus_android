package com.vhennus.feed.presentation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.google.gson.Gson
import com.vhennus.NavScreen
import com.vhennus.R
import com.vhennus.chat.domain.CreateChatReq
import com.vhennus.chat.presentation.validateCreateChat
import com.vhennus.feed.data.FeedViewModel
import com.vhennus.feed.domain.Comment
import com.vhennus.feed.domain.CreateCommentReq
import com.vhennus.general.presentation.LoadImageWithPlaceholder
import com.vhennus.general.utils.CLog
import com.vhennus.general.utils.prettyDate2
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold
import com.vhennus.ui.theme.Gray
import com.vhennus.ui.theme.Surf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.ocpsoft.prettytime.PrettyTime
import java.nio.file.WatchEvent
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.random.Random


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun singlePostScreen(id:String, feedViewModel: FeedViewModel, navController: NavController) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(true) {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                feedViewModel.getSinglePosts(id)
                feedViewModel.getUserName()
                feedViewModel.getLikedPost()
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
    val userName = feedViewModel.userName.collectAsState().value
    var isRefresh = remember { mutableStateOf(false) }
    val likedPosts = feedViewModel.likedPosts.collectAsState()
    val scrollState = rememberScrollState()



    LaunchedEffect(feedUIState.isCreateCommentError) {
        if (feedUIState.isCreateCommentError) {
            Toast.makeText(context, feedUIState.createCommentErrorMessage, Toast.LENGTH_SHORT)
                .show()
            feedViewModel.clearUIData()
        }
    }

    LaunchedEffect(feedUIState.isCreateCommentSuccess) {
        if (feedUIState.isCreateCommentSuccess) {
            Toast.makeText(context, "Sent!", Toast.LENGTH_SHORT).show()
            feedViewModel.clearUIData()
        }
    }
    LaunchedEffect(feedUIState.isGetSinglePostRefresh) {
        if (feedUIState.isGetSinglePostRefresh) {
            feedViewModel.getSinglePosts(id)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .pullToRefresh(feedUIState.isGetSinglePostRefresh, pullToRefreshState, onRefresh = {
                CLog.debug("REFRESHING", "")
                feedViewModel.updateSinglePOstRefresh(true)
            })

        ,
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BackTopBar("Post", navController)

        if(feedUIState.isGetSinglePostRefresh){
            AnimatedPreloader(modifier = Modifier.size(size = 60.dp), MaterialTheme.colorScheme.primary)
        }

        Column(
            modifier= Modifier.weight(1f)
                .verticalScroll(scrollState)
        ) {
            SinglePost(
                post.value.post,
                navController,
                userName.toString(),
                onLike = {
                    feedViewModel.likePost(post.value.post.id)
                    // remove post locally if it is already in db
                    if(likedPosts.value.contains(post.value.post.id)){
                        feedViewModel.removeLikeLocal(post.value.post.id)
                    }else{
                        feedViewModel.likePostLocal(post.value.post.id)
                    }

                    feedViewModel.getLikedPost()
                },
                onPostClick = {
                    navController.navigate(NavScreen.SinglePost.route+"/${post.value.post.id}"){
                        popUpTo(NavScreen.SinglePost.route+"/${post.value.post.id}"){inclusive=true}
                    }
                },
                likedPosts = likedPosts.value
            )

            Row (
                modifier = Modifier.fillMaxWidth().background(Surf).height(40.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text("Comments", style = MaterialTheme.typography.titleMedium)
            }

            for (comment in post.value.comments){
                comment(comment, navController)
            }
        }



        Row (
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface)
                .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)

        ){
            OutlinedTextField(value = comment.value,
                onValueChange = {
                    comment.value = it
                },
                shape = RoundedCornerShape(30.dp),
                placeholder = { Text(text = "Message") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(end = 1.dp)
                    .heightIn(min = 52.dp)
                ,
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Send // Shows "Send" button on keyboard
                ),
                singleLine = false,
                keyboardActions = KeyboardActions(
                    onSend = {
                        feedViewModel.createComment(id, CreateCommentReq(comment.value))
                        comment.value = ""
                        // refresh feed
                        feedViewModel.getSinglePosts(id)
                    }
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
                    focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.tertiary
                )

            )
        }

    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun singlePostScreenxxx(id:String, feedViewModel: FeedViewModel, navController: NavController){
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
                    if(post.value.post.profile_image.isEmpty() || post.value.post.profile_image.isBlank()){

                        Image(
                            painter = painter,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.size(40.dp).clip(CircleShape)
                        )
                    }else{
                        LoadImageWithPlaceholder(post.value.post.profile_image,
                            modifier = Modifier.size(40.dp)
                                .clip(CircleShape)
                        )
                    }

                    // actual post
                    Column (
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ){
                        Text(text = post.value.post.user_name, style= MaterialTheme.typography.titleLarge)
                        Text(text = getPrettyDate(post.value.post.created_at), style= MaterialTheme.typography.bodySmall)
                        Text(text = post.value.post.text, style= MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal))

                        if(post.value.post.image.isNotEmpty()){
                            LoadImageWithPlaceholder(post.value.post.image,
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
                    comment(comment, navController)
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
fun comment(comment:Comment, navController: NavController){

    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(16.dp)
    ){
        Row {
            Text(text ="@"+comment.user_name, style= MaterialTheme.typography.titleSmall,
                modifier = Modifier.clickable(onClick = {
                    navController.navigate(NavScreen.OtherUserProfileScreen.route+"/${comment.user_name}"){
                        popUpTo(NavScreen.OtherUserProfileScreen.route+"/${comment.user_name}"){inclusive=true}
                    }
                })
            )
        }
        Text(text = getPrettyDate(comment.created_at), style= MaterialTheme.typography.bodySmall)
        Text(text = comment.text, style= MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Normal))

        HorizontalDivider(
            color = Gray,
            thickness = 2.dp
        )
    }

}