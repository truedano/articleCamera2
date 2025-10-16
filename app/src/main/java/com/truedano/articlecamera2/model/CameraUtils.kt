package com.truedano.articlecamera2.model

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Locale

class CameraUtils {
    companion object {
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

        fun takePhoto(
            imageCapture: ImageCapture,
            context: Context,
            onImageSaved: (String) -> Unit = {},
            onArticleExtracted: (String) -> Unit = {},
            onError: () -> Unit = {}
        ) {
            // Get the API key and model from ApiKeyManager
            val apiKeyManager = ApiKeyManager(context)
            val apiKey = apiKeyManager.getApiKey()
            val model = apiKeyManager.getModel()

            if (apiKey.isEmpty()) {
                Toast.makeText(context, "請先設定API金鑰", Toast.LENGTH_LONG).show()
                return
            }
            
            if (model.isEmpty()) {
                Toast.makeText(context, "請先設定模型", Toast.LENGTH_LONG).show()
                return
            }
            
            // 在新執行緒中驗證API Key的有效性
            val isValid = try {
                val validator = GeminiApiKeyValidator(context)
                // 使用 runBlocking 在同步上下文中執行 suspend 函數
                runBlocking {
                    validator.isValid(apiKey, model)
                }
            } catch (_: Exception) {
                false
            }
            
            if (!isValid) {
                Toast.makeText(context, "API金鑰或模型設定無效，請重新設定", Toast.LENGTH_LONG).show()
                return
            }

            // 檢查是否支援相機功能
            if (androidx.core.content.ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
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
                androidx.core.content.ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exception: ImageCaptureException) {
                        exception.printStackTrace()
                        Toast.makeText(context, "拍照失敗: ${exception.message}", Toast.LENGTH_LONG).show()
                        onError()
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        // For Android 10+, the URI should be provided and image is already saved to MediaStore
                        val savedUri = output.savedUri

                        // Extract the file path from URI for ImageToTextConverter
                        savedUri?.let { uri ->
                            if (apiKey.isNotEmpty()) {
                                // Run the image processing on a background thread
                                Thread {
                                    try {
                                        // Since convertImageToText is a suspend function, we need to call it differently
                                        // Create a coroutine scope to handle the suspend function
                                        val result = runBlocking {
                                            // Directly process the image from URI without saving to cache
                                            imageToTextConverter.convertImageToTextDirectly(context, uri, apiKey)
                                        }
                                        // Post the result back to the main thread if needed for UI updates
                                        context.mainLooper?.let { looper ->
                                            val mainHandler = android.os.Handler(looper)
                                            mainHandler.post {
                                                // Call the callback with the result
                                                onImageSaved(result)
                                                // Also call the new callback for article extraction
                                                onArticleExtracted(result)
                                            }
                                        } ?: run {
                                            // Fallback if mainLooper is null
                                            onImageSaved(result)
                                            onArticleExtracted(result)
                                        }
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                        context.mainLooper?.let { looper ->
                                            val mainHandler = android.os.Handler(looper)
                                            mainHandler.post {
                                                Toast.makeText(context, "AI處理失敗: ${e.message}", Toast.LENGTH_LONG).show()
                                                onError()
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

    }
}