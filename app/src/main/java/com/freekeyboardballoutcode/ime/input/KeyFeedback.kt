package com.freekeyboardballoutcode.ime.input

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.SoundEffectConstants
import android.view.View

/** Gboard-like keypress sound / haptic feedback. */
object KeyFeedback {
    fun play(
        view: View,
        sound: Boolean,
        haptic: Boolean,
    ) {
        if (sound) {
            view.playSoundEffect(SoundEffectConstants.CLICK)
        }
        if (haptic) {
            vibrate(view.context)
        }
    }

    private fun vibrate(context: Context) {
        val vibrator = if (Build.VERSION.SDK_INT >= 31) {
            val mgr = context.getSystemService(VibratorManager::class.java)
            mgr?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } ?: return
        if (!vibrator.hasVibrator()) return
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(18, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(18)
        }
    }

    fun audioManager(context: Context): AudioManager? =
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
}
