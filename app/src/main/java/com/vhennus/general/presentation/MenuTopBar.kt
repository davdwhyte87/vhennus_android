package com.vhennus.general.presentation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.vhennus.NavScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuTopBar(navController: NavController){
    TopAppBar(
        title ={  },
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Outlined.Menu , contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = {
                navController.navigate(NavScreen.SearchPage.route)
            }) {
                Icon(imageVector = Icons.Outlined.Search , contentDescription = "Search")
            }
        }
    )
}