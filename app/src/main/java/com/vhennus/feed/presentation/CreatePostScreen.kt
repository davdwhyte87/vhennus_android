package com.vhennus.feed.presentation

import android.net.Uri
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.work.WorkInfo
import com.vhennus.feed.data.FeedViewModel
import com.vhennus.feed.domain.CreatePostReq
import com.vhennus.general.presentation.LoadImageWithPlaceholder
import com.vhennus.general.presentation.LoadImageWithUri
import com.vhennus.general.utils.CLog
import com.vhennus.profile.domain.UpdateProfileRequest
import com.vhennus.ui.GeneralScaffold


@Composable
fun createPostScreen(navController: NavController, feedViewModel: FeedViewModel){
    DisposableEffect(true) {
        onDispose {
            feedViewModel.clearModelData()
            feedViewModel.clearUIData()
        }
    }
    val postText = remember {
        mutableStateOf("")
    }
    val feedUIState = feedViewModel.feedUIState.collectAsState()
    if(feedUIState.value.isCreatePostError){
        Toast.makeText(LocalContext.current, feedUIState.value.createPostErrorMessage, Toast.LENGTH_SHORT).show()
        feedViewModel.clearUIData()
    }

    if(feedUIState.value.isCreatePostSuccess){
        Toast.makeText(LocalContext.current, "Post Created!", Toast.LENGTH_SHORT).show()
        feedViewModel.clearUIData()
        //navController.popBackStack()
    }

    val context = LocalContext.current
    val workStatus  = feedViewModel.workStatus.collectAsState().value
    val imageUri = feedViewModel.imageUri.collectAsState().value
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                feedViewModel.setImageURI(uri)
                //profileViewModel.uploadImage(uri)
            } catch (e: Exception) {

                Log.e("FilePicker", "Error processing file: ${e.message}")
                Toast.makeText(context, "Error selecting image", Toast.LENGTH_SHORT).show()
            }
        } else {
            Log.e("FilePicker", "No URI selected")
            Toast.makeText(context, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }
    GeneralScaffold(
        topBar = { createPostNav(navController,
            {
                feedViewModel.createPostB(CreatePostReq(text = postText.value, null))

        }, feedViewModel) }, floatingActionButton = { /*TODO*/ }) {


        Column (
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ){
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
                        CLog.debug("POST IMAGE URL", uploadedUrl.toString())
                        feedViewModel.createPost(CreatePostReq(
                            postText.value, uploadedUrl
                        ))
                        feedViewModel.resetUploadWorkStatus()
                        Text("Sending post...")
                    }
                    WorkInfo.State.FAILED -> {
                        Text("Upload failed.", color = Color.Red)
                    }
                    else -> {}
                }
            }
            IconButton(onClick = {
                launcher.launch("image/*")
            }) {
                Icon(Icons.Outlined.Image, "Image")
            }
            TextField(value = postText.value,
                onValueChange = { postText.value = it},
                maxLines = 20,
                singleLine = false,
                modifier = Modifier.fillMaxWidth().height(400.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                placeholder = { Text(
                    text = "What are you thinking?",
                    color = Color.Gray,

                    )}
            )
            // image preview
            Row {
                if(imageUri != null){
                    LoadImageWithUri(imageUri, modifier = Modifier.width(100.dp).height(100.dp))
                }
            }
        }


    }
}