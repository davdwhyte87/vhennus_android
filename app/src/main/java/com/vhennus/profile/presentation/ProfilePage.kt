package com.vhennus.profile.presentation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ArrowRight
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.FormatListNumbered
import androidx.compose.material.icons.outlined.PersonAddAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.vhennus.NavScreen
import com.vhennus.R
import com.vhennus.feed.data.FeedViewModel
import com.vhennus.feed.domain.Post
import com.vhennus.feed.presentation.post
import com.vhennus.general.presentation.LoadImageWithPlaceholder
import com.vhennus.profile.data.ProfileViewModel
import com.vhennus.trivia.presentation.shimmerEffect
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold
import com.vhennus.ui.GeneralTopBar
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.pullToRefresh
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.vhennus.feed.presentation.SinglePost
import com.vhennus.general.utils.CLog
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.theme.Blue_Gray
import com.vhennus.ui.theme.Gray2
import com.vhennus.ui.theme.Red
import com.vhennus.ui.theme.Surf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun profilePagexxx(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    feedViewModel: FeedViewModel
){

    val lifecycleOwner = LocalLifecycleOwner.current
    val posts = feedViewModel.allMyPost.collectAsState().value
    val listState = rememberLazyListState()

    //effects
    DisposableEffect(true) {
        val observer = LifecycleEventObserver{_,event->
            if(event == Lifecycle.Event.ON_RESUME){
//                profileViewModel.getMyProfile()
//                feedViewModel.getAllMyPosts()

                // update the users notifiy firebase token in the background
                profileViewModel.updateNotificationToken()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {

        }
    }
    val post = remember {
        mutableStateOf(
           Post(
               user_name = "jay_rome",
               text = "Kobe’s passing is really sticking w/ me in a way I didn’t expect.\n" +
                       "He was an icon, the kind of person who wouldn’t die this way. My wife compared it to Princess Di’s accident.",
           )
        )
    }
    val myProfile = profileViewModel.myProfile.collectAsState().value
    val profileUiState = profileViewModel.profileUIState.collectAsState().value

    GeneralScaffold(
        topBar = {GeneralTopBar()},
        floatingActionButton = {}
    ) {
        Column (
            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // display default image if no ptofile pic exists
            if(myProfile.profile.image.isEmpty() == true || myProfile.profile.image.isBlank() == true){
                Image(
                    painter = painterResource(R.drawable.p1),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(60.dp).clip(CircleShape)
                )
            }else{
                LoadImageWithPlaceholder(
                    myProfile.profile.image.toString(),
                    modifier = Modifier.size(60.dp)
                        .clip(CircleShape)
                )
            }

            if(!myProfile.profile.name.isEmpty() || !myProfile.profile.name.isBlank()){
                Text(myProfile.profile.name, style = MaterialTheme.typography.titleMedium)
            }

            // username
            if(profileUiState.isGetProfileLoading && myProfile.profile.user_name.isBlank()){
                Box(modifier = Modifier.size(height = 20.dp, width = 100.dp).shimmerEffect())
            }else{
                Text("@"+myProfile.profile.user_name, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(20.dp))

            if(profileUiState.isGetProfileLoading && myProfile.profile.bio.isBlank()){
                Box(modifier = Modifier.size(height = 40.dp, width = 600.dp).shimmerEffect())
            }else{
                if(!myProfile.profile.bio.isEmpty() || !myProfile.profile.bio.isBlank()){
                    Text(myProfile.profile.bio, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(20.dp))

                }else{
                    Text("About me ....", style = MaterialTheme.typography.bodyMedium)

                }
            }

            Spacer(modifier = Modifier.height(20.dp))
            Row (
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ){
                Button(
                    onClick = {
                        navController.navigate(NavScreen.FriendRequestPage.route)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.surface
                    ),
                ) {
                    Text("Requests", style = MaterialTheme.typography.titleMedium)
                }

                Button(
                    onClick = {
                        navController.navigate(NavScreen.EditProfilePage.route)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.surface
                    ),
                ) {
                    Text("Edit", style = MaterialTheme.typography.titleMedium)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            // friends button
            Button(
                onClick = {
                    //
                    navController.navigate(NavScreen.MyFriendsPage.route)
                },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.surface
                ),
            ) {
                Row (
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text("Friends", style = MaterialTheme.typography.titleMedium)
                    Icon(Icons.Filled.ChevronRight, "")
                }

            }
            Spacer(modifier = Modifier.height(20.dp))
            Text("My Posts", style = MaterialTheme.typography.titleLarge)

            if(profileUiState.isGetProfileLoading && post.value.id.isBlank()){
                Column (
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(modifier = Modifier.size(height = 20.dp, width = 300.dp).shimmerEffect())
                    Box(modifier = Modifier.size(height = 300.dp, width = 600.dp).shimmerEffect())
                }

            }else{

                for(post in posts.reversed()){
                    post(
                        remember { mutableStateOf(post) },
                        navController,
                        myProfile.profile.user_name,
                        onLike = {},
                        onPostClick = {
                            navController.navigate(NavScreen.SinglePost.route+"/${post.id}")
                        },
                    )
                }

//                LazyColumn(
//                    state = listState,
//                    modifier = Modifier.fillMaxSize()
//                ) {
//                    items(posts){post->
//
//                    }
//                }

            }

        }
    }

}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun profilePagexx(navController: NavController,
                        profileViewModel: ProfileViewModel,
                        feedViewModel: FeedViewModel) {
    val imagePainter = painterResource(id = R.drawable.dp1) // Replace with your image

    Box(modifier = Modifier.fillMaxSize()) {
        // Surface with Background Image
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(LocalConfiguration.current.screenHeightDp.dp *0.4f), // 40% Height
        ) {
            Image(
                painter = imagePainter,
                contentDescription = "Background Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        }
        AppRoundIconButton(
            modifier = Modifier.align(Alignment.BottomEnd),
            icon = Icons.Default.CameraAlt,
            desc = "Edit profile image",
            {}
        )

        // Top Navigation Bar (Transparent)
        TopAppBar(
            title = { /* Empty for cleaner UI */ },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                   CircularIconButton({ navController.popBackStack()})
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent, // Transparent background
                navigationIconContentColor = Color.White
            ),
            modifier = Modifier.background(Color.Transparent)
        )

        // Content Below Image
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(LocalConfiguration.current.screenHeightDp.dp *0.4f) // Push content below image
                .background(Color.White)
        ) {
            Text(
                text = "Hello, User!",
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Hello, User!",
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Hello, User!",
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )
            Text(
                text = "Hello, User!",
                fontSize = 24.sp,
                modifier = Modifier.padding(16.dp)
            )

        }
    }
}

@Composable
fun CircularIconButton(onClick: () -> Unit) {
    Button(
        onClick = { onClick()},
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Gray), // Change background color
        contentPadding = PaddingValues(12.dp), // Adjust padding
        elevation = ButtonDefaults.elevatedButtonElevation(4.dp),
    ) {
        Icon(
            imageVector = Icons.Filled.ArrowBackIosNew,
            contentDescription = "Back",
            tint = Color.White // Make the icon white
        )
    }
}


@Composable
fun AppRoundIconButton(
    modifier: Modifier,
    icon: ImageVector,
    desc:String,
    onClick: ()->Unit
){


    IconButton(
        onClick = {onClick()},
        modifier = modifier
            // Align to bottom-right
            .offset(x = (-16).dp, y = (-16).dp) // Adjust positioning
            .size(48.dp)
            .clip(CircleShape)
            .background(Color.Black.copy(alpha = 0.6f)) // Semi-transparent background

    ) {
        Icon(
            imageVector = icon,
            contentDescription = desc,
            tint = Color.White
        )
    }
}

//@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun profilePage(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    feedViewModel: FeedViewModel,
    showBackButton: Boolean = false
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(true) {
        val observer = LifecycleEventObserver{_,event->
            if(event == Lifecycle.Event.ON_RESUME){
                // update the users notifiy firebase token in the background
                profileViewModel.updateNotificationToken()
                feedViewModel.getLikedPost()
                feedViewModel.getUserName()
                feedViewModel.getAllMyPosts()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {

        }
    }

    val imagePainter = painterResource(id = R.drawable.p2) // Replace with your image
    val scrollState = rememberScrollState()
    val myProfile = profileViewModel.myProfile.collectAsState().value
    val profileUiState = profileViewModel.profileUIState.collectAsState().value
    val pullToRefreshState = rememberPullToRefreshState()
    val posts = feedViewModel.allMyPost.collectAsState().value
    val likedPosts = feedViewModel.likedPosts.collectAsState()
    val userName = feedViewModel.userName.collectAsState().value

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


                if(myProfile.profile.image.isNotEmpty()  || myProfile.profile.image.isNotBlank()){
                    LoadImageWithPlaceholder(
                        myProfile.profile.image.toString(),
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
            if(showBackButton){
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
            }



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
                                    Text(myProfile.profile.name, style = MaterialTheme.typography.titleMedium)
                                    Text("@${myProfile.profile.user_name}", style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Normal,
                                        modifier = Modifier.alpha(0.6f).padding(top=4.dp)
                                    )
                                }

                                Button(
                                    onClick = {navController.navigate(NavScreen.EditProfilePage.route)},
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color.White,
                                        contentColor = MaterialTheme.colorScheme.secondary,

                                        ),
                                    border = BorderStroke(2.dp, Gray2)
                                ) {
                                    Text("Edit Profile", style = MaterialTheme.typography.titleSmall)
                                }
                            }
                            if(myProfile.profile.bio.isEmpty() || myProfile.profile.bio.isBlank()){
                                Text(
                                    "About me .....",
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.alpha(0.6f).fillMaxWidth()
                                )
                            }else{
                                Text(
                                    myProfile.profile.bio,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.alpha(0.6f).fillMaxWidth()
                                )
                            }


                            Row (
                                modifier = Modifier
                                    .size(width = 80.dp, 30.dp).clickable(onClick = {
                                        navController.navigate(NavScreen.MyFriendsPage.route)
                                    })
                            ) {
                                Text(myProfile.friends.size.toString(), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold,)
                                Text("Friends", style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Normal,
                                    modifier = Modifier.padding(start = 8.dp).alpha(0.6f)
                                )
                            }


                            Text("Friends", style = MaterialTheme.typography.titleSmall,
                                modifier = Modifier,
                                fontWeight = FontWeight.Bold
                            )


                            ElevatedButton(
                                onClick = {
                                    navController.navigate(NavScreen.SearchPage.route)
                                },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = Color.White, // Background color
                                    contentColor = MaterialTheme.colorScheme.secondary // Text color
                                ),
                                modifier = Modifier.fillMaxWidth().height(70.dp),
                                contentPadding = PaddingValues(top = 23.dp, start = 16.dp, end = 16.dp, bottom = 23.dp),
                                shape = RoundedCornerShape(20.dp),
                                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Icon(Icons.Outlined.PersonAddAlt, "Add friend",
                                        Modifier.size(24.dp)
                                    )
                                    Text("Add Friends", style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Normal,
                                        modifier = Modifier.padding(start = 23.dp)
                                    )
                                }
                            }


                            ElevatedButton(
                                onClick = { navController.navigate(NavScreen.FriendRequestPage.route) },
                                colors = ButtonDefaults.elevatedButtonColors(
                                    containerColor = Color.White, // Background color
                                    contentColor = MaterialTheme.colorScheme.secondary // Text color
                                ),
                                modifier = Modifier.fillMaxWidth().height(70.dp),
                                contentPadding = PaddingValues(top = 23.dp, start = 16.dp, end = 16.dp, bottom = 23.dp),
                                shape = RoundedCornerShape(20.dp),
                                elevation = ButtonDefaults.elevatedButtonElevation(defaultElevation = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    verticalAlignment = Alignment.CenterVertically
                                ){
                                    Icon(Icons.Outlined.FormatListNumbered, "Friend Requests", Modifier.size(24.dp))
                                    Text("Requests", style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.padding(start = 23.dp)
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Icon(Icons.AutoMirrored.Filled.ArrowRight, "Requests",

                                        )
                                }
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
                            for (post in posts){
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
                                    likedPosts = likedPosts.value,
                                    isSingleScreen = false
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
