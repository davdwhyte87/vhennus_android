package com.vhennus.feed.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ModeComment
import androidx.compose.material.icons.outlined.RemoveRedEye
import androidx.compose.material.icons.sharp.Favorite
import androidx.compose.material.icons.sharp.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
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
import com.vhennus.feed.domain.PostFeed
import com.vhennus.general.presentation.LoadImageWithPlaceholder
import com.vhennus.general.utils.CLog
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.GeneralScaffold
import com.vhennus.ui.theme.Gray
import com.vhennus.ui.theme.Gray3
import com.vhennus.ui.theme.Green
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.random.Random
import org.ocpsoft.prettytime.PrettyTime
import sh.calvin.autolinktext.rememberAutoLinkText
import java.text.ParseException
import java.util.regex.Pattern

@Composable
fun FeedScreen(
  navHostController: NavController,
  feedViewModel: FeedViewModel
){
  val lifecycleOwner = LocalLifecycleOwner.current
  DisposableEffect(true) {

    val observer = LifecycleEventObserver { _, event ->
      if (event == Lifecycle.Event.ON_RESUME) {
        feedViewModel.getAllPosts(false)
        feedViewModel.getUserName()
        feedViewModel.getLikedPost()
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
  val likedPosts = feedViewModel.likedPosts.collectAsState()
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

  LaunchedEffect(feedUIState.value.isFeedLoadingError) {
    if(feedUIState.value.isFeedLoadingError){
      Toast.makeText(context, feedUIState.value.getFeedErrorMessage, Toast.LENGTH_SHORT).show()
    }
  }
  LaunchedEffect(listState) {
    snapshotFlow { listState.firstVisibleItemIndex }
      .collect { firstVisibleItemIndex ->
        feedViewModel.updateFeedScrollToTop(false)
      }
  }

  FeedScaffold(topBar = { feedNav() }, floatingActionButton = {
    Button(onClick = { navHostController.navigate(NavScreen.CreatePostScreen.route)})
    { Icon(imageVector = Icons.Filled.Add, contentDescription ="Add" )}
  }) {

    Column(
      verticalArrangement = Arrangement.Center,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      if(feedUIState.value.isFeedLoading){
        AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.primary)
      }
      LazyColumn (
        modifier = Modifier.fillMaxSize(),
        state = listState
      ) {
        items(posts.value){post->
          SinglePost(
            post,
            navHostController,
            userName.toString(),
            onLike = {
              feedViewModel.likePost(post.id)
              // remove post locally if it is already in db
              if(likedPosts.value.contains(post.id)){
                feedViewModel.removeLikeLocal(post.id)
              }else{
                feedViewModel.likePostLocal(post.id)
              }

              feedViewModel.getLikedPost()
            },
            onPostClick = {
              CLog.debug("POST CLICKED", "YES")
              navHostController.navigate(NavScreen.SinglePost.route+"/${post.id}")
            },
            likedPosts = likedPosts.value,
            isSingleScreen = false
          )
//          com.vhennus.feed.presentation.post(post = remember{mutableStateOf(post)}, navHostController, userName.value, {feedViewModel.likePost(post.id)}, {
//            navHostController.navigate(NavScreen.SinglePost.route+"/${post.id}")
//          })
        }
      }
    }
  }
}

fun getPrettyDate(date: String): String{
  val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.getDefault())
  if(date.isEmpty() || date.isBlank()){
    return ""
  }

  var prettyPostDate = ""
  // Parse the string into a Date object
  try {
    val parsedDate = inputFormat.parse(date)
    val prettyTime = PrettyTime()
    prettyPostDate = prettyTime.format(parsedDate)
  } catch (e: ParseException) {
    CLog.error("PRETTY DATE ERROR", e.toString())
  }

  return prettyPostDate
}

@Composable
fun post(
  post: MutableState<PostFeed>,
  navController: NavController,
  userName:String,
  onLike: ()->Unit,
  onPostClick:()->Unit,
  likedPosts: List<String> = emptyList<String>()
){
  Row (
    modifier = Modifier.fillMaxWidth(),
    horizontalArrangement = Arrangement.spacedBy(10.dp)
  ){




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
    if(post.value.profile_image .isEmpty() || post.value.profile_image.isBlank()){
      Image(
        painter = painter,
        contentDescription = "",
        contentScale = ContentScale.Crop,
        modifier = Modifier.size(40.dp).clip(CircleShape)
      )
    }else{
      LoadImageWithPlaceholder(post.value.profile_image,
        modifier = Modifier.size(40.dp)
          .clip(CircleShape)
      )
    }

    // actual post
    Column (
      horizontalAlignment = Alignment.Start,
      verticalArrangement = Arrangement.SpaceEvenly,
      modifier = Modifier
    ){
      Text(text = post.value.user_name, style=MaterialTheme.typography.titleLarge)
      Text(text = getPrettyDate(post.value.created_at), style=MaterialTheme.typography.bodySmall)
      Text(text = post.value.text, style=MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Normal),
        modifier = Modifier.clickable(onClick = {
          onPostClick()
        }))

      if(post.value.image.isNotEmpty()){
        LoadImageWithPlaceholder(post.value.image,
          modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp, max = 300.dp)
            .clip(RoundedCornerShape(20.dp))
        )
      }

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,

      ) {
        IconButton(
          onClick = {
            //navController.navigate(NavScreen.CreateCommentScreen.route+"/"+post.value.id)
            navController.navigate(NavScreen.SinglePost.route+"/${post.value.id}")
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
            Text(if (post.value.comment_count == 0) "" else post.value.comment_count.toString(),
              style=MaterialTheme.typography.bodySmall,
              modifier = Modifier.padding(start = 3.dp)
            )
          }

        }

        IconButton(onClick = {
          onLike()
        },
          colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.secondary
          ),
          modifier = Modifier.padding(0.dp)
        ) {
          Row {
            Icon(imageVector =if(likedPosts.contains(post.value.id)) Icons.Sharp.Favorite else Icons.Sharp.FavoriteBorder,
              contentDescription = "Like",
              modifier = Modifier.size(18.dp)
            )
            Text(text = if (post.value.like_count == 0) "" else post.value.like_count.toString(),
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



@Composable
fun SinglePost(
  post: PostFeed,
  navController: NavController,
  userName:String,
  onLike: ()->Unit,
  onPostClick:()->Unit,
  likedPosts: List<String>,
  isSingleScreen: Boolean
){

  Column {
    // col 1 -- profile pic and name
    Row (
      modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp)
      ,
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ){
      Box(modifier = Modifier.clickable(onClick = {
        navController.navigate(NavScreen.OtherUserProfileScreen.route+"/${post.user_name}"){
          popUpTo(NavScreen.OtherUserProfileScreen.route+"/${post.user_name}"){inclusive=true}
        }
      })){
        if (post.profile_image.isEmpty() || post.profile_image.isBlank()){
          Image(
            painter = painterResource(drawable.p5),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(40.dp).clip(CircleShape)
          )
        }else{
          LoadImageWithPlaceholder(post.profile_image,
            modifier = Modifier.size(40.dp)
              .clip(CircleShape)
          )
        }
      }

      Column {
        Row(
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          Text(post.name, style = MaterialTheme.typography.titleSmall)
          Text("@${post.user_name}", style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.alpha(0.6f))
        }
        Text(getPrettyDate(post.created_at), style = MaterialTheme.typography.bodyMedium)
      }

    }



    // col2  -- text
    Row(
      modifier = Modifier.fillMaxWidth().padding(start = 16.dp,
        end = 16.dp, top = 8.dp, bottom = 16.dp
      ).clickable(onClick = {
        onPostClick()
      })
    ) {
     // clickable text

      //ClickableTextWithLinks(text)
      if (isSingleScreen){
        val text = post.text
        Text(text=AnnotatedString.rememberAutoLinkText(text))
      }else{
        val text = truncateTextToWords(post.text, 100)
        Text(text=AnnotatedString.rememberAutoLinkText(text))
      }

     //TruncatedText(post.text)
    }


    if(post.image.isNotEmpty() || post.image.isNotBlank()){
      LoadImageWithPlaceholder(post.image,
        modifier = Modifier.fillMaxWidth()
      )
    }


    Row (
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(16.dp),
      modifier = Modifier.padding(16.dp)
    ){
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        IconButton(
          onClick = {onLike()},
          modifier = Modifier.size(24.dp)
        ) {
          if(likedPosts.contains(post.id)){
            Icon(Icons.Outlined.Favorite, "Like", modifier = Modifier.size(20.dp),
              tint = MaterialTheme.colorScheme.primary
            )
          }else{
            Icon(Icons.Outlined.FavoriteBorder, "Like", modifier = Modifier.size(20.dp) )
          }

        }
        Text(if (post.like_count<1)"" else post.like_count.toString(), style= MaterialTheme.typography.labelLarge)
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        IconButton(
          onClick = {},
          modifier = Modifier.size(24.dp)
        ) {
          Icon(Icons.Outlined.ModeComment, "Comment", modifier = Modifier.size(20.dp) )
        }
        Text(if (post.comment_count <1){""} else post.comment_count.toString(), style= MaterialTheme.typography.labelLarge)
      }
    }
    HorizontalDivider(
      color = Gray,
      thickness = 2.dp
    )
  }
}
fun truncateTextToWords(text: String, wordLimit: Int): String {
  val words = text.split("\\s+".toRegex())  // Split text into words
  return if (words.size > wordLimit) {
    words.take(wordLimit).joinToString(" ") + " ... see more"
  } else {
    text
  }
}

@Composable
fun ClickableTextWithLinks(text: String, modifier: Modifier = Modifier) {
  val context = LocalContext.current

  val annotatedText = buildAnnotatedString {
    val urlPattern =Pattern.compile("(https?://[\\w./?=&-]+)")
    val matcher = urlPattern.matcher(text)
    var lastIndex = 0

    while (matcher.find()) {
      val start = matcher.start()
      val end = matcher.end()

      // Append normal text before the link
      append(text.substring(lastIndex, start))

      // Add clickable link styling
      val url = text.substring(start, end)
      pushStringAnnotation(tag = "URL", annotation = url)
      withStyle(
        style = SpanStyle(
          color = Color.Blue,
          textDecoration = TextDecoration.Underline
        )
      ) {
        append(url)
      }
      pop()

      lastIndex = end
    }

    // Append remaining text after last link
    if (lastIndex < text.length) {
      append(text.substring(lastIndex))
    }
  }

  Text(
    text = annotatedText,
    modifier = modifier
      .padding(16.dp)
      .clickable {
        annotatedText.getStringAnnotations(tag = "URL", start = 0, end = annotatedText.length)
          .firstOrNull()?.let { annotation ->
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(annotation.item))
            context.startActivity(intent)
          }
      },
    fontSize = 16.sp
  )
}

@Composable
fun TruncatedText(postText: String) {
  val wordLimit = 100
  val words = postText.split(" ")
  val truncatedText = if (words.size > wordLimit) {
    words.take(wordLimit).joinToString(" ") + " ... see more"
  } else {
    postText
  }

  Text(text = truncatedText)
}

@Composable
fun TruncatedTextChatList(
  postText: String,
  modifier: Modifier = Modifier,
  style: TextStyle=TextStyle.Default) {

  val wordLimit = 6
  val words = postText.split(" ")
  val truncatedText = if (words.size > wordLimit) {
    words.take(wordLimit).joinToString(" ") + "..."
  } else {
    postText
  }
  Text(text = truncatedText, modifier,style= style, maxLines = 1,overflow = TextOverflow.Ellipsis)
}

@Composable
fun ExpandableText(postText: String) {
  val wordLimit = 30
  val words = postText.split(" ")
  var expanded = remember { mutableStateOf(false) }

  val displayText = if (!expanded.value && words.size > wordLimit) {
    words.take(wordLimit).joinToString(" ") + " ... see more"
  } else {
    postText
  }

  val annotatedText = buildAnnotatedString {
    append(displayText)
    if (!expanded.value && words.size > wordLimit) {
      val seeMoreStart = displayText.indexOf("see more")
      addStyle(
        style = SpanStyle( color = Gray3, fontWeight = FontWeight.Bold),
        start = seeMoreStart,
        end = seeMoreStart + "see more".length,

      )
      addStringAnnotation(
        tag = "SEE_MORE",
        annotation = "see_more",
        start = seeMoreStart,
        end = seeMoreStart + "see more".length
      )
    }
  }

  ClickableText(
    text = annotatedText,
    style = TextStyle(fontSize = 16.sp),
    onClick = { offset ->
      annotatedText.getStringAnnotations("SEE_MORE", offset, offset).firstOrNull()?.let {
        expanded.value = true
      }
    }
  )
}

