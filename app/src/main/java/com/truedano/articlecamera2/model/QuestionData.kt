package com.truedano.articlecamera2.model

data class Question(
    val id: Int,
    val questionText: String,
    val options: List<String>, // 選項 A, B, C, D
    val correctAnswer: String, // 正確答案 (A, B, C, D)
    val explanation: String = "" // 解釋 (可選)
)

data class QuestionPaper(
    val grade: Int,
    val questionCount: Int,
    val questions: List<Question>,
    val sourceText: String // 原始文章內容
)