package com.deezer.exoapplication.player.presentation

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.deezer.exoapplication.core.data.MetaDataReader
import com.deezer.exoapplication.core.domain.model.MetaData
import com.deezer.exoapplication.player.data.PlaybackStateObserver
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

@OptIn(ExperimentalCoroutinesApi::class)
class PlayerViewModelTest {
    private val testDispatcher = StandardTestDispatcher()

    private lateinit var metadataReader: MetaDataReader
    private lateinit var player: Player

    private lateinit var playbackEndObserver: PlaybackStateObserver
    private lateinit var playbackStateFlow: MutableStateFlow<Int>

    private lateinit var viewModel: PlayerViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        metadataReader = mockk()
        player = mockk(relaxed = true)

        playbackStateFlow= MutableStateFlow(Player.STATE_IDLE)
        playbackEndObserver = mockk()
        every { playbackEndObserver.playerPlaybackStateFlow }.returns(playbackStateFlow)

        viewModel = PlayerViewModel(metadataReader, player, playbackEndObserver)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when viewModel is created, state is an empty list of track`() = runTest {
        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        assertEquals(emptyList(), viewModel.uiState.value)
    }

    @Test
    fun `when user adds a first track, state is updated with the new track and played`() = runTest {
        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        //Given
        val uri = mockk<Uri>(relaxed = true)
        val metadata = MetaData("test.mp3")
        every { metadataReader.getMetaDataFromUri(uri) }.returns(metadata)

        //When
        viewModel.onTrackAdded(uri)

        testScheduler.advanceUntilIdle()

        //Then
        assertEquals(1, viewModel.uiState.value.size)
        assertEquals(metadata.fileName, viewModel.uiState.value.first().title)
        assert(viewModel.uiState.value.first().isSelected)
        verify(exactly = 1) { player.play() }

    }

    @Test
    fun `when user adds track to an existing playlist, state is updated with the new track but it's not played`() = runTest {
        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        //Given
        val uris = List(15) { index ->
            mockk<Uri>(relaxed = true)
                .also {
                    every { metadataReader.getMetaDataFromUri(it) }
                        .returns(MetaData("track_$index.mp3"))
                }
        }

        val newTrackMetadata = MetaData("newTrack.mp3")
        val newTrackUri = mockk<Uri>(relaxed = true)
        every { metadataReader.getMetaDataFromUri(newTrackUri) }.returns(newTrackMetadata)

        //When
        uris.forEach { viewModel.onTrackAdded(it) }
        viewModel.onTrackAdded(newTrackUri)

        testScheduler.advanceUntilIdle()

        //Then
        assertEquals(16, viewModel.uiState.value.size)
        assertEquals(newTrackMetadata.fileName, viewModel.uiState.value.last().title)
        assert(!viewModel.uiState.value.last().isSelected)
        verify(exactly = 1) { player.play() }

    }

    @Test
    fun `when user removes a track, state is updated without it`() = runTest {
        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        //Given
        val uris = List(15) { index ->
            mockk<Uri>(relaxed = true)
                .also {
                    every { metadataReader.getMetaDataFromUri(it) }
                        .returns(MetaData("track_$index.mp3"))
                }
        }
        uris.forEach { viewModel.onTrackAdded(it) }
        testScheduler.advanceUntilIdle()

        //When
        val trackToRemove = viewModel.uiState.value[7]
        viewModel.onTrackRemoved(trackToRemove.id)

        testScheduler.advanceUntilIdle()

        //Then
        assertEquals(14, viewModel.uiState.value.size)
        assertNull(viewModel.uiState.value.find { it.title == "track_7.mp3" })
    }

    @Test
    fun `when user removes a sole playing track, player medias are cleared`() = runTest {
        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        //Given
        val uri = mockk<Uri>(relaxed = true)
        val metadata = MetaData("test.mp3")
        every { metadataReader.getMetaDataFromUri(uri) }.returns(metadata)
        viewModel.onTrackAdded(uri)
        testScheduler.advanceUntilIdle()

        //When
        val trackToRemove = viewModel.uiState.value.first()
        viewModel.onTrackRemoved(trackToRemove.id)

        testScheduler.advanceUntilIdle()

        //Then
        assert(viewModel.uiState.value.isEmpty())
        verify { player.clearMediaItems() }
    }

    @Test
    fun `when user tap a track, track is selected and played`() = runTest {
        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        //Given
        val uris = List(15) { index ->
            mockk<Uri>(relaxed = true)
                .also {
                    every { metadataReader.getMetaDataFromUri(it) }
                        .returns(MetaData("track_$index.mp3"))
                }
        }
        uris.forEach { viewModel.onTrackAdded(it) }
        testScheduler.advanceUntilIdle()

        //When
        val trackToSelect = viewModel.uiState.value[4]
        viewModel.onTrackSelected(trackToSelect.id)

        testScheduler.advanceUntilIdle()

        //Then
        assert(viewModel.uiState.value.find { it.title == "track_4.mp3" }!!.isSelected)
        verify(exactly = 1) { player.setMediaItem(MediaItem.fromUri(uris[4])) }
        verify(exactly = 2) { player.play() }
    }

    @Test
    fun `when a track ends, next track is played if possible`() = runTest {
        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        //Given
        val uris = List(15) { index ->
            mockk<Uri>(relaxed = true)
                .also {
                    every { metadataReader.getMetaDataFromUri(it) }
                        .returns(MetaData("track_$index.mp3"))
                }
        }
        uris.forEach { viewModel.onTrackAdded(it) }
        testScheduler.advanceUntilIdle()

        //When
        playbackStateFlow.emit(Player.STATE_ENDED)

        testScheduler.advanceUntilIdle()

        //Then
        assert(viewModel.uiState.value[1].isSelected)
        verify(exactly = 1) { player.setMediaItem(MediaItem.fromUri(uris[1])) }
        verify(exactly = 2) { player.setMediaItem(any()) }
        verify(exactly = 2) { player.play() }
    }

    @Test
    fun `when the last track ends, its  removed from the player items`() = runTest {
        // Create an empty collector for the StateFlow
        backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
            viewModel.uiState.collect {}
        }

        //Given
        val uri = mockk<Uri>(relaxed = true)
        val metadata = MetaData("test.mp3")
        every { metadataReader.getMetaDataFromUri(uri) }.returns(metadata)

        //When
        viewModel.onTrackAdded(uri)
        testScheduler.advanceUntilIdle()
        playbackStateFlow.emit(Player.STATE_ENDED)
        testScheduler.advanceUntilIdle()

        //Then
        assertNull(viewModel.uiState.value.find { it.isSelected })
        verify(exactly = 1) { player.clearMediaItems() }
    }
}
