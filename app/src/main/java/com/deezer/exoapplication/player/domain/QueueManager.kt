package com.deezer.exoapplication.player.domain

import com.deezer.exoapplication.player.domain.model.TrackId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import javax.inject.Inject

class QueueManager @Inject constructor() {

    private val _selectedTrackIdFlow = MutableStateFlow<TrackId?>(null)
    val selectedTrackIdFlow : StateFlow<TrackId?> get() = _selectedTrackIdFlow
    private val selectedTrackId get() = selectedTrackIdFlow.value

    private val _playlistFlow = MutableStateFlow<List<TrackId>>(emptyList())
    val playlistFlow : StateFlow<List<TrackId>> get() = _playlistFlow
    private val playlist get() = playlistFlow.value

    suspend fun next() = withContext(Dispatchers.Main) {
        val selectedTrackIndex = playlist.indexOf(selectedTrackId)
        if (selectedTrackIndex == playlist.lastIndex) {
            _selectedTrackIdFlow.update { null }
        } else {
            _selectedTrackIdFlow.update { playlist[selectedTrackIndex + 1] }
        }
    }

    suspend fun addTrack(id: TrackId) = withContext(Dispatchers.Main) {
        _playlistFlow.update { playlist + id }
        if (selectedTrackId == null) _selectedTrackIdFlow.update { id }
    }

    suspend fun removeTrack(id: TrackId) = withContext(Dispatchers.Main) {
        if (id == selectedTrackId) { _selectedTrackIdFlow.update { null } }
        _playlistFlow.update { playlist.filter { it != id } }
    }

    suspend fun selectTrack(id: TrackId) = withContext(Dispatchers.Main) {
        _selectedTrackIdFlow.update { id }
    }
}
