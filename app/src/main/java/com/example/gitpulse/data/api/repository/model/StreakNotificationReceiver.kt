package com.example.gitpulse

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat

class StreakNotificationReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val prefs = context.getSharedPreferences("gitpulse", Context.MODE_PRIVATE)
        val currentStreak = prefs.getString("currentStreak", "0")?.toIntOrNull() ?: 0
        val committedToday = prefs.getBoolean("committedToday", false)

        if (currentStreak > 0 && !committedToday) {
            showNotification(context, currentStreak)
        }
    }

    private fun showNotification(context: Context, streak: Int) {
        val channelId = "streak_guardian"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Streak Guardian",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when your streak is at risk"
            }
            notificationManager.createNotificationChannel(channel)
        }

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = androidx.core.app.NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("⚠️ Streak at Risk!")
            .setContentText("You have 4 hours left to save your $streak day streak! 🔥")
            .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(1001, notification)
    }
}