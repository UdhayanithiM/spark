package com.example.sparkapp.ui.screens.module

import androidx.lifecycle.ViewModel
import com.example.sparkapp.R
import com.example.sparkapp.data.VideoItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ModuleViewModel : ViewModel() {

    private val _videos = MutableStateFlow<List<VideoItem>>(emptyList())
    val videos: StateFlow<List<VideoItem>> = _videos

    init {
        loadVideos()
    }

    private fun loadVideos() {
        // This list connects your titles to your video files (video1, video2, etc.)
        val videoList = listOf(
            VideoItem("1. Introduction to school related psychological issues", R.raw.video1),
            VideoItem("2. Identification of school related psychological issues", R.raw.video2),
            VideoItem("3. Autism spectrum disorder", R.raw.video3),
            VideoItem("4. Role of school counselor in autism spectrum disorder", R.raw.video4),
            VideoItem("5. Attention deficit hyperactivity disorder", R.raw.video5),
            VideoItem("6. Role of school counselor in Attention deficit hyperactivity disorder", R.raw.video6),
            VideoItem("7. Specific learning disability", R.raw.video7),
            VideoItem("8. Role of school counselor in Specific learning disability", R.raw.video8),
            VideoItem("9. Intellectual disability", R.raw.video9),
            VideoItem("10. Role of school counselor in Intellectual disability", R.raw.video10),
            VideoItem("11. Conduct disorder", R.raw.video11),
            VideoItem("12. Role of school counselor in Conduct disorder", R.raw.video12),
            VideoItem("13. School refusal", R.raw.video13),
            VideoItem("14. Anxiety disorder", R.raw.video14),
            VideoItem("15. Role of school counselor in anxiety disorder", R.raw.video15),
            VideoItem("16. Depressive disorder", R.raw.video16),
            VideoItem("17. Role of school counselor in depressive disorder", R.raw.video17)
        )
        _videos.value = videoList
    }
}