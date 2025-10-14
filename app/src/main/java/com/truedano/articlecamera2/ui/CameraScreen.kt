package com.truedano.articlecamera2.ui

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
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
import com.truedano.articlecamera2.model.ImageToTextConverter
import com.truedano.articlecamera2.ui.theme.BlueAccent
import com.truedano.articlecamera2.ui.theme.DarkCharcoalBackground
import com.truedano.articlecamera2.ui.theme.DarkGrayBackground
import com.truedano.articlecamera2.ui.theme.GrayText
import com.truedano.articlecamera2.ui.theme.LightGrayBorder
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.ExecutionException

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    onNeedPermission: () -> Unit = {},
    hasCameraPermission: Boolean = false,
    onNavigateToApiKey: () -> Unit,
    onNavigateToCamera: () -> Unit,
    selectedScreen: String
) {
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val context = LocalContext.current
    var isCameraReady by remember { mutableStateOf(false) }

    // Request permission if not granted
    if (!hasCameraPermission) {
        onNeedPermission()
    }

    Box(
        modifier = modifier
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
                .height(130.dp)
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
                val isCameraSelected = selectedScreen == "Camera"
                val isApiKeySelected = selectedScreen == "API Key"

                // API Key 項目 (左側)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onNavigateToApiKey() }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "API Key",
                        tint = if (isApiKeySelected) BlueAccent else GrayText,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "API Key",
                        color = if (isApiKeySelected) BlueAccent else GrayText,
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
                        .clickable { onNavigateToCamera() }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Camera",
                        tint = if (isCameraSelected) BlueAccent else GrayText,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "Camera",
                        color = if (isCameraSelected) BlueAccent else GrayText,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        // 快 shutter button (overlay on the bottom navigation)
        Button(
            onClick = {
                if (hasCameraPermission && isCameraReady && imageCapture != null) {
                    takePhoto(imageCapture!!, context) { result ->
                        // The image has been processed by Gemini, and the result is available in 'result'
                        // The result is already logged in the ImageToTextConverter
                    }
                } else if (!hasCameraPermission) {
                    onNeedPermission()
                } else {
                    Toast.makeText(context, "相機尚未準備完成", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-50).dp)
                .size(72.dp)
                .border(3.dp, LightGrayBorder, CircleShape)
                .zIndex(1f),
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = BlueAccent
            ),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
        ) {
            // No icon, pure solid circular button
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
                            it.surfaceProvider = previewView.surfaceProvider
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
fun takePhoto(
    imageCapture: ImageCapture, 
    context: android.content.Context,
    onImageSaved: (String) -> Unit = {}
) {
    // Get the API key from SharedPreferences (as stored in MainActivity)
    val sharedPref = context.getSharedPreferences("api_key_prefs", android.content.Context.MODE_PRIVATE)
    val apiKey = sharedPref.getString("gemini_api_key", "") ?: ""
    
    if (apiKey.isEmpty()) {
        Toast.makeText(context, "請先設定API金鑰", Toast.LENGTH_LONG).show()
        return
    }
    
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

    val imageToTextConverter = ImageToTextConverter()

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
                val savedUri = output.savedUri
                Toast.makeText(context, "照片已保存至相簿", Toast.LENGTH_LONG).show()

                // Extract the file path from URI for ImageToTextConverter
                savedUri?.let { uri ->
                    if (apiKey.isNotEmpty()) {
                        // Run the image processing on a background thread
                        Thread {
                            try {
                                // Since convertImageToText is a suspend function, we need to call it differently
                                // Create a coroutine scope to handle the suspend function
                                val result = runBlocking {
                                    imageToTextConverter.convertImageToText(getRealPathFromURI(context, uri), apiKey)
                                }
                                // Post the result back to the main thread if needed for UI updates
                                context.mainLooper?.let { looper ->
                                    val mainHandler = android.os.Handler(looper)
                                    mainHandler.post {
                                        // Call the callback with the result
                                        onImageSaved(result)
                                    }
                                } ?: run {
                                    // Fallback if mainLooper is null
                                    onImageSaved(result)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                                context.mainLooper?.let { looper ->
                                    val mainHandler = android.os.Handler(looper)
                                    mainHandler.post {
                                        Toast.makeText(context, "AI處理失敗: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                }
                            }
                        }.start()
                    }
                }
            }
        }
    )
}

// Helper function to get the real path from URI
fun getRealPathFromURI(context: android.content.Context, uri: Uri): String {
    // For Android 10+, we need to copy the file to app's cache directory since MediaStore provides a content URI
    // For Android 10+, copy to cache directory
    val inputStream = context.contentResolver.openInputStream(uri)
    val fileName = "temp_image_${System.currentTimeMillis()}.jpg"
    val outputFile = java.io.File(context.cacheDir, fileName)

    try {
        inputStream?.use { input ->
            java.io.FileOutputStream(outputFile).use { output ->
                input.copyTo(output)
            }
        }
        return outputFile.absolutePath
    } catch (e: Exception) {
        e.printStackTrace()
        throw e
    }
}

private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"