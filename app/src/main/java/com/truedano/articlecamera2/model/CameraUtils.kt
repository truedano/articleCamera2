package com.truedano.articlecamera2.model

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileOutputStream
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
            // Get the API key from ApiKeyManager
            val apiKeyManager = ApiKeyManager(context)
            val apiKey = apiKeyManager.getApiKey()

            if (apiKey.isEmpty()) {
                Toast.makeText(context, "請先設定API金鑰", Toast.LENGTH_LONG).show()
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

            val imageToTextConverter = ImageToTextConverter(context)

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

        // Helper function to get the real path from URI
        fun getRealPathFromURI(context: Context, uri: Uri): String {
            // For Android 10+, we need to copy the file to app's cache directory since MediaStore provides a content URI
            // For Android 10+, copy to cache directory
            val inputStream = context.contentResolver.openInputStream(uri)
            val fileName = "temp_image_${System.currentTimeMillis()}.jpg"
            val outputFile = File(context.cacheDir, fileName)

            try {
                inputStream?.use { input ->
                    FileOutputStream(outputFile).use { output ->
                        input.copyTo(output)
                    }
                }
                return outputFile.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                throw e
            }
        }
    }
}