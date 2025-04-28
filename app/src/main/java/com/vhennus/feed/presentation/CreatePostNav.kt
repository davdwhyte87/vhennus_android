package com.vhennus.feed.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vhennus.feed.data.FeedViewModel
import com.vhennus.ui.AnimatedPreloader


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun createPostNav(
    navController: NavController,
    onClick : ()->Unit,
    feedViewModel: FeedViewModel
){
    val feedUIState = feedViewModel.feedUIState.collectAsState()

    if(feedUIState.value.isCreatePostSuccess){
        navController.navigateUp()
    }

    CenterAlignedTopAppBar(
        title = { Text(text = "", textAlign = TextAlign.Center) },
        navigationIcon = {
            Text(text = "Cancel",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable (onClick = {navController.navigateUp()})
            )
        },
        actions = {
            Button(onClick = {
                onClick()
                //navController.popBackStack()
            }) {
                if(feedUIState.value.isCreatePostLoading){
                    AnimatedPreloader(modifier = Modifier.size(size = 30.dp), MaterialTheme.colorScheme.surface)
                }else {
                    Text(text = "Post", style = MaterialTheme.typography.titleSmall)
                }

            }
        },
        modifier = Modifier.padding(15.dp)
    )
}