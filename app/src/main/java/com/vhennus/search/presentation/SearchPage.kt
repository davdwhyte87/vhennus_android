package com.vhennus.search.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vhennus.R
import com.vhennus.general.presentation.LoadImageWithPlaceholder
import com.vhennus.profile.data.ProfileViewModel
import com.vhennus.profile.domain.MiniProfile
import com.vhennus.profile.domain.ProfileUIState
import com.vhennus.profile.domain.SendFriendRequest
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold


@Composable
fun SearchPage(
    navController: NavController,
    profileViewModel: ProfileViewModel
){
    val context = LocalContext.current
    val profileResults = profileViewModel.profileSearchResults.collectAsState().value
    val searchUIState = profileViewModel.searchUIState.collectAsState().value
    val profileUIState = profileViewModel.profileUIState.collectAsState().value

    // effects
    LaunchedEffect(searchUIState.isSearchError) {
        // show error message if search fails
        if(searchUIState.isSearchError){
            Toast.makeText(context, searchUIState.searchErrorMessage, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(profileUIState.isSendFriendRequestError) {
        // show error message if search fails
        if(profileUIState.isSendFriendRequestError){
            Toast.makeText(context, profileUIState.sendFriendRequestError, Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(profileUIState.isSendFriendRequestSuccess) {
        // show error message if search fails
        if(profileUIState.isSendFriendRequestSuccess){
            Toast.makeText(context, "Sent!", Toast.LENGTH_SHORT).show()
        }
    }

    DisposableEffect(true) {
        onDispose {
            profileViewModel.resetModelData()
            profileViewModel.resetUIState()
        }
    }

    GeneralScaffold(
        topBar = { BackTopBar("Search", navController) },
        floatingActionButton = {}
    ) {

        val searchText = remember {
            mutableStateOf("")
        }
        Column (
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(value = searchText.value,
                onValueChange = {
                    searchText.value = it
                },
                shape = RoundedCornerShape(30.dp),
                placeholder = { Text(text = "Search") },
                modifier = Modifier
                    .padding(end = 16.dp).fillMaxWidth(),
                singleLine = true,
                trailingIcon =  {
                    IconButton(
                        onClick = {
                            //va;idate input
                            validateSearchData(context, searchText.value)

                            // search
                            profileViewModel.searchProfiles(searchText.value)
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.surface,
                            containerColor = MaterialTheme.colorScheme.primary
                        )

                    ) {
                        Icon(Icons.AutoMirrored.Outlined.Send, "Send")
                    } },
            )

            // search results
            if(searchUIState.isSearchLoading){
                AnimatedPreloader(modifier = Modifier.size(size = 60.dp), MaterialTheme.colorScheme.primary)
            }
//            val items = listOf(1,2,3,4,5,6)
            LazyColumn (
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(profileResults){item->
                    ProfileSearchItem(item,profileUIState, profileViewModel)
                }
            }
        }
    }
}


@Composable
fun ProfileSearchItem(
    profile: MiniProfile,
    profileUIState: ProfileUIState,
    profileViewModel: ProfileViewModel
){
    Row (
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.clickable(onClick = {

        }).fillMaxWidth()
    ) {
        if(profile.image?.isEmpty() == true || profile.image?.isBlank() == true){
            Image(
                painter = painterResource(R.drawable.p1),
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(60.dp).clip(CircleShape)
            )
        }else{
            LoadImageWithPlaceholder(
                profile.image.toString(),
                modifier = Modifier.size(60.dp)
                    .clip(CircleShape)
            )
        }
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(profile.name.toString(), style = MaterialTheme.typography.titleLarge)
            Text("@${profile.user_name}", style = MaterialTheme.typography.bodyLarge)
            Row (
                modifier = Modifier.fillMaxWidth().padding(start = 20.dp, end = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Button(onClick = {
//                    profileViewModel.acceptFriendRequest(request.id)
                    profileViewModel.sendFriendRequest(SendFriendRequest(user_name = profile.user_name))
                },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if(profileUIState.isSendFriendRequestLoading){
                        AnimatedPreloader(modifier = Modifier.size(size = 30.dp), MaterialTheme.colorScheme.surface)
                    }else {
                        Text(text = "Add Friend")
                    }
                }
            }
        }
    }
}

fun validateSearchData(context: Context, message:String):Boolean {
    if (message.isBlank() || message.isEmpty()) {
        Toast.makeText(context, "Empty text", Toast.LENGTH_SHORT).show()
        return false
    }
    return true
}
