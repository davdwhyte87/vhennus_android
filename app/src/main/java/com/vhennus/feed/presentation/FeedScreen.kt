package com.vhennus.feed.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.sharp.Favorite
import androidx.compose.material.icons.sharp.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
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
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.vhennus.NavScreen
import com.vhennus.R.*
import com.vhennus.feed.data.FeedViewModel
import com.vhennus.feed.domain.Post
import com.vhennus.general.utils.CLog
import com.vhennus.ui.GeneralScaffold
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random
import org.ocpsoft.prettytime.PrettyTime
import java.text.ParseException

@Composable
fun FeedScreen(
  navHostController: NavController,
  feedViewModel: FeedViewModel
){
  val lifecycleOwner = LocalLifecycleOwner.current
  DisposableEffect(true) {

    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        feedViewModel.getAllPosts()
        feedViewModel.getUserName()
      }
    }

    lifecycleOwner.lifecycle.addObserver(observer)
    onDispose {

    }
  }
  val conext= LocalContext.current
//  val posts = listOf(Post("mm", "doglass_g",
//    "2nd oct 2024 9pm",
//    "This is reminder that we all have a lot of things to do. Let us all take ou time to look through the texts ",
//    ""
//  ))

  val context = LocalContext.current
  val posts = feedViewModel.allPost.collectAsState()
  val feedUIState = feedViewModel.feedUIState.collectAsState()

  LaunchedEffect(feedUIState.value.isFeedLoadingError) {
    if(feedUIState.value.isFeedLoadingError){
      Toast.makeText(context, feedUIState.value.getFeedErrorMessage, Toast.LENGTH_SHORT).show()
    }
  }

  GeneralScaffold(topBar = { feedNav() }, floatingActionButton = {
    Button(onClick = { navHostController.navigate(NavScreen.CreatePostScreen.route)})
    { Icon(imageVector = Icons.Filled.Add, contentDescription ="Add" )}
  }) {
    val feedUIState = feedViewModel.feedUIState.collectAsState()
    val listState = rememberLazyListState()
    val coroutine = rememberCoroutineScope()
    val userName = feedViewModel.userName.collectAsState()
    LaunchedEffect(feedUIState.value.isScrollToFeedTop) {
      if (feedUIState.value.isScrollToFeedTop){
        if (listState.firstVisibleItemIndex > 0) {

            listState.scrollToItem(0)

        }
      }
    }

    LaunchedEffect(listState) {
      snapshotFlow { listState.firstVisibleItemIndex }
        .collect { firstVisibleItemIndex ->
          feedViewModel.updateFeedScrollToTop(false)
        }
    }
    LazyColumn (
      modifier = Modifier.fillMaxSize(),
      state = listState
    ) {
      items(posts.value.reversed()){post->
        com.vhennus.feed.presentation.post(post = remember{mutableStateOf(post)}, navHostController, userName.value, {feedViewModel.likePost(post.id)}, {
          navHostController.navigate(NavScreen.SinglePost.route+"/${post.id}")
        })
      }
    }
  }
}



@Composable
fun post(
  post: MutableState<Post>,
  navController: NavController,
  userName:String,
  onLike: ()->Unit,
  onPostClick:()->Unit
){
  Row (
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ){

    val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS 'UTC'", Locale.getDefault())
    val likes = post.value.likes.toMutableList()

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
      drawable.p1,
      drawable.p2,
      drawable.p3,
      drawable.p4,
      drawable.p5
    )
    val randomImage = images[Random.nextInt(images.size)]
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
      Text(text = post.value.text, style=MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal),
        modifier = Modifier.clickable(onClick = {
          onPostClick()
        }))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,

      ) {
        IconButton(
          onClick = {
            navController.navigate(NavScreen.CreateCommentScreen.route+"/"+post.value.id)
          },
          colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.secondary
          ),
          modifier = Modifier.padding(0.dp)
        ) {
          Row {
            Icon(imageVector = Icons.Outlined.ModeComment,
              contentDescription = "Comment",
              modifier = Modifier.size(18.dp)
            )
            Text(if (post.value.comments?.count() == 0) "" else post.value.comments?.count()?.toString() ?: "",
              style=MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(start = 3.dp)
            )
          }

        }
        IconButton(onClick = {
          onLike()

          if(post.value.likes.contains(userName)){
            val npost =post.value.copy(likes = post.value.likes - userName)
            post.value = npost
            //post.value.likes.remove(userName)
          }else{
            val npost =post.value.copy(likes = post.value.likes + userName)
            post.value = npost
            //post.value.likes.add(userName)
          }
        },
          colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.secondary
          ),
          modifier = Modifier.padding(0.dp)
        ) {
          Row {
            Icon(imageVector =if(post.value.likes.contains(userName)) Icons.Sharp.Favorite else Icons.Sharp.FavoriteBorder,
              contentDescription = "Like",
              modifier = Modifier.size(18.dp)
            )
            Text(text = if (post.value.likes.count() == 0)"" else post.value.likes.count().toString(),
              style=MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(start = 3.dp)
            )
          }

        }
//        Button(onClick = {  },
//          colors = ButtonDefaults.buttonColors(
//            containerColor = MaterialTheme.colorScheme.surface,
//            contentColor = MaterialTheme.colorScheme.secondary,
//          )
//        ) {
//          Icon(imageVector = Icons.Outlined.RemoveRedEye,
//            contentDescription = "Seen",
//            modifier = Modifier.size(16.dp)
//          )
//          Text(text = post.value.number_of_views.toString(), style=MaterialTheme.typography.bodySmall)
//        }
      }
    }
  }
//  Spacer(
//    modifier = Modifier
//      .height(0.3.dp) // Set the height of the line
//      .fillMaxWidth() // Make the line fill the width of its parent
//      .background(Color.Gray) // Set the color of the line
//  )
}

fun keyIn(context: Context, userName:String, token:String){

  val mshared = context.getSharedPreferences("user_data", Context.MODE_PRIVATE)
  val mSharedEditor = mshared.edit()
  mSharedEditor.putString("user_name", userName)
  mSharedEditor.apply()


  val masterKey = MasterKey.Builder(context)
    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
    .build()

  val sharedPreferences = EncryptedSharedPreferences.create(
    context,
    "secure_prefs",
    masterKey,
    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
  )

  val editor = sharedPreferences.edit()
  editor.putString("auth_token",
    token)
  editor.apply()
}