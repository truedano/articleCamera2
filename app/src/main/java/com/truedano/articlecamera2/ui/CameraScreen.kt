package com.truedano.articlecamera2.ui

import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.truedano.articlecamera2.model.CameraUtils
import com.truedano.articlecamera2.ui.theme.BlueAccent
import com.truedano.articlecamera2.ui.theme.DarkCharcoalBackground
import com.truedano.articlecamera2.ui.theme.DarkGrayBackground
import com.truedano.articlecamera2.ui.theme.GrayText
import com.truedano.articlecamera2.ui.theme.LightGrayBorder
import java.util.concurrent.ExecutionException

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    onNeedPermission: () -> Unit = {},
    hasCameraPermission: Boolean = false,
    onNavigateToApiKey: () -> Unit,
    onNavigateToCamera: () -> Unit,
    onNavigateToQuestionSettings: (String) -> Unit, // 更新導航到問題設定的回調，接收文章內容
    selectedScreen: String
) {
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    var isCameraReady by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    val context = LocalContext.current

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
                windowInsets = WindowInsets(0, 0, 0)
            )
            
            // 相機預覽區域
            if (hasCameraPermission) {
                CameraPreview(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(DarkGrayBackground),
                    onImageCaptureReady = { capture ->
                        imageCapture = capture
                        isCameraReady = true
                    },
                    onError = { error ->
                        // Handle camera error
                        isCameraReady = false
                        Toast.makeText(context, error, Toast.LENGTH_LONG).show()
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
                        contentDescription = "API 金鑰設定",
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
                
                // 中間空白區域，為快 shutter 按鈕預留空間
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
                        contentDescription = "相機功能",
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
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-50).dp)
                .size(72.dp)
                .zIndex(1f)
        ) {
            if (isProcessing) {
                // 顯示處理中的進度指示器
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color(0x80000000), CircleShape), // 半透明黑色背景
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = Color.White,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(32.dp)
                    )
                }
            } else {
                Button(
                    onClick = {
                        if (hasCameraPermission && isCameraReady && imageCapture != null && !isProcessing) {
                            // 檢查API Key和Model是否正確設定
                            val apiKeyManager = com.truedano.articlecamera2.model.ApiKeyManager(context)
                            val apiKey = apiKeyManager.getApiKey()
                            val model = apiKeyManager.getModel()
                            
                            if (apiKey.isEmpty()) {
                                Toast.makeText(context, "請先設定API金鑰", Toast.LENGTH_LONG).show()
                                onNavigateToApiKey()
                                return@Button
                            }
                            
                            if (model.isEmpty()) {
                                Toast.makeText(context, "請先設定模型", Toast.LENGTH_LONG).show()
                                onNavigateToApiKey()
                                return@Button
                            }
                            
                            isProcessing = true
                            CameraUtils.takePhoto(
                                imageCapture = imageCapture!!,
                                context = context,
                                onImageSaved = { result ->
                                    // The image has been processed by Gemini, and the result is available in 'result'
                                    // The result is already logged in the ImageToTextConverter
                                },
                                onArticleExtracted = { articleText ->
                                    // 當文章內容提取完成後，導向問題設定頁面
                                    isProcessing = false
                                    onNavigateToQuestionSettings(articleText)
                                },
                                onError = {
                                    // 如果發生錯誤，也要將處理狀態設為false
                                    isProcessing = false
                                }
                            )
                        } else if (!hasCameraPermission) {
                            onNeedPermission()
                        } else if (isProcessing) {
                            Toast.makeText(context, "正在處理圖片中，請稍候...", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "相機尚未準備完成，請稍後再試", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .border(3.dp, LightGrayBorder, CircleShape)
                        .semantics { contentDescription = "拍照按鈕" },
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
    }
}

// 移除多餘的函數

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,
    onImageCaptureReady: (ImageCapture) -> Unit,
    onError: (String) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var previewView by remember { mutableStateOf<PreviewView?>(null) }
    var cameraProvider by remember { mutableStateOf<ProcessCameraProvider?>(null) }
    
    // Clean up camera when the composable is disposed
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_DESTROY) {
                cameraProvider?.unbindAll()
            }
        }
        
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            cameraProvider?.unbindAll()
        }
    }
    
    AndroidView(
        factory = { ctx ->
            val view = PreviewView(ctx).apply {
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }
            previewView = view
            view
        },
        modifier = modifier
    )
    
    // Initialize camera after the view is created
    previewView?.let { view ->
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        
        cameraProviderFuture.addListener({
            try {
                val provider = cameraProviderFuture.get()
                cameraProvider = provider
                
                // Unbind all previous camera use cases if any
                provider.unbindAll()
                
                val preview = Preview.Builder()
                    .build()
                    .also {
                        it.surfaceProvider = view.surfaceProvider
                    }
                
                // 設置圖片捕獲用例
                val imageCapture = ImageCapture.Builder()
                    .setTargetRotation(view.display.rotation)
                    .build()
                
                onImageCaptureReady(imageCapture)
                
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                
                try {
                    // Bind the camera to the lifecycle
                    provider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageCapture
                    )
                } catch (exc: Exception) {
                    // Handle binding failure
                    Log.e("CameraPreview", "相機綁定失敗", exc)
                    val errorMsg = "相機綁定失敗: ${exc.message}"
                    onError(errorMsg)
                }
            } catch (e: ExecutionException) {
                // Handle camera provider retrieval error
                Log.e("CameraPreview", "相機提供者錯誤", e)
                val errorMsg = "相機提供者錯誤: ${e.message}"
                onError(errorMsg)
            } catch (e: InterruptedException) {
                // Handle interruption
                Log.e("CameraPreview", "相機初始化被中斷", e)
                val errorMsg = "相機初始化被中斷"
                onError(errorMsg)
            }
        }, ContextCompat.getMainExecutor(context))
    }
}

