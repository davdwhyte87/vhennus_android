package com.vhennus.auth.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vhennus.NavScreen
import com.vhennus.R
import com.vhennus.auth.data.AuthViewModel
import com.vhennus.auth.domain.ConfirmAccountReq
import com.vhennus.auth.domain.ResendCodeReq
import com.vhennus.auth.domain.SignupReq
import com.vhennus.auth.domain.USER_TYPE
import com.vhennus.general.presentation.InputField
import com.vhennus.general.presentation.PasswordTextField
import com.vhennus.ui.AnimatedPreloader

@Composable
fun VerifyScreen(
    authViewModel: AuthViewModel,
    navHostController: NavController,
    email: String
){
    val code = remember {
        mutableStateOf("")
    }




    val password = remember {
        mutableStateOf("")
    }

    val password2 = remember {
        mutableStateOf("")
    }
    val authUIState = authViewModel.authUIState.collectAsState()
    val context = LocalContext.current

    if(authUIState.value.isSignupError){
        Toast.makeText(LocalContext.current, authUIState.value.signupErrorMessage, Toast.LENGTH_SHORT).show()
        authViewModel.resetSignupUIState()
    }

    LaunchedEffect(authUIState.value.isVerifyAccountSuccess) {
        if(authUIState.value.isVerifyAccountSuccess){
            Toast.makeText(context, "Success. You can login now!", Toast.LENGTH_SHORT).show()

            navHostController.navigate(NavScreen.LoginScreen.route)
            // redirect to validate page
            authViewModel.resetSignupUIState()
        }
    }

    LaunchedEffect(authUIState.value.resendCodeSuccess) {
        if(authUIState.value.resendCodeSuccess){
            Toast.makeText(context, "Code sent!", Toast.LENGTH_SHORT).show()
            authViewModel.resetSignupUIState()
            // redirect to validate page
        }
    }

    LaunchedEffect(authUIState.value.resendCodeError) {
        if(authUIState.value.resendCodeError){
            Toast.makeText(context, authUIState.value.resendCodeErrorMessage, Toast.LENGTH_SHORT).show()
            authViewModel.resetSignupUIState()
            // redirect to validate page
        }
    }

    LaunchedEffect(authUIState.value.isVerifyAccountError) {
        if(authUIState.value.isVerifyAccountError){
            Toast.makeText(context, authUIState.value.verifyAccountErrorMessage, Toast.LENGTH_SHORT).show()
            authViewModel.resetSignupUIState()
            // redirect to validate page
        }
    }


    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
        modifier = Modifier.fillMaxWidth().padding(16.dp)
    ){

        Image(
            painterResource(R.mipmap.vhennuslogo2),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
        )

        Text(
            text="Verify Account",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding( bottom =  20.dp, )
        )

        InputField(code, "Verification Code")

        Button(onClick = {
            if (!validateVerifyAccountInput(code.value, context)){
                return@Button
            }
            val req = ConfirmAccountReq(
                code = code.value,
                email = email
            )
            authViewModel.verifyAccount(req)

        },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.size(width = 200.dp, height = 50.dp)
        ) {

            if(authUIState.value.isVerifyAccountLoading){
                AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
            }else {
                Text(text = "Verify",
                    style = MaterialTheme.typography.titleSmall
                )
            }

        }

        Row {
            Text(text = "Resend Code",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline,
                modifier = Modifier.clickable(onClick = {
                    authViewModel.resendCode(ResendCodeReq(email))
                }).padding(5.dp)
            )
            if (authUIState.value.resendCodeLoading){
                AnimatedPreloader(modifier = Modifier.size(size = 30.dp), MaterialTheme.colorScheme.primary)
            }

        }

    }
}

fun validateVerifyAccountInput(code:String, context: Context,):Boolean{
    val hasSpace = code.any { it.isWhitespace() }
    if (hasSpace){
        Toast.makeText(context, "code should have no spaces", Toast.LENGTH_SHORT).show()
        return false
    }
    if (code.isBlank()){
        Toast.makeText(context, "code cannot be blank", Toast.LENGTH_SHORT).show()
        return false
    }
    return true
}