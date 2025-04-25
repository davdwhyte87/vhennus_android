package com.amorgens.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.style.TextAlign
import androidx.navigation.NavController
import com.amorgens.NavScreen


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(pageName:String, navController: NavController){
    CenterAlignedTopAppBar(
        title = { Text(text = pageName, textAlign = TextAlign.Center)},
        navigationIcon = {
            IconButton(onClick = {
                navController.navigate(NavScreen.HomeScreen.route)
            }) {
                Icon(imageVector = Icons.Outlined.Home ,
                    contentDescription = "Home",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    )
}