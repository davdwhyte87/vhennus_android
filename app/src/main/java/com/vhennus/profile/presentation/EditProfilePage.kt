package com.vhennus.profile.presentation

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.sharp.AddCard
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.work.WorkInfo
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.vhennus.R
import com.vhennus.general.presentation.LoadImageWithPlaceholder
import com.vhennus.general.utils.CLog
import com.vhennus.general.utils.uploadFileToFirebase
import com.vhennus.profile.data.ProfileViewModel
import com.vhennus.profile.domain.UpdateProfileRequest
import com.vhennus.ui.AnimatedPreloader
import com.vhennus.ui.BackTopBar
import com.vhennus.ui.GeneralScaffold
import com.vhennus.ui.theme.Gray2
import com.vhennus.wallet.domain.CreateWalletReq
import kotlin.contracts.contract

@Composable
fun editProfilePagexxx(
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
            profileViewModel.resetUploadWorkStatus()
        }
    }
    val profile = profileViewModel.myProfile.collectAsState().value
    val profileUiState = profileViewModel.profileUIState.collectAsState().value
    val bio = remember{
        mutableStateOf(profile.profile.bio.toString())
    }
    val name = remember {
        mutableStateOf(profile.profile.name.toString())
    }

    val context = LocalContext.current
    val workStatus  = profileViewModel.workStatus.collectAsState().value
    var uploadProgress by remember { mutableStateOf(0) }
    val selectedImage = remember { mutableStateOf<Uri?>(null) }
    var newUploadedUrl = ""
    val uploadSuccess = remember { mutableStateOf(false) }
    val currentUploadSuccess = rememberUpdatedState(uploadSuccess.value)

    LaunchedEffect(profileUiState.isUpdateProfileSuccess) {
        if(profileUiState.isUpdateProfileSuccess){
            Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show()
            profileViewModel.resetUIState()
        }

    }

    LaunchedEffect(profileUiState.isUpdateProfileError) {
        if(profileUiState.isUpdateProfileError){
            Toast.makeText(context, profileUiState.updateProfileErrorMessage, Toast.LENGTH_SHORT).show()
            profileViewModel.resetUIState()
        }

    }
    LaunchedEffect(currentUploadSuccess.value) {
        if(currentUploadSuccess.value){
            // if uploadd successful then send api request to save image
//            profileViewModel.updateProfile(UpdateProfileRequest(
//                image = newUploadedUrl,
//                null,
//                null
//            ))
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                // Process the URI (e.g., upload to Firebase)
                CLog.debug("FILE NAME", uri.toString())
//                uploadImageToCloudinary(context, uri) { progress ->
//                    uploadProgress = progress
//                }

                profileViewModel.uploadImage(uri)

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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ){
            if(profile.profile.image.isEmpty() == true || profile.profile.image?.isBlank() == true){
                Image(
                    painter = painterResource(R.drawable.p1),
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.size(60.dp).clip(CircleShape)
                )
            }else{
                LoadImageWithPlaceholder(
                    profile.profile.image.toString(),
                    modifier = Modifier.size(60.dp)
                        .clip(CircleShape)
                )
            }
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

            workStatus?.let { info ->
                when (info.state) {
                    WorkInfo.State.ENQUEUED ->{
                        Text("On Queue...")
                    }
                    WorkInfo.State.RUNNING -> {
                        Text("Uploading...")
                    }
                    WorkInfo.State.SUCCEEDED -> {
                        val uploadedUrl = info.outputData.getString("uploadedUrl")
//                        if (uploadedUrl != null){
//                            newUploadedUrl = uploadedUrl
//                            uploadSuccess.value = true
//                        }
                        if (!uploadedUrl.isNullOrEmpty()) {
                            profileViewModel.updateProfile(
                                UpdateProfileRequest(
                                    image = uploadedUrl,
                                    null,
                                    null,
                                    null
                                )
                            )
                        }
                        profileViewModel.resetUploadWorkStatus()

//                        Text("Upload successful!")
                    }
                    WorkInfo.State.FAILED -> {
                        Text("Upload failed.", color = Color.Red)
                    }
                    else -> {}
                }
            }
            if (profileUiState.isUploadImageLoading){
                Text("Updating profile ...")
            }
//            Spacer(modifier = Modifier.height(20.dp))
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
//            Spacer(modifier = Modifier.height(20.dp))
            Button(onClick = {
                if(!updateBioValidation(context, bio.value, name.value)){
                   return@Button
                }
                profileViewModel.updateProfile(UpdateProfileRequest(image = null, bio.value, name = name.value, null ))
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


@OptIn(ExperimentalMaterial3Api::class)
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
            profileViewModel.resetUploadWorkStatus()
        }
    }
    val profile = profileViewModel.myProfile.collectAsState().value
    val profileUiState = profileViewModel.profileUIState.collectAsState().value
    val bio = remember{
        mutableStateOf(profile.profile.bio.toString())
    }
    val name = remember {
        mutableStateOf(profile.profile.name.toString())
    }

    val context = LocalContext.current
    val workStatus  = profileViewModel.workStatus.collectAsState().value
    var uploadProgress by remember { mutableStateOf(0) }
    val selectedImage = remember { mutableStateOf<Uri?>(null) }
    var newUploadedUrl = ""
    val uploadSuccess = remember { mutableStateOf(false) }
    val currentUploadSuccess = rememberUpdatedState(uploadSuccess.value)

    LaunchedEffect(profileUiState.isUpdateProfileSuccess) {
        if(profileUiState.isUpdateProfileSuccess){
            Toast.makeText(context, "Updated!", Toast.LENGTH_SHORT).show()
            profileViewModel.resetUIState()
        }

    }

    LaunchedEffect(profileUiState.isUpdateProfileError) {
        if(profileUiState.isUpdateProfileError){
            Toast.makeText(context, profileUiState.updateProfileErrorMessage, Toast.LENGTH_SHORT).show()
            profileViewModel.resetUIState()
        }

    }
    LaunchedEffect(currentUploadSuccess.value) {
        if(currentUploadSuccess.value){
            // if uploadd successful then send api request to save image
//            profileViewModel.updateProfile(UpdateProfileRequest(
//                image = newUploadedUrl,
//                null,
//                null
//            ))
        }
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                // Process the URI (e.g., upload to Firebase)
                CLog.debug("FILE NAME", uri.toString())
//                uploadImageToCloudinary(context, uri) { progress ->
//                    uploadProgress = progress
//                }

                profileViewModel.uploadImage(uri)

            } catch (e: Exception) {
                Log.e("FilePicker", "Error processing file: ${e.message}")
            }
        } else {
            Log.e("FilePicker", "No URI selected")
        }
    }
    val imagePainter = painterResource(id = R.drawable.p2)
