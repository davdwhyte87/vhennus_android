package com.vhennus.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
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
import com.vhennus.feed.domain.Post
import com.vhennus.feed.presentation.post
import com.vhennus.general.presentation.LoadImageWithPlaceholder
import com.vhennus.profile.data.ProfileViewModel
import com.vhennus.trivia.presentation.shimmerEffect
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold
import com.vhennus.ui.GeneralTopBar


@Composable
fun profilePage(
    navController: NavController,
    profileViewModel: ProfileViewModel
){

    val lifecycleOwner = LocalLifecycleOwner.current
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
    val post = remember {
        mutableStateOf(
           Post(
               user_name = "jay_rome",
               text = "Kobe’s passing is really sticking w/ me in a way I didn’t expect.\n" +
                       "He was an icon, the kind of person who wouldn’t die this way. My wife compared it to Princess Di’s accident.",

           )
        )
    }
    val profile = profileViewModel.profile.collectAsState().value
    val profileUiState = profileViewModel.profileUIState.collectAsState().value

    GeneralScaffold(
        topBar = {GeneralTopBar()},
        floatingActionButton = {}
    ) {
        Column (
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // display default image if no ptofile pic exists
            if(profile.image.isEmpty() || profile.image.isBlank()){
                Image(
                    painter = painterResource(R.drawable.p1),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(60.dp).clip(CircleShape)
                )
            }else{
                LoadImageWithPlaceholder(profile.image,
                    modifier = Modifier.size(60.dp)
                        .clip(CircleShape)
                )
            }

            if(!profile.name.isEmpty() || !profile.name.isBlank()){
                Text(profile.name, style = MaterialTheme.typography.titleMedium)
            }

            // username
            if(profileUiState.isGetProfileLoading){
                Box(modifier = Modifier.size(height = 20.dp, width = 100.dp).shimmerEffect())
            }else{
                Text("@"+profile.user_name, style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.height(20.dp))

            if(profileUiState.isGetProfileLoading){
                Box(modifier = Modifier.size(height = 40.dp, width = 600.dp).shimmerEffect())
            }else{
                if(!profile.bio.isEmpty() || !profile.bio.isBlank()){
                    Text(profile.bio, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.padding(20.dp))

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

            if(profileUiState.isGetProfileLoading){
                Column (
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Box(modifier = Modifier.size(height = 20.dp, width = 300.dp).shimmerEffect())
                    Box(modifier = Modifier.size(height = 300.dp, width = 600.dp).shimmerEffect())
                }

            }else{
                post(
                    post,
                    navController,
                    "jerome",
                    onLike = {}
                ) { }
            }

        }
    }

}