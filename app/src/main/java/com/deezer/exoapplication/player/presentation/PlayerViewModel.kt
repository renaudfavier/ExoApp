package com.deezer.exoapplication.player.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.deezer.exoapplication.player.domain.QueueManager
import com.deezer.exoapplication.player.domain.TrackRepository
import com.deezer.exoapplication.player.domain.model.TrackId
import com.deezer.exoapplication.player.presentation.model.PlayerScreenUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    val player: Player,
    private val trackRepository: TrackRepository,
    private val queueManager: QueueManager,
    private val mapper: TrackUiMapper,
) : ViewModel() {

    private val isPlayingListener = IsPlayingListener()
    private val isPlayerPlaying = MutableStateFlow(player.isPlaying)

    init {
        player.addListener(isPlayingListener)
    }

    val uiState = queueManager.playlistFlow
        .map { playlist ->
            playlist.mapNotNull { id ->
                trackRepository.getTrack(id).getOrNull()
            }
        }.combine(queueManager.selectedTrackIdFlow) { playlist, selectedTrackId ->
            playlist to selectedTrackId
        }.combine(isPlayerPlaying) { (playlist, selectedTrackId), isPlaying ->
            mapper.map(playlist, selectedTrackId, isPlaying)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlayerScreenUiModel.Empty
        )

    fun onTrackSelected(id: TrackId) = viewModelScope.launch {
        queueManager.selectTrack(id)
    }

    fun onTrackRemoved(id: TrackId) = viewModelScope.launch {
        queueManager.removeTrack(id)
    }

    fun onTrackAdded(uri: Uri) = viewModelScope.launch {
        trackRepository.addTrack(uri).fold(
            onSuccess = { trackId -> queueManager.addTrack(trackId) },
            onFailure = { TODO() }
        )
    }

    fun onPause() = viewModelScope.launch {
        player.pause()
    }

    fun onResume() = viewModelScope.launch {
        player.play()
    }

    override fun onCleared() {
        player.removeListener(isPlayingListener)
        super.onCleared()
    }

    inner class IsPlayingListener: Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            viewModelScope.launch {
                isPlayerPlaying.update { isPlaying }
            }
        }
    }
}
