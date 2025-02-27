package com.deezer.exoapplication.player.domain.model

typealias TrackId = String

data class Track(
    val id: TrackId,
    val name: String,
    val uri: String
)
