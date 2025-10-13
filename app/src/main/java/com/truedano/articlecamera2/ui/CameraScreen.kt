package com.truedano.articlecamera2.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.truedano.articlecamera2.ui.theme.BlueAccent
import com.truedano.articlecamera2.ui.theme.DarkCharcoalBackground
import com.truedano.articlecamera2.ui.theme.DarkGrayBackground
import com.truedano.articlecamera2.ui.theme.GrayText
import com.truedano.articlecamera2.ui.theme.LightGrayBorder
import java.util.concurrent.ExecutionException

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkGrayBackground)
    ) {
        // 主要內容
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // 頂部應用欄
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Article Camera",
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkCharcoalBackground
                ),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
            
            // 相機預覽區域
            CameraPreview(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(DarkGrayBackground)
            )
        }
        
        // 底部導航欄
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(80.dp)
                .background(DarkCharcoalBackground)
                .windowInsetsPadding(WindowInsets.navigationBars)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // API Key 項目 (左側)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { /* API Key 頁面導航 */ }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "API Key",
                        tint = GrayText,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "API Key",
                        color = GrayText,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
                
                // 中間空白區域，為快門按鈕預留空間
                Box(modifier = Modifier.size(80.dp))
                
                // Camera 項目 (右側)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { /* Camera 頁面導航 */ }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Camera",
                        tint = BlueAccent,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Camera",
                        color = BlueAccent,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        // 快門按鈕 (覆蓋在底部導航欄之上)
        Button(
            onClick = { /* 拍照功能 */ },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-20).dp)
                .size(72.dp)
                .border(3.dp, LightGrayBorder, CircleShape)
                .zIndex(1f),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = BlueAccent
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
            // 無圖示，純實心圓形按鈕
        }
    }
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
                                    it.surfaceProvider = previewView.surfaceProvider
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