package com.truedano.articlecamera2.ui

import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.camera.core.AspectRatio
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.runtime.rememberCoroutineScope
import com.truedano.articlecamera2.model.CameraUtils
import com.truedano.articlecamera2.model.VersionUtils
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
    var showProcessingDialog by remember { mutableStateOf(false) }
    var showCameraMenu by remember { mutableStateOf(false) }
    var isContinuousMode by remember { mutableStateOf(false) }
    var continuousPhotoCount by remember { mutableIntStateOf(0) }
    var capturedImageUris by remember { mutableStateOf<List<android.net.Uri>>(emptyList()) }
    var lastCapturedImageUri by remember { mutableStateOf<android.net.Uri?>(null) } // 添加状态变量存储最近拍摄图片的URI
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Request permission if not granted
    if (!hasCameraPermission) {
        onNeedPermission()
    }
    
    // Launcher for selecting multiple images from gallery
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (uris.isNotEmpty()) {
            scope.launch {
                // Process the selected images with AI
                val apiKeyManager = com.truedano.articlecamera2.model.ApiKeyManager(context)
                val apiKey = apiKeyManager.getApiKey()
                
                if (apiKey.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "請先設定API金鑰", Toast.LENGTH_LONG).show()
                        onNavigateToApiKey()
                    }
                    return@launch
                }
                
                // Process the selected images using ImageToTextConverter
                withContext(Dispatchers.Main) {
                    isProcessing = true
                    showProcessingDialog = true
                }
                
                try {
                    val imageToTextConverter = com.truedano.articlecamera2.model.ImageToTextConverter()
                    
                    // Process all images in a single API call
                    val result = imageToTextConverter.convertMultipleImagesToTextDirectly(context, uris, apiKey)
                    
                    // Update UI on main thread
                    withContext(Dispatchers.Main) {
                        isProcessing = false
                        showProcessingDialog = false
                        onNavigateToQuestionSettings(result)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    withContext(Dispatchers.Main) {
                        isProcessing = false
                        showProcessingDialog = false
                        Toast.makeText(context, "處理圖片失敗: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
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
                        val context = LocalContext.current
                        val versionName = VersionUtils.getVersionWithPrefix(context)
                        Text(
                            text = "Article Camera $versionName",
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
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(DarkGrayBackground)
                ) {
                    CameraPreview(
                        modifier = Modifier
                            .fillMaxSize(),
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
                }
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
        
        // 显示缩略图，确保在底部导航栏之上
        lastCapturedImageUri?.let { capturedUri ->
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 16.dp, bottom = 160.dp) // 增加底部间距以避免与导航栏重叠
                    .zIndex(8f) // 设置较高的zIndex确保在导航栏之上
            ) {
                ThumbnailDisplay(
                    imageUri = capturedUri,
                    modifier = Modifier
                )
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
                .zIndex(5f) // 設定較低的zIndex，確保dialog在上層
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

        // 顯示執行中dialog (全螢幕遮罩)
        if (showProcessingDialog) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x80000000)) // 半透明黑色背景
                    .zIndex(10f), // 確保在最上層
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier
                        .padding(24.dp)
                        .background(DarkCharcoalBackground, RoundedCornerShape(12.dp))
                        .padding(24.dp)
                ) {
                    // 顯示進度指示器
                    CircularProgressIndicator(
                        color = BlueAccent,
                        strokeWidth = 4.dp,
                        modifier = Modifier.size(48.dp)
                    )
                    
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "處理中，請稍候...",
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        
                        Text(
                            text = "正在分析圖片並提取文章內容",
                            color = GrayText,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
        
        // 長按提示文本
        var showLongPressHint by remember { mutableStateOf(true) }
        
        // 顯示 "長按有更多功能" 文本，5秒後自動消失
        if (showLongPressHint) {
            Text(
                text = "長按有更多功能",
                color = Color.White,
                fontSize = 16.sp, // 增大字體
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .offset(y = (-150).dp) // 調整位置在拍照按鈕上方更遠一些
                    .zIndex(7f)
            )
            
            // 使用副作用在5秒後隱藏提示
            androidx.compose.runtime.LaunchedEffect(Unit) {
                delay(5000) // 5秒
                showLongPressHint = false
            }
        }
        
        // 快 shutter button (overlay on the bottom navigation)
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(y = (-50).dp)
                .size(72.dp)
                .zIndex(6f) // 設定較低的zIndex，但仍高於底部導航欄，但低於dialog
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
                // 顯示連續拍照模式的 UI，當處於連續拍照模式時
                if (isContinuousMode) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(3.dp, LightGrayBorder, CircleShape)
                            .background(BlueAccent, CircleShape) // 保持藍色背景以進行拍照
                            .clickable {
                                if (hasCameraPermission && isCameraReady && imageCapture != null && !isProcessing) {
                                    // 檢查API Key和Model是否正確設定
                                    val apiKeyManager = com.truedano.articlecamera2.model.ApiKeyManager(context)
                                    val apiKey = apiKeyManager.getApiKey()
                                    val model = apiKeyManager.getModel()
                                    
                                    if (apiKey.isEmpty()) {
                                        Toast.makeText(context, "請先設定API金鑰", Toast.LENGTH_LONG).show()
                                        onNavigateToApiKey()
                                    } else if (model.isEmpty()) {
                                        Toast.makeText(context, "請先設定模型", Toast.LENGTH_LONG).show()
                                        onNavigateToApiKey()
                                    } else {
                                        // 在連續拍照模式下僅捕獲照片，不進行AI處理
                                        isProcessing = true
                                        CameraUtils.capturePhotoOnly(
                                            imageCapture = imageCapture!!,
                                            context = context,
                                            onImageCaptured = { uri ->
                                                // 儲存圖片 URI 用於後續處理
                                                capturedImageUris = capturedImageUris + uri
                                                // 更新照片計數
                                                continuousPhotoCount++
                                                // 更新最近拍攝的圖片URI以顯示縮圖
                                                lastCapturedImageUri = uri
                                                // Use coroutine scope to update UI after a short delay to ensure UI responsiveness
                                                scope.launch {
                                                    delay(100) // Small delay to allow UI to update
                                                    withContext(Dispatchers.Main) {
                                                        isProcessing = false
                                                    }
                                                }
                                            },
                                            onError = {
                                                scope.launch {
                                                    withContext(Dispatchers.Main) {
                                                        isProcessing = false
                                                    }
                                                }
                                            }
                                        )
                                    }
                                } else if (!hasCameraPermission) {
                                    onNeedPermission()
                                } else if (isProcessing) {
                                    Toast.makeText(context, "正在處理圖片中，請稍候...", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "相機尚未準備完成，請稍後再試", Toast.LENGTH_SHORT).show()
                                }
                            }
                            .semantics { contentDescription = "連續拍照按鈕" }
                    )
                    
                    // 顯示「結束」按鈕（覆蓋在拍照按鈕上方）
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .align(Alignment.TopEnd)
                            .offset(10.dp, (-10).dp)
                            .border(2.dp, LightGrayBorder, CircleShape)
                            .background(Color.Red, CircleShape)
                            .clickable {
                                // 結束連續拍照模式
                                if (capturedImageUris.isNotEmpty()) {
                                    // 如果已拍攝照片，處理這些照片
                                    scope.launch {
                                        withContext(Dispatchers.Main) {
                                            isProcessing = true
                                            showProcessingDialog = true
                                        }
                                        
                                        try {
                                            val apiKeyManager = com.truedano.articlecamera2.model.ApiKeyManager(context)
                                            val apiKey = apiKeyManager.getApiKey()
                                            val imageToTextConverter = com.truedano.articlecamera2.model.ImageToTextConverter()
                                            
                                            // Process all captured images using ImageToTextConverter
                                            val result = imageToTextConverter.convertMultipleImagesToTextDirectly(context, capturedImageUris, apiKey)
                                            
                                            // Update UI on main thread
                                            withContext(Dispatchers.Main) {
                                                isProcessing = false
                                                showProcessingDialog = false
                                                isContinuousMode = false
                                                capturedImageUris = emptyList()
                                                continuousPhotoCount = 0
                                                onNavigateToQuestionSettings(result)
                                            }
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                            withContext(Dispatchers.Main) {
                                                isProcessing = false
                                                showProcessingDialog = false
                                                isContinuousMode = false
                                                capturedImageUris = emptyList()
                                                continuousPhotoCount = 0
                                                Toast.makeText(context, "處理圖片失敗: ${e.message}", Toast.LENGTH_LONG).show()
                                            }
                                        }
                                    }
                                } else {
                                    // 如果沒有拍攝照片，直接退出連續拍照模式
                                    scope.launch {
                                        withContext(Dispatchers.Main) {
                                            isContinuousMode = false
                                            continuousPhotoCount = 0
                                        }
                                    }
                                }
                            }
                    ) {
                        Text(
                            text = "X",
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                    
                    // 顯示拍攝照片數量（在按鈕上方）
                    Text(
                        text = " $continuousPhotoCount ",
                        color = Color.Yellow,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .offset((-10).dp, (-10).dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .border(3.dp, LightGrayBorder, CircleShape)
                            .background(BlueAccent, CircleShape)
                            .combinedClickable(
                                onClick = {
                                    if (hasCameraPermission && isCameraReady && imageCapture != null && !isProcessing) {
                                        // 檢查API Key和Model是否正確設定
                                        val apiKeyManager = com.truedano.articlecamera2.model.ApiKeyManager(context)
                                        val apiKey = apiKeyManager.getApiKey()
                                        val model = apiKeyManager.getModel()
                                        
                                        if (apiKey.isEmpty()) {
                                            Toast.makeText(context, "請先設定API金鑰", Toast.LENGTH_LONG).show()
                                            onNavigateToApiKey()
                                        } else if (model.isEmpty()) {
                                            Toast.makeText(context, "請先設定模型", Toast.LENGTH_LONG).show()
                                            onNavigateToApiKey()
                                        } else {
                                            if (isContinuousMode) {
                                                // 在連續拍照模式下拍照
                                                isProcessing = true
                                                CameraUtils.takePhoto(
                                                    imageCapture = imageCapture!!,
                                                    context = context,
                                                    onImageSaved = { result ->
                                                        // 更新照片計數
                                                        scope.launch {
                                                            delay(100) // Small delay to allow UI to update
                                                            withContext(Dispatchers.Main) {
                                                                continuousPhotoCount++
                                                                isProcessing = false
                                                            }
                                                        }
                                                    },
                                                    onArticleExtracted = { articleText ->
                                                        // 在連續拍照模式下，不立即導航
                                                    },
                                                    onError = {
                                                        scope.launch {
                                                            withContext(Dispatchers.Main) {
                                                                isProcessing = false
                                                            }
                                                        }
                                                    },
                                                    onImageCaptured = { uri ->
                                                        // 儲存圖片 URI 用於後續處理
                                                        capturedImageUris = capturedImageUris + uri
                                                        // 更新最近拍攝的圖片URI以顯示縮圖
                                                        lastCapturedImageUri = uri
                                                    }
                                                )
                                            } else {
                                                // 一般拍照模式
                                                // 檢查API Key和Model是否正確設定
                                                val apiKeyManager = com.truedano.articlecamera2.model.ApiKeyManager(context)
                                                val apiKey = apiKeyManager.getApiKey()
                                                val model = apiKeyManager.getModel()
                                                
                                                if (apiKey.isEmpty()) {
                                                    Toast.makeText(context, "請先設定API金鑰", Toast.LENGTH_LONG).show()
                                                    onNavigateToApiKey()
                                                } else if (model.isEmpty()) {
                                                    Toast.makeText(context, "請先設定模型", Toast.LENGTH_LONG).show()
                                                    onNavigateToApiKey()
                                                } else {
                                                    scope.launch {
                                                        withContext(Dispatchers.Main) {
                                                            isProcessing = true
                                                            showProcessingDialog = true
                                                        }
                                                        
                                                        // Use the CameraUtils.takePhoto function which now handles coroutines internally
                                                        CameraUtils.takePhoto(
                                                            imageCapture = imageCapture!!,
                                                            context = context,
                                                            onImageSaved = { result ->
                                                                // The image has been processed by Gemini, and the result is available in 'result'
                                                                // The result is already logged in the ImageToTextConverter
                                                            },
                                                            onArticleExtracted = { articleText ->
                                                                // 當文章內容提取完成後，導向問題設定頁面
                                                                scope.launch {
                                                                    withContext(Dispatchers.Main) {
                                                                        isProcessing = false
                                                                        showProcessingDialog = false
                                                                        onNavigateToQuestionSettings(articleText)
                                                                    }
                                                                }
                                                            },
                                                            onError = {
                                                                // 如果發生錯誤，也要將處理狀態設為false
                                                                scope.launch {
                                                                    withContext(Dispatchers.Main) {
                                                                        isProcessing = false
                                                                        showProcessingDialog = false
                                                                    }
                                                                }
                                                            },
                                                            onImageCaptured = { uri ->
                                                                // 更新最近拍攝的圖片URI以顯示縮圖
                                                                scope.launch {
                                                                    withContext(Dispatchers.Main) {
                                                                        lastCapturedImageUri = uri
                                                                    }
                                                                }
                                                            }
                                                        )
                                                    }
                                                }
                                            }
                                        }
                                    } else if (!hasCameraPermission) {
                                        onNeedPermission()
                                    } else if (isProcessing) {
                                        Toast.makeText(context, "正在處理圖片中，請稍候...", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(context, "相機尚未準備完成，請稍後再試", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onLongClick = {
                                    showCameraMenu = true
                                }
                            )
                            .semantics { contentDescription = "拍照按鈕" }
                    )
                }
                
                // 拍照功能選單
                DropdownMenu(
                    expanded = showCameraMenu,
                    onDismissRequest = { showCameraMenu = false },
                    modifier = Modifier
                        .background(DarkCharcoalBackground)
                ) {
                    DropdownMenuItem(
                        text = { Text("選擇圖片", color = Color.White) },
                        onClick = {
                            showCameraMenu = false
                            // Launch the image picker (supports multiple images)
                            imagePickerLauncher.launch("image/*")
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("連續拍照", color = Color.White) },
                        onClick = {
                            showCameraMenu = false
                            // Enter continuous shooting mode
                            isContinuousMode = true
                            continuousPhotoCount = 0
                            capturedImageUris = emptyList()
                        }
                    )
                }
            }
        }
    }
}

// 移除多餘的函數

/**
 * 顯示圖片縮圖的 Composable
 */
@Composable
fun ThumbnailDisplay(
    imageUri: android.net.Uri,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var bitmap by remember { mutableStateOf<android.graphics.Bitmap?>(null) }
    val scope = rememberCoroutineScope()
    
    // 使用 LaunchedEffect 來載入圖片，使用 imageUri 作為 key
    LaunchedEffect(imageUri) {
        bitmap = null // 重置圖片
        if (imageUri != android.net.Uri.EMPTY) {
            scope.launch {
                // 在IO线程中加载图像
                withContext(Dispatchers.IO) {
                    try {
                        val inputStream = context.contentResolver.openInputStream(imageUri)
                        if (inputStream != null) {
                            // 使用inSampleSize创建缩略图以提高性能
                            val options = android.graphics.BitmapFactory.Options().apply {
                                inJustDecodeBounds = true
                                // 先获取图片尺寸
                                android.graphics.BitmapFactory.decodeStream(inputStream, null, this)
                                
                                // 计算合适的inSampleSize
                                inSampleSize = calculateInSampleSize(this, 200, 200) // 目标尺寸200x200
                                inJustDecodeBounds = false
                            }
                            
                            // 重新打开输入流以再次读取图片
                            val inputStreamForDecode = context.contentResolver.openInputStream(imageUri)
                            if (inputStreamForDecode != null) {
                                val loadedBitmap = android.graphics.BitmapFactory.decodeStream(inputStreamForDecode, null, options)
                                if (loadedBitmap != null) {
                                    // 切换回主线程更新状态
                                    withContext(Dispatchers.Main) {
                                        bitmap = loadedBitmap
                                    }
                                }
                                inputStreamForDecode.close()
                            }
                            inputStream.close()
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Log.e("ThumbnailDisplay", "Error loading thumbnail: ${e.message}")
                    }
                }
            }
        }
    }
    
    bitmap?.let { thumbnailBitmap ->
        androidx.compose.foundation.Image(
            bitmap = thumbnailBitmap.asImageBitmap(),
            contentDescription = "最近拍攝的照片縮圖",
            modifier = modifier
                .size(80.dp)
                .border(2.dp, BlueAccent, RoundedCornerShape(8.dp))
                .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
        )
    }
}

/**
 * 计算适当的inSampleSize值以优化内存使用
 */
fun calculateInSampleSize(options: android.graphics.BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {
        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        // 计算最大的inSampleSize值，该值使最终的缩放尺寸大于等于目标尺寸
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}

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
                // 使用 FILL_CENTER 確保預覽填滿整個視圖，同時保持比例
                scaleType = PreviewView.ScaleType.FIT_CENTER
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
                
                // 設定圖片捕獲用例，優先使用 4:3 長寬比 (常用於拍照)
                val imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY)
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .setTargetRotation(view.display.rotation)
                    .build()
                
                // 設定預覽用例，使用與圖片捕獲相同的長寬比
                val preview = Preview.Builder()
                    .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                    .build()
                    .also {
                        it.surfaceProvider = view.surfaceProvider
                    }
                
                onImageCaptureReady(imageCapture)
                
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
                
                try {
                    // Bind the camera to the lifecycle with same aspect ratio for both use cases
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