//    val name = remember { mutableStateOf("") }
//    val bio = remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    Box(
        modifier = Modifier.fillMaxSize().verticalScroll(scrollState)
    ){
        Surface(
            modifier = Modifier.fillMaxWidth()
                .height(LocalConfiguration.current.screenHeightDp.dp *0.4f),
            color = Gray2
        ) {
            if(profile.profile.image.isNotEmpty() ||profile.profile.image.isNotBlank() ){
                LoadImageWithPlaceholder(
                    profile.profile.image.toString(),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
        // Top Navigation Bar (Transparent)
        TopAppBar(
            title = { /* Empty for cleaner UI */ },
            navigationIcon = {
                IconButton(onClick = {    }) {
                    CircularIconButton({navController.navigateUp() })
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.Transparent, // Transparent background
                navigationIconContentColor = Color.White
            ),
            modifier = Modifier.background(Color.Transparent)
        )
        // edit image icon
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(LocalConfiguration.current.screenHeightDp.dp * 0.4f) // Ensure it aligns inside the image
        ) {
            AppRoundIconButton(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp), // Adds some space from the edge
                icon = Icons.Default.CameraAlt,
                desc = "Edit profile image",
                onClick = {launcher.launch("image/*")}
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize() // Make the surface fill the column width
                .padding(top =LocalConfiguration.current.screenHeightDp.dp *0.4f)
                .padding(start = 16.dp, end=16.dp)

            ) {
            Column(
                modifier = Modifier.fillMaxSize().padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(40.dp)
            ){
                // image upload state
                workStatus?.let { info ->
                    when (info.state) {
                        WorkInfo.State.ENQUEUED ->{
                            Text("On Queue...")
                        }
                        WorkInfo.State.RUNNING -> {
                            Text("Uploading...")
                        }
                        WorkInfo.State.SUCCEEDED -> {
                            val uploadedUrl = info.outputData.getString("uploadedUrl")
//                        if (uploadedUrl != null){
//                            newUploadedUrl = uploadedUrl
//                            uploadSuccess.value = true
//                        }
                            if (!uploadedUrl.isNullOrEmpty()) {
                                profileViewModel.updateProfile(
                                    UpdateProfileRequest(
                                        image = uploadedUrl,
                                        null,
                                        null,
                                        null
                                    )
                                )
                            }
                            profileViewModel.resetUploadWorkStatus()

//                        Text("Upload successful!")
                        }
                        WorkInfo.State.FAILED -> {
                            Text("Upload failed.", color = Color.Red)
                        }
                        else -> {}
                    }
                }
                if (profileUiState.isUploadImageLoading){
                    Text("Updating profile ...")
                }

                OutlinedTextField(
                    value = name.value,
                    onValueChange = {name.value = it},
                    modifier = Modifier.fillMaxWidth()
                        .height(56.dp)
                    ,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Gray2
                    ),
                    placeholder = {Text("Your Name",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.alpha(0.6f)
                    )}
                )

                OutlinedTextField(
                    value = bio.value,
                    onValueChange = {bio.value = it},
                    modifier = Modifier.fillMaxWidth()
                        .height(281.dp)

                    ,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = Gray2
                    ),
                    placeholder = {Text("About you here ...",
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.alpha(0.6f)
                        )},
                    singleLine = false,
                    maxLines = 8
                )

                Button(onClick = {
                    if(!updateBioValidation(context, bio.value, name.value)){
                        return@Button
                    }
                    profileViewModel.updateProfile(UpdateProfileRequest(image = null, bio.value, name = name.value, null ))
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
}



fun uploadImageToCloudinary(context: android.content.Context, imageUri: Uri, onProgress: (Int) -> Unit) {
    try {

        MediaManager.get().upload(imageUri)
            .unsigned("preset1") // Use an unsigned upload preset if you don't need server-side authentication
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {
                    // Upload started
                }

                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                    val progress = (bytes * 100 / totalBytes).toInt()
                    onProgress(progress)
                }

                override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                    val url = resultData["secure_url"] as String
                    // Handle success, e.g., save the URL to your database
                    println("Image uploaded successfully. URL: $url")

                }

                override fun onError(requestId: String, error: ErrorInfo) {
                    // Handle error
                    println("Upload error: ${error.description}")
                }

                override fun onReschedule(requestId: String, error: ErrorInfo) {
                    // Handle reschedule
                }
            })
            .dispatch()
    }catch (e:Exception){
        CLog.error("UPLOAD ERROR", e.toString())
    }

}

