package com.vhennus.menu.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Gamepad
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.vhennus.ui.GeneralScaffold
import com.vhennus.ui.GeneralTopBar
import androidx.navigation.NavController
import com.vhennus.general.presentation.MenuTopBar
import com.vhennus.menu.domain.MenuItemData
import com.vhennus.ui.theme.Surf

@Composable
fun MenuScreen(navController: NavController){

    Column(
        modifier = Modifier.background(Surf).fillMaxSize().padding(start = 16.dp, end=16.dp)
    ) {
        MenuTopBar(navController)
//            Text(text = "Menu",
//                style =  MaterialTheme.typography.headlineLarge,
//                color = MaterialTheme.colorScheme.secondary)

        val menus = listOf(
            MenuItemData(
                    title = "Wallet",
                    route = "wallet",
                    icon = Icons.Outlined.AccountBalanceWallet
            ),
            MenuItemData(
                title = "Logout",
                route = "logout",
                icon = Icons.Outlined.Close
            ),
            MenuItemData(
                title = "Settings",
                route = "settings",
                icon = Icons.Outlined.Settings
            ),
//                MenuItemData(
//                    title = "Trivia",
//                    route = "trivia",
//                    icon = Icons.Outlined.Gamepad
//                )
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.End
        ) {
            items(menus.size){index->
                MenuItem(iconImage =menus[index].icon, menuTitle = menus[index].title, menuRoute = menus[index].route, navController)
            }
        }


    }
}