package com.amorgens.trade.presentation

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
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amorgens.NavScreen
import com.amorgens.trade.domain.SellOrder
import com.amorgens.ui.theme.Black
import com.amorgens.ui.theme.Gray
import com.amorgens.ui.theme.Green
import com.amorgens.ui.theme.Red
import com.amorgens.wallet.presentation.WalletListItem


@Composable
fun sellOrderList(navController: NavController){
    val orders = listOf(1,9,90,0)
    LazyColumn(
        modifier = Modifier
            .padding(bottom = 10.dp)
            .fillMaxHeight()
    ) {
        items(orders){order->
           sellOrderListItem(navController)
        }
    }
}


@Composable
fun sellOrderListItem(navController: NavController){
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Gray),
        modifier = Modifier
            .padding(top = 10.dp)
            .clickable(onClick = {
                navController.navigate(NavScreen.SingleOrderScreen.route+"/djkomod")
            }),
    ) {
        Column(
            modifier= Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            // is online or offline
            Row(
            ) {
                Icon(imageVector = Icons.Filled.Circle,
                    contentDescription = "",
                    tint = Green,
                    modifier = Modifier.size(20.dp).padding(2.dp)
                )
                Text(text = "Online", color = MaterialTheme.colorScheme.secondary)
            }
            // order information
            Row  (
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ){
                // username and limit
                Column {
                    Text(text = "Uremzinke_100",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(text = "Limit : Min 200,000 - Max 5,000,000",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                // amount and value
                Column (

                ) {
                    Text(text = "200,000 Kc",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(text = "NGN 5,000,000",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.secondary
                    )

                }
            }
            // ratings
            Row (
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                // rating up
                Row (
                    modifier = Modifier.padding(5.dp)
                ){
                    Icon(imageVector = Icons.Filled.ThumbUp,
                        contentDescription = "rating",
                        tint = Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(text = "100",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                //reting down
                Row (
                    modifier = Modifier.padding(5.dp)
                ) {
                    Icon(imageVector = Icons.Filled.ThumbDown,
                        contentDescription = "rating",
                        tint = Red,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(text = "2",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }
        }
    }

}