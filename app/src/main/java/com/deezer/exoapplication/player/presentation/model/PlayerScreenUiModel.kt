package com.deezer.exoapplication.player.presentation.model

import com.google.common.collect.ImmutableList

data class PlayerScreenUiModel(
    val isPlayButtonEnabled: Boolean,
    val isPlaying: Boolean,
    val tracks: ImmutableList<TrackUiModel>,
) {
    companion object {
        val Empty = PlayerScreenUiModel(
            isPlayButtonEnabled = true,
            isPlaying = false,
            tracks = ImmutableList.of()
        )
    }
}
