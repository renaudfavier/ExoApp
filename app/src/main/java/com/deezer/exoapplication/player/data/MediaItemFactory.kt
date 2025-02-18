package com.deezer.exoapplication.player.data

import androidx.media3.common.MediaItem
import javax.inject.Inject

//This simple class is there to avoid static calls in the viewmodel hence making it more testable
class MediaItemFactory @Inject constructor() {
    fun createFromUri(uri:String): MediaItem = MediaItem.fromUri(uri)
}
