package com.vhennus.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vhennus.NavScreen
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun preLoginScreen(
    navHostController: NavController
){
    val sheetState = rememberModalBottomSheetState()
    val coroutine = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = {  coroutine.launch { sheetState.show() }},
        sheetState = sheetState,
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(50.dp),
            modifier = Modifier.fillMaxWidth()
        ) {

            // text
            Text(text = "You Need To Login or Signup",
                style = MaterialTheme.typography.titleLarge
            )
            // signup button
            Button(onClick = {navHostController.navigate(NavScreen.SignupScreen.route)},
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.size(width = 200.dp, height = 50.dp)
            ) {
                Text(text = "Signup",
                    style = MaterialTheme.typography.titleLarge
                )
            }

            // login button
            Button(onClick = {navHostController.navigate(NavScreen.LoginScreen.route)},
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier.size(width = 200.dp, height = 50.dp)
            ) {
                Text(text = "Login",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}