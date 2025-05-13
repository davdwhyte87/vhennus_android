package com.vhennus.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.vhennus.ui.theme.Purple
import com.vhennus.ui.theme.Surf
import com.vhennus.ui.theme.White
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeneralTopBar(){
    TopAppBar(
        title ={  },
        navigationIcon = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Outlined.Menu , contentDescription = "Menu")
            }
        },
        actions = {
            IconButton(onClick = { /*TODO*/ }) {
                Icon(imageVector = Icons.Outlined.Notifications , contentDescription = "Notification")
            }

        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListTopBar(){
    val systemUiController = rememberSystemUiController()
    val statusBarColor = Purple

    LaunchedEffect(statusBarColor) {
        systemUiController.setStatusBarColor(statusBarColor)
    }
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Purple
        ),
        title = { Text(text = "", textAlign = TextAlign.Start) },
        navigationIcon = {
            Text(text = "Vhennus", textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp),
                color = White
            )
        },
        actions = {
        }
    )
}