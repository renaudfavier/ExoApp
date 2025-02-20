package com.deezer.exoapplication.player.presentation

import com.deezer.exoapplication.player.domain.model.Track
import com.deezer.exoapplication.player.domain.model.TrackId
import com.deezer.exoapplication.player.presentation.model.PlayerScreenUiModel
import com.deezer.exoapplication.player.presentation.model.TrackUiModel
import com.google.common.collect.ImmutableList
import javax.inject.Inject

class TrackUiMapper @Inject constructor() {

    fun map(playlist: List<Track>, selectedTrackId: TrackId?, isPlaying: Boolean) =
        PlayerScreenUiModel(
            isPlayButtonEnabled = selectedTrackId != null,
            isPlaying = isPlaying,
            tracks = playlist.map(selectedTrackId)
        )

    private fun List<Track>.map(selectedTrackId: TrackId?) = map {
        it.map(it.id == selectedTrackId)
    }.toImmutableList()

    private fun List<TrackUiModel>.toImmutableList() =
        ImmutableList.builderWithExpectedSize<TrackUiModel>(size)
            .apply {
                addAll(this@toImmutableList)
            }.build()

    private fun Track.map(isSelected: Boolean): TrackUiModel =
        TrackUiModel(
            id = id,
            title = name,
            isSelected = isSelected
        )
}
