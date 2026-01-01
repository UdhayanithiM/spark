package com.example.sparkapp.data

/**
 * A data class to hold the information for each video
 * @param title The text to display
 * @param videoResourceId The ID from the res/raw folder (e.g., R.raw.video1)
 */
data class VideoItem(
    val title: String,
    val videoResourceId: Int
)