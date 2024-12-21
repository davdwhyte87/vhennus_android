package com.vhennus.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vhennus.NavScreen
import com.vhennus.R
import com.vhennus.general.presentation.LoadImageWithPlaceholder
import com.vhennus.profile.data.ProfileViewModel
import com.vhennus.profile.domain.Profile
import com.vhennus.trivia.presentation.shimmerEffect
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold


@Composable
fun myFriendsPage(
    navController: NavController,
    profileViewModel:ProfileViewModel
){
    val searchText = remember {
        mutableStateOf("")
    }

    val profile = profileViewModel.profile.collectAsState().value
    val profileUIState = profileViewModel.profileUIState.collectAsState().value

    GeneralScaffold(
        topBar = { BackTopBar("My Friends",navController ) },
        floatingActionButton = {}
    ) {

        Column (
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
//            Spacer(modifier = Modifier.height(10.dp))
            OutlinedTextField(
                value =searchText.value ,
                onValueChange = { searchText.value = it  },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(0.dp)
                ,
                placeholder = { Text("Search...", style = MaterialTheme.typography.bodySmall ) },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                leadingIcon = {
                    Icon(Icons.Outlined.Search, "Search")
                }
            )

            // list of friends
            val friends = profile.friends_models

            if (profileUIState.isGetProfileLoading){
                FriendsPageLoadingState()
            }else{
                LazyColumn (
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ){
                    items(friends){ item->
                        FriendListItem(item, navController)
                    }
                }
            }

        }
    }
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
fun FriendListItem(profile:Profile, navController: NavController){
    Row (
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.clickable(onClick = {
            // open chat
            navController.navigate(NavScreen.SingleChatScreen.route)
        }).fillMaxWidth()
    ) {
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

        Column {
            Text(profile.name, style = MaterialTheme.typography.titleMedium)
            Text(profile.user_name, style = MaterialTheme.typography.bodyMedium)
            Text(profile.bio, style = MaterialTheme.typography.bodySmall,  overflow = TextOverflow.Ellipsis)
        }
    }
}