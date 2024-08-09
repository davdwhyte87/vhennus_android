package com.amorgens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.rememberNavController
import com.amorgens.home.presentation.HomeScreen
import com.amorgens.trade.data.OrderViewModel
import com.amorgens.ui.theme.AmorgensTheme
import com.amorgens.wallet.data.WalletViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val walletViewModel:WalletViewModel = hiltViewModel()
            val orderViewModel:OrderViewModel = hiltViewModel()

            val navController = rememberNavController()
            AmorgensTheme(darkTheme = false) {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {

                    AppNav(navController =navController,
                        walletViewModel ,
                        orderViewModel
                    )
                }
            }
        }
    }
}




//@Preview(showBackground = true)
//@Composable
//fun GreetingPreview() {
//    AmorgensTheme {
//        Greeting("Android")
//    }
//}