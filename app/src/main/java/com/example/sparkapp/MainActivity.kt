package com.example.sparkapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.sparkapp.ui.theme.SparkAppTheme // Use your project's theme name

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Set the "SparkAppTheme" (which we'll edit) as the app's theme
            SparkAppTheme {
                // Call our navigation controller
                MainNavigation()
            }
        }
    }
}