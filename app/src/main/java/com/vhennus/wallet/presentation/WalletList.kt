package com.vhennus.wallet.presentation

import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vhennus.NavScreen
import com.vhennus.general.utils.CLog
import com.vhennus.general.utils.formatBigDecimalWithCommas
import com.vhennus.ui.theme.Green
import com.vhennus.ui.theme.Red
import com.vhennus.wallet.data.WalletViewModel
import com.vhennus.wallet.domain.Account
import com.vhennus.wallet.domain.Transaction
import com.vhennus.wallet.domain.Wallet
import java.math.BigDecimal


@Composable
fun WalletList(navController: NavController, wallets:List<Wallet>){
//    val wallets = listOf(
//        Wallet("","Jerrybon", "Jerrybon22", "8909393"),
//                Wallet("","Norebu", "Greenvile", "459330939")
//    )
    LazyColumn(
        modifier = Modifier.padding(bottom = 200.dp).fillMaxHeight()
    ) {
        items(wallets){wallet->
            WalletListItem(wallet = wallet, navController)
        }
    }
}


@Composable
fun WalletListItem(wallet: Wallet, navController: NavController){
    val context = LocalContext.current

    ElevatedCard(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier.padding(top = 10.dp).clickable(onClick = {
            navController.navigate(NavScreen.SingleWalletScreen.route+"/"+wallet.walletAddress)
        }),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
    ) {
        Row (modifier=Modifier.fillMaxWidth().padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = wallet.walletAddress,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Column {
                Text(text = String.format("%,.2f", wallet.balance)+"Kc",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(text = "NGN "+String.format("%,.2f", getExchangeValue(context, wallet.balance)),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}

fun getExchangeValue(application: Context, amount:BigDecimal):BigDecimal{
    val sharedPreferences = application.getSharedPreferences("exchange_rates", Context.MODE_PRIVATE)
    try {
        val rate = BigDecimal( sharedPreferences.getString("NGN", "0.5"))
        return rate.multiply(amount)
    }catch (e:Exception){
        return BigDecimal("0.00")
    }
}



@Composable
fun WalletListItem(
    wallet: Account,
    currency:String,
    usdPrice: BigDecimal,
    ngnPrice: BigDecimal,
    isBalanceHidden: Boolean,
    onclick:()->Unit,

){
    val balanceString = remember { mutableStateOf("") }

    LaunchedEffect(currency) {
        when (currency){
            "NGN"-> {
                balanceString.value= formatBigDecimalWithCommas((wallet.balance*usdPrice)*ngnPrice)
            }
            "USD" -> {
                balanceString.value= formatBigDecimalWithCommas(wallet.balance*usdPrice)
            }
        }
    }

    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.padding(top = 16.dp).clickable(onClick = {
            onclick()
        })
    ) {
        IconButton(onClick = {},
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary),
            modifier = Modifier.size(50.dp)
        ) {
            Icon(
                 Icons.Outlined.AccountBalanceWallet, "", tint = MaterialTheme.colorScheme.surface,
                modifier = Modifier.size(25.dp))
        }
        Row(

        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(wallet.address, style = MaterialTheme.typography.titleSmall)
                Text(if (isBalanceHidden){"****"}else{ formatBigDecimalWithCommas(wallet.balance) + " VEC"}, style = MaterialTheme.typography.bodySmall)
            }
            Column(
                modifier = Modifier
            ) {
                Text(if (isBalanceHidden){"****"}else{ balanceString.value+currency}, style = MaterialTheme.typography.titleSmall)
            }

        }

    }
}
