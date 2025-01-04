package com.vhennus.chat.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vhennus.R
import com.vhennus.general.presentation.LoadImageWithPlaceholder
import com.vhennus.profile.domain.Profile


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(navController: NavController, profile: Profile){
    CenterAlignedTopAppBar(
        title = {  },
        navigationIcon = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                IconButton(onClick = {
                    navController.popBackStack()
                }) {
                    Icon(imageVector = Icons.Sharp.ArrowBackIosNew ,
                        contentDescription = "Home",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }


                if(profile.image.isEmpty() || profile.image.isBlank()){
                    Image(
                        painter = painterResource(R.drawable.p1),
                        contentDescription = "",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.size(40.dp).clip(CircleShape)
                    )
                }else{
                    LoadImageWithPlaceholder(profile.image,
                        modifier = Modifier.size(40.dp)
                            .clip(CircleShape)
                    )
                }
                Text(profile.user_name, style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(start = 10.dp))
            }


        },
        actions = {

        }
    )
}