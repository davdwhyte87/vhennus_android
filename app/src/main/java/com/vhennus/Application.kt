package com.vhennus

import android.app.Application
import com.cloudinary.android.MediaManager
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application: Application(){
    override fun onCreate() {
        super.onCreate()
        // cloudinary
        MediaManager.init(this, mapOf(
            "cloud_name" to "dsxzbvfur",
            "api_key" to "412968929612186",
            "api_secret" to "Seav7Xjl8fLOv4T8uTjqIzq9Ks8"
        ))
    }
}