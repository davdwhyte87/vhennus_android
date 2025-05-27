package com.vhennus.earnings.presentation

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Money
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.vhennus.NavScreen
import com.vhennus.earnings.data.EarningsViewModel
import com.vhennus.general.presentation.AppScaffold
import com.vhennus.general.presentation.CustomSnackbarVisuals
import com.vhennus.general.presentation.SnackbarType
import com.vhennus.general.utils.copyToClipboard
import com.vhennus.general.utils.formatBigDecimalWithCommas
import com.vhennus.profile.data.ProfileViewModel
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.theme.Red
import com.vhennus.wallet.data.WalletViewModel
import com.vhennus.wallet.domain.GetWalletReq
import com.vhennus.wallet.domain.GetWalletTransactionsReq
import kotlinx.coroutines.launch
import java.math.BigDecimal


@Composable
fun EarningsScreen(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    earningsViewModel: EarningsViewModel,
    ){
    var isChecked = remember { mutableStateOf(true) }
    var snackbarHostState = remember { SnackbarHostState() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val myProfile = profileViewModel.myProfile.collectAsState().value
    val earningsUIState = earningsViewModel.earningsUIState.collectAsState().value
    val scope = rememberCoroutineScope()
    val refAmount = remember{mutableStateOf(BigDecimal.ZERO)}
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        val pref = context.getSharedPreferences("app", Context.MODE_PRIVATE)
        refAmount.value = BigDecimal(pref.getString("ref_amount", "0.00"))
    }
    DisposableEffect(true) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                profileViewModel.getMyProfile()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            earningsViewModel.resetUIState()
        }
    }
    LaunchedEffect(earningsUIState.isActivateEarningsSuccess) {
        if(earningsUIState.isActivateEarningsSuccess){
            profileViewModel.getMyProfile()
            scope.launch{
                snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                    message = "Successfully",
                    type = SnackbarType.SUCCESS
                ))
            }
            earningsViewModel.resetUIState()
        }
    }
    LaunchedEffect(myProfile) {
        isChecked.value = myProfile.profile.is_earnings_activated
    }
    LaunchedEffect(earningsUIState.isActivateEarningsError) {
        if(earningsUIState.isActivateEarningsError){
            scope.launch{
                snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                    message = earningsUIState.activateEarningsErrorMessage,
                    type = SnackbarType.ERROR
                ))
            }
            earningsViewModel.resetUIState()
        }
    }

    LaunchedEffect(earningsUIState.isCashoutError) {
        if(earningsUIState.isCashoutError){
            scope.launch{
                snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                    message = earningsUIState.cashoutErrorMessage,
                    type = SnackbarType.ERROR
                ))
            }
            earningsViewModel.resetUIState()
        }
    }

    LaunchedEffect(earningsUIState.isCashoutSuccess) {
        if(earningsUIState.isCashoutSuccess){
            scope.launch{
                snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                    message = "Cashout Successful",
                    type = SnackbarType.SUCCESS
                ))
            }
            earningsViewModel.resetUIState()
        }
    }
    AppScaffold(
        topBar = { BackTopBar("Earnings", navController) },
        snackbarHostState = snackbarHostState
    ) {
        Column {
            Column(
                modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(horizontal = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 22.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BadgedBox(badge = { Badge(containerColor = MaterialTheme.colorScheme.secondary )
                    {Text(myProfile.profile.referred_users.size.toString(), style = MaterialTheme.typography.bodySmall)} }) {
                        Icon(Icons.Outlined.Group, "", tint = MaterialTheme.colorScheme.surface)
                    }
                    Text("${formatBigDecimalWithCommas(BigDecimal(myProfile.profile.referred_users.size)*refAmount.value)} VEC",  style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.surface)
                }
                Row (
                    modifier = Modifier.fillMaxWidth().padding(vertical = 22.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ){
                    Icon(Icons.Outlined.Timer, "Platform earnings", tint = MaterialTheme.colorScheme.surface)
                    Text("${myProfile.profile.unclaimed_earnings} VEC", style = MaterialTheme.typography.titleSmall,color = MaterialTheme.colorScheme.surface)
                }

                Column(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Total Earnings", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.surface)
                    Text("${formatBigDecimalWithCommas(myProfile.profile.unclaimed_earnings)} VEC", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.surface)
                }
            }

            Column (
                modifier = Modifier.padding( horizontal = 16.dp, vertical = 24.dp),
                verticalArrangement = Arrangement.spacedBy(21.dp)
            ){

                Row(
                    modifier = Modifier.fillMaxWidth().padding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(25.dp)
                    ) {
                        Icon(Icons.Outlined.Group, "", modifier = Modifier.size(24.dp))
                        Text("Referral code is: ${myProfile.profile.user_name}", style= MaterialTheme.typography.titleSmall)
                    }
                    IconButton(onClick = {
                        copyToClipboard(context, "Referral code", myProfile.profile.user_name)
                        scope.launch{
                            snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                                message = "Code copied!",
                                type = SnackbarType.SUCCESS
                            ))
                        }
                    }) {
                        Icon(Icons.Outlined.ContentCopy, "", modifier = Modifier.size(24.dp))
                    }

                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(25.dp)
                    ) {
                        Icon(Icons.Outlined.Money, "", modifier = Modifier.size(24.dp))
                        Text("Activate earnings", style= MaterialTheme.typography.titleSmall)
                        if(earningsUIState.isActivateEarningsLoading){   AnimatedPreloader(modifier = Modifier.size(size = 30.dp), MaterialTheme.colorScheme.primary)}
                    }
                    Switch(
                        checked = isChecked.value,
                        onCheckedChange = {
                            earningsViewModel.activateEarnings()
                                          },
                        modifier = Modifier.scale(0.7f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth().padding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(25.dp),
                        modifier = Modifier.clickable(onClick = {
                            navController.navigate(NavScreen.SelectEarningsScreen.route+"/${myProfile.profile.wallets}")
                        }).fillMaxWidth()
                    ) {
                        Icon(Icons.Default.AccountBalanceWallet, "", modifier = Modifier.size(24.dp))
                        Column {
                            Text("Select earnings wallet", style= MaterialTheme.typography.titleSmall)
                            Text("Address: ${myProfile.profile.earnings_wallet}", style= MaterialTheme.typography.bodySmall)
                        }

                    }

                }

                Row(
                    modifier = Modifier.fillMaxWidth().padding(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(25.dp),
                        modifier = Modifier.fillMaxWidth().clickable(onClick = {
                            earningsViewModel.cashOut()
                        })
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Send, "", modifier = Modifier.size(24.dp))
                        Text("Withdraw to wallet", style= MaterialTheme.typography.titleSmall)
                        if(earningsUIState.isCashoutLoading){   AnimatedPreloader(modifier = Modifier.size(size = 30.dp), MaterialTheme.colorScheme.primary)}
                    }

                }
            }

        }
    }

}

