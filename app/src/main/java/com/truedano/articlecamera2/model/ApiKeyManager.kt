package com.truedano.articlecamera2.model

import android.content.Context
import androidx.core.content.edit

class ApiKeyManager(context: Context) {
    
    private val sharedPreferences = context.getSharedPreferences("api_key_prefs", Context.MODE_PRIVATE)
    
    companion object {
        private const val GEMINI_API_KEY = "gemini_api_key"
        private const val GEMINI_MODEL = "gemini_model"
        private const val DEFAULT_MODEL = "gemini-2.5-flash"
    }
    
    fun saveApiKey(apiKey: String) {
        sharedPreferences.edit { putString(GEMINI_API_KEY, apiKey) }
    }
    
    fun getApiKey(): String {
        return sharedPreferences.getString(GEMINI_API_KEY, "") ?: ""
    }
    
    fun saveModel(model: String) {
        sharedPreferences.edit { putString(GEMINI_MODEL, model) }
    }
    
    fun getModel(): String {
        return sharedPreferences.getString(GEMINI_MODEL, DEFAULT_MODEL) ?: DEFAULT_MODEL
    }
}