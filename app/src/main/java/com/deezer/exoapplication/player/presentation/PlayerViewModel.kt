package com.deezer.exoapplication.player.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.Player
import com.deezer.exoapplication.player.domain.QueueManager
import com.deezer.exoapplication.player.domain.TrackRepository
import com.deezer.exoapplication.player.domain.model.TrackId
import com.google.common.collect.ImmutableList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    val player: Player,
    private val trackRepository: TrackRepository,
    private val queueManager: QueueManager,
    private val mapper: TrackUiMapper,
) : ViewModel() {

    val uiState = queueManager.playlistFlow
        .map { playlist ->
            playlist.mapNotNull { id ->
                trackRepository.getTrack(id).getOrNull()
            }
        }.combine(queueManager.selectedTrackIdFlow) { playlist, selectedTrackId ->
            mapper.map(playlist, selectedTrackId)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = ImmutableList.of()
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
}
