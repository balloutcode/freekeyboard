package com.freekeyboardballoutcode.ime.input

import android.content.Context
import android.media.AudioManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.SoundEffectConstants
import android.view.View

/** Gboard-like keypress sound / haptic feedback with optional strength. */
object KeyFeedback {
    fun play(
        view: View,
        sound: Boolean,
        haptic: Boolean,
        soundVolume: Float = 1f,
        hapticStrength: Float = 1f,
    ) {
        if (sound) {
            // Click sound; stream volume is system-controlled — we gate by preference strength.
            if (soundVolume > 0.05f) {
                view.playSoundEffect(SoundEffectConstants.CLICK)
            }
        }
        if (haptic && hapticStrength > 0.05f) {
            vibrate(view.context, hapticStrength)
        }
    }

    private fun vibrate(context: Context, strength: Float) {
        val vibrator = if (Build.VERSION.SDK_INT >= 31) {
            val mgr = context.getSystemService(VibratorManager::class.java)
            mgr?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        } ?: return
        if (!vibrator.hasVibrator()) return
        val durationMs = (12 + 18 * strength.coerceIn(0.25f, 1f)).toLong()
        val amplitude = (80 + 175 * strength.coerceIn(0.25f, 1f)).toInt().coerceIn(1, 255)
        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(durationMs, amplitude))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(durationMs)
        }
    }

    fun audioManager(context: Context): AudioManager? =
        context.getSystemService(Context.AUDIO_SERVICE) as? AudioManager
}
