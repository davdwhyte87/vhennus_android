package com.vhennus.profile.presentation

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.automirrored.outlined.Message
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.Message
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.vhennus.NavScreen
import com.vhennus.R
import com.vhennus.feed.data.FeedViewModel
import com.vhennus.feed.presentation.SinglePost
import com.vhennus.general.presentation.LoadImageWithPlaceholder
import com.vhennus.general.utils.CLog
import com.vhennus.profile.data.ProfileViewModel
import com.vhennus.profile.domain.SendFriendRequest
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.theme.Gray2
import com.vhennus.ui.theme.Surf
import kotlinx.coroutines.flow.MutableStateFlow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtherUserProfile(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    feedViewModel: FeedViewModel,
    userName:String
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(true) {
        val observer = LifecycleEventObserver{_,event->
            if(event == Lifecycle.Event.ON_RESUME){
                // update the users notifiy firebase token in the background
                profileViewModel.updateNotificationToken()
                profileViewModel.getUserProfile(userName)
                feedViewModel.getAllUserPosts(userName)
                feedViewModel.getLikedPost()
                feedViewModel.getUserName()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {

        }
    }
    val isMassageEnabled = remember { mutableStateOf(true) }
    val isSendRequestEnabled = remember { mutableStateOf(true) }

    val imagePainter = painterResource(id = R.drawable.p2) // Replace with your image
    val scrollState = rememberScrollState()
    val userProfile = profileViewModel.otherUserProfile.collectAsState().value
    val profileUiState = profileViewModel.profileUIState.collectAsState().value
    val pullToRefreshState = rememberPullToRefreshState()
    val otherUserPosts = feedViewModel.allOtherUserPost.collectAsState().value
    val likedPosts = feedViewModel.likedPosts.collectAsState()
    val listState = rememberLazyListState()
    val myUserName = feedViewModel.userName.collectAsState()
    val profileUIState = profileViewModel.profileUIState.collectAsState().value
    val context = LocalContext.current


    LaunchedEffect(userProfile) {

        userProfile.friends.map {
            r->
            if (r.user_name == myUserName.value) {
                isMassageEnabled.value = true
                isSendRequestEnabled.value = false

            }else{
                isMassageEnabled.value = false
                isSendRequestEnabled.value = true
            }
        }
        if(userProfile.friends.isEmpty()){
            isMassageEnabled.value = false
        }
    }

    LaunchedEffect(profileUIState.isSendFriendRequestError) {
        // show error message if search fails
        if(profileUIState.isSendFriendRequestError){
            Toast.makeText(context, profileUIState.sendFriendRequestError, Toast.LENGTH_SHORT).show()
            profileViewModel.resetUIState()
        }
    }

    LaunchedEffect(profileUIState.isSendFriendRequestSuccess) {
        // show error message if search fails
        if(profileUIState.isSendFriendRequestSuccess){
            Toast.makeText(context, "Sent!", Toast.LENGTH_SHORT).show()
            profileViewModel.resetUIState()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.pullToRefresh(profileUiState.isGetProfileLoading, pullToRefreshState, onRefresh = {
            CLog.debug("REFRESHING", "")
            profileViewModel.getMyProfile()
        })
    ) {
        if(profileUiState.isGetProfileLoading){
            AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.primary)
        }
        Box(modifier = Modifier.fillMaxSize().verticalScroll(scrollState),

            ) {

            // Surface with Background Image
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(LocalConfiguration.current.screenHeightDp.dp *0.4f), // 40% Height
                color = Gray2
            ) {


                if(userProfile.profile.image.isNotEmpty()  || userProfile.profile.image.isNotBlank()){
                    LoadImageWithPlaceholder(
                        userProfile.profile.image.toString(),
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

//            Box(
//                modifier = Modifier
//                    .fillMaxWidth()
//                    .height(LocalConfiguration.current.screenHeightDp.dp * 0.4f) // Ensure it aligns inside the image
//            ) {
//                AppRoundIconButton(
//                    modifier = Modifier
//                        .align(Alignment.BottomEnd)
//                        .padding(16.dp), // Adds some space from the edge
//                    icon = Icons.Default.CameraAlt,
//                    desc = "Edit profile image",
//                    {}
//                )
//            }

            // Top Navigation Bar (Transparent)
        TopAppBar(
            title = { /* Empty for cleaner UI */ },
            navigationIcon = {
                IconButton(onClick = { navController.navigate(NavScreen.HomeScreen.route)  }) {
                    CircularIconButton({navController.popBackStack() })
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent, // Transparent background
                navigationIconContentColor = Color.White
            ),
            modifier = Modifier.background(Color.Transparent)
        )


            // Content Below Image
            Surface(
                modifier = Modifier
                    .fillMaxSize() // Make the surface fill the column width
                    .padding(top =LocalConfiguration.current.screenHeightDp.dp *0.4f -20.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                color = Color.White,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier.padding(start = 0.dp, end=0.dp)
                        .fillMaxSize()
                    ,

                    ){
                    Column (
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column (
                            modifier = Modifier.padding(start = 16.dp, end=16.dp)
                            .fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ){
                            Row(
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.padding(top = 32.dp).fillMaxWidth()
                            ) {
                                Column {
                                    Text(userProfile.profile.name, style = MaterialTheme.typography.titleMedium)
                                    Text("@${userProfile.profile.user_name}", style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Normal,
                                        modifier = Modifier.alpha(0.6f).padding(top=4.dp)
                                    )
                                }
                            }

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Button(
                                    onClick = {
                                        profileViewModel.sendFriendRequest(SendFriendRequest(user_name = userProfile.profile.user_name))
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor =Color.White ,

                                        ),
                                    enabled = isSendRequestEnabled.value,
                                    modifier = Modifier.width(182.dp)

                                ) {
                                    if(profileUIState.isSendFriendRequestLoading){
                                        AnimatedPreloader(modifier = Modifier.size(size = 30.dp), MaterialTheme.colorScheme.surface)
                                    }else {
                                        Text("Send Request", style = MaterialTheme.typography.titleSmall)
                                    }

                                }

                                Button(
                                    onClick = {navController.navigate(NavScreen.SingleChatScreen.route+"/${userName}")},
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = MaterialTheme.colorScheme.secondary,

                                        ),
                                    enabled = isMassageEnabled.value,
                                    border = BorderStroke(2.dp, Gray2)
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Message", style = MaterialTheme.typography.titleSmall)
                                        Icon(Icons.AutoMirrored.Outlined.Message, "Message", Modifier.size(16.dp))
                                    }
                                }
                            }

                            if(userProfile.profile.bio.isEmpty() || userProfile.profile.bio.isBlank()){
                                Text(
                                    "About me .....",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.alpha(0.6f).fillMaxWidth()
                                )
                            }else{
                                Text(
                                    userProfile.profile.bio,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.alpha(0.6f).fillMaxWidth()
                                )
                            }


                            Row (
                                modifier = Modifier
                                    .size(width = 80.dp, 30.dp).clickable(onClick = {
                                        //navController.navigate(NavScreen.MyFriendsPage.route)
                                    })
                            ) {
                                Text(userProfile.friends.size.toString(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold,)
                                Text("Friends", style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.padding(start = 8.dp).alpha(0.6f)
                                )
                            }

                        }

                        Row (
                            modifier = Modifier.fillMaxWidth().background(Surf).height(40.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text("Posts", style = MaterialTheme.typography.titleMedium)
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            for(post in otherUserPosts){
                                SinglePost(
                                    post,
                                    navController,
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
                                        navController.navigate(NavScreen.SinglePost.route+"/${post.id}")
                                    },
                                    likedPosts = likedPosts.value
                                )
                            }
                        }



                    }


                }


            }
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(top =LocalConfiguration.current.screenHeightDp.dp *0.4f) // Push content below image
//                .background(Color.Blue)
//        ) {
//            Text(
//                text = "Hello, User!",
//                fontSize = 24.sp,
//                modifier = Modifier.padding(16.dp)
//            )
//            Text(
//                text = "Hello, User!",
//                fontSize = 24.sp,
//                modifier = Modifier.padding(16.dp)
//            )
//            Text(
//                text = "Hello, User!",
//                fontSize = 24.sp,
//                modifier = Modifier.padding(16.dp)
//            )
//            Text(
//                text = "Hello, User!",
//                fontSize = 24.sp,
//                modifier = Modifier.padding(16.dp)
//            )
//
//        }
        }
    }

}
