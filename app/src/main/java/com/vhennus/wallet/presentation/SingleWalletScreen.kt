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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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


@Composable
fun SingleWalletScreen(
    address: String,
    navController: NavController,
    walletViewModel: WalletViewModel
){
    val lifecycleOwner = LocalLifecycleOwner.current
    // clear model data
    DisposableEffect(true) {
        //walletViewModel.clearModelData()

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                walletViewModel.getWalletRemote(GetWalletReq(address))
                walletViewModel.getExchangeRate()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            walletViewModel.clearModelData()
        }
    }
    // reset all ui data
    LaunchedEffect(true) {
        // clear state data
       walletViewModel.resetUIState()
    }
    // get single wallet from remote server
//    LaunchedEffect(true){
//        walletViewModel.getWalletRemote(GetWalletReq(address))
//    }
    val singleWallet = walletViewModel.singleWalletC.collectAsState().value
    val uiState = walletViewModel.walletUIState.collectAsState().value


    // handle errors
    if (uiState.isError){
        Log.d("XXXXX GOT ERROR", "YEt")
        Toast.makeText(LocalContext.current,uiState.errorMessage, Toast.LENGTH_SHORT).show()
        //walletViewModel.clearError()
    }

    val clipboardManager: androidx.compose.ui.platform.ClipboardManager = LocalClipboardManager.current
    val context = LocalContext.current
    GeneralScaffold(topBar = { BackTopBar("Wallet", navController) }, floatingActionButton = {  }) {
        var isExpanded = remember {
            mutableStateOf(false)
        }
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // page loader
            if (uiState.isSingleWalletPageLoading){
                AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.primary)
            }else {
                if(uiState.isError){
                    IconButton(onClick = {
                        walletViewModel.getWalletRemote(GetWalletReq(address))
                    }) {
                        Icon(imageVector = Icons.Outlined.Refresh, contentDescription = "Refresh page")
                    }
                }
            }

            // elevated card to show balance
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
                            text = String.format("%,.2f", singleWallet.chain.chain.last().balance),
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
                val menus = listOf(
                    WalletMenu("Send", Icons.Sharp.Rocket),
                    WalletMenu("Buy", Icons.Sharp.ArrowUpward),
                    WalletMenu("Sell", Icons.Sharp.ArrowDownward),
                    WalletMenu("Copy", Icons.Sharp.ContentCopy)
                )
                menus.forEachIndexed { index, walletMenu ->
                    WalletMenuItem(name = walletMenu.name, icon = walletMenu.icon ) {
                        when (walletMenu.name){
                            "Send"-> {
                                navController.navigate(NavScreen.TransferScreen.route+"/${address}")
                            }
                            "Buy"->{
                                navController.navigate(NavScreen.ShopCoinsScreen.route+"/${address}")
                            }
                            "Sell"->{
                                navController.navigate(NavScreen.CreateSellOrderScreen.route+"/"+address)
                            }
                            "Copy"->{
                                clipboardManager.setText(AnnotatedString(address))
                                Toast.makeText(context, "Address Copied!", Toast.LENGTH_SHORT).show()
                            }
                        }

                    }
                }
            }

            // text trnasaction
            Text(text = "Transactions",
                style = MaterialTheme.typography.bodyLarge
            )

            // transactionlist

            TransactionList(address, singleWallet.chain.chain)

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
            .size(width = 60.dp, height = 60.dp),
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
                modifier = Modifier.size(24.dp)
            )
            Text(text = name, style = MaterialTheme.typography.bodySmall)
        }
    }
}


@Preview
@Composable
fun SingleWalletScreenxx(){
//    HomeTopBar("Wallet")
    Column(
        verticalArrangement = Arrangement.spacedBy(32.dp)
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
                IconButton(onClick = {},
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = MaterialTheme.colorScheme.surface
                    )
                    ) {
                    Icon(Icons.Default.KeyboardArrowDown, "", modifier = Modifier.size(30.dp))
                }
                Text("USD", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.surface)
                Text("2,000.00", modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.surface)
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
                Text("Wallet Balance", style = MaterialTheme.typography.titleSmall,
                    color =MaterialTheme.colorScheme.surface )
            }
            Row (
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ){
                Text("2,000,000", style = MaterialTheme.typography.headlineLarge,
                    color =MaterialTheme.colorScheme.surface )
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            WalletMenuItem("Copy", Icons.Outlined.CopyAll)
            Spacer(modifier = Modifier.width(74.dp))
            WalletMenuItem("Send", Icons.AutoMirrored.Default.Send)
        }

        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            val transactions = listOf<Transaction>(
                Transaction(
                    "", receiverAddress = "jamine",
                    senderAddress = "berry",
                    "40000",
                    "34th June, 3035"
                ),
                Transaction(
                    "",
                    receiverAddress = "brennu",
                    senderAddress = "sassy",
                    "3000000",
                    "34th June, 3035"
                )
            )
            Text("Wallet Activities", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(transactions){transaction->
                    TransactionListItem2("sassy", transaction) {

                    }
                }
            }
        }
    }
}



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

@Composable
fun WalletMenuItem(name: String, icon: ImageVector){
    Button(onClick = {},
        colors = ButtonDefaults.buttonColors(
            contentColor = MaterialTheme.colorScheme.surface,
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.sizeIn(minWidth = 74.dp, minHeight = 56.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, name, Modifier.size(25.dp))
            Text(name, style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.surface )
        }

    }
}