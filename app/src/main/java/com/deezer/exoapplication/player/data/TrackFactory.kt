package com.deezer.exoapplication.player.data

import android.net.Uri
import com.deezer.exoapplication.core.domain.MetaDataReader
import com.deezer.exoapplication.player.domain.model.Track
import java.util.UUID
import javax.inject.Inject

class TrackFactory @Inject constructor(
    private val metaDataReader: MetaDataReader
) {
    fun createTrack(uri: Uri) = Track(
        id = UUID.randomUUID(),
        name = metaDataReader.getMetaDataFromUri(uri)?.fileName ?: "No Name",
        uri = uri.toString()
    )
}


