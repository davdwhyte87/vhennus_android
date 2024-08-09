package com.amorgens.trade.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.ThumbDown
import androidx.compose.material.icons.filled.ThumbUp
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
import com.amorgens.ui.GeneralScaffold
import com.amorgens.ui.HomeTopBar
import com.amorgens.ui.theme.Black
import com.amorgens.ui.theme.Gray
import com.amorgens.ui.theme.Green
import com.amorgens.ui.theme.Red


@Composable
fun myOrdersScreen(
    navController: NavController
){
    GeneralScaffold(topBar = {HomeTopBar(pageName = "My Orders", navController )}, floatingActionButton = { /*TODO*/ }) {
        Column {
            Text(text = "Sell Orders",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            mySellOrdersList(navController)
            Text(text = "Buy Orders",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.padding(bottom = 20.dp)
            )
            myBuyOrdersList(navController )
        }
    }
}



@Composable
fun mySellOrdersList(navController: NavController){
    val items = listOf(1, 2)
    items.forEachIndexed { index,   i ->
        mySellOrderListItem(navController)
    }
}

@Composable
fun mySellOrderListItem(navController: NavController){
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Gray),
        modifier = Modifier
            .padding(top = 10.dp)
            .clickable(onClick = {
                navController.navigate(NavScreen.SingleSellOrderScreen.route)
            }),
    ) {
        Column(
            modifier= Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
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

        }
    }

}


@Composable
fun myBuyOrdersList(navController: NavController){
    val items = listOf(1, 2)
    items.forEachIndexed { index,   i ->
        myBuyOrderListItem(navController)
    }
}

@Composable
fun myBuyOrderListItem(navController: NavController){
    Card(
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Gray),
        modifier = Modifier
            .padding(top = 10.dp)
            .clickable(onClick = {
                navController.navigate(NavScreen.SingleOrderScreen.route + "/djkomod")
            }),
    ) {
        Column(
            modifier= Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {


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

        }
    }

}