package com.vhennus.earnings.presentation

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
fun SelectWalletScreen(){
    data class WalletData(
        val address:String,
        val amount:String
    )
    val items = listOf<WalletData>(
        WalletData(address = "nasioodo_929js", amount = "20,000,000"),
        WalletData(address = "beeny()heriom9", amount = "200,000"),
        WalletData(address = "galaxyboytunchi", amount = "340,000"),
        WalletData(address = "raymonedbay", amount = "40,000,000"),
        WalletData(address = "gracianavaru0_", amount = "6,000,000")
    )
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items.forEach {item->
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),

            ) {
                IconButton(onClick = {},
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary),
                    modifier = Modifier.size(50.dp)
                ) {
                    Icon(
                        Icons.Outlined.AccountBalanceWallet, "", tint = MaterialTheme.colorScheme.surface,
                        modifier = Modifier.size(25.dp))
                }

                Column (
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ){

                    Text(item.address, style= MaterialTheme.typography.titleSmall)
                    Text(item.amount +" VEC", style= MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}