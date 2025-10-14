package com.truedano.articlecamera2.model

import android.content.Context
import androidx.core.content.edit

class ApiKeyManager(context: Context) {
    
    private val sharedPreferences = context.getSharedPreferences("api_key_prefs", Context.MODE_PRIVATE)
    
    fun saveApiKey(apiKey: String) {
        sharedPreferences.edit { putString("gemini_api_key", apiKey) }
    }
    
    fun getApiKey(): String {
        return sharedPreferences.getString("gemini_api_key", "") ?: ""
    }
    
    fun saveModel(model: String) {
        sharedPreferences.edit { putString("gemini_model", model) }
    }
    
    fun getModel(): String {
        return sharedPreferences.getString("gemini_model", "gemini-2.5-flash") ?: "gemini-2.5-flash"
    }
}