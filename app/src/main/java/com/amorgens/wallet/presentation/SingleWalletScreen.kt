package com.amorgens.wallet.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.sharp.Cloud
import androidx.compose.material.icons.sharp.ContentCopy
import androidx.compose.material.icons.sharp.Rocket
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amorgens.ui.GeneralScaffold
import com.amorgens.ui.HomeTopBar


@Composable
fun SingleWalletScreen(address: String, navController: NavController){
    GeneralScaffold(topBar = { HomeTopBar("Wallet") }, floatingActionButton = {  }) {
        var isExpanded = remember {
            mutableStateOf(false)
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ElevatedCard(
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.padding(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier
                            .padding(1.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)

                    ) {
                        Text(
                            text = "2,000,000",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.surface
                        )
                        Text(
                            text = "Total assets",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(1.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "35,000",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.surface
                        )
                        Box(
                            modifier = Modifier.clickable(onClick = {
                                if (isExpanded.value) isExpanded.value =
                                    false else isExpanded.value = true
                            })
                        ) {
                            Row {
                                Icon(
                                    imageVector = Icons.Outlined.KeyboardArrowDown,
                                    contentDescription = ""
                                )
                                Text(
                                    text = "USD",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.surface
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = isExpanded.value,
                            onDismissRequest = { isExpanded.value = false }) {
                            DropdownMenuItem(text = { Text(text = "USD") }, onClick = { })
                        }
                    }

                }
            }


            Row (
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val menus = listOf(WalletMenu("Send", Icons.Sharp.Rocket),
                    WalletMenu("Trade", Icons.Sharp.Cloud),
                    WalletMenu("Copy", Icons.Sharp.ContentCopy)
                )
                menus.forEachIndexed { index, walletMenu ->
                    WalletMenuItem(name = walletMenu.name, icon = walletMenu.icon ) {
//                        navController.navigate()
                    }
                }
            }

            // text trnasaction
            Text(text = "Transactions",
                style = MaterialTheme.typography.bodyLarge
            )

            // transactionlist

            TransactionList()

        }
    }
}

data class WalletMenu(
    val name:String,
    val icon:ImageVector
)

@Composable
fun WalletMenuItem(name:String, icon: ImageVector, onclick:()->Unit){
    ElevatedCard (
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp),
        modifier = Modifier
            .padding(10.dp)
            .size(width = 100.dp, height = 100.dp),
        onClick = {
            onclick()
        }
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(),
            verticalArrangement = Arrangement.Center,

            ){
            Icon(imageVector = icon,
                contentDescription = name,
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(40.dp)
            )
            Text(text = name)
        }
    }
}