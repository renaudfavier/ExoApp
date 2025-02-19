package com.deezer.exoapplication.core.domain

import android.net.Uri
import com.deezer.exoapplication.core.domain.model.MetaData

interface MetaDataReader {
    fun getMetaDataFromUri(contentUri: Uri): MetaData?
}