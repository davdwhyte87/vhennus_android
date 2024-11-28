package com.vhennus.chat.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vhennus.R


@Preview
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(navController: NavController){
    CenterAlignedTopAppBar(
        title = {  },
        navigationIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ){
                IconButton(onClick = {
                    navController.navigateUp()
                }) {
                    Icon(imageVector = Icons.Sharp.ArrowBackIosNew ,
                        contentDescription = "Home",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Image(
                    painter = painterResource(R.drawable.dp1),
                    contentDescription = "Profile",
                    modifier = Modifier.size(38.dp)
                        .clip(CircleShape)
                )
                Text("Johnremol345", style = MaterialTheme.typography.titleMedium)
            }


        },
        actions = {

        }
    )
}