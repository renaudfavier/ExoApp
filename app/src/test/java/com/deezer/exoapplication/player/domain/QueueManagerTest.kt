package com.deezer.exoapplication.player.domain

import app.cash.turbine.test
import com.deezer.exoapplication.player.domain.model.TrackId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*

import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class QueueManagerTest {

    private val testDispatcher = StandardTestDispatcher()

    private lateinit var queueManager: QueueManager

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        queueManager = QueueManager()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when queue is created, playlist is empty and no track is selected`() = runTest {
        assertNull(queueManager.selectedTrackIdFlow.value)
        assertEquals(emptyList<TrackId>(), queueManager.playlistFlow.value)
    }

    @Test
    fun `adding a first track selects it`() = runTest {
        queueManager.selectedTrackIdFlow.test {

            skipItems(1)

            queueManager.addTrack("id_1")

            assertEquals("id_1", awaitItem())

            ensureAllEventsConsumed()
        }
    }

    @Test
    fun `calling next when last track is select unselect it`() = runTest {
        queueManager.selectedTrackIdFlow.test {

            assertEquals(null, awaitItem())

            queueManager.addTrack("id_1")

            skipItems(2)

            queueManager.next()
            assertEquals(null, awaitItem())

            expectNoEvents()
        }
    }

    @Test
    fun `calling next when no track is selected does nothing`() = runTest {
        queueManager.selectedTrackIdFlow.test {

            queueManager.addTrack("id_1")
            queueManager.next()

            // null then id_1 then null
            skipItems(3)

            queueManager.next()

            ensureAllEventsConsumed()
        }
    }


}