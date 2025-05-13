package com.vhennus.earnings.presentation

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.outlined.Wallet
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vhennus.ui.theme.Red

@Preview
@Composable
fun EarningsScreen(){
    var isChecked = remember { mutableStateOf(true) }
    Column {
        Column(
            modifier = Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.primary).padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BadgedBox(badge = { Badge(containerColor = MaterialTheme.colorScheme.secondary ){Text("240", style = MaterialTheme.typography.bodySmall)} }) {
                    Icon(Icons.Outlined.Group, "", tint = MaterialTheme.colorScheme.surface)
                }
                Text("249,000 VEC",  style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.surface)
            }
            Row (
                modifier = Modifier.fillMaxWidth().padding(vertical = 22.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ){
                Icon(Icons.Outlined.Money, "Platform earnings", tint = MaterialTheme.colorScheme.surface)
                Text("500,000,000 VEC", style = MaterialTheme.typography.titleSmall,color = MaterialTheme.colorScheme.surface)
            }

            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 48.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Total Earnings", style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.surface)
                Text("20,000,000,000 VEC", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.surface)
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
                    Text("Refer a friend", style= MaterialTheme.typography.titleSmall)
                }
                IconButton(onClick = {}) {
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
                }
                Switch(
                    checked = isChecked.value,
                    onCheckedChange = { isChecked.value = it },
                    modifier = Modifier.scale(0.7f)
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(25.dp)
                ) {
                    Icon(Icons.Default.AccountBalanceWallet, "", modifier = Modifier.size(24.dp))
                    Column {
                        Text("Selected earnings wallet", style= MaterialTheme.typography.titleSmall)
                        Text("Address: melonia_eiu889", style= MaterialTheme.typography.bodySmall)
                    }

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
                    Icon(Icons.AutoMirrored.Filled.Send, "", modifier = Modifier.size(24.dp))
                    Text("Withdraw to wallet", style= MaterialTheme.typography.titleSmall)
                }

            }
        }

    }
}

