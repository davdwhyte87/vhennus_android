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
import com.vhennus.auth.domain.SignupReq
import com.vhennus.auth.domain.USER_TYPE
import com.vhennus.general.presentation.PasswordTextField
import com.vhennus.ui.AnimatedPreloader


@Composable
fun signUpScreen(
    authViewModel: AuthViewModel,
    navHostController: NavController
){
    val userName = remember {
        mutableStateOf("")
    }

    val email = remember {
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

    LaunchedEffect(authUIState.value.isSignupSuccess) {
        if(authUIState.value.isSignupSuccess){
            Toast.makeText(context, "Success. Verify Email!", Toast.LENGTH_SHORT).show()
            userName.value = ""
            password.value = ""
            password2.value =""
            authViewModel.resetSignupUIState()

            // redirect to validate page
            navHostController.navigate(NavScreen.VerifyAccount.route+"/${email.value}"){
                popUpTo(NavScreen.VerifyAccount.route+"/${email.value}"){inclusive = true}
            }
        }
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

        Text(
            text="Create account",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding( bottom =  20.dp, )
        )
        OutlinedTextField(value = userName.value,
            onValueChange = {
                userName.value = it
            },
            shape = RoundedCornerShape(20.dp),
            placeholder = { Text(text = "Username") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom =  10.dp, )
        )

        OutlinedTextField(value = email.value,
            onValueChange = {
                email.value = it
            },
            shape = RoundedCornerShape(20.dp),
            placeholder = { Text(text = "Email") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom =  10.dp, )
        )

        PasswordTextField(
            password = password.value,
            onPasswordChange = { password.value = it }
        )


        PasswordTextField(
            password = password2.value,
            onPasswordChange = { password2.value = it }
        )



        Button(onClick = {
            if (!validateInput(userName.value, password.value, password2.value, context, email.value)){
                return@Button
            }
            val signupReq = SignupReq(
                user_name =userName.value,
                password= password.value,
                email = email.value,
                user_type = USER_TYPE.User
            )
            authViewModel.signup(signupReq)

        },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.surface
            ),
            modifier = Modifier.size(width = 200.dp, height = 50.dp)
        ) {

            if(authUIState.value.isSignupButtonLoading){
                AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
            }else {
                Text(text = "Signup",
                    style = MaterialTheme.typography.titleSmall
                )
            }

        }
        Text(text = "Login",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(onClick = {
                navHostController.navigate(NavScreen.LoginScreen.route)
            }).padding(5.dp)
        )
    }
}

fun validateInput(userName:String, password:String, password2:String, context: Context, email: String):Boolean{
    val isAllLowerCase = userName.all { it.isLowerCase() }
    if (!isAllLowerCase){
        Toast.makeText(context, "username should be all lowercase", Toast.LENGTH_SHORT).show()
        return false
    }
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

    if(password != password2){
        Toast.makeText(context, "passwords do not match", Toast.LENGTH_SHORT).show()
        return false
    }

    val isvalidEMail =email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    if (!isvalidEMail){
        Toast.makeText(context, "Invalid email", Toast.LENGTH_SHORT).show()
        return false
    }
    return true
}