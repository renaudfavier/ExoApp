package com.deezer.exoapplication.player.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.deezer.exoapplication.core.data.MetaDataReader
import com.deezer.exoapplication.player.domain.model.Track
import com.deezer.exoapplication.player.data.PlaybackStateObserver
import com.deezer.exoapplication.player.domain.model.TrackId
import com.deezer.exoapplication.player.presentation.model.TrackUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    val player: Player,
    playbackObserver: PlaybackStateObserver,
    private val metadataReader: MetaDataReader,
) : ViewModel() {

    private val trackMap = mutableMapOf<TrackId, Track>()

    private val selectedTrackIdFlow = MutableStateFlow<TrackId?>(null)
    private val selectedTrackId get() = selectedTrackIdFlow.value

    private val playlistFlow = MutableStateFlow<List<TrackId>>(emptyList())
    private val playlist get() = playlistFlow.value

    private val playerPlaybackStateFlow = playbackObserver.playerPlaybackStateFlow

    init {
        player.prepare()
        playTrackOnSelectionChanged()
        playNextTrackOnPlaybackEnded()
    }

    val uiState = playlistFlow
        .combine(selectedTrackIdFlow) { playlist, selectedTrackId ->
            playlist.map { trackId ->
                TrackUiModel(
                    id = trackId,
                    title = trackMap[trackId]?.name ?: "No Name",
                    isSelected = trackId == selectedTrackId
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onTrackSelected(id: TrackId) {
        selectedTrackIdFlow.update { id }
    }

    fun onTrackRemoved(id: TrackId) {
        if (id == selectedTrackId) {
            //clearMediaItems() triggers a Player.STATE_END event that we observe and call playNextTrackInQueue()
            //So the track to remove will no longer be selected
            player.clearMediaItems()
        }
        trackMap.remove(id)
        playlistFlow.update { playlist.filter { it != id } }
    }

    fun onTrackAdded(uri: Uri) {
        val track = createTrack(uri)
        trackMap[track.id] = track
        playlistFlow.update { playlist + track.id }
        if (selectedTrackId == null) selectedTrackIdFlow.update { track.id }
    }

    private fun playTrackOnSelectionChanged() = selectedTrackIdFlow
        .mapNotNull {
            trackMap[it]?.uri
        }.onEach {
            val mediaItem = MediaItem.fromUri(it)
            player.setMediaItem(mediaItem)
            player.play()
        }.launchIn(viewModelScope)

    private fun playNextTrackOnPlaybackEnded() = playerPlaybackStateFlow
        .filter { it == Player.STATE_ENDED }
        .onEach { playNextTrackInQueue() }
        .launchIn(viewModelScope)

    private fun playNextTrackInQueue() {
        val selectedTrackIndex = playlist.indexOf(selectedTrackId)
        if (selectedTrackIndex == playlist.lastIndex) {
            selectedTrackIdFlow.update { null }
            player.clearMediaItems()
        } else {
            selectedTrackIdFlow.update { playlist[selectedTrackIndex + 1] }
        }
    }

    private fun createTrack(uri: Uri) =
        Track(
            id = UUID.randomUUID(),
            name = metadataReader.getMetaDataFromUri(uri)?.fileName ?: "No Name",
            uri = uri.toString()
        )

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
