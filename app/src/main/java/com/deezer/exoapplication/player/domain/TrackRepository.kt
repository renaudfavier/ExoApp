package com.deezer.exoapplication.player.domain

import android.net.Uri
import com.deezer.exoapplication.player.domain.model.Track
import com.deezer.exoapplication.player.domain.model.TrackId

interface TrackRepository {
    fun addTrack(uri: Uri): Result<TrackId>
    fun getTrack(id: TrackId): Result<Track>
}
