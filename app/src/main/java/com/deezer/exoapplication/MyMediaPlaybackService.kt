package com.deezer.exoapplication

import android.app.Service
import android.content.Intent
import android.content.pm.ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat

class MyMediaPlaybackService : Service() {

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            Action.START.toString() -> start()
            Action.STOP.toString() -> stopSelf()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    private fun start() {
        val notification = NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Track is playing")
            .build()

        startForeground(
            ID,
            notification,
        )

    }

    enum class Action { START, STOP }

    companion object {
        const val ID = 8943545
        const val CHANNEL_ID = "exo_app_channel"
    }
}