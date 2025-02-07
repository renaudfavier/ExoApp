package com.deezer.exoapplication.player

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.random.Random

data class AudioItem(val name: String, val mediaItem: MediaItem)

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val metadataReader: MetaDataReader,
    val player: Player,
) : ViewModel() {

    private val trackMap = mutableMapOf<Int, AudioItem>()
    private val selectedTrackFlow = MutableStateFlow<Int?>(null)
    private val playListFlow = MutableStateFlow<List<Int>>(emptyList())

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

    init {
        player.prepare()
        playTrackOnSelectionChanged()
    }

    fun onTrackSelected(id: Int) {
        selectedTrackFlow.update { id }
    }

    fun onTrackRemove(id: Int) {
        trackMap.remove(id)
        if (selectedTrackFlow.value == id) {
            selectedTrackFlow.update { null }
            player.stop()
            player.removeMediaItem(0)
        }
        playListFlow.update { playListFlow.value.filter { it != id } }
    }

    fun onTrackAdd(uri: Uri?) {
        uri ?: return
        val trackId = newTrackId()
        trackMap[trackId] = createAudioItem(uri)
        playListFlow.update { playListFlow.value + trackId }
    }

    private fun playTrackOnSelectionChanged() = viewModelScope.launch {
        selectedTrackFlow
            .mapNotNull {
                trackMap[it]?.mediaItem
            }.onEach {
                player.setMediaItem(it)
                player.play()
            }.collect()
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
