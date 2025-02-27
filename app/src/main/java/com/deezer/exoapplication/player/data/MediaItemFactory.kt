package com.deezer.exoapplication.player.data

import androidx.media3.common.MediaItem
import javax.inject.Inject

//This simple class is there to avoid static calls in classes we'd potentially unit test
class MediaItemFactory @Inject constructor() {
    fun createFromUri(uri:String): MediaItem = MediaItem.fromUri(uri)
}
