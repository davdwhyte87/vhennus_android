package com.vhennus.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vhennus.NavScreen
import com.vhennus.R.drawable
import com.vhennus.auth.data.AuthViewModel
import com.vhennus.auth.domain.ChangePasswordReq
import com.vhennus.auth.domain.GetResetPasswordCodeReq
import com.vhennus.general.presentation.AppButtonLarge
import com.vhennus.general.presentation.AppScaffold
import com.vhennus.general.presentation.CustomSnackbarVisuals
import com.vhennus.general.presentation.InputField
import com.vhennus.general.presentation.SnackbarType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@Composable
fun ResetPasswordScreen(
    authViewModel: AuthViewModel,
    navController: NavController
){
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    val authUIState = authViewModel.authUIState.collectAsState().value
    val tempUser = authViewModel.tempUserName.collectAsState().value
    val userName = remember { mutableStateOf("") }
    val password = remember { mutableStateOf("") }
    val code = remember { mutableStateOf("") }
    val password2 = remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    userName.value = tempUser



    LaunchedEffect(authUIState.isSendResetPasswordCodeSuccess) {
        if(authUIState.isSendResetPasswordCodeSuccess){
            scope.launch{
                snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                    message = "Code sent to mail",
                    type = SnackbarType.SUCCESS
                ))
            }
        }

    }

    LaunchedEffect(authUIState.isSendResetPasswordCodeError) {
        if(authUIState.isSendResetPasswordCodeError){
            scope.launch{
                snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                    message = authUIState.sendResetPasswordCodeErrorMessage,
                    type = SnackbarType.ERROR
                ))
            }
        }
    }

    LaunchedEffect(authUIState.isChangePasswordError) {
        if(authUIState.isChangePasswordError){
            scope.launch{
                snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                    message = authUIState.changePasswordErrorMessage,
                    type = SnackbarType.ERROR
                ))
            }
        }
    }

    LaunchedEffect(authUIState.isChangePasswordSuccess) {
        if(authUIState.isChangePasswordSuccess){
            scope.launch{
                snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                    message = "Success! Login now",
                    type = SnackbarType.SUCCESS
                ))
            }
            navController.navigate(NavScreen.LoginScreen.route){
                popUpTo(NavScreen.LoginScreen.route){inclusive=true}
            }
        }
    }


    AppScaffold(
        snackbarHostState = snackbarHostState,
        topBar = {}
    ) {
        Column (
            verticalArrangement = Arrangement.spacedBy(25.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp).verticalScroll(scrollState)
        ) {
            Image(
                painter = painterResource(drawable.emaillogo),
                contentDescription = "",
                modifier = Modifier.size(184.dp)
            )
            Text("Verification Code",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 20.dp, end = 20.dp),
                textAlign = TextAlign.Center
            )
            Text("A password reset link has been sent to your email address."
                , style = MaterialTheme.typography.bodyMedium
            )
            InputField(
                userName,
                "Username"
            )
            InputField(
                code,
                "Code from email"
            )
            InputField(
                password,
                "New password"
            )
            InputField(
                password2,
                "Confirm new password"
            )
            AppButtonLarge(
                "Reset Password",
                isLoading = authUIState.isChangePasswordLoading
            ) {
                if(!validateResetPasswordInput(
                    code.value,
                    password.value,
                    password2.value,
                    snackbarHostState,
                    scope
                )){
                    return@AppButtonLarge
                }

                authViewModel.changePassword(ChangePasswordReq(
                    userName.value,
                    code.value,
                    password.value
                ))
            }
            Text(text = "Resend code",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = {
                    // navHostController.navigate(NavScreen.SendForgotPasswordCodeScreen.route)
                    authViewModel.sendResetPasswordCode(GetResetPasswordCodeReq(userName.value))
                }).padding(5.dp)
            )
        }
    }
}

fun validateResetPasswordInput(
    code:String,
    password:String,
    password2:String,
    snackbarHostState: SnackbarHostState,
    scope: CoroutineScope
): Boolean{
    if(code.isEmpty() || code.isBlank()){
        scope.launch{
          snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
              message = "Code should not be empty",
              type = SnackbarType.ERROR
          ))
        }
        return false
    }

    if(password.isEmpty() || password.isBlank()){
        scope.launch{
            snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                message = "Password should not be empty",
                type = SnackbarType.ERROR
            ))
        }
        return false
    }

    if(password != password2){
        scope.launch{
            snackbarHostState.showSnackbar(visuals = CustomSnackbarVisuals(
                message = "Passwords do not match",
                type = SnackbarType.ERROR
            ))
        }
        return false
    }


   return true
}
