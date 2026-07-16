package com.example.universalvideodownloader.data.local

import androidx.room.TypeConverter
import com.example.universalvideodownloader.domain.extractor.MediaType

class Converters {
    @TypeConverter
    fun fromMediaType(value: MediaType): String {
        return value.name
    }

    @TypeConverter
    fun toMediaType(value: String): MediaType {
        return try {
            MediaType.valueOf(value)
        } catch (e: Exception) {
            MediaType.UNKNOWN
        }
    }
}
