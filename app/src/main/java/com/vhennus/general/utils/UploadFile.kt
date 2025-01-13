package com.vhennus.general.utils

import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.impl.utils.futures.SettableFuture
import androidx.work.workDataOf
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.google.common.util.concurrent.ListenableFuture
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.UUID


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


class ImageUploadWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val imageUriString = inputData.getString("imageUri") ?: return Result.failure()
        val imageUri = Uri.parse(imageUriString)
        val userName = inputData.getString("userName")?: return Result.failure()

        return try {
            val resultUrl = uploadImageToCloudinary(imageUri, userName)
            if (resultUrl != null) {
                Result.success(workDataOf("uploadedUrl" to resultUrl))
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private suspend fun uploadImageToCloudinary(imageUri: Uri, userName:String): String? {
        return suspendCancellableCoroutine { continuation ->
            MediaManager.get().upload(imageUri)
                .option("folder", "profile")
                .option("public_id", userName)
                .option("overwrite", true)
                .option("upload_preset", "preset1")
                .callback(object : UploadCallback {
                    override fun onStart(requestId: String) {}

                    override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}

                    override fun onSuccess(requestId: String, resultData: Map<*, *>) {
                        val uploadedUrl = resultData["secure_url"] as String
                        if (continuation.isActive) {
                            continuation.resume(uploadedUrl, null)
                        }
                    }

                    override fun onError(requestId: String, error: ErrorInfo) {
                        if (continuation.isActive) {
                            continuation.resume(null, null)
                        }
                    }

                    override fun onReschedule(requestId: String, error: ErrorInfo) {}
                })
                .dispatch()
        }
    }
}