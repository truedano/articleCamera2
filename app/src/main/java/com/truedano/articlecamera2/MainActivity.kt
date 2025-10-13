package com.truedano.articlecamera2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.truedano.articlecamera2.ui.CameraScreen
import com.truedano.articlecamera2.ui.theme.ArticleCamera2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ArticleCamera2Theme {
                CameraScreen()
            }
        }
    }
}