package com.vhennus.profile.presentation

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Cancel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.vhennus.NavScreen
import com.vhennus.R
import com.vhennus.general.presentation.LoadImageWithPlaceholder
import com.vhennus.profile.data.ProfileViewModel
import com.vhennus.profile.domain.FriendRequestWithProfile
import com.vhennus.profile.domain.ProfileUIState
import com.vhennus.trivia.data.formatDateWithDateTimeFormatter
import com.vhennus.trivia.presentation.shimmerEffect
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold
import com.vhennus.ui.theme.Gray2
import com.vhennus.ui.theme.White


@Composable
fun FriendRequestsPage(
    navController: NavController,
    profileViewModel: ProfileViewModel
    ){

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(true) {
        val observer = LifecycleEventObserver{_,event->
            if(event == Lifecycle.Event.ON_RESUME){
                profileViewModel.getMyFriendRequests()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            profileViewModel.resetUIState()
        }
    }

    val requests = profileViewModel.myFriendRequests.collectAsState().value
    val profileUIState = profileViewModel.profileUIState.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(profileUIState.isGetFriendRequestsError) {
        if(profileUIState.isGetFriendRequestsError){
            Toast.makeText(context, profileUIState.getFrienRequestsErrorMessage, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(profileUIState.isAcceptRequestError) {
        if(profileUIState.isAcceptRequestError){
            Toast.makeText(context, profileUIState.acceptRequestErrorMessage, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(profileUIState.isAcceptRequestSuccess) {
        if(profileUIState.isAcceptRequestSuccess){
            Toast.makeText(context, "Request accepted successfully.", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(profileUIState.isRejectRequestError) {
        if(profileUIState.isRejectRequestError){
            Toast.makeText(context, profileUIState.rejectRequestErrorMessage, Toast.LENGTH_SHORT).show()
        }
    }
    LaunchedEffect(profileUIState.isRejectRequestSuccess) {
        if(profileUIState.isRejectRequestSuccess){
            Toast.makeText(context, "Request rejected successfully.", Toast.LENGTH_SHORT).show()
        }
    }


    Column {
        BackTopBar("Requests", navController)
        if(profileUIState.isGetFriendRequestsLoading && requests.isEmpty()){
            FriendRequestsPageLoadingState()
        }else{
            LazyColumn (
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(requests){ request->
                    FriendRequestItem(request, profileViewModel, profileUIState, navController)
                }
            }
        }
    }

}

@Composable
fun FriendRequestItem(
    request: FriendRequestWithProfile,
    profileViewModel: ProfileViewModel,
    profileUIState: ProfileUIState,
    navController: NavController
    ){

    Card (
        modifier = Modifier
            .fillMaxWidth().padding(top = 2.dp, bottom =16.dp, start = 16.dp, end = 16.dp ),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row (
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth().height(92.dp).padding(start = 16.dp, end = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.fillMaxHeight().clickable(onClick = {
                    navController.navigate(NavScreen.OtherUserProfileScreen.route+"/${request.user_name}")
                })
            ) {
                if(request.image.isEmpty() == true || request.image.isBlank() == true){
                    Image(
                        painter = painterResource(R.drawable.p1),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                    )
                }else{

                    LoadImageWithPlaceholder(
                        request.image.toString(),
                        modifier = Modifier.size(40.dp)
                            .clip(CircleShape)
                    )
                }
            }


            Column (
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxHeight()
            ){
                Text(request.name.toString()
                    , style = MaterialTheme.typography.titleSmall)
                Text(request.user_name, style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.alpha(0.6f)
                )
            }

            Button(
                onClick = { profileViewModel.acceptFriendRequest(request.id)},
                colors = ButtonDefaults.buttonColors(
                    containerColor =  MaterialTheme.colorScheme.primary,
                    contentColor = White,
                    ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier.size(width = 85.dp, height = 44.dp).padding(0.dp),
                contentPadding = PaddingValues(4.dp)

            ) {
                if(profileUIState.isAcceptRequestLoading){
                    AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
                }else {
                    Text("Accept", style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(0.dp)
                    )
                }
            }

            IconButton (
                onClick = { profileViewModel.rejectFriendRequest(request.id)},

                modifier = Modifier.size(24.dp)
            ) {
                Icon(Icons.Outlined.Cancel, "Decline", )
            }
        }

    }
}

@Composable
fun FriendRequestsPageLoadingState(){
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