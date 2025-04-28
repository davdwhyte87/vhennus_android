package com.vhennus.wallet.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.sharp.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vhennus.general.presentation.AppButtonLarge
import com.vhennus.general.presentation.AppScaffold
import com.vhennus.general.presentation.CustomSnackbarVisuals
import com.vhennus.general.presentation.InputFieldWithLabel
import com.vhennus.general.presentation.SnackbarType
import com.vhennus.general.utils.CLog
import com.vhennus.general.utils.KeyGenerator
import com.vhennus.general.utils.signMessage
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold
import com.vhennus.ui.theme.Gray2
import com.vhennus.ui.theme.Red
import com.vhennus.ui.theme.White
import com.vhennus.wallet.data.WalletViewModel
import com.vhennus.wallet.domain.AddWalletReq
import com.vhennus.wallet.domain.GetWalletReq
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sign


@Composable
fun AddWalletScreen(
    navController: NavController,
    walletViewModel: WalletViewModel
){
    val name = remember { mutableStateOf("") }
    val snackbarHostState =remember{ SnackbarHostState() }
    val walletUIState = walletViewModel.walletUIState.collectAsState().value
    val address = remember { mutableStateOf("") }
    val seedPhrase = remember { mutableStateOf("") }


    LaunchedEffect(walletUIState.isAddWalletError) {
        if(walletUIState.isAddWalletError){
            snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                message = walletUIState.addWalletErrorMessage,
                type = SnackbarType.ERROR
            ))
            walletViewModel.resetUI()
        }
    }

    LaunchedEffect(walletUIState.isAddWalletSuccess) {
        if(walletUIState.isAddWalletSuccess){
            snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                message = "Wallet added!",
                type = SnackbarType.SUCCESS
            ))
            walletViewModel.resetUI()
        }
    }

    AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {BackTopBar("Add Wallet", navController)}
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            Text("To import your existing wallet, Please enter the password you created during the wallet creation.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(30.dp)
            )

            // input fields
            Column(
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                InputFieldWithLabel(
                    address,
                    "Enter Wallet Address",
                    "Wallet Address"
                )

                InputFieldWithLabel(
                    seedPhrase,
                    "Enter Seed Phrase",
                    "Seed phrase"
                )
            }

            // action button

            AppButtonLarge(text = "Import",
                isLoading = walletUIState.isAddWalletButtonLoading,

                ) {
                if(!addWalletFormValidation(snackbarHostState, address.value, seedPhrase.value)){
                    return@AppButtonLarge
                }

                // sign message
                val (priv, pub) = KeyGenerator.generateKeysFromSeed(seedPhrase.value)
                val message  = "hello benny".toString()
                val signature = signMessage(message, priv)
                CLog.debug("ADD WALLET SINGATURE", signature)
                CLog.debug("PUB KEY ADD WALLET", pub)

                // send to server
                val req = AddWalletReq(
                    address.value,
                    message,
                    signature

                )
                walletViewModel.addWallet(req)
            }
        }
    }

}

fun addWalletFormValidation(
    snackbarHostState: SnackbarHostState,
    address: String,
    seed_phrase:String,
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

