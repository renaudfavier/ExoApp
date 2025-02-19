package com.deezer.exoapplication.player.data

import android.net.Uri
import com.deezer.exoapplication.player.domain.TrackRepository
import com.deezer.exoapplication.player.domain.model.Track
import com.deezer.exoapplication.player.domain.model.TrackId
import javax.inject.Inject

class InMemoryTrackRepository @Inject constructor(
    private val trackFactory: TrackFactory
): TrackRepository {

    private val trackMap = mutableMapOf<TrackId, Track>()

    override fun addTrack(uri: Uri): Result<TrackId> {
        val track = trackFactory.createTrack(uri)
        trackMap[track.id] = track
        return Result.success(track.id)
    }

    override fun getTrack(id: TrackId): Result<Track> {
        val track = trackMap[id] ?: return Result.failure(NoSuchElementException())
        return Result.success(track)
    }

}