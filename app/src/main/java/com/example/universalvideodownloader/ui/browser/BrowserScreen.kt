package com.example.universalvideodownloader.ui.browser

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrowserScreen(
    viewModel: BrowserViewModel = hiltViewModel()
) {
    val inputUrl by viewModel.inputUrl.collectAsState()
    val activeUrl by viewModel.url.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val canGoBack by viewModel.canGoBack.collectAsState()
    val canGoForward by viewModel.canGoForward.collectAsState()
    val currentSession by viewModel.currentSession.collectAsState()
    val captureCount = currentSession?.activeEvents?.count { !it.isAd } ?: 0
    val adCount = currentSession?.activeEvents?.count { it.isAd } ?: 0
    val isVideoPlaying = currentSession?.isVideoPlaying == true

    Scaffold(
        topBar = {
            Column {
                TopAppBar(
                    title = { 
                        TextField(
                            value = inputUrl,
                            onValueChange = { viewModel.updateInputUrl(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go),
                            keyboardActions = KeyboardActions(
                                onGo = { viewModel.loadUrl(inputUrl) }
                            ),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                                focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                                unfocusedIndicatorColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                            )
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = { /* TODO: WebView'e geri bildirim gönderilecek */ },
                            enabled = canGoBack
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.loadUrl(activeUrl) }) {
                            Icon(Icons.Default.Refresh, contentDescription = "Yenile")
                        }
                    }
                )
                if (isLoading && progress > 0f) {
                    LinearProgressIndicator(
                        progress = progress,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        },
        floatingActionButton = {
            if (captureCount > 0) {
                ExtendedFloatingActionButton(
                    onClick = { /* Bottom sheet açılacak */ },
                    icon = { Icon(Icons.Default.KeyboardArrowDown, contentDescription = "İndirme") },
                    text = { 
                        val text = if (isVideoPlaying) "Video Bulundu ($captureCount)" else "Medya Yakalandı ($captureCount)"
                        val adText = if (adCount > 0) " | $adCount Reklam Gizlendi" else ""
                        Text(text + adText)
                    }
                )
            } else {
                FloatingActionButton(onClick = { /* Bottom sheet açılacak */ }) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "İndirme")
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
            CustomWebView(
                url = activeUrl,
                onPageStarted = { viewModel.onPageStarted(it) },
                onPageFinished = { viewModel.onPageFinished(it) },
                onProgressChanged = { viewModel.onProgressChanged(it) },
                onNavigationStateChanged = { back, forward -> 
                    viewModel.updateNavigationState(back, forward) 
                },
                onEventCaptured = { viewModel.onEventCaptured(it) }
            )
        }
    }
}
