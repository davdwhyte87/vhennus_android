package com.vhennus.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.vhennus.NavScreen
import com.vhennus.R
import com.vhennus.chat.data.ChatViewModel
import com.vhennus.general.presentation.LoadImageWithPlaceholder
import com.vhennus.profile.data.ProfileViewModel
import com.vhennus.profile.domain.MiniProfile
import com.vhennus.trivia.presentation.shimmerEffect
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold
import com.vhennus.ui.theme.Blue_Gray
import com.vhennus.ui.theme.Purple80
import com.vhennus.ui.theme.White


@Composable
fun myFriendsPage(
    navController: NavController,
    profileViewModel:ProfileViewModel,
    chatViewModel: ChatViewModel
){
    val lifecycleOwner = LocalLifecycleOwner.current
    val scrollState = rememberScrollState()
    DisposableEffect(true) {
        val observer = LifecycleEventObserver{_,event->
            if(event == Lifecycle.Event.ON_RESUME){
                profileViewModel.getMyProfile()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {

        }
    }
    val searchText = remember {
        mutableStateOf("")
    }

    val myProfile = profileViewModel.myProfile.collectAsState().value
    val profileUIState = profileViewModel.profileUIState.collectAsState().value
    Column (
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        BackTopBar("My Friends",navController )
//            Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value =searchText.value ,
            onValueChange = { searchText.value = it  },
            modifier = Modifier
                .fillMaxWidth()
                .height(58.dp)
                .padding(start = 16.dp, end = 16.dp)

            ,
            placeholder = { Text("Search...", style = MaterialTheme.typography.bodySmall ) },
            singleLine = true,
            shape = RoundedCornerShape(30.dp),
            leadingIcon = {
                Icon(Icons.Outlined.Search, "Search")
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.tertiary,
                unfocusedContainerColor = MaterialTheme.colorScheme.tertiary,
                focusedBorderColor = MaterialTheme.colorScheme.tertiary,
                unfocusedBorderColor = MaterialTheme.colorScheme.tertiary
            ),

            )

        // list of friends
        val friends = myProfile.friends.distinctBy { it.user_name }

        if (profileUIState.isGetProfileLoading){
            FriendsPageLoadingState()
        }else{
            LazyColumn (
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.padding(top = 15.dp).fillMaxSize()
            ){

                items(friends){ item->
                    FriendListItem(item, navController, chatViewModel)
                }
            }
        }

    }
//    GeneralScaffold(
//        topBar = { BackTopBar("My Friends",navController ) },
//        floatingActionButton = {}
//    ) {
//
//
//
//    }
}

@Composable
fun FriendsPageLoadingState(){
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(listOf(1,2,3)){
            Row (
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(modifier = Modifier.size(width = 50.dp, height = 50.dp).shimmerEffect())
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(modifier = Modifier.size(width = 300.dp, height = 50.dp).shimmerEffect())
                    Box(modifier = Modifier.size(width = 300.dp, height = 50.dp).shimmerEffect())
                }

            }
        }

    }
}

@Composable
fun FriendListItem(
    profile: MiniProfile,
    navController: NavController,
    chatViewModel: ChatViewModel
){

    Card (
        modifier = Modifier
            .fillMaxWidth().padding(top = 1.dp, bottom =1.dp, start = 16.dp, end = 16.dp )
            .clickable(onClick = {
                navController.navigate(NavScreen.SingleChatScreen.route+"/${profile.user_name}")
            })
        ,
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(21.dp),
            modifier = Modifier.fillMaxWidth().height(80.dp).padding(start = 16.dp, end = 16.dp)
                ,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if(profile.image.isEmpty() == true || profile.image.isBlank() == true){
                Image(
                    painter = painterResource(R.drawable.p1),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(40.dp).clip(CircleShape)
                )
            }else{

                LoadImageWithPlaceholder(
                    profile.image.toString(),
                    modifier = Modifier.size(40.dp)
                        .clip(CircleShape)
                )
            }

            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxHeight()
            ){
                Text(profile.name.toString()
                    , style = MaterialTheme.typography.titleSmall)
                Text(profile.user_name, style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.alpha(0.6f)
                )
            }
        }

    }
//    Row (
//        horizontalArrangement = Arrangement.spacedBy(21.dp),
//        modifier = Modifier.clickable(onClick = {
//            // set receiver profile inmemory
//            //chatViewModel.setSingleChatReceiverProfile(profile)
//            // open chat
//            navController.navigate(NavScreen.SingleChatScreen.route+"/${profile.user_name}")
//        }).fillMaxWidth().height(80.dp)
//    ) {
//        if(profile.image?.isEmpty() == true || profile.image?.isBlank() == true){
//            Image(
//                painter = painterResource(R.drawable.p1),
//                contentDescription = "",
//                contentScale = ContentScale.Crop,
//                modifier = Modifier.size(80.dp).clip(CircleShape)
//            )
//        }else{
//
//            LoadImageWithPlaceholder(
//                profile.image.toString(),
//                modifier = Modifier.size(80.dp)
//                    .clip(CircleShape)
//            )
//        }
//
//        Column (
//            verticalArrangement = Arrangement.Center,
//            horizontalAlignment = Alignment.Start,
//            modifier = Modifier.fillMaxSize()
//        ){
//            Text(profile.name.toString(), style = MaterialTheme.typography.titleSmall)
//            Text("@${profile.user_name}", style = MaterialTheme.typography.bodyMedium,
//                modifier = Modifier.padding(top = 4.dp))
////            Text(profile.bio.toString(), style = MaterialTheme.typography.bodyMedium,
////                overflow = TextOverflow.Ellipsis,
////                modifier = Modifier.alpha(0.6f)
////            )
//        }
//    }
}