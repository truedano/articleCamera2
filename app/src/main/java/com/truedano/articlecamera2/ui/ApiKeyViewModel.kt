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

    private val validator = GeminiApiKeyValidator()
    private val apiKeyManager = ApiKeyManager(application)

    private val _apiKey = MutableStateFlow("")
    val apiKey: StateFlow<String> = _apiKey.asStateFlow()

    private val _validationState = MutableStateFlow<ValidationState>(ValidationState.Idle)
    val validationState: StateFlow<ValidationState> = _validationState.asStateFlow()

    init {
        // Load the API key from SharedPreferences when the ViewModel is created
        _apiKey.value = apiKeyManager.getApiKey()
    }

    fun onApiKeyChange(newApiKey: String) {
        _apiKey.value = newApiKey
        // Reset validation state when the key changes
        _validationState.value = ValidationState.Idle
    }

    fun validateApiKey() {
        viewModelScope.launch {
            _validationState.value = ValidationState.Loading
            val isValid = validator.isValid(_apiKey.value)
            _validationState.value = if (isValid) {
                ValidationState.Success
            } else {
                ValidationState.Failure
            }
        }
    }

    fun saveApiKey() {
        apiKeyManager.saveApiKey(_apiKey.value)
    }

    fun getApiKey(): String {
        return apiKeyManager.getApiKey()
    }
}

sealed interface ValidationState {
    object Idle : ValidationState
    object Loading : ValidationState
    object Success : ValidationState
    object Failure : ValidationState
}
