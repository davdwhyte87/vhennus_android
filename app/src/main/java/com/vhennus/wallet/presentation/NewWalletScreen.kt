package com.vhennus.wallet.presentation

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.sharp.AddCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.vhennus.general.presentation.AppButtonLarge
import com.vhennus.general.presentation.AppScaffold
import com.vhennus.general.presentation.CustomSnackbarVisuals
import com.vhennus.general.presentation.InputFieldWithLabel
import com.vhennus.general.presentation.SnackbarType
import com.vhennus.general.utils.CLog
import com.vhennus.general.utils.KeyGenerator
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold
import com.vhennus.ui.theme.Gray2
import com.vhennus.ui.theme.Red
import com.vhennus.ui.theme.White
import com.vhennus.wallet.data.WalletViewModel
import com.vhennus.wallet.domain.CreateWalletReq
import com.vhennus.wallet.domain.GetWalletReq
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal



fun formatter(action: String, data:String):String{
    val message:String = action+"\n"+data+"\n"+"\n0"+"\n0"+"\n0"+"\n"
    return message
}

fun createWalletFormValidation(
    context: Context,
    snackbarHostState: SnackbarHostState,
    address: String,
    seed_phrase:String,
    confirm_seed_phrase:String,
):Boolean{
    val scope = CoroutineScope(Dispatchers.Main)
    if (address.isBlank() || address.isEmpty()){
        scope.launch{
            snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                message = "Address cannot be blank",
                type = SnackbarType.ERROR
            ))
        }

        return false
    }

    if(seed_phrase != confirm_seed_phrase){
        scope.launch{
            snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                message = "Seed phrases do not match",
                type = SnackbarType.ERROR
            ))
        }

        return false
    }

    if (address.contains(" ")){
        scope.launch{
            snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                message = "No spaces allowed in address",
                type = SnackbarType.ERROR
            ))
        }

        return false
    }

    val isLowercase = address.all { it.isLowerCase() || !it.isLetter() }

    if(!isLowercase){
        scope.launch{
            snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                message = "Address must be all lowercase",
                type = SnackbarType.ERROR
            ))
        }

        return false
    }
    return true
}



@Composable
fun NewWalletScreen(
    navController:NavController,
    walletViewModel: WalletViewModel
){
    val snackbarHostState = remember { SnackbarHostState() }
    val address = remember { mutableStateOf("") }
    val seed_phrase = remember { mutableStateOf("") }
    val confirm_seed_phrase = remember { mutableStateOf("") }
    val wallet_name = remember { mutableStateOf("") }
    val public_key = remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val walletUIState = walletViewModel.walletUIState.collectAsState().value
    val scrollState = rememberScrollState()

    LaunchedEffect(walletUIState.createWalletSuccess) {
        if (walletUIState.createWalletSuccess){
            snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                message = "Wallet Created",
                type = SnackbarType.SUCCESS
            ))
            walletViewModel.resetUIState()
        }
    }

    DisposableEffect(true) {


        onDispose {
            walletViewModel.resetUIState()
        }
    }

    LaunchedEffect(walletUIState.createWalletError) {
        if (walletUIState.createWalletError){
            snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                message = walletUIState.createWalletErrorMessage,
                type = SnackbarType.ERROR
            ))
            walletViewModel.resetUIState()
        }
    }

    AppScaffold(
        topBar = {BackTopBar("Create Wallet", navController)},
        snackbarHostState =  snackbarHostState
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp).fillMaxSize().verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {

//            Text("Lets start by creating your wallet address, and seed phrase.",
//                style = MaterialTheme.typography.bodyMedium
//            )

            // input fields
            Column(
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {

                InputFieldWithLabel(
                    data = address,
                    labelText = "Create wallet address",
                    placeHolderText = "Public address (Eg. belgium30@)"
                )

                InputFieldWithLabel(
                    data = wallet_name,
                    labelText = "Create wallet name",
                    placeHolderText = "shopping funds"
                )


                InputFieldWithLabel(
                    data = seed_phrase,
                    labelText = "Create seed phrase",
                    placeHolderText = "Welington_Johnson4300#89"
                )

                InputFieldWithLabel(
                    data = confirm_seed_phrase,
                    labelText = "Confirm seed phrase",
                    placeHolderText = "Stanly is the one true king 0938@"
                )
            }

            // caution text
            Row (
                horizontalArrangement = Arrangement.spacedBy(15.dp)
            ){
                IconButton(onClick = {


                },
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Red,
                        contentColor = Red
                    ),
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(Icons.Default.Info, "",
                        modifier = Modifier.size(30.dp),
                        tint = White
                    )
                }

                Text("Vhennus does not save your seed phrase, do not forget it because it cannot be changed or recovered! " +
                        "",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding( end = 10.dp).alpha(0.6f)
                )
            }

            AppButtonLarge(text = "Create Wallet",
                isLoading = walletUIState.isCreateWalletButtonLoading,

                ) {

                if (!createWalletFormValidation(context, address = address.value,
                        seed_phrase=seed_phrase.value,
                        confirm_seed_phrase=confirm_seed_phrase.value,
                        snackbarHostState = snackbarHostState
                    )){
                    return@AppButtonLarge
                }


                val (priv,pub) = KeyGenerator.generateKeysFromSeed(seed_phrase.value)
                CLog.debug("PUB KEY", pub)
                val req = CreateWalletReq(
                    address.value, wallet_name.value, pub
                )

                walletViewModel.createWallet(req, priv)
            }


        }
    }

}

