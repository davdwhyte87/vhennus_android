package com.vhennus.wallet.presentation

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.outlined.Rocket
import androidx.compose.material.icons.sharp.Rocket
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.vhennus.general.data.GeneralViewModel
import com.vhennus.general.presentation.AppButtonLarge
import com.vhennus.general.presentation.AppScaffold
import com.vhennus.general.presentation.CustomSnackbarVisuals
import com.vhennus.general.presentation.InputField
import com.vhennus.general.presentation.InputFieldWithLabel
import com.vhennus.general.presentation.SnackbarType
import com.vhennus.general.presentation.showCustomToast
import com.vhennus.general.utils.CLog
import com.vhennus.general.utils.KeyGenerator
import com.vhennus.general.utils.formatBigDecimalWithCommas
import com.vhennus.general.utils.getTxId
import com.vhennus.general.utils.signTransaction
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold
import com.vhennus.wallet.data.WalletViewModel
import com.vhennus.wallet.domain.GetWalletReq
import com.vhennus.wallet.domain.GetWalletTransactionsReq
import com.vhennus.wallet.domain.TransferReq
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.BigInteger
import java.util.UUID


@Composable
fun TransferScreen(
    navController: NavController,
    walletAddress:String,
    walletViewModel: WalletViewModel,
    generalViewModel: GeneralViewModel
){
    val lifecycleOwner = LocalLifecycleOwner.current

    // clear model data
//    DisposableEffect(true) {
//
//        val observer = LifecycleEventObserver { _, event ->
//            if (event == Lifecycle.Event.ON_RESUME) {
//                walletViewModel.getWalletFromBlockchain(GetWalletReq(address))
//
//            }
//        }
//
//        lifecycleOwner.lifecycle.addObserver(observer)
//
//        onDispose {
//            //walletViewModel.clearModelData()
//        }
//    }
//
    val address = remember { mutableStateOf("") }
    val amount = remember { mutableStateOf("") }
    val seed = remember { mutableStateOf("") }
    val walletUIState = walletViewModel.walletUIState.collectAsState().value
    val singleWallet = walletViewModel.singleWalletC.collectAsState().value
    val context =  LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = CoroutineScope(Dispatchers.Main)
    val currency = walletViewModel.selectedCurrency.collectAsState().value
    val systemData = generalViewModel.systemData.collectAsState().value

    LaunchedEffect(Unit) {
        walletViewModel.getSelectedCurrency()
    }

    LaunchedEffect(walletUIState.isTransferSuccessful) {
        if(walletUIState.isTransferSuccessful){
            scope.launch{
                snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                    message = "Successful",
                    type = SnackbarType.SUCCESS
                ))
            }
            address.value = ""
            seed.value = ""
            amount.value = ""
            walletViewModel.resetUIState()
        }
    }

    LaunchedEffect(walletUIState.isTransferError) {
        if(walletUIState.isTransferError){
            snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                message = walletUIState.transferErrorMessage,
                type = SnackbarType.ERROR
            ))

            walletViewModel.resetUIState()
        }

    }

    AppScaffold(
        snackbarHostState= snackbarHostState,
        topBar = {BackTopBar("Transfer", navController)}
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp).fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {

            // input fields
            Column(
                verticalArrangement = Arrangement.spacedBy(32.dp)
            ) {
                InputField(
                    address,
                    "Wallet Address"
                )

                InputField(
                    amount,
                    "Amount",
                    isNumeric = true,

                )
                Text("${formatBigDecimalWithCommas(textToBigDecimal(amount.value))} VEC",
                    style = MaterialTheme.typography.titleSmall
                    )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("${currency} ${formatBigDecimalWithCommas(calculateConvertedBalance(amount.value, currency, systemData, ))}", style = MaterialTheme.typography.bodySmall)
                    Icon(Icons.Filled.CurrencyExchange, "",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Text("Max", style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.clickable(onClick = {
                            amount.value = singleWallet.balance.toString()
                        })
                    )
                }

                InputField(
                    seed,
                    "Seed phrase",
                )
            }

            // action button

            AppButtonLarge(text = "Send",
                isLoading = walletUIState.isTransferButtonLoading,
                isIcon = true,
                icon = Icons.Outlined.Rocket
            ) {
                if(!validateTransferInput(amount.value, address.value, snackbarHostState)){
                    return@AppButtonLarge
                }
                val (priv, pub) =KeyGenerator.generateKeysFromSeed(seed.value)
                val timestampSeconds = System.currentTimeMillis() / 1000
                val amount_str = textToBigDecimal(amount.value).toString()
                val id = getTxId(walletAddress, address.value,amount_str, timestampSeconds)
                val signature = signTransaction(walletAddress, address.value,amount_str,
                    timestampSeconds,id,priv)
                val req = TransferReq(
                    sender = walletAddress,
                    receiver = address.value,
                    amount = amount_str,
                    id = id,
                    timestamp = timestampSeconds,
                    signature = signature
                )
                walletViewModel.transfer(req)
            }
        }
    }

}

fun validateTransferInput(amount: String, address:String, snackbarHostState: SnackbarHostState): Boolean{
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

     try {
         BigDecimal(amount)

    } catch (e: NumberFormatException) {
         scope.launch{
             snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                 message = "Invalid amount m",
                 type = SnackbarType.ERROR
             ))
         }
        false
    }

    return true
}

fun textToBigDecimal(number: String): BigDecimal{
    CLog.debug("NUMERIC AMOUNT", number)
    try {
       return  BigDecimal(number)
    }catch (e: Exception){
        return BigDecimal.ZERO
    }
}