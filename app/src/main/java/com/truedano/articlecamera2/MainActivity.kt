package com.truedano.articlecamera2

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.truedano.articlecamera2.ui.CameraScreen
import com.truedano.articlecamera2.ui.theme.ArticleCamera2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // 設置沉浸式狀態欄和導航欄
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        
        // Register the permission launcher
        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            // Callback for permission result - handled by the composable
        }
        
        setContent {
            ArticleCamera2Theme {
                CameraScreen(
                    onNeedPermission = { 
                        if (ContextCompat.checkSelfPermission(
                                this, 
                                Manifest.permission.CAMERA
                            ) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                        }
                    },
                    hasCameraPermission = ContextCompat.checkSelfPermission(
                        this, 
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                )
            }
        }
    }
}