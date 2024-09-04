package com.amorgens.feed.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.sharp.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.amorgens.NavScreen
import com.amorgens.R.*
import com.amorgens.feed.data.FeedViewModel
import com.amorgens.feed.domain.Post
import com.amorgens.ui.GeneralScaffold


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

  val posts = feedViewModel.allPost.collectAsState()
  val feedUIState = feedViewModel.feedUIState.collectAsState()

  if(feedUIState.value.isFeedLoadingError){
    Toast.makeText(LocalContext.current, feedUIState.value.getFeedErrorMessage, Toast.LENGTH_SHORT).show()
  }
  GeneralScaffold(topBar = { feedNav() }, floatingActionButton = {
    Button(onClick = { navHostController.navigate(NavScreen.CreatePostScreen.route)})
    { Icon(imageVector = Icons.Filled.Add, contentDescription ="Add" )}
  }) {
    LazyColumn (
      modifier = Modifier.fillMaxSize()
    ) {
      items(posts.value){post->
        com.amorgens.feed.presentation.post(post = post)
      }
    }
  }
}



@Composable
fun post(post:Post){
  Row (
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ){
    // profile pic
    val painter = painterResource(id = drawable.profile)
    Image(
      painter,
      contentDescription = null,
      modifier = Modifier
        .size(50.dp)
        .clip(CircleShape)
    )
    // actual post
    Column (
      horizontalAlignment = Alignment.Start,
      verticalArrangement = Arrangement.SpaceEvenly
    ){
      Text(text = post.user_name, style=MaterialTheme.typography.titleLarge)
      Text(text = post.created_at, style=MaterialTheme.typography.bodySmall)
      Text(text = post.text, style=MaterialTheme.typography.bodyLarge)

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Button(
          onClick = {  },
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.secondary
          )
        ) {
          Icon(imageVector = Icons.Outlined.ModeComment, contentDescription = "Comment")
          Text(text = "12")
        }
        Button(onClick = {  },
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.secondary
          )
        ) {
          Icon(imageVector = Icons.Sharp.FavoriteBorder, contentDescription = "Like")
          Text(text = "300")
        }
        Button(onClick = {  },
          colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.secondary,
          )
        ) {
          Icon(imageVector = Icons.Outlined.RemoveRedEye, contentDescription = "Seen")
          Text(text = "2,399")
        }
      }
    }
  }
  Spacer(
    modifier = Modifier
      .height(0.3.dp) // Set the height of the line
      .fillMaxWidth() // Make the line fill the width of its parent
      .background(Color.Gray) // Set the color of the line
  )
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