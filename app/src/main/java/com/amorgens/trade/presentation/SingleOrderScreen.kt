package com.amorgens.trade.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.sharp.AddCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.amorgens.trade.data.OrderViewModel
import com.amorgens.trade.domain.BuyOrder
import com.amorgens.trade.domain.SellOrder
import com.amorgens.ui.AnimatedPreloader
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
    orderViewModel.resetSingleOrderScreenState()
    val isExpanded = remember {
        mutableStateOf(false)
    }


    val message = remember {
        mutableStateOf("")
    }


    LaunchedEffect(true) {
        // get buy order
        orderViewModel.resetSuccessAndError()
        orderViewModel.getSingleBuyOrder(id)
        orderViewModel.getUserName()

    }
    val tradeStateUI = orderViewModel.tradeUIState.collectAsState()
    val singleBuyOrder = orderViewModel.singleBuyOrder.collectAsState()
    val userName = orderViewModel.userName.collectAsState()
    val singleSellOrder = orderViewModel.singleSellOrder.collectAsState()

    LaunchedEffect(singleBuyOrder.value.id) {
        orderViewModel.getSingleSellOrders(singleBuyOrder.value.sell_order_id)
    }
    if(tradeStateUI.value.isConfirmBuyOrderError){
        Toast.makeText(LocalContext.current, tradeStateUI.value.confirmBuyOrderErrorMessage, Toast.LENGTH_SHORT).show()
        orderViewModel.resetSingleOrderScreenState()

    }

    if(tradeStateUI.value.isConfirmBuyOrderSuccess){
        Toast.makeText(LocalContext.current, "Order confirmed", Toast.LENGTH_SHORT).show()
        orderViewModel.resetSingleOrderScreenState()
        orderViewModel.getSingleBuyOrder(id)
    }
    GeneralScaffold(topBar = { BackTopBar(pageName = "Order details", navController = navController) }, floatingActionButton = { /*TODO*/ }) {
        Column (
            modifier = Modifier.fillMaxSize()
        ){

            // top buttons
            Row {
                // confirm button
                Button(onClick = {
                    orderViewModel.login()
                    if (userName.value == singleBuyOrder.value.user_name){
                        orderViewModel.buyerConfirmBuyOrder(singleBuyOrder.value.id)
                    }else{
                        if (userName.value == singleSellOrder.value.user_name){
                            orderViewModel.sellerConfirmBuyOrder(singleBuyOrder.value.id)
                        }
                    }

                },
                    colors = ButtonDefaults.buttonColors(containerColor = Green),
                    enabled =    getIsConfirmButtonEnabled(userName, singleBuyOrder, singleSellOrder)
                ) {
                    if(tradeStateUI.value.isConfirmBuyOrderButtonLoading){
                        AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
                    }else {
                        Text(text = "Confirm Transaction",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.surface
                        )
                    }


                }

                // cancel button
                if (userName.value == singleBuyOrder.value.user_name){
                    Button(onClick = {
                        orderViewModel.cancelSellOrder(id)
                    },
                        colors = ButtonDefaults.buttonColors(containerColor = Red)
                    ) {


                        if(tradeStateUI.value.isLoading){
                            AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
                        }else {
                            Text(text = "Cancel",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.surface
                            )
                        }

                    }
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
                            Text(text = "Account Number: "+ (singleSellOrder.value.payment_method_data?.account_number))
                            Text(text = "Account Name: "+ singleSellOrder.value.payment_method_data?.account_name)
                            Text(text = "bank Name: "+ singleSellOrder.value.payment_method_data?.bank_name)
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
                    Text(text = singleBuyOrder.value.user_name,
                        color = MaterialTheme.colorScheme.surface)
                }

            }

        }
    }






}

fun getIsConfirmButtonEnabled(userName:State<String>, singleBuyOrder:State<BuyOrder>, singleSellOrder: State<SellOrder>):Boolean{
    if (userName.value == singleBuyOrder.value.user_name){
        Log.d("getIsConfirmButtonEnabled XX", "buyer logged in")
        //buyer logged in
        if(singleBuyOrder.value.is_buyer_confirmed){
            // buyer confirmed
            Log.d("getIsConfirmButtonEnabled XX", "buyer confirmed")
            return false
        }else {
            // buyer not confirmed
            Log.d("getIsConfirmButtonEnabled XX", "buyer not confirmed")
            return true
        }
    }else{
        Log.d("getIsConfirmButtonEnabled XX", "single sell order username ... "+singleSellOrder.value.user_name )
        if (userName.value == singleSellOrder.value.user_name){

            //seller logged in
            Log.d("getIsConfirmButtonEnabled XX", "seller logged in")
            if (singleBuyOrder.value.is_seller_confirmed){
                // seller confirmed
                Log.d("getIsConfirmButtonEnabled XX", "seller confirmed")
                return false
            }else {
                // seller not confirmed
                Log.d("getIsConfirmButtonEnabled XX", "seller not confirmed")
                return true
            }
        }else {
            Log.d("getIsConfirmButtonEnabled XX", "unknown user logged in  .. "+ userName.value)
            // unknown user logged in
            return  false
        }
    }
}