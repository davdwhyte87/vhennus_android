package com.vhennus.general.utils
import android.content.Context
import android.content.res.Resources
import android.media.MediaPlayer
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.vhennus.R
import javax.inject.Inject


class SoundVibratorHelper @Inject constructor(private val context: Context) {
    private val mediaPlayer = MediaPlayer.create(context, R.raw.bellsound)
    private val lowbelMedia = MediaPlayer.create(context, R.raw.lowbellsound)

    private val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val manager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
        manager.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    }

    fun playSoundAndVibrate() {
        // Play sound
        mediaPlayer.seekTo(0)
        mediaPlayer.start()

        // Vibrate for 500ms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(500)
        }
    }

    fun playLowBel(){
        lowbelMedia.seekTo(0)
        lowbelMedia.start()
    }

    fun cleanup() {
        mediaPlayer.release()
    }
}