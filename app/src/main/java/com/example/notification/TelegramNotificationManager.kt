package com.example.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color as AndroidColor
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.model.NotificationTheme

object TelegramNotificationManager {
    private const val CHANNEL_GROUP_ID = "telegram_custom_channels"

    fun createChannelForTheme(context: Context, theme: NotificationTheme): String {
        val channelId = "channel_${theme.id}"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = when (theme.priorityLevel) {
                "URGENT" -> NotificationManager.IMPORTANCE_HIGH
                "HIGH" -> NotificationManager.IMPORTANCE_HIGH
                "SILENT" -> NotificationManager.IMPORTANCE_LOW
                else -> NotificationManager.IMPORTANCE_DEFAULT
            }

            val channel = NotificationChannel(
                channelId,
                "TeleProto: ${theme.name}",
                importance
            ).apply {
                description = theme.description
                enableLights(theme.edgeGlow)
                try {
                    lightColor = AndroidColor.parseColor(theme.ledColorHex)
                } catch (_: Exception) {}

                val vibrationPattern = getVibrationPatternArray(theme.vibrationPatternName)
                if (vibrationPattern != null) {
                    enableVibration(true)
                    this.vibrationPattern = vibrationPattern
                } else {
                    enableVibration(false)
                }
            }

            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
        return channelId
    }

    fun triggerNotification(
        context: Context,
        theme: NotificationTheme,
        title: String,
        message: String,
        isSecret: Boolean = false
    ) {
        val channelId = createChannelForTheme(context, theme)
        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val displayTitle = if (isSecret && theme.id == "secret_stealth") "🔒 Telegram Secret" else title
        val displayText = if (isSecret && theme.id == "secret_stealth") "New end-to-end encrypted message" else message

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(displayTitle)
            .setContentText(displayText)
            .setPriority(
                if (theme.priorityLevel == "URGENT") NotificationCompat.PRIORITY_MAX
                else NotificationCompat.PRIORITY_HIGH
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        try {
            builder.setColor(AndroidColor.parseColor(theme.previewColorHex))
        } catch (_: Exception) {}

        // Vibrate device locally according to theme
        triggerHaptic(context, theme.vibrationPatternName)

        try {
            NotificationManagerCompat.from(context).notify(
                System.currentTimeMillis().toInt(),
                builder.build()
            )
        } catch (_: SecurityException) {
            // Handled when notification permission is not granted yet
        }
    }

    private fun getVibrationPatternArray(patternName: String): LongArray? {
        return when (patternName) {
            "Double Pulse" -> longArrayOf(0, 150, 100, 150)
            "Heartbeat" -> longArrayOf(0, 120, 80, 200, 80, 120)
            "Single Long" -> longArrayOf(0, 400)
            "Morse SOS" -> longArrayOf(0, 100, 100, 100, 100, 100, 200, 300, 100, 300, 100, 300, 200, 100, 100, 100)
            "Gentle Tick" -> longArrayOf(0, 50)
            else -> null
        }
    }

    fun triggerHaptic(context: Context, patternName: String) {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            val pattern = getVibrationPatternArray(patternName) ?: longArrayOf(0, 100)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(pattern, -1)
            }
        } catch (_: Exception) {}
    }
}
