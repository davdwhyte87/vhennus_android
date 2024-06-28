package com.amorgens.home.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.ChatBubble
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.amorgens.NavScreen
import com.amorgens.feed.presentation.FeedScreen
import com.amorgens.home.presentation.components.BottomNavItem
import com.amorgens.menu.presentation.MenuScreen
import com.amorgens.ui.theme.Purple

// home screen will have buttom navigation and will contains four screens

@Composable
fun HomeScreen(navController: NavController){
    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }

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
    val pagerState: PagerState = rememberPagerState {
        navItems.size
    }
    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    LaunchedEffect(pagerState.currentPage) {
        if (!pagerState.isScrollInProgress) {
            selectedTabIndex = pagerState.currentPage
        }
    }
    Column {
        HorizontalPager(state = pagerState, modifier= Modifier
            .fillMaxWidth()
            .weight(1f)) {
            if(it == 0){
                FeedScreen()
            }
            if(it == 1){

            }
            if(it == 2){

            }
            if(it == 3){
                MenuScreen(navController)
            }
        }
        TabRow(selectedTabIndex = selectedTabIndex) {
            navItems.forEachIndexed { index, bottomNavItem ->
                Tab(selected = index==selectedTabIndex,
                    onClick = {selectedTabIndex = index},
                    icon = {
                        Icon(
                            imageVector = if (selectedTabIndex == index){bottomNavItem.selectedIcon}else{bottomNavItem.unselectedIcon},
                            contentDescription = bottomNavItem.title,
                            tint = Purple
                        )
                    }
                )
            }
        }
    }
}
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}
