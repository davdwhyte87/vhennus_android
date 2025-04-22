package com.vhennus.wallet.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CopyAll
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material.icons.sharp.ArrowDownward
import androidx.compose.material.icons.sharp.ArrowUpward
import androidx.compose.material.icons.sharp.ContentCopy
import androidx.compose.material.icons.sharp.Rocket
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.vhennus.NavScreen
import com.vhennus.general.presentation.AppScaffold
import com.vhennus.general.presentation.CustomSnackbarVisuals
import com.vhennus.general.presentation.SnackbarType
import com.vhennus.general.utils.CLog
import com.vhennus.general.utils.formatBigDecimalWithCommas
import com.vhennus.profile.data.ProfileViewModel
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold
import com.vhennus.ui.HomeTopBar
import com.vhennus.ui.theme.Green
import com.vhennus.ui.theme.Red
import com.vhennus.ui.theme.White
import com.vhennus.wallet.data.WalletViewModel
import com.vhennus.wallet.domain.GetWalletReq
import com.vhennus.wallet.domain.Transaction
import java.math.BigDecimal

@Composable
fun SingleWalletScreen(
    address: String,
    navController: NavController,
    walletViewModel: WalletViewModel
){

    val lifecycleOwner = LocalLifecycleOwner.current

    // clear model data
    DisposableEffect(true) {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {

            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            //walletViewModel.clearModelData()
        }
    }




    var expanded = remember { mutableStateOf(false) }
    val wallets = walletViewModel.allWallets.collectAsState().value
    val walletUIState = walletViewModel.walletUIState.collectAsState().value
    val context = LocalContext.current
    var totalAsset = remember { mutableStateOf(BigDecimal.ZERO) }
    var totalAssetString = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }




    LaunchedEffect(walletUIState.isGetAllWalletsError) {
        if (walletUIState.isGetAllWalletsError){
            //context.showCustomErrorToast(walletUIState.getAllWalletsErrorMessage)
            snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                message = walletUIState.getAllWalletsErrorMessage,
                type = SnackbarType.ERROR
            ))
        }
    }



    AppScaffold(
        topBar ={ HomeTopBar("Wallets", navController)} ,
        snackbarHostState=snackbarHostState
    ) {
        Column(
        ) {

            Column(
                modifier = Modifier.background(MaterialTheme.colorScheme.primary)
                    .padding(32.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)

            ) {

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box {
                        IconButton(onClick = {expanded.value = true},
                            colors = IconButtonDefaults.iconButtonColors(
                                contentColor = MaterialTheme.colorScheme.surface
                            )
                        ) {
                            Icon(Icons.Default.KeyboardArrowDown, "", modifier = Modifier.size(30.dp))
                        }

                        DropdownMenu(
                            expanded = expanded.value,
                            onDismissRequest = { expanded.value = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("NGN") },
                                onClick = {
                                    // Handle click
                                    expanded.value = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("USD") },
                                onClick = {
                                    // Handle click
                                    expanded.value = false
                                }
                            )
                        }
                    }

                    Text("USD", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.surface)
                    Text("13,000.00", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.surface)
                    IconButton(onClick = {},
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Icon(Icons.Default.VisibilityOff, "Visibility",
                            modifier = Modifier.size(25.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Row (
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text("Total Asset", style = MaterialTheme.typography.titleSmall,
                        color =MaterialTheme.colorScheme.surface )
                }
                Row (
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ){
                    Text(totalAssetString.value, style = MaterialTheme.typography.headlineMedium,
                        color =MaterialTheme.colorScheme.surface )
                }
            }
            Spacer(modifier=Modifier.height(32.dp))

            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                WalletMenuItem("Add", Icons.Outlined.Add){
                    navController.navigate(NavScreen.AddWalletScreen.route)
                }
                Spacer(modifier = Modifier.width(74.dp))
                WalletMenuItem("New", Icons.Outlined.Check){
                    navController.navigate(NavScreen.NewWalletScreen.route)
                }
            }
            Spacer(modifier=Modifier.height(32.dp))

            Column(
                modifier = Modifier.padding(24.dp)
            ) {

                Text("Wallet List", style = MaterialTheme.typography.titleMedium)
                LazyColumn {
                    items(wallets){wallet->
                        WalletListItem(wallet) {
                            navController.navigate(NavScreen.SingleWalletScreen.route+"/${wallet.address}")
                        }
                    }
                }
            }
        }
    }
}


data class WalletMenu(
    val name:String,
    val icon:ImageVector
)


@Composable
fun TransactionListItem2(address:String, transaction: Transaction, onclick:()->Unit){
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(top = 16.dp)
    ) {
        IconButton(onClick = {},
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.size(50.dp)
            ) {
            Icon(
                if (transaction.senderAddress == address) Icons.Filled.Send else Icons.Filled.Add, "", tint = MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(25.dp))
        }
        Row(

        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("Received", style = MaterialTheme.typography.titleSmall)
                Text(transaction.receiverAddress, style = MaterialTheme.typography.bodySmall)
            }

            Column {
                Text("+2,000,000,000 VEC", style = MaterialTheme.typography.bodyMedium, color = if (transaction.senderAddress == address) Red else Green)
                Text(transaction.dateTime, style = MaterialTheme.typography.bodyMedium, color = if (transaction.senderAddress == address) Red else Green)
            }
        }

    }
}
