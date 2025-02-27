package com.deezer.exoapplication.core.data

import android.app.Application
import android.net.Uri
import android.provider.MediaStore
import com.deezer.exoapplication.core.domain.MetaDataReader
import com.deezer.exoapplication.core.domain.model.MetaData
import javax.inject.Inject

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
