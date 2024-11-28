package com.vhennus.profile.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.vhennus.R


@Composable
fun profilePage(){
    Column (
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(R.drawable.dp1),
            contentDescription = "Profile",
            modifier = Modifier.size(width = 100.dp, height = 100.dp).clip(RectangleShape),
            contentScale = ContentScale.Fit
        )

        Row (
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Bio", style = MaterialTheme.typography.titleMedium)
            Text("I am not sure what to say, but I am just the type of guy to do the brink bronx tolf not to watch ",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(){
            Button(
             onClick = {}
            ) {
                Row {
                    Icon(Icons.Filled.Edit, "")
                    Text("Edit", style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        Row (
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text("Occupation", style = MaterialTheme.typography.titleMedium)
            Text("Athelete, surgeon",
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}