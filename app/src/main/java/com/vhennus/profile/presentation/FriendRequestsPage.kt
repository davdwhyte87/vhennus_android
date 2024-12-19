package com.vhennus.profile.presentation

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.vhennus.R
import com.vhennus.general.presentation.LoadImageWithPlaceholder
import com.vhennus.profile.data.ProfileViewModel
import com.vhennus.profile.domain.FriendRequest
import com.vhennus.profile.domain.ProfileUIState
import com.vhennus.profile.domain.UpdateProfileRequest
import com.vhennus.trivia.presentation.shimmerEffect
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold


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

    GeneralScaffold(
        topBar = {BackTopBar("Friend Requests", navController)},
        floatingActionButton = {}
    ) {

        if(profileUIState.isGetFriendRequestsLoading){
            FriendRequestsPageLoadingState()
        }else{
            LazyColumn (
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(requests){ request->
                    FriendRequestItem(request, profileViewModel, profileUIState)
                }
            }
        }
    }
}

@Composable
fun FriendRequestItem(
    request:FriendRequest,
    profileViewModel: ProfileViewModel,
    profileUIState: ProfileUIState
    ){
    Row (
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.clickable(onClick = {

        }).fillMaxWidth()
    ) {
        Image(
            painter = painterResource(R.drawable.p1),
            contentDescription = "",
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(60.dp).clip(CircleShape)
        )

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(request.requester_profile.name, style = MaterialTheme.typography.titleLarge)
            Text(request.requester_profile.user_name, style = MaterialTheme.typography.bodyLarge)
            Row (
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Button(onClick = {

                    profileViewModel.acceptFriendRequest(request.id)
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(width = 100.dp, height = 40.dp)
                ) {
                    if(profileUIState.isAcceptRequestLoading){
                        AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
                    }else {
                        Text(text = "Accept")
                    }

                }

                Button(onClick = {
                    profileViewModel.rejectFriendRequest(request.id)
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.size(width = 100.dp, height = 40.dp)
                ) {
                    if(profileUIState.isRejectRequestLoading){
                        AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.primary)
                    }else {
                        Text(text = "Reject")
                    }
                }
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