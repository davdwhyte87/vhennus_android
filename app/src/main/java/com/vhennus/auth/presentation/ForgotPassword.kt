package com.vhennus.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vhennus.auth.data.AuthViewModel
import com.vhennus.general.presentation.AppButtonLarge
import com.vhennus.general.presentation.AppScaffold
import com.vhennus.general.presentation.InputField
import com.vhennus.ui.BackTopBar


@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    authViewModel: AuthViewModel
){
    val user_name = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = { BackTopBar("     ", navController ) }
    ) {
        Column (
            verticalArrangement = Arrangement.spacedBy(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Text("Forgot Password?", style = MaterialTheme.typography.titleLarge)
            Text("Please enter your user name, you will receive a code in the email linked to your account."
                , style = MaterialTheme.typography.bodyMedium
            )

            InputField(
                user_name,
                "Enter user name"
            )

            AppButtonLarge(
                "Send Code",
                isLoading = false
            ) {
                authViewModel.getUserName()
            }
        }
    }

}