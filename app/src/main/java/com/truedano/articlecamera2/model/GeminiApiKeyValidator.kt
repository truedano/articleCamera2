package com.truedano.articlecamera2.model

import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.flow.first

class GeminiApiKeyValidator(private val context: android.content.Context) {
    /**
     * Checks if the given Gemini API key is valid by making a network request.
     *
     * @param apiKey The API key to validate.
     * @return `true` if the key is valid, `false` otherwise.
     */
    suspend fun isValid(apiKey: String, modelName: String? = null): Boolean {
        if (apiKey.isBlank()) {
            return false
        }
        return try {
            val actualModelName = modelName ?: run {
                val apiKeyManager = ApiKeyManager(context)
                apiKeyManager.getModel()
            }
            val generativeModel = GenerativeModel(
                // Use the provided model or fallback to the one from settings to validate the API key
                modelName = actualModelName,
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
