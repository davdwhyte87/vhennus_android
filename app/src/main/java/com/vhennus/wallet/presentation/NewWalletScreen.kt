package com.vhennus.wallet.presentation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.sharp.AddCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold
import com.vhennus.wallet.data.WalletViewModel
import com.vhennus.wallet.domain.CreateWalletReq
import com.vhennus.wallet.domain.GetWalletReq
import java.math.BigDecimal


@Composable
fun NewWalletScreen(navController:NavController, walletViewModel: WalletViewModel){
    // clear model data
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    DisposableEffect(true) {
        //walletViewModel.clearModelData()

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                walletViewModel.getUserName(context)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            walletViewModel.clearModelData()
        }
    }
    val walletUIState = walletViewModel.walletUIState.collectAsState()
    val scope = rememberCoroutineScope()
    val userName = walletViewModel.userName.collectAsState()

    // navigate back to previous screen after crating wallet and reset the navigate screen switch
    if(walletUIState.value.createWalletScreenNavigateBack){
        navController.popBackStack()
        walletViewModel.updateCreateWalletScreenNavigateBack(false)
    }

    // show toast for success and error
    if (walletUIState.value.createWalletSuccess){
        Toast.makeText(LocalContext.current, walletUIState.value.createWalletSuccessMessage, Toast.LENGTH_SHORT).show()
        walletViewModel.clearCreateWalletSuccessData()
    }
    if (walletUIState.value.createWalletError){
        Toast.makeText(LocalContext.current, walletUIState.value.createWalletErrorMessage, Toast.LENGTH_SHORT).show()
        walletViewModel.clearCreateWalletErrorData()
    }
    LaunchedEffect(walletUIState.value) {
        Log.d("XXX UPDATE", "Updated")
    }
    GeneralScaffold(topBar = { BackTopBar(pageName = "New Wallet", navController = navController) }, floatingActionButton = { /*TODO*/ }) {

        Column {

            val walletAddress = remember {
                mutableStateOf("")
            }
            val walletPassPhrase = remember{
                mutableStateOf("")
            }
            val walletName = remember{
                mutableStateOf("")
            }
            val vcIDUserName = remember{
                mutableStateOf("")
            }
            var checked by remember { mutableStateOf(false) }

            Box(modifier = Modifier.fillMaxWidth()){
                Column (
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
//                    OutlinedTextField(value = walletName.value,
//                        onValueChange = {
//                            walletName.value = it
//                        },
//                        shape = RoundedCornerShape(20.dp),
//                        placeholder = { Text(text = "Wallet Name") },
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(end = 16.dp)
//                    )

                    OutlinedTextField(value = userName.value,
                        onValueChange = {
                            vcIDUserName.value = userName.value
                        },
                        shape = RoundedCornerShape(20.dp),
                        placeholder = { Text(text = "VcID Username") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
                        enabled = false
                    )

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
                    OutlinedTextField(value = walletPassPhrase.value,
                        onValueChange = {
                                        walletPassPhrase.value = it
                        },
                        shape = RoundedCornerShape(20.dp),
                        placeholder = { Text(text = "Pass Phrase") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 16.dp),
                        enabled = !checked
                    )
                    Checkbox(
                        checked = checked,
                        onCheckedChange = { checked = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Use Vhenncoin ID password.")
                    Button(onClick = {
                        val createWalletReq = CreateWalletReq(
                            walletAddress.value.lowercase(),
                            walletPassPhrase.value,
                            "",
                           userName.value,
                            checked
                        )
                        // check validation
                        if (!formValidation(context, createWalletReq)){
                            return@Button
                        }
                        // loading button
                        walletViewModel.updateIsCreateWalletButtonLoading(true)


                        walletViewModel.createWallet(createWalletReq)

                    },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.surface
                        ),
                        modifier = Modifier.size(width = 200.dp, height = 50.dp)
                    ) {
                        Log.d("XXXXCC WalletButton Loading", walletUIState.value.isCreateWalletButtonLoading.toString())
                        if(walletUIState.value.isCreateWalletButtonLoading){
                            AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
                        }else {
                            Icon(imageVector = Icons.Sharp.AddCard, contentDescription = "Create" )
                            Text(text = "Create")
                        }
                    }
                }
            }
        }
    }
}

fun formatter(action: String, data:String):String{
    val message:String = action+"\n"+data+"\n"+"\n0"+"\n0"+"\n0"+"\n"
    return message
}

fun formValidation(context: Context, req:CreateWalletReq):Boolean{


    if (req.address.isBlank() || req.address.isEmpty()){
        Toast.makeText(context, "Blank  address", Toast.LENGTH_SHORT).show()
        return false
    }
    if (req.vcid_username.isEmpty()){
        Toast.makeText(context, "Empty VCID", Toast.LENGTH_SHORT).show()
        return false
    }

    if (req.address.contains(" ")){
        Toast.makeText(context, "No space allowed in address", Toast.LENGTH_SHORT).show()
        return false
    }

    if (req.vcid_username.contains(" ")){
        Toast.makeText(context, "No space allowed VCID", Toast.LENGTH_SHORT).show()
        return false
    }


    return true
}