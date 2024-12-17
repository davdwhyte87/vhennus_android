package com.vhennus.profile.presentation

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.sharp.AddCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import com.vhennus.R
import com.vhennus.general.utils.uploadFileToFirebase
import com.vhennus.profile.data.ProfileViewModel
import com.vhennus.profile.domain.UpdateProfileRequest
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold
import com.vhennus.wallet.domain.CreateWalletReq
import com.vhennus.wallet.presentation.formValidation
import kotlin.contracts.contract

@Composable
fun editProfilePage(
    navController: NavController,
    profileViewModel: ProfileViewModel
){

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(true) {
        val observer = LifecycleEventObserver{_,event->
            if(event == Lifecycle.Event.ON_RESUME){
                profileViewModel.getMyProfile()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            profileViewModel.resetUIState()
        }
    }
    val profile = profileViewModel.profile.collectAsState().value
    val profileUiState = profileViewModel.profileUIState.collectAsState().value
    val bio = remember{
        mutableStateOf(profile.bio)
    }
    val name = remember {
        mutableStateOf(profile.name)
    }
    val context = LocalContext.current
    LaunchedEffect(profileUiState.isUpdateProfileSuccess) {
        if(profileUiState.isUpdateProfileSuccess){
            Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show()
        }
    }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                // Process the URI (e.g., upload to Firebase)
                uploadFileToFirebase(context, uri)
            } catch (e: Exception) {
                Log.e("FilePicker", "Error processing file: ${e.message}")
            }
        } else {
            Log.e("FilePicker", "No URI selected")
        }
    }
    GeneralScaffold(
        topBar = { BackTopBar("Edit Profile", navController) },
        floatingActionButton = {}
    ) {
        Column (
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Image(painter = painterResource(R.drawable.p1),
                "",
                modifier = Modifier.size(120.dp).clip(CircleShape)
                )
            Button(onClick = {
                launcher.launch("image/*")
            },
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.surface
                ),

            ) {
                Icon(Icons.Filled.Edit, "")
            }
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedTextField(
                value =name.value ,
                onValueChange = { name.value = it  },
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = { Text("Name") },
                singleLine = true,
                shape = RoundedCornerShape(8.dp)
            )
            OutlinedTextField(
                value =bio.value ,
                onValueChange = { bio.value = it  },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp), // Adjust height for multi-line input
                placeholder = { Text("Type something...") },
                maxLines = Int.MAX_VALUE, // Allows unlimited lines
                singleLine = false, // Ensures multi-line capability
                shape = RoundedCornerShape(8.dp) // Optional styling
            )
            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                if(!updateBioValidation(context, bio.value, name.value)){
                   return@Button
                }
                profileViewModel.updateProfile(UpdateProfileRequest(image = "", bio.value, name = name.value ))
            },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.size(width = 200.dp, height = 50.dp)
            ) {
                if(profileUiState.isUpdateProfileLoading){
                    AnimatedPreloader(modifier = Modifier.size(size = 50.dp), MaterialTheme.colorScheme.surface)
                }else {
                    Text(text = "Edit")
                }

            }
        }
    }
}

fun updateBioValidation(context: Context, bio:String, name:String):Boolean{
    if(name.isBlank() || name.isEmpty()){
        Toast.makeText(context, "Name cannot be empty", Toast.LENGTH_SHORT).show()
        return false
    }
    if(bio.isBlank() || bio.isEmpty()){
        Toast.makeText(context, "Bio cannot be empty", Toast.LENGTH_SHORT).show()
        return false
    }

    return true
}

