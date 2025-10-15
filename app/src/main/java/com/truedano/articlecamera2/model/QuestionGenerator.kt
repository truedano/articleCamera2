package com.truedano.articlecamera2.model

import android.content.Context
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject

class QuestionGenerator(private val context: Context) {
    
    companion object {
        private const val TAG = "QuestionGenerator"
    }
    
    suspend fun generateQuestions(
        articleText: String,
        grade: Int,
        questionCount: Int,
        apiKey: String
    ): QuestionPaper? {
        return withContext(Dispatchers.IO) {
            try {
                // Get the model name from ApiKeyManager
                val apiKeyManager = ApiKeyManager(context)
                val configuredModel = apiKeyManager.getModel()
                
                // Define a list of models to try in order of preference
                val modelsToTry = listOf(configuredModel, "gemini-2.5-flash", "gemini-1.5-pro", "gemini-1.0-pro")
                
                var result: QuestionPaper? = null
                var lastException: Exception? = null
                
                // Try each model in sequence until one succeeds
                for (modelName in modelsToTry) {
                    try {
                        val generativeModel = GenerativeModel(
                            modelName = modelName,
                            apiKey = apiKey
                        )
                        
                        // Create the prompt for generating multiple-choice questions
                        val prompt = createQuestionGenerationPrompt(articleText, grade, questionCount)
                        
                        // Generate content from the prompt
                        val response = generativeModel.generateContent(prompt)
                        val generatedText = response.text ?: ""
                        
                        Log.d(TAG, "Generated text from model $modelName: $generatedText")
                        
                        // Parse the generated text to extract questions
                        result = parseGeneratedQuestions(generatedText, articleText, grade, questionCount)
                        
                        if (result != null) {
                            Log.d(TAG, "Successfully used model: $modelName to generate questions")
                            break // Exit the loop if successful
                        }
                        
                    } catch (e: Exception) {
                        Log.w(TAG, "Model $modelName failed: ${e.message}")
                        lastException = e
                        // Continue to the next model
                    }
                }
                
                if (result != null) {
                    result
                } else {
                    // If all models failed
                    val errorMsg = "All models failed to generate questions. Last error: ${lastException?.message}"
                    Log.e(TAG, errorMsg)
                    null
                }
                
            } catch (e: Exception) {
                Log.e(TAG, "Error during question generation: ${e.message}", e)
                null
            }
        }
    }
    
    private fun createQuestionGenerationPrompt(articleText: String, grade: Int, questionCount: Int): String {
        return """
            你是一個專業的教育工作者，請根據以下文章內容為國小${grade}年級的學生生成${questionCount}道選擇題。
            
            文章內容：
            $articleText
            
            請按照以下JSON格式返回結果：
            {
                "questions": [
                    {
                        "id": 1,
                        "question": "問題內容",
                        "options": [
                            "A. 選項1",
                            "B. 選項2", 
                            "C. 選項3",
                            "D. 選項4"
                        ],
                        "correct_answer": "A",
                        "explanation": "解釋為什麼這個答案是正確的"
                    }
                ]
            }
            
            請確保：
            1. 問題內容適合國小${grade}年級的學生程度
            2. 選項清晰明確，只有一個正確答案
            3. 解釋簡潔易懂
            4. 問題與文章內容密切相關
            5. 所有${questionCount}道題目都要包含在JSON中
        """.trimIndent()
    }
    
    private fun parseGeneratedQuestions(
        generatedText: String,
        sourceText: String,
        grade: Int,
        questionCount: Int
    ): QuestionPaper? {
        return try {
            // 尝试从生成的文本中提取JSON部分
            var jsonStr = generatedText
            val jsonStart = generatedText.indexOf('{')
            val jsonEnd = generatedText.lastIndexOf('}')
            
            if (jsonStart != -1 && jsonEnd != -1 && jsonEnd > jsonStart) {
                jsonStr = generatedText.substring(jsonStart, jsonEnd + 1)
            }
            
            val jsonObject = JSONObject(jsonStr)
            val questionsArray = jsonObject.getJSONArray("questions")
            
            val questions = mutableListOf<Question>()
            
            for (i in 0 until questionsArray.length()) {
                val questionObj = questionsArray.getJSONObject(i)
                
                val id = questionObj.optInt("id", i + 1)
                val questionText = questionObj.optString("question")
                val optionsArray = questionObj.getJSONArray("options")
                val correctAnswer = questionObj.optString("correct_answer")
                val explanation = questionObj.optString("explanation", "")
                
                val options = mutableListOf<String>()
                for (j in 0 until optionsArray.length()) {
                    options.add(optionsArray.getString(j))
                }
                
                questions.add(
                    Question(
                        id = id,
                        questionText = questionText,
                        options = options,
                        correctAnswer = correctAnswer,
                        explanation = explanation
                    )
                )
            }
            
            QuestionPaper(
                grade = grade,
                questionCount = questionCount,
                questions = questions,
                sourceText = sourceText
            )
            
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing generated questions: ${e.message}", e)
            // 尝试另一种解析方法，如果生成的内容包含JSON格式
            tryAlternativeParsing(generatedText, sourceText, grade, questionCount)
        }
    }
    
    private fun tryAlternativeParsing(
        generatedText: String,
        sourceText: String,
        grade: Int,
        questionCount: Int
    ): QuestionPaper? {
        // 如果JSON解析失败，尝试使用正则表达式或其他方法解析
        // 这里实现一个简单的备用解析方法
        Log.d(TAG, "Using alternative parsing method")
        
        // 示例：如果生成的文本包含特定格式，可以在这里解析
        // 由于JSON是最可靠的格式，我们主要依赖JSON解析
        // 如果JSON解析失败，返回null表示解析失败
        return null
    }
}