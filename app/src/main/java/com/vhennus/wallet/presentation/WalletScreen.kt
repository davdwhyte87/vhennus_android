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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddCard
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material.icons.outlined.KeyboardArrowDown
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.vhennus.NavScreen
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.GeneralScaffold
import com.vhennus.ui.HomeTopBarWithOptions
import com.vhennus.wallet.data.WalletViewModel
import com.vhennus.wallet.domain.Transaction
import com.vhennus.wallet.domain.Wallet
import java.math.BigDecimal


@Composable
fun WalletScreen(navController: NavController, walletViewModel: WalletViewModel){
    val lifecycleOwner = LocalLifecycleOwner.current
    // clear model data
    DisposableEffect(true) {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                walletViewModel.getAllWallets()
                walletViewModel.getExchangeRate()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            //walletViewModel.clearModelData()
        }
    }


    // get all wallets
//    LaunchedEffect(true) {
//        walletViewModel.getAllWallets()
//    }


    val wallets = walletViewModel.allWallets.collectAsState().value

    // update wallet locally
    LaunchedEffect(Unit) {
        //walletViewModel.getAllWallets()
        val walletNames = mutableListOf<String>()

//        wallets.forEachIndexed { index, wallet ->
//            walletNames.add(wallet.walletAddress)
//        }
//        Log.d("WALLET NAMES XXXX", walletNames.toString())
//        walletViewModel.updateWalletsLocal(walletNames)
    }

    val walletUIState = walletViewModel.walletUIState.collectAsState().value


    // show error toast if loading wallets data fails
    if (walletUIState.isError){
        Log.d("XXXXX GOT ERROR", "YEt")
        Toast.makeText(LocalContext.current,walletUIState.errorMessage, Toast.LENGTH_SHORT).show()
        walletViewModel.clearError()
    }

    var totalBalance = BigDecimal("0.0")

    wallets.forEachIndexed { index, wallet ->
        totalBalance = totalBalance.add(wallet.balance)
        //Log.d("XXDECIMAL", "${scaledBigDecimal}")
    }
    Log.d("XXFLOAT ${wallets.getOrNull(0)?.balance}", "totalBalance")
    GeneralScaffold(topBar = { HomeTopBarWithOptions("Wallets", navController) }, floatingActionButton = {  }) {
        var isExpanded = remember {
            mutableStateOf(false)
        }
        Column  (
            modifier = Modifier.fillMaxHeight(),
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
                        Text(text =String.format("%,.2f", totalBalance) ,
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
                        Text(text =  "NGN "+String.format("%,.2f", getExchangeValue(LocalContext.current, totalBalance)),
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
                                Text(text = "NGN",
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

                val menus = listOf(
                    WalletMenu("Add", Icons.Default.Add),
                    WalletMenu("New", Icons.Default.AddCard),
                    WalletMenu("Orders", Icons.Default.Storage)
                )
                menus.forEachIndexed { index, walletMenu ->
                    WalletMenuItem(iconImage = walletMenu.icon , menuTitle = walletMenu.name) {
                        if (index == 0){
                            navController.navigate(NavScreen.AddWalletScreen.route)
                        }

                        if (index ==1){
                            navController.navigate(NavScreen.NewWalletScreen.route)
                        }

                        if (index==2){
                            navController.navigate(NavScreen.MyOrdersScreen.route)
                        }

                    }
                }
            }

            Log.d("Syncing?? XXX", walletUIState.isSyncingLocalWallet.toString())
            if (walletUIState.isSyncingLocalWallet){

                AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.primary)
            }

            WalletList(navController,wallets)
        }
    }
}



@Preview
@Composable
fun WalletScreenxxx(){
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
                Text("13,000,000,000 VEC", style = MaterialTheme.typography.headlineLarge,
                    color =MaterialTheme.colorScheme.surface )
            }
        }

        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            WalletMenuItem("Add", Icons.Outlined.Add)
            Spacer(modifier = Modifier.width(74.dp))
            WalletMenuItem("New", Icons.Outlined.Check)
        }

        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            val wallets = listOf<Wallet>(
                Wallet(
                    walletName = "saidi numer",
                    walletAddress = "arsenal_xenno_mobia",
                    balance = BigDecimal("200000")
                ),
                Wallet(
                    walletName = "saidi numer",
                    walletAddress = "romaneshalone#2900",
                    balance = BigDecimal("39490000")
                )
            )
            Text("Wallet List", style = MaterialTheme.typography.titleMedium)
            LazyColumn {
                items(wallets){wallet->
                    WalletListItem(wallet) {

                    }
                }
            }
        }
    }
}

