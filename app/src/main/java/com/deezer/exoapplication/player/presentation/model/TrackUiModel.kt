package com.deezer.exoapplication.player.presentation.model

import com.deezer.exoapplication.core.domain.model.TrackId

data class TrackUiModel(val id: TrackId, val title: String, val isSelected: Boolean)
