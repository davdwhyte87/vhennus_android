package com.amorgens.wallet.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Rocket
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amorgens.ui.BackTopBar
import com.amorgens.ui.GeneralScaffold


@Composable
fun TransferScreen(navController: NavController, walletAddress:String){
    GeneralScaffold(topBar = { BackTopBar(pageName = "Transfer", navController = navController) }, floatingActionButton = { /*TODO*/ }) {

        Column {
            Text(text = walletAddress,
                style = MaterialTheme.typography.titleLarge
            )

            val amount = remember {
                mutableStateOf("")
            }
            Box(modifier = Modifier.fillMaxWidth()){
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(value = amount.value,
                        onValueChange = {},
                        shape = RoundedCornerShape(20.dp),
                        placeholder = { Text(text = "Amount")},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                    )
                    OutlinedTextField(value = amount.value,
                        onValueChange = {},
                        shape = RoundedCornerShape(20.dp),
                        placeholder = { Text(text = "Wallet Address")},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                    )
                    OutlinedTextField(value = amount.value,
                        onValueChange = {},
                        shape = RoundedCornerShape(20.dp),
                        placeholder = { Text(text = "Pass Phrase")},
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                    )
                    
                    IconButton(onClick = { /*TODO*/ },
                        modifier = Modifier.size(width = 200.dp, height = 50.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row (
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ){
                            Text(text = "Send")
                            Icon(imageVector = Icons.Sharp.Rocket, contentDescription = "Send" )
                        }

                    }
                }
            }
        }
    }
}