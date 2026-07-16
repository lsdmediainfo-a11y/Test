package com.example.universalvideodownloader.ui.downloads

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.universalvideodownloader.data.local.DownloadEntity

@Composable
fun DownloadsScreen(
    viewModel: DownloadsViewModel = hiltViewModel()
) {
    val downloads by viewModel.allDownloads.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(selectedTabIndex = 0) {
            Tab(selected = true, onClick = {}, text = { Text("Devam Eden") })
            Tab(selected = false, onClick = {}, text = { Text("Tamamlanan") })
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(downloads) { download ->
                DownloadItemCard(
                    download = download,
                    onPause = { viewModel.pauseDownload(download.id) },
                    onResume = { viewModel.resumeDownload(download.id) },
                    onCancel = { viewModel.cancelDownload(download.id) }
                )
            }
        }
    }
}

@Composable
fun DownloadItemCard(
    download: DownloadEntity,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = download.outputName, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = if (download.totalBytes > 0) download.downloadedBytes.toFloat() / download.totalBytes else 0f,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "Durum: ${download.status}")
                val downloadedMB = download.downloadedBytes / 1024 / 1024
                val totalMB = download.totalBytes / 1024 / 1024
                Text(text = "$downloadedMB MB / $totalMB MB")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                if (download.status == "DOWNLOADING") {
                    Button(onClick = onPause) { Text("Duraklat") }
                } else if (download.status == "PAUSED" || download.status == "FAILED") {
                    Button(onClick = onResume) { Text("Devam Et") }
                }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(onClick = onCancel) { Text("İptal") }
            }
        }
    }
}
