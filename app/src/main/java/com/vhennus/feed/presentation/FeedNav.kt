package com.vhennus.feed.presentation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.vhennus.ui.theme.Surf


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun feedNav(){
    val systemUiController = rememberSystemUiController()
    SideEffect {
        systemUiController.setStatusBarColor(Surf)
    }
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Surf
        ),
        title = { Text(text = "", textAlign = TextAlign.Start) },
        navigationIcon = {
            Text(text = "Vhennus", textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 16.dp)
            )
//            IconButton(onClick = {
//
//            }) {
//                Icon(imageVector = Icons.Outlined.Home ,
//                    contentDescription = "Home",
//                    tint = MaterialTheme.colorScheme.primary
//                )
//            }
        },
        actions = {
            Row (

            ) {

            }
//            IconButton(onClick = {}) {
//                Icon(imageVector = Icons.Outlined.Menu ,
//                    contentDescription = "Menu",
//                    tint = MaterialTheme.colorScheme.primary
//                )
//            }
        }
    )
}