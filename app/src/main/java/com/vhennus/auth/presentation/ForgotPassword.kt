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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vhennus.NavScreen
import com.vhennus.auth.data.AuthViewModel
import com.vhennus.auth.domain.GetResetPasswordCodeReq
import com.vhennus.general.presentation.AppButtonLarge
import com.vhennus.general.presentation.AppScaffold
import com.vhennus.general.presentation.CustomSnackbarVisuals
import com.vhennus.general.presentation.InputField
import com.vhennus.general.presentation.SnackbarType
import com.vhennus.ui.BackTopBar
import kotlinx.coroutines.launch


@Composable
fun ForgotPasswordScreen(
    navController: NavController,
    authViewModel: AuthViewModel
){
    val user_name = remember { mutableStateOf("") }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val authUIState = authViewModel.authUIState.collectAsState().value


    LaunchedEffect(authUIState.isSendResetPasswordCodeError) {
        if(authUIState.isSendResetPasswordCodeError){
            scope.launch{
                snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                    message = authUIState.sendResetPasswordCodeErrorMessage,
                    type = SnackbarType.ERROR
                ))
            }
            authViewModel.resetUI()
        }
    }


    LaunchedEffect(authUIState.isSendResetPasswordCodeSuccess) {
        if(authUIState.isSendResetPasswordCodeSuccess){
            navController.navigate(NavScreen.ResetPasswordScreen.route)
            authViewModel.resetUI()
        }
    }

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
                isLoading = authUIState.isSendResetPasswordCodeLoading
            ) {
                if (user_name.value.isBlank() || user_name.value.isEmpty()){
                    scope.launch{
                        snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                            message = "Username cannot be blank",
                            type = SnackbarType.ERROR
                        ))
                    }
                    return@AppButtonLarge
                }
                authViewModel.sendResetPasswordCode(GetResetPasswordCodeReq(user_name= user_name.value))
            }
        }
    }

}