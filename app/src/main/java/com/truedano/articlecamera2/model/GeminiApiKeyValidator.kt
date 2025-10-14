package com.truedano.articlecamera2.model

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.first

class GeminiApiKeyValidator {
    /**
     * Checks if the given Gemini API key is valid by making a network request.
     *
     * @param apiKey The API key to validate.
     * @return `true` if the key is valid, `false` otherwise.
     */
    suspend fun isValid(apiKey: String): Boolean {
        if (apiKey.isBlank()) {
            return false
        }
        return try {
            val generativeModel = GenerativeModel(
                // Let's try the latest model to rule out model availability issues.
                modelName = "gemini-1.5-pro-latest",
                apiKey = apiKey
            )
            // A lightweight streaming call to validate the key and model access.
            generativeModel.generateContentStream("test").first()
            true
        } catch (e: Exception) {
            // The call will throw an exception if the API key is invalid or the model is inaccessible.
            e.printStackTrace()
            false
        }
    }
}
