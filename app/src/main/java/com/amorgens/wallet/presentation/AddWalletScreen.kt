package com.amorgens.wallet.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material.icons.sharp.AddCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amorgens.ui.AnimatedPreloader
import com.amorgens.ui.BackTopBar
import com.amorgens.ui.GeneralScaffold
import com.amorgens.wallet.data.WalletViewModel
import com.amorgens.wallet.domain.GetBalanceReq


@Composable
fun AddWalletScreen(navController: NavController, walletViewModel: WalletViewModel){
    val walletUIState = walletViewModel.walletUIState.collectAsState()

    // navigate back to previous screen after crating wallet and reset the navigate screen switch
    if(walletUIState.value.isAddWalletDone){
        navController.popBackStack()
        walletViewModel.updateIsAddWalletDone(false)
    }

    // show toast for success and error
    if (walletUIState.value.isSuccess){
        Toast.makeText(LocalContext.current, walletUIState.value.successMessage, Toast.LENGTH_SHORT).show()
        walletViewModel.clearSuccess()
    }
    if (walletUIState.value.isError){
        Log.d("XXXXX GOT ERROR", "YEt")
        Toast.makeText(LocalContext.current,walletUIState.value.errorMessage, Toast.LENGTH_SHORT).show()
        walletViewModel.clearError()
    }
    val walletAddress = remember {
        mutableStateOf("")
    }
    val walletPassPhrase = remember{
        mutableStateOf("")
    }
//    LaunchedEffect(walletUIState.value) {
//        Log.d("XXX UPDATE", "Updated")
//    }
    GeneralScaffold(topBar = { BackTopBar(pageName = "New Wallet", navController = navController) }, floatingActionButton = { /*TODO*/ }) {

        Column {
            Text(text = "Add an existing wallet")

            val amount = remember {
                mutableStateOf("")
            }
            Box(modifier = Modifier.fillMaxWidth()){
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedTextField(value = walletAddress.value,
                        onValueChange = {
                                        walletAddress.value = it
                        },
                        shape = RoundedCornerShape(20.dp),
                        placeholder = { Text(text = "Wallet Address") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                    )
                    OutlinedTextField(value = amount.value,
                        onValueChange = {walletPassPhrase.value = it},
                        shape = RoundedCornerShape(20.dp),
                        placeholder = { Text(text = "Pass Phrase") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp)
                    )

                    Button(onClick = {
                        walletViewModel.updateIsAddWalletButtonLoading(true)
                        val getBalanceReq = GetBalanceReq(address = walletAddress.value)
                        walletViewModel.getBalanceRemote(getBalanceReq)
                    },
                        modifier = Modifier.size(width = 200.dp, height = 50.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.surface
                        ),

                    ) {
                        if(walletUIState.value.isAddWalletButtonLoading){
                            AnimatedPreloader(modifier = Modifier.size(size = 50.dp))
                        }else {
                            Text(text = "Add")
                            Icon(imageVector = Icons.Sharp.Add, contentDescription = "Add" )
                        }

                    }
                }
            }
        }
    }
}