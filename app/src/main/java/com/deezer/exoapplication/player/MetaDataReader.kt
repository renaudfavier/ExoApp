package com.deezer.exoapplication.player

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import javax.inject.Inject

data class MetaData(val fileName: String)

interface MetaDataReader {
    fun getMetaDataFromUri(contentUri: Uri): MetaData?
}

class MetadataReaderImpl @Inject constructor(
    private val app: Application
) : MetaDataReader {
    override fun getMetaDataFromUri(contentUri: Uri): MetaData? {
        if (contentUri.scheme != "content") return null

        val fileName = app.contentResolver
            .query(
                /* uri = */ contentUri,
                /* projection = */ arrayOf(MediaStore.Audio.AudioColumns.DISPLAY_NAME),
                /* selection = */ null,
                /* selectionArgs = */ null,
                /* sortOrder = */ null
            )?.use {
                cursor ->
                val index = cursor.getColumnIndex(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(index)
            }

        return fileName?.let { fullFileName ->
            MetaData(fileName = Uri.parse(fullFileName)?.lastPathSegment ?: return null)
        }
    }
}
