package com.vhennus.menu.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.vhennus.ui.GeneralScaffold
import com.vhennus.ui.GeneralTopBar
import androidx.navigation.NavController
import com.vhennus.menu.domain.MenuItemData

@Composable
fun MenuScreen(navController: NavController){


    GeneralScaffold(topBar = { GeneralTopBar() }, floatingActionButton = { }) {
        Column {
            Text(text = "Menu",
                style =  MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary)

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
                )
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
}