package com.vhennus.auth.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vhennus.NavScreen
import com.vhennus.R
import com.vhennus.auth.data.AuthViewModel
import com.vhennus.auth.domain.LoginReq
import com.vhennus.general.presentation.PasswordTextField
import com.vhennus.general.utils.CLog
import com.vhennus.ui.AnimatedPreloader

@Composable
fun loginScreen(
    navHostController: NavController,
    authViewModel:AuthViewModel
){

    val authUIState = authViewModel.authUIState.collectAsState()
    val context = LocalContext.current
    val tempEmail = authViewModel.tempLoginEmail.collectAsState()
    val loginResp = authViewModel.loginResp.collectAsState().value
    val tempEmailConfirmed =authViewModel.tempLoginEmailConfirmed.collectAsState().value

    LaunchedEffect(authUIState.value.isLoginError) {
        if(authUIState.value.isLoginError){
            Toast.makeText(context, authUIState.value.loginErrorMessage, Toast.LENGTH_SHORT).show()
            authViewModel.resetLoginUIState()
        }
    }


    LaunchedEffect(loginResp ) {
        CLog.debug("TEMP EMAIL", tempEmail.value)
        if(authUIState.value.isLoginSuccess){
            Toast.makeText(context, "Success!", Toast.LENGTH_SHORT).show()

            // if users email is not confirmed, verify the user

            if(!loginResp.email_confirmed){
                navHostController.navigate(NavScreen.VerifyAccount.route+"/${loginResp.email}"){
                    popUpTo(NavScreen.VerifyAccount.route+"/${loginResp.email}"){inclusive = true}
                }
            }else{
                navHostController.navigate(NavScreen.HomeScreen.route){
                    popUpTo(NavScreen.HomeScreen.route){inclusive = true}
                }
            }
            authViewModel.resetLoginUIState()
        }
    }


    val userName = remember {
        mutableStateOf("")
    }

    val password = remember {
        mutableStateOf("")
    }


    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ){
        Image(
            painterResource(R.mipmap.vhennuslogo2),
            contentDescription = null,
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
        )

        OutlinedTextField(value = userName.value,
            onValueChange = {
                userName.value = it
            },
            shape = RoundedCornerShape(20.dp),
            placeholder = { Text(text = "Username") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom =  10.dp, )
        )

        PasswordTextField(
            password = password.value,
            onPasswordChange = { password.value = it }
        )

        Button(onClick = {
            if (!loginFormValidation(userName.value, password.value, context)){
                return@Button
            }
            val loginReq = LoginReq(
                user_name =userName.value,
                password=password.value
            )
            authViewModel.login(loginReq)

        },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.size(width = 200.dp, height = 50.dp)
        ) {
            if(authUIState.value.isLoginButtonLoading){
                AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
            }else {
                Text(text = "Login",
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }
        Text(text = "Signup",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(onClick = {
               navHostController.navigate(NavScreen.SignupScreen.route)
            }).padding(5.dp)
        )

        Text(text = "Forgot Password?",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(onClick = {
                navHostController.navigate(NavScreen.SendForgotPasswordCodeScreen.route)
            }).padding(5.dp)
        )
    }
}

fun loginFormValidation(userName:String, password:String, context: Context):Boolean{
    val isAllLowerCase = userName.all { it.isLowerCase() }
//    if (!isAllLowerCase){
//        Toast.makeText(context, "username should be all lowercase", Toast.LENGTH_SHORT).show()
//        return false
//    }
    val hasSpace = userName.any { it.isWhitespace() }
    if (hasSpace){
        Toast.makeText(context, "username should have no spaces", Toast.LENGTH_SHORT).show()
        return false
    }
    if (userName.isBlank()){
        Toast.makeText(context, "username cannot be blank", Toast.LENGTH_SHORT).show()
        return false
    }
    if (password.isBlank()){
        Toast.makeText(context, "password cannot be blank", Toast.LENGTH_SHORT).show()
        return false
    }


    return true
}
