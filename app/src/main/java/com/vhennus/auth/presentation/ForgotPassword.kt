package com.vhennus.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vhennus.general.presentation.AppButtonLarge
import com.vhennus.general.presentation.InputField


@Preview
@Composable
fun ForgotPasswordScreen(){
    val email = remember { mutableStateOf("") }
    Column (
        verticalArrangement = Arrangement.spacedBy(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Text("Forgot Password?", style = MaterialTheme.typography.titleLarge)
        Text("Please, enter your email address below to receive a password reset code."
            , style = MaterialTheme.typography.bodyMedium
        )

        InputField(
            email,
            "Enter email address"
        )
        AppButtonLarge(
            "Reset Password",
            isLoading = false
        ) { }
    }
}