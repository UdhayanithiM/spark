package com.example.sparkapp.ui.screens.module

import android.app.Activity
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController

@Composable
fun FullScreenPlayerScreen(
    navController: NavController,
    videoId: Int
) {
    val context = LocalContext.current

    // --- 1. IMMERSIVE MODE LOGIC ---
    // Hide Status Bar and Navigation Bar for Cinema Experience
    DisposableEffect(Unit) {
        val window = (context as? Activity)?.window
        if (window != null) {
            val insetsController = WindowCompat.getInsetsController(window, window.decorView)
            insetsController.hide(WindowInsetsCompat.Type.systemBars())
            insetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }

        onDispose {
            // Restore System Bars when leaving
            val window = (context as? Activity)?.window
            if (window != null) {
                val insetsController = WindowCompat.getInsetsController(window, window.decorView)
                insetsController.show(WindowInsetsCompat.Type.systemBars())
            }
        }
    }

    val videoUri = Uri.parse("android.resource://${context.packageName}/$videoId")

    // --- 2. PLAYER SETUP ---
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(videoUri)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true // Auto-play
        }
    }

    // Cleanup player
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    // --- 3. UI LAYOUT ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black) // Cinema Black Background
    ) {
        // Video View
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = true
                    // Ensure video fits screen while maintaining aspect ratio
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Close Button Overlay
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(top = 24.dp, start = 24.dp) // Adjust padding for cutouts
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape) // Semi-transparent backing
                .size(48.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Close Player",
                tint = Color.White
            )
        }
    }
}