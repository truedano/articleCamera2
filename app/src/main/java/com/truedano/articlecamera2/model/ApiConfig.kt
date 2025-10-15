package com.truedano.articlecamera2.model

/**
 * API 配置常量類
 * 用於管理所有 API 相關的配置參數
 */
object ApiConfig {
    // 定義可用的 Gemini 模型列表
    val GEMINI_MODELS = listOf("gemini-2.5-flash", "gemini-2.5-flash-lite")

    // Google AI Studio 網址
    const val GOOGLE_AI_STUDIO_URL = "https://aistudio.google.com/app/apikey"
    const val GOOGLE_AI_STUDIO_WEBSITE = "Google AI Studio website"
}