package com.vhennus.general.utils

import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage


fun uploadFileToFirebase(context: Context, fileUri: Uri) {
    Log.d("FILE LINK", fileUri.toString())
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference.child("images/profile_pics/test.jpg")

//    val storageRef = Firebase.storage.reference.child("${fileUri.lastPathSegment}")

    try {
        val contentResolver: ContentResolver = context.contentResolver
        contentResolver.openInputStream(fileUri)?.use { inputStream ->
            val uploadTask = storageRef.putStream(inputStream)
            uploadTask
                .addOnSuccessListener {
                    storageRef.downloadUrl.addOnSuccessListener { uri ->
                        Toast.makeText(context, "File uploaded: $uri", Toast.LENGTH_LONG).show()
                        Log.d("Firebase", "Download URL: $uri")
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Upload failed: ${e.message}", Toast.LENGTH_LONG).show()
                    Log.e("Firebase", "Upload failed", e)
                }
                .addOnProgressListener { taskSnapshot ->
                    val progress =
                        (100.0 * taskSnapshot.bytesTransferred) / taskSnapshot.totalByteCount
                    Log.d("Firebase", "Upload is $progress% done")
                }
        }

    }catch (e:Exception){
        Log.d("UPLOAD ERROR", e.toString())
    }

}