package com.amorgens.wallet.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.amorgens.NavScreen
import com.amorgens.ui.theme.Green
import com.amorgens.wallet.domain.Block
import com.amorgens.wallet.domain.Transaction


@Composable
fun  TransactionList(blocks: List<Block>){
//    val transactions = listOf(
//        Transaction("", "Renuubomi", "300", "34th, may, 2024"),
//        Transaction("", "Lumannie", "4,9900", "3th, Joune, 2024"),
//        Transaction("", "Gerrinu_899", "4,9900", "3th, Joune, 2024")
//    )

    blocks.forEachIndexed { index, block ->
        val transaction = Transaction(block.id,block.receiver_address, block.sender_address,block.amount.toString(), block.date_created)

        TransactionListItem(transaction = transaction) {
            // nothing
        }
    }
}

@Composable
fun TransactionListItem( transaction: Transaction, onclick:()->Unit){
    ElevatedCard(
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .padding(top = 10.dp)
            .clickable(onClick = {
                onclick
            }),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
    ) {
        Row (modifier= Modifier
            .fillMaxWidth()
            .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = transaction.senderAddress,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.secondary
            )
            Column (
                horizontalAlignment = Alignment.End
            ){
                Text(text = transaction.amount+"Kc",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Green
                )
                Text(text = transaction.dateTime,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
