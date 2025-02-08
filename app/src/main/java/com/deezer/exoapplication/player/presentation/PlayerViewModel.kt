package com.deezer.exoapplication.player.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.deezer.exoapplication.core.data.MetaDataReader
import com.deezer.exoapplication.core.domain.model.TrackId
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
import javax.inject.Inject
import kotlin.random.Random

data class Track(val name: String, val mediaItem: MediaItem)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val metadataReader: MetaDataReader,
    val player: Player,
    playbackObserver: PlaybackStateObserver
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

    fun onTrackAdd(uri: Uri?) {
        uri ?: return
        val trackId = newTrackId()
        trackMap[trackId] = createTrack(uri)
        playlistFlow.update { playlist + trackId }
        if (selectedTrackId == null) selectedTrackIdFlow.update { trackId }
    }

    private fun playTrackOnSelectionChanged() = selectedTrackIdFlow
        .mapNotNull {
            trackMap[it]?.mediaItem
        }.onEach {
            player.setMediaItem(it)
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

    private fun newTrackId(): TrackId =
        Random.nextInt().takeUnless { it in trackMap } ?: newTrackId()

    private fun createTrack(uri: Uri): Track {
        val metadata = metadataReader.getMetaDataFromUri(uri)
        val name = metadata?.fileName ?: "No Name"
        return Track(name, MediaItem.fromUri(uri))
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
