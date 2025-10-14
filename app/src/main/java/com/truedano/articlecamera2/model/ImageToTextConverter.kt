package com.truedano.articlecamera2.model

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import java.io.FileInputStream

class ImageToTextConverter {

    companion object{
        private const val TAG = "ImageToTextConverter"
    }

    /**
     * Converts an image to text using the Gemini API.
     *
     * @param imagePath The path to the image file to convert.
     * @param apiKey The API key to use for the Gemini API.
     * @return The text extracted from the image.
     */
    suspend fun convertImageToText(imagePath: String, apiKey: String): String {
        return convertImageToTextInternal(imagePath, apiKey)
    }

    private suspend fun convertImageToTextInternal(imagePath: String, apiKey: String): String {
        return try {
            // Load the image from the file path
            val bitmap = loadBitmapFromFile(imagePath)
            
            if (bitmap == null) {
                Log.e(TAG, "Failed to load bitmap from path: $imagePath")
                return "Error: Failed to load image"
            }

            // Try model names that are currently available for image processing
            val modelName = "gemini-2.5-flash"
            
            var result: String? = null
            var lastException: Exception? = null

            try {
                // For models that support images (like gemini-1.5-flash and gemini-pro-vision)
                val generativeModel = GenerativeModel(
                    modelName = modelName,
                    apiKey = apiKey
                )

                // Create content with image and text prompt
                val inputContent = content {
                    text("Describe the content of this image in detail.")
                    image(bitmap)
                }

                // Generate content from the image
                val response = generativeModel.generateContent(inputContent)
                result = response.text ?: "No text generated from the image"

                Log.d(TAG, "Successfully used model: $modelName")

            } catch (e: Exception) {
                Log.w(TAG, "Model $modelName failed: ${e.message}")
                lastException = e
                // Continue to the next model
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
     * Loads a bitmap from a file path.
     */
    private fun loadBitmapFromFile(filePath: String): Bitmap? {
        return try {
            FileInputStream(filePath).use { inputStream ->
                BitmapFactory.decodeStream(inputStream)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading bitmap from file: $filePath", e)
            null
        }
    }
}