package com.vhennus.settings.presentation

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.vhennus.NavScreen
import com.vhennus.auth.data.AuthViewModel
import com.vhennus.profile.data.ProfileViewModel
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold


@Composable
fun SettingsPage(
    navController: NavController,
    profileViewModel: ProfileViewModel,
    authViewModel: AuthViewModel
){
    val settingsUIState = profileViewModel.settingsUIState.collectAsState().value
    val context = LocalContext.current
    LaunchedEffect(settingsUIState.isDeleteAccountError) {
        if (settingsUIState.isDeleteAccountError){
            Toast.makeText(context, settingsUIState.deleteAccountErrorMessage, Toast.LENGTH_SHORT).show()
            profileViewModel.resetUIState()
        }
    }
    LaunchedEffect(settingsUIState.isDeleteAccountSuccess) {
        if (settingsUIState.isDeleteAccountSuccess){
            // logout
            profileViewModel.resetUIState()
            authViewModel.logout()
            navController.navigate(NavScreen.LoginScreen.route)
        }
    }
    GeneralScaffold(
        topBar = { BackTopBar("Settings", navController) },
        floatingActionButton = {}
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Button(onClick = {
                profileViewModel.deleteAccount()
            },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors().copy(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                if(settingsUIState.isDeleteAccountLoading){
                    AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
                }else{
                    Text("Delete account", style = MaterialTheme.typography.titleLarge)
                }

            }
        }
    }
}