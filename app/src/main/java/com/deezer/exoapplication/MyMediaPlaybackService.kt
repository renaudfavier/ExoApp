package com.deezer.exoapplication

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.media3.common.Player
import com.deezer.exoapplication.player.data.MediaItemFactory
import com.deezer.exoapplication.player.domain.QueueManager
import com.deezer.exoapplication.player.domain.SongEndedRepository
import com.deezer.exoapplication.player.domain.TrackRepository
import com.deezer.exoapplication.player.domain.model.TrackId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class MyMediaPlaybackService : Service() {

    @Inject lateinit var player: Player
    @Inject lateinit var queueManager: QueueManager
    @Inject lateinit var songEndedRepository: SongEndedRepository
    @Inject lateinit var trackRepository: TrackRepository
    @Inject lateinit var mediaItemFactory: MediaItemFactory

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        when (intent?.action) {
            Action.START.toString() -> start()
            Action.STOP.toString() -> {
                player.release()
                stopSelf()
                isServiceRunning = false
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }



    private fun start() {
        player.prepare()
        val notification = NotificationCompat
            .Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("Media Player Notification")
            .build()

        startForeground(
            ID,
            notification,
        )
        isServiceRunning = true
        reactOnSelectedTrackChanged()
        playNextTrackOnSongEnded()

    }

    private fun reactOnSelectedTrackChanged() = queueManager
        .selectedTrackIdFlow
        .onEach { trackId ->
            if(trackId == null) {
                player.clearMediaItems()
            } else {
                playTrack(trackId)
            }
        }.launchIn(serviceScope)

    private fun playTrack(trackId: TrackId) {
        trackRepository.getTrack(trackId).fold(
            onSuccess = {
                val mediaItem = mediaItemFactory.createFromUri(it.uri)
                player.setMediaItem(mediaItem)
                player.play()
            },
            onFailure = { TODO() }
        )
    }

    private fun playNextTrackOnSongEnded() = songEndedRepository
        .observeSongEnded()
        .onEach { queueManager.next() }
        .launchIn(serviceScope)

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    enum class Action { START, STOP }

    companion object {
        var isServiceRunning = false
        const val ID = 8943545
        const val CHANNEL_ID = "exo_app_channel"
    }
}