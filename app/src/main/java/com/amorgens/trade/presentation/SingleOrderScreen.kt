package com.amorgens.trade.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amorgens.trade.data.OrderViewModel
import com.amorgens.ui.BackTopBar
import com.amorgens.ui.GeneralScaffold
import com.amorgens.ui.theme.Blue_Gray
import com.amorgens.ui.theme.Green
import com.amorgens.ui.theme.Red


@Composable
fun singleOrderScreen(
    navController: NavController,
    orderViewModel: OrderViewModel,
    id: String
){
    val isExpanded = remember {
        mutableStateOf(false)
    }

    val message = remember {
        mutableStateOf("")
    }
    GeneralScaffold(topBar = { BackTopBar(pageName = "Order details", navController = navController) }, floatingActionButton = { /*TODO*/ }) {
        Column (
            modifier = Modifier.fillMaxSize()
        ){

            // top buttons
            Row {
                // confirm button
                Button(onClick = {  },
                    colors = ButtonDefaults.buttonColors(containerColor = Green)
                ) {
                    Text(text = "Confirm Transaction",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.surface
                    )

                }

                // cancel button
                Button(onClick = {  },
                    colors = ButtonDefaults.buttonColors(containerColor = Red)
                ) {
                    Text(text = "Cancel",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.surface
                    )

                }
            }

            // payment details
            Column {
                Row (
                    modifier = Modifier.clickable(onClick = {
                        if(isExpanded.value){
                            isExpanded.value = false
                        }else{
                            isExpanded.value = true
                        }
                    })
                ){
                    Text(text = "Payment Details ")
                    Icon(imageVector = Icons.Filled.ArrowDropDown, contentDescription = "")

                }
                if(isExpanded.value){
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Blue_Gray),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column (
                            modifier = Modifier.padding(10.dp)
                        ) {
                            Text(text = "Account Number: 829839830")
                            Text(text = "Account Name: Derick Jones")
                            Text(text = "bank Name: WUINM")
                        }
                    }

                }
            }

            // chat view
            val scrollState = rememberScrollState()
            Column (
                modifier = Modifier
                    .verticalScroll(scrollState)
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 40.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Card (
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.End),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiary)
                ) {
                    Text(text = "Only way I can soo this going  is if t becomes cleanr that at thjen end of the day thekk ",
                        color = MaterialTheme.colorScheme.secondary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(15.dp)
                    )
                }

                Card (
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .align(Alignment.Start),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Only way I can soo this going  is if t becomes cleanr that at thjen end of the day thekk ",
                        color = MaterialTheme.colorScheme.surface,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(15.dp)
                    )
                }
            }

            // chat text box

            Row (

            ){
                OutlinedTextField(value = message.value,
                    onValueChange = {
                        message.value = it
                    },
                    shape = RoundedCornerShape(20.dp),
                    placeholder = { Text(text = "Message") },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .padding(end = 16.dp)
                )
                Button(onClick = { },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Send",
                        color = MaterialTheme.colorScheme.surface)
                }

            }

        }
    }



}