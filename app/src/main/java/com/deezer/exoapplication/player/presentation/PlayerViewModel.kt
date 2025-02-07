package com.deezer.exoapplication.player.presentation

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.deezer.exoapplication.core.data.MetaDataReader
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

data class AudioItem(val name: String, val mediaItem: MediaItem)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val metadataReader: MetaDataReader,
    val player: Player,
    playbackObserver: PlaybackStateObserver
) : ViewModel() {

    private val trackMap = mutableMapOf<Int, AudioItem>()
    private val selectedTrackFlow = MutableStateFlow<Int?>(null)
    private val playListFlow = MutableStateFlow<List<Int>>(emptyList())
    private val playerPlaybackStateFlow = playbackObserver.playerPlaybackStateFlow

    init {
        player.prepare()
        playTrackOnSelectionChanged()
        playNextTrackOnPlaybackEnded()
    }

    val uiState = playListFlow
        .combine(selectedTrackFlow) { playList, selectedTrack ->
            playList.map { id ->
                TrackUiModel(
                    id = id,
                    title = trackMap[id]?.name ?: "No Name",
                    isSelected = id == selectedTrack
                )
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onTrackSelected(id: Int) {
        selectedTrackFlow.update { id }
    }

    fun onTrackRemove(id: Int) {
        if (selectedTrackFlow.value == id) {
            player.clearMediaItems()
        }
        trackMap.remove(id)
        playListFlow.update { playListFlow.value.filter { it != id } }
    }

    fun onTrackAdd(uri: Uri?) {
        uri ?: return
        val trackId = newTrackId()
        trackMap[trackId] = createAudioItem(uri)
        playListFlow.update { playListFlow.value + trackId }
        if (selectedTrackFlow.value == null) selectedTrackFlow.update { trackId }
    }

    private fun playTrackOnSelectionChanged() = selectedTrackFlow
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
        val index = playListFlow.value.indexOf(selectedTrackFlow.value)
        if (index == playListFlow.value.lastIndex) {
            selectedTrackFlow.update { null }
            player.clearMediaItems()
        } else {
            selectedTrackFlow.update { playListFlow.value[index + 1] }
        }
    }

    private fun newTrackId(): Int =
        Random.nextInt().takeUnless { it in trackMap } ?: newTrackId()

    private fun createAudioItem(uri: Uri): AudioItem {
        val metadata = metadataReader.getMetaDataFromUri(uri)
        val name = metadata?.fileName ?: "No Name"
        return AudioItem(name, MediaItem.fromUri(uri))
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}
