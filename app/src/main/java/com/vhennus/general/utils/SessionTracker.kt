package com.vhennus.general.utils

import android.content.SharedPreferences
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import java.util.concurrent.TimeUnit

class SessionTracker(
    private val prefs: SharedPreferences
) : LifecycleEventObserver {

    private var sessionStart: Long = 0L

    init {
        // Attach to the application’s lifecycle
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        when (event) {
            Lifecycle.Event.ON_START -> {
                // App goes foreground
                CLog.debug("TIMER START", "")
                sessionStart = System.currentTimeMillis()
            }
            Lifecycle.Event.ON_STOP -> {

                // App goes background
                val sessionEnd = System.currentTimeMillis()
                val elapsedMs = sessionEnd - sessionStart

                // Convert to minutes and accumulate
                val prevMinutes = prefs.getLong("time_spent", 0L)
                val addedMinutes = TimeUnit.MILLISECONDS.toMinutes(elapsedMs)
                prefs.edit()
                    .putLong("time_spent", prevMinutes + addedMinutes)
                    .apply()
                CLog.debug("TIMER STOP", (prevMinutes + addedMinutes).toString())
            }
            else -> { /* ignore */ }
        }
    }

    fun getTotalMinutes(): Long =
        prefs.getLong("time_spent", 0L)
}
