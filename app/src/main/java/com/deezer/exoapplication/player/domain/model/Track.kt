package com.deezer.exoapplication.player.domain.model

import java.util.UUID

typealias TrackId = UUID

data class Track(
    val id: TrackId,
    val name: String,
    val uri: String
)
