package com.truedano.articlecamera2.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import java.util.concurrent.ExecutionException

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen() {
    // 定義顏色
    val darkBackgroundColor = Color(0xFF000000) // 深黑色
    val grayBackgroundColor = Color(0xFF333333) // 深灰色
    val blueColor = Color(0xFF2196F3) // 藍色
    val grayColor = Color(0xFF888888) // 灰色
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Article Camera",
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkBackgroundColor
                )
            )
        },
        bottomBar = {
            NavigationBar(
                modifier = Modifier.height(80.dp),
                containerColor = darkBackgroundColor
            ) {
                // API Key 項目 (未選中)
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "API Key",
                            tint = grayColor
                        )
                    },
                    label = {
                        Text(
                            text = "API Key",
                            color = grayColor,
                            textAlign = TextAlign.Center
                        )
                    },
                    selected = false,
                    onClick = { /* API Key 頁面導航 */ }
                )
                
                // Camera 項目 (選中)
                NavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Camera",
                            tint = blueColor
                        )
                    },
                    label = {
                        Text(
                            text = "Camera",
                            color = blueColor,
                            textAlign = TextAlign.Center
                        )
                    },
                    selected = true,
                    onClick = { /* Camera 頁面導航 */ }
                )
            }
        },
        content = { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // 主要內容區域 - 相機預覽
                CameraPreview(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                )
                
                // 快門按鈕
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    FloatingActionButton(
                        onClick = { /* 拍照功能 */ },
                        containerColor = blueColor,
                        contentColor = Color.White,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(8.dp),
                        shape = androidx.compose.foundation.shape.CircleShape
                    ) {
                        // 這裡可以放置拍照圖標
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = "Take Photo",
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun CameraPreview(modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { PreviewView(context) }
    
    AndroidView(
        factory = { previewView },
        modifier = modifier,
        update = { previewView ->
            when (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)) {
                PackageManager.PERMISSION_GRANTED -> {
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                    
                    cameraProviderFuture.addListener({
                        try {
                            val cameraProvider = cameraProviderFuture.get()
                            
                            val preview = Preview.Builder()
                                .build()
                                .also {
                                    it.setSurfaceProvider(previewView.surfaceProvider)
                                }
                            
                            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                            
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview
                            )
                        } catch (e: ExecutionException) {
                            // Handle camera binding error
                            e.printStackTrace()
                        } catch (e: InterruptedException) {
                            // Handle camera binding interruption
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(context))
                }
                PackageManager.PERMISSION_DENIED -> {
                    // 處理權限被拒絕的情況
                    // 在實際應用中，您可能需要請求權限
                }
            }
        }
    )
}