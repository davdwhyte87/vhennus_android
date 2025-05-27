package com.vhennus.home.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.vhennus.R.drawable
import com.vhennus.auth.data.AuthViewModel
import com.vhennus.chat.data.ChatViewModel
import com.vhennus.chat.presentation.AllChatsScreen
import com.vhennus.earnings.data.EarningsViewModel
import com.vhennus.feed.data.FeedViewModel
import com.vhennus.feed.presentation.FeedScreen
import com.vhennus.general.data.GeneralViewModel
import com.vhennus.general.presentation.LoadImageWithPlaceholder
import com.vhennus.general.utils.CLog
import com.vhennus.home.presentation.components.BottomNavItem
import com.vhennus.menu.presentation.MenuScreen
import com.vhennus.profile.data.ProfileViewModel
import com.vhennus.profile.presentation.profilePage
import com.vhennus.ui.theme.Black
import com.vhennus.ui.theme.Green
import com.vhennus.ui.theme.Purple
import com.vhennus.ui.theme.Surf
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.math.BigDecimal

// home screen will have buttom navigation and will contains four screens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController,
               feedViewModel: FeedViewModel,
               chatViewModel: ChatViewModel,
               authViewModel: AuthViewModel,
               profileViewModel: ProfileViewModel,
               generalViewModel: GeneralViewModel,
               earningsViewModel: EarningsViewModel
){
    val systemData = generalViewModel.systemData.collectAsState()
    val context = LocalContext.current
    val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
    val versionName = packageInfo.versionName
    val feedUIState = feedViewModel.feedUIState.collectAsState()
    val isGetSystemDataSuccess = generalViewModel.isGetSystemDataSuccess.collectAsState().value
    val showUpdateModal = remember {
        mutableStateOf(false)
    }
    val myProfile = profileViewModel.myProfile.collectAsState().value

    val isUnreadMessage = chatViewModel.isUnreadMessage.collectAsState()

    val lifecycleOwner = LocalLifecycleOwner.current

    // effects
    DisposableEffect(Unit) {


        val observer = LifecycleEventObserver{_,event->
            if(event == Lifecycle.Event.ON_RESUME){
                authViewModel.getUserName()
                //chatViewModel.getAllMyChatPairs()
                profileViewModel.getMyProfile()
                // get the app version
                generalViewModel.getSystemData()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {

        }
    }

    LaunchedEffect(isGetSystemDataSuccess) {
        if(isGetSystemDataSuccess){

            if (systemData.value.android_app_version != versionName ){
                CLog.debug("NEW APP VERSION", "YEs")
                showUpdateModal.value = true
            }else{
                showUpdateModal.value = false
            }
        }
    }

    LaunchedEffect(isGetSystemDataSuccess) {
        if(isGetSystemDataSuccess){

            // send data to post earnings if any
            val prefs = context.getSharedPreferences("app", Context.MODE_PRIVATE)
            val mins = prefs.getLong("time_spent", 0L)
            CLog.debug("CHECKING EARNINGS",mins.toString() )
            if (mins == 0L){
                return@LaunchedEffect
            }
            val earning =systemData.value.price_per_min * BigDecimal(mins)
            earningsViewModel.postEarnings(earning)
            // send request
        }
    }

    LaunchedEffect(true) {
        // connect to websocket
        chatViewModel.connectToChatWS()
    }


    //CLog.debug("SYSTEM DATA", systemData.value.android_app_version )
    //CLog.error("APP VERSION",versionName )
    //clog("EAT ME", " YUM YUM")
    //Sentry.captureException(RuntimeException("This app uses Sentry! :)"))


    val sheetState = rememberModalBottomSheetState()
    val coroutine = rememberCoroutineScope()
    val navItems = listOf(
        BottomNavItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home,
            hasNews = false,
            badgeCount = 0,
            route =""
        ),
        BottomNavItem(
            title = "Chat",
            selectedIcon = Icons.Filled.ChatBubble,
            unselectedIcon = Icons.Outlined.ChatBubbleOutline,
            hasNews = false,
            badgeCount = 0,
            route = ""
        ),
        BottomNavItem(
            title = "Profile",
            selectedIcon = Icons.Filled.Person,
            unselectedIcon = Icons.Outlined.Person,
            hasNews = false,
            badgeCount = 0,
            route = ""
        ),
        BottomNavItem(
            title = "Menu",
            selectedIcon = Icons.Filled.Menu,
            unselectedIcon = Icons.Outlined.Menu,
            hasNews = false,
            badgeCount = 0,
            route = ""
        ),
        )
    val pagerState: PagerState = rememberPagerState(0){navItems.size}



//    LaunchedEffect(pagerState.currentPage) {
//        if (!pagerState.isScrollInProgress) {
//            selectedTabIndex = pagerState.currentPage
//        }
//    }

    val coroutineScope = rememberCoroutineScope()
    Column {

        HorizontalPager(state = pagerState, modifier= Modifier
            .fillMaxWidth()
            .weight(1f)
        ) {page->

            when(page){
                0-> FeedScreen(navController, feedViewModel)
                1-> AllChatsScreen(navController, chatViewModel,authViewModel )
                2-> {
                    // get profile data and users posts

                    profilePage(navController, profileViewModel, feedViewModel)
                }
                3-> MenuScreen(navController)
            }
//            if(selectedTabIndex == 0){
//                FeedScreen(navController, feedViewModel)
//            }
//            if(selectedTabIndex == 1){
//                AllChatsScreen(navController, chatViewModel,authViewModel )
//            }
//            if(selectedTabIndex == 2){
//                profilePage(navController, profileViewModel)
//            }
//            if(selectedTabIndex == 3){
//                MenuScreen(navController)
//            }
        }
        TabRow(selectedTabIndex = pagerState.currentPage,
            containerColor = Surf,
            contentColor = Black,
            modifier = Modifier.height(80.dp)
        ) {
            navItems.forEachIndexed { index, bottomNavItem ->
                Tab(selected = pagerState.currentPage ==index,

                    onClick = {

                              },
                    icon = {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp),

                        ) {
                            BadgedBox(badge = {
                                when(index){
                                    0-> {}
                                    1-> {
                                        if(isUnreadMessage.value){
                                            Badge()
                                        }
                                    }
                                }

                            }) {
                                IconButton(
                                    onClick = {
                                        coroutineScope.launch {
                                            pagerState.animateScrollToPage(index)
                                        }
//                        selectedTabIndex = index
                                        if (index == 0){
                                            // prevent reload when coming from other tabs
                                            if (0 == pagerState.currentPage){
                                                //CLog.debug("INDEX CURRPAGE", pagerState.currentPage.toString())
                                                feedViewModel.getAllPosts(true)
                                            }else{
                                                feedViewModel.getAllPosts(false)
                                            }
                                        }
                                        if(index == 2){
                                            profileViewModel.getMyProfile()
                                            feedViewModel.getAllMyPosts()
                                        }
                                    },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = if (pagerState.currentPage == index){ MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)}else {Surf}
                                    )
                                ) {
                                    if (index == 2 ){
                                        if (myProfile.profile.image.isBlank() || myProfile.profile.image.isEmpty()) {
                                            Image(
                                                painter = painterResource(drawable.p2),
                                                contentDescription = "",
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.size(24.dp).clip(CircleShape)
                                            )
                                        }else{
                                            LoadImageWithPlaceholder(myProfile.profile.image,
                                                modifier = Modifier.size(24.dp)
                                                    .clip(CircleShape)
                                            )
                                        }

                                    }else{
                                        Icon(
                                            imageVector = if (pagerState.currentPage == index){bottomNavItem.selectedIcon}else{bottomNavItem.unselectedIcon},
                                            contentDescription = bottomNavItem.title,
                                            tint = Black
                                        )
                                    }

                                }

                            }
                            Text(bottomNavItem.title, style = MaterialTheme.typography.labelLarge)

                        }


                    }
                )
            }
        }



        if (showUpdateModal.value){
            ModalBottomSheet(
                onDismissRequest = { coroutine.launch { sheetState.show() }},
                sheetState = sheetState,
            ) {
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(50.dp),
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                ) {

                    // text
                    Text(text = "There is a new version of the app, please update! Click the download button or go to the website. Vhennus.com",
                        style = MaterialTheme.typography.titleMedium
                    )
                    // signup button
                    Button(onClick = {
                        val url = systemData.value.apk_link
                        if (url.isBlank() || !isValidUrl(url)) {
                            Toast.makeText(context, "Invalid URL", Toast.LENGTH_SHORT).show()
                        } else {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        }

                    },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.size(width = 200.dp, height = 50.dp)
                    ) {
                        Text(text = "Update App",
                            style = MaterialTheme.typography.titleLarge
                        )
                    }

                }
            }
        }
    }
}

fun isValidUrl(url: String): Boolean {
    return try {
        val uri = Uri.parse(url)
        uri.scheme in listOf("http", "https") // Only allow valid schemes
    } catch (e: Exception) {
        false
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}


@Composable
fun chatScreen(){
    Text(
        text = "Chat feature coming soon ",

    )
}


@Composable
fun profileScreen(){
    Text(
        text = "Profile feature coming soon ",

        )
}
