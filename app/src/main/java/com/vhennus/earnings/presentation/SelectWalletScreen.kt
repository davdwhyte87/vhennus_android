package com.vhennus.earnings.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vhennus.earnings.data.EarningsViewModel
import com.vhennus.earnings.domain.EarningsUIState
import com.vhennus.general.presentation.AppScaffold
import com.vhennus.general.utils.formatBigDecimalWithCommas
import com.vhennus.profile.data.ProfileViewModel
import com.vhennus.profile.domain.UpdateProfileRequest
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.BackTopBar
import com.vhennus.wallet.data.WalletViewModel


@Composable
fun SelectWalletScreen(
    navController: NavController,
    walletViewModel: WalletViewModel,
    walletsString:String,
    profileViewModel: ProfileViewModel,
    earningsViewModel: EarningsViewModel,
    walletsViewModel: WalletViewModel
){
    val snackBarHost = remember { SnackbarHostState() }
    val wallets = walletViewModel.allWallets.collectAsState().value
    val earningsUIState = earningsViewModel.earningsUIState.collectAsState().value
    val walletsUIState = walletsViewModel.walletUIState.collectAsState().value

    LaunchedEffect(Unit) {
        walletViewModel.getAllWallets(walletsString)
    }

    AppScaffold(
        topBar = { BackTopBar("Select Wallet", navController) },
        snackbarHostState = snackBarHost

    ) {


        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if(walletsUIState.isGetAllWalletsLoading){
                AnimatedPreloader(
                    modifier = Modifier.size(50.dp) ,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            wallets.forEach {item->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxWidth().clickable(onClick = {
                        // update earnings wallet
                        profileViewModel.updateProfile(UpdateProfileRequest(
                            earnings_wallet = item.address,
                            image = null,
                            bio = null,
                            name = null,
                            app_f_token = null
                        ))
                        navController.popBackStack()
                    })
                    ) {
                    IconButton(onClick = {},
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary),
                        modifier = Modifier.size(50.dp)
                    ) {
                        Icon(
                            Icons.Outlined.AccountBalanceWallet, "", tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(25.dp))
                    }

                    Column (
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ){

                        Text(item.address, style= MaterialTheme.typography.titleSmall)
                        Text("${formatBigDecimalWithCommas(item.balance)} VEC", style= MaterialTheme.typography.bodySmall)
                    }
                }
            }
        }
    }

}