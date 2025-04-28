package com.vhennus.trivia.presentation

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.vhennus.NavScreen
import com.vhennus.trivia.data.TriviaViewModel
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold


@Composable
fun postTriviaPlayPage(
    navController: NavController,
    triviaViewModel:TriviaViewModel
    ){

    val lifecycleOwner = LocalLifecycleOwner.current


    DisposableEffect(true) {

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // get game
               triviaViewModel.resetUiState()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
           // triviaViewModel.resetResultState()
        }
    }

    val result  = triviaViewModel.gameResult.collectAsState()
    var resultReady = false
    LaunchedEffect(result.value) {
        if(result.value == "W"|| result.value=="CSW" || result.value=="C"){
            resultReady = true
        }
    }
    GeneralScaffold(
        topBar = { BackTopBar("Trivia", navController) },
        floatingActionButton ={}
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            //AnimatedPreloader(modifier = Modifier.size(size = 500.dp), MaterialTheme.colorScheme.primary)
//            if(!resultReady){
//                Box(modifier = Modifier.fillMaxWidth().size(300.dp).shimmerEffect())
//            }
            Log.d("SELUTL", result.value)
            if(result.value == "W"){
                SorryAnimationLoader(modifier = Modifier.size(500.dp))
                Text("Wrong answer, sorry try again tomorrow", style = MaterialTheme.typography.titleMedium)
            }
            if (result.value == "CSW") {
                TryAgainAnimationLoader(modifier = Modifier.size(500.dp))
                Text("Correct, but somebody beat you to it. Try again next time",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(10.dp)
                )
            }

            if(result.value == "C"){
                SuccessAnimationLoader(modifier = Modifier.size(500.dp))
                Text("Congratulations you win. Vhenncoins have been sent to your wallet", style = MaterialTheme.typography.titleMedium)
            }
            Button(onClick = {
                navController.navigate(NavScreen.HomeScreen.route){
                   popUpToRoute
                }
            },
                modifier = Modifier.size(width = 250.dp, height = 40.dp)
            ) {
                Text("Home")
            }
        }
    }

}