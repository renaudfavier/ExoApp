package com.deezer.exoapplication.player.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.deezer.exoapplication.player.data.MediaPlayer
import com.deezer.exoapplication.player.domain.QueueManager
import com.deezer.exoapplication.player.domain.model.TrackId
import com.deezer.exoapplication.player.domain.repository.IsPlayingRepository
import com.deezer.exoapplication.player.domain.repository.TrackRepository
import com.deezer.exoapplication.player.presentation.model.PlayerScreenUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val mediaPlayer: MediaPlayer,
    isPlayingRepository: IsPlayingRepository,
    private val trackRepository: TrackRepository,
    private val queueManager: QueueManager,
    private val mapper: TrackUiMapper,
) : ViewModel() {

    private val isPlayerPlaying = isPlayingRepository
        .observeIsPlaying()
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            false
        )

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
        mediaPlayer.pause()
    }

    fun onResume() = viewModelScope.launch {
        mediaPlayer.play()
    }
}
