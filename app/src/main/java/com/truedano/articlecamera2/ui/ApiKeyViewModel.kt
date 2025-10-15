package com.truedano.articlecamera2.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.truedano.articlecamera2.model.ApiKeyManager
import com.truedano.articlecamera2.model.GeminiApiKeyValidator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ApiKeyViewModel(application: Application) : AndroidViewModel(application) {

    private val apiKeyManager = ApiKeyManager(application)
    private val validator = GeminiApiKeyValidator(application)
    
    // 添加默認模型常量
    companion object {
        private const val DEFAULT_MODEL = "gemini-2.5-flash"
    }

    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _selectedModel = MutableStateFlow("")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _validationState = MutableStateFlow<ValidationState>(ValidationState.Idle)
    val validationState: StateFlow<ValidationState> = _validationState.asStateFlow()

    init {
        // Load the API key and model from SharedPreferences when the ViewModel is created
        loadApiKeyAndModel()
    }

    private fun loadApiKeyAndModel() {
        _apiKey.value = apiKeyManager.getApiKey()
        _selectedModel.value = apiKeyManager.getModel()
    }

    fun onApiKeyChange(newApiKey: String) {
        _apiKey.value = newApiKey
        // Reset validation state when the key changes
        _validationState.value = ValidationState.Idle
    }

    fun onModelChange(newModel: String) {
        _selectedModel.value = newModel
    }

    fun validateApiKey() {
        // Validate input before starting validation
        if (_apiKey.value.isBlank()) {
            _validationState.value = ValidationState.Failure("API Key cannot be empty")
            return
        }
        
        val modelValue = _selectedModel.value.ifEmpty { DEFAULT_MODEL }
        _selectedModel.value = modelValue // Ensure we always have a valid model selected

        viewModelScope.launch {
            _validationState.value = ValidationState.Loading
            try {
                val isValid = validator.isValid(_apiKey.value, modelValue)
                _validationState.value = if (isValid) {
                    ValidationState.Success
                } else {
                    ValidationState.Failure("Invalid API key or model")
                }
            } catch (e: Exception) {
                _validationState.value = ValidationState.Failure("Validation failed: ${e.message ?: "Unknown error"}")
            }
        }
    }

    fun saveApiKey() {
        try {
            apiKeyManager.saveApiKey(_apiKey.value)
            apiKeyManager.saveModel(_selectedModel.value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getApiKey(): String {
        return apiKeyManager.getApiKey()
    }

}

sealed interface ValidationState {
    object Idle : ValidationState
    object Loading : ValidationState
    object Success : ValidationState
    data class Failure(val message: String) : ValidationState
}
