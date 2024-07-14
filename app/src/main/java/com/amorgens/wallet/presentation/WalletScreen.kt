package com.amorgens.wallet.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.parser.IntegerParser
import com.amorgens.NavScreen
import com.amorgens.ui.GeneralScaffold
import com.amorgens.ui.HomeTopBar
import com.amorgens.wallet.data.WalletViewModel


@Composable
fun WalletScreen(navController: NavController, walletViewModel: WalletViewModel){
    // get all wallets
    walletViewModel.getAllWallets()
    val wallets = walletViewModel.allWallets.collectAsState().value

    var totalBalance = 0.0f
    wallets.forEachIndexed { index, wallet ->
        totalBalance += wallet.balance.toFloat()
    }
    GeneralScaffold(topBar = { HomeTopBar("Wallets", navController) }, floatingActionButton = {  }) {
        var isExpanded = remember {
            mutableStateOf(false)
        }
        Column  (
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ){
            ElevatedCard(
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                modifier = Modifier.padding(0.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column (
                        modifier = Modifier
                            .padding(1.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)

                    ) {
                        Text(text = totalBalance.toString(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.surface
                        )
                        Text(text = "Total assets",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.surface
                        )
                    }
                    Row (
                        modifier = Modifier
                            .padding(1.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "35,000",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.surface
                        )
                        Box (
                            modifier = Modifier.clickable(onClick = {
                                if (isExpanded.value) isExpanded.value = false else isExpanded.value = true
                            })
                        ){
                            Row {
                               Icon(imageVector = Icons.Outlined.KeyboardArrowDown, contentDescription ="" )
                                Text(text = "USD",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.surface
                                )
                            }
                        }
                        DropdownMenu(expanded = isExpanded.value, onDismissRequest = { isExpanded.value = false }) {
                            DropdownMenuItem(text = { Text(text = "USD")}, onClick = {  })
                        }
                    }

                }
            }

            Row {

                val menus = listOf(WalletMenu("Add", Icons.Default.Add),
                    WalletMenu("New", Icons.Default.AddCard)
                )
                menus.forEachIndexed { index, walletMenu ->
                    WalletMenuItem(iconImage = walletMenu.icon , menuTitle = walletMenu.name) {
                        if (index == 0){
                            navController.navigate(NavScreen.AddWalletScreen.route)
                        }

                        if (index ==1){
                            navController.navigate(NavScreen.NewWalletScreen.route)
                        }

                    }
                }
            }


            WalletList(navController,wallets)
        }
    }
}

