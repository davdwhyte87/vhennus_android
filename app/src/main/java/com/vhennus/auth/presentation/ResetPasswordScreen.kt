package com.vhennus.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.vhennus.R.drawable
import com.vhennus.general.presentation.AppButtonLarge
import com.vhennus.general.presentation.InputField

@Preview
@Composable
fun ResetPasswordScreen(){
    val email = remember { mutableStateOf("") }
    Column (
        verticalArrangement = Arrangement.spacedBy(25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(24.dp)
    ) {
        Image(
            painter = painterResource(drawable.emaillogo),
            contentDescription = "",
            modifier = Modifier.size(184.dp)
        )
        Text("Password reset email has been sent",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(start = 20.dp, end = 20.dp)
        )
        Text("A password reset link has been sent to your email address."
            , style = MaterialTheme.typography.bodyMedium
        )

        InputField(
            email,
            ""
        )
        AppButtonLarge(
            "Reset Password",
            isLoading = false
        ) { }
    }
}