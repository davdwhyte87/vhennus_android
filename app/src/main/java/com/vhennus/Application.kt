package com.vhennus

import android.app.Application
import android.content.Context
import com.cloudinary.android.MediaManager
import com.vhennus.general.utils.SessionTracker
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class Application: Application(){
    override fun onCreate() {
        super.onCreate()

        val prefs = getSharedPreferences("app", Context.MODE_PRIVATE)
        SessionTracker(prefs)

        // cloudinary
        MediaManager.init(this, mapOf(
            "cloud_name" to "dsxzbvfur",
            "api_key" to "412968929612186",
            "api_secret" to "Seav7Xjl8fLOv4T8uTjqIzq9Ks8"
        ))
    }
}