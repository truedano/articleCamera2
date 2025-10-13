package com.truedano.articlecamera2.ui

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutionException

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    onNeedPermission: () -> Unit = {},
    hasCameraPermission: Boolean = false
) {
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val context = LocalContext.current
    var isCameraReady by remember { mutableStateOf(false) }

    // Request permission if not granted
    if (!hasCameraPermission) {
        onNeedPermission()
    }

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
            if (hasCameraPermission) {
                CameraPreview(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(DarkGrayBackground),
                    setImageCapture = { 
                        imageCapture = it 
                        isCameraReady = true
                    },
                    onError = { error ->
                        // Handle camera error
                        isCameraReady = false
                    }
                )
            } else {
                // Show permission request message if permission not granted
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(DarkGrayBackground),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "需要相機權限",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "請允許相機權限以使用相機功能",
                            color = GrayText,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
        
        // 底部導航欄
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(120.dp)
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
            onClick = {
                if (hasCameraPermission && isCameraReady && imageCapture != null) {
                    takePhoto(imageCapture!!, context)
                } else if (!hasCameraPermission) {
                    onNeedPermission()
                } else {
                    Toast.makeText(context, "相機尚未準備完成", Toast.LENGTH_SHORT).show()
                }
            },
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
fun CameraPreview(
    modifier: Modifier = Modifier,
    setImageCapture: (ImageCapture) -> Unit = {},
    onError: (String) -> Unit = {}
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val previewView = remember { 
        PreviewView(context).apply {
            // Set the scale type for the preview
            scaleType = PreviewView.ScaleType.FILL_CENTER
        }
    }
    
    AndroidView(
        factory = { previewView },
        modifier = modifier,
        update = { previewView ->
            val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
            
            cameraProviderFuture.addListener({
                try {
                    val cameraProvider = cameraProviderFuture.get()
                    
                    // Unbind all previous camera use cases if any
                    cameraProvider.unbindAll()
                    
                    val preview = Preview.Builder()
                        .build()
                        .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                    
                    // 設置圖片捕獲用例
                    val imageCapture = ImageCapture.Builder()
                        .setTargetRotation(previewView.display.rotation)
                        .build()
                    
                    setImageCapture(imageCapture)
                    
                    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                    
                    try {
                        // Bind the camera to the lifecycle
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageCapture
                        )
                    } catch (exc: Exception) {
                        // Handle binding failure
                        exc.printStackTrace()
                        val errorMsg = "相機綁定失敗: ${exc.message}"
                        Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                        onError(errorMsg)
                    }
                } catch (e: ExecutionException) {
                    // Handle camera provider retrieval error
                    e.printStackTrace()
                    val errorMsg = "相機提供者錯誤: ${e.message}"
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    onError(errorMsg)
                } catch (e: InterruptedException) {
                    // Handle interruption
                    e.printStackTrace()
                    val errorMsg = "相機初始化被中斷"
                    Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                    onError(errorMsg)
                }
            }, ContextCompat.getMainExecutor(context))
        }
    )
}

// 拍照函數
fun takePhoto(imageCapture: ImageCapture, context: android.content.Context) {
    // 檢查是否支援相機功能
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
        return
    }

    val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US).format(System.currentTimeMillis())
    
    // For Android 10+ (API 29+), use MediaStore (which is always the case since minSdk=29)
    val contentValues = android.content.ContentValues().apply {
        put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, "${name}.jpg")
        put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES + "/ArticleCamera2")
    }

    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        contentValues
    ).build()

    // 設置照片捕獲的回調
    imageCapture.takePicture(
        outputOptions,
        ContextCompat.getMainExecutor(context),
        object : ImageCapture.OnImageSavedCallback {
            override fun onError(exception: ImageCaptureException) {
                exception.printStackTrace()
                Toast.makeText(context, "拍照失敗: ${exception.message}", Toast.LENGTH_LONG).show()
            }

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                // For Android 10+, the URI should be provided and image is already saved to MediaStore
                output.savedUri
                Toast.makeText(context, "照片已保存至相簿", Toast.LENGTH_LONG).show()
            }
        }
    )
}

private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"