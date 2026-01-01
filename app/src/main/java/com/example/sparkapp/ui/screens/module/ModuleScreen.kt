package com.example.sparkapp.ui.screens.module

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.sparkapp.data.VideoItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ModuleScreen(
    navController: NavController,
    viewModel: ModuleViewModel = viewModel()
) {
    val videos by viewModel.videos.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Module") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            // Display all 17 video items from the ViewModel
            items(videos) { videoItem ->
                VideoPlayerItem(
                    videoItem = videoItem,
                    onClick = {
                        // Navigate to fullscreen player
                        navController.navigate("fullscreen_player/${videoItem.videoResourceId}")
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Add the "Next" button at the end of the list
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        // This is where we will go next
                        // We will create "scenario" in the next step
                        navController.navigate("scenario")
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Next to Case Scenarios", style = MaterialTheme.typography.titleMedium)
                }
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}

/**
 * This is the UI for a single row in the list.
 * It shows the title and a non-playing video player that acts as a thumbnail.
 */
@Composable
private fun VideoPlayerItem(
    videoItem: VideoItem,
    onClick: () -> Unit
) {
    val context = LocalContext.current
    val videoUri = Uri.parse("android.resource://${context.packageName}/${videoItem.videoResourceId}")

    // Create an ExoPlayer instance just for this item
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUri)
            setMediaItem(mediaItem)
            prepare() // Prepare but don't play
        }
    }

    // Release the player when the item leaves the screen
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick) // Make the whole card clickable
        ) {
            // Video player UI
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = {
                        PlayerView(it).apply {
                            player = exoPlayer
                            useController = false // Hide controls for thumbnail
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
                // Play icon overlay, just like the Flutter app
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = "Play",
                    tint = Color.White.copy(alpha = 0.7f),
                    modifier = Modifier.size(64.dp)
                )
            }

            // Title below the video
            Text(
                text = videoItem.title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}