package com.example.universalvideodownloader.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.universalvideodownloader.domain.extractor.MediaType

@Entity(tableName = "downloads")
data class DownloadEntity(
    @PrimaryKey
    val id: String,
    val sourceUrl: String,
    val outputName: String,
    val outputUri: String?, // SAF için DocumentFile URI
    val mediaType: MediaType,
    val status: String, // PENDING, DOWNLOADING, COMPLETED, FAILED, PAUSED
    val progress: Int,
    val downloadedBytes: Long,
    val totalBytes: Long,
    val speed: Long,
    val errorCode: String?,
    val createdAt: Long,
    val updatedAt: Long
)
