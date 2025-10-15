package com.truedano.articlecamera2.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content

class ImageToTextConverter() {

    companion object {
        private const val TAG = "ImageToTextConverter"
        private const val MAX_BITMAP_SIZE = 2048 // Maximum width/height for processing
    }

    /**
     * Converts an image from a URI to text using the Gemini API, without saving to device.
     *
     * @param context The context to access the content resolver
     * @param uri The URI of the image to convert
     * @param apiKey The API key to use for the Gemini API.
     * @return The text extracted from the image.
     */
    suspend fun convertImageToTextDirectly(context: Context, uri: Uri, apiKey: String): String {
        return try {
            // Load and optimize the image from the URI
            val bitmap = loadAndOptimizeBitmapFromUri(context, uri)
            
            if (bitmap == null) {
                Log.e(TAG, "Failed to load bitmap from URI: $uri")
                return "Error: Failed to load image"
            }

            // Get the model name from ApiKeyManager
            val apiKeyManager = ApiKeyManager(context)
            val configuredModel = apiKeyManager.getModel()
            
            // Define a list of models to try in order of preference
            val modelsToTry = listOf(configuredModel, "gemini-2.5-flash", "gemini-1.5-pro", "gemini-1.0-pro")
            
            var result: String? = null
            var lastException: Exception? = null

            // Try each model in sequence until one succeeds
            for (modelName in modelsToTry) {
                try {
                    // For models that support images (like gemini-1.5-flash and gemini-pro-vision)
                    val generativeModel = GenerativeModel(
                        modelName = modelName,
                        apiKey = apiKey
                    )

                    // Create content with image and text prompt
                    val inputContent = content {
                        text("Please extract and describe all text content from this image in detail. If there are articles, please format them clearly.")
                        image(bitmap)
                    }

                    // Generate content from the image
                    val response = generativeModel.generateContent(inputContent)
                    result = response.text ?: "No text generated from the image"

                    Log.d(TAG, "Successfully used model: $modelName")
                    break // Exit the loop if successful

                } catch (e: Exception) {
                    Log.w(TAG, "Model $modelName failed: ${e.message}")
                    lastException = e
                    // Continue to the next model
                }
            }
            
            if (result != null) {
                // Log the output text as requested
                Log.d(TAG, "Generated text from image: $result")
                result
            } else {
                // If all models failed
                val errorMsg = "All models failed. Last error: ${lastException?.message}"
                Log.e(TAG, errorMsg)
                "Error: $errorMsg"
            }

        } catch (e: Exception) {
            Log.e(TAG, "Error during image to text conversion: ${e.message}", e)
            "Error: ${e.message}"
        }
    }

    /**
     * Loads a bitmap from a URI and optimizes it for API processing.
     */
    private fun loadAndOptimizeBitmapFromUri(context: Context, uri: Uri): Bitmap? {
        return try {
            // First decode bounds to check dimensions
            val options = BitmapFactory.Options().apply {
                inJustDecodeBounds = true
            }
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }
            
            // Calculate sample size to reduce image size if needed
            options.inSampleSize = calculateInSampleSize(options)
            options.inJustDecodeBounds = false
            
            // Decode the actual bitmap with the calculated sample size
            context.contentResolver.openInputStream(uri)?.use { inputStream ->
                BitmapFactory.decodeStream(inputStream, null, options)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bitmap from URI: $uri", e)
            null
        }
    }

    /**
     * Calculates the sample size to reduce bitmap dimensions while maintaining aspect ratio.
     */
    private fun calculateInSampleSize(options: BitmapFactory.Options): Int {
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > MAX_BITMAP_SIZE || width > MAX_BITMAP_SIZE) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while (halfHeight / inSampleSize >= MAX_BITMAP_SIZE || halfWidth / inSampleSize >= MAX_BITMAP_SIZE) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }
}