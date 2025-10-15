package com.truedano.articlecamera2.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.truedano.articlecamera2.model.QuestionGenerator
import com.truedano.articlecamera2.model.QuestionPaper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ArticleQuestionViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _articleText = MutableStateFlow("")

    private val _selectedGrade = MutableStateFlow(1)
    val selectedGrade: StateFlow<Int> = _selectedGrade.asStateFlow()
    
    private val _selectedQuestionCount = MutableStateFlow(5)
    val selectedQuestionCount: StateFlow<Int> = _selectedQuestionCount.asStateFlow()
    
    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()
    
    private val _questionPaper = MutableStateFlow<QuestionPaper?>(null)
    val questionPaper: StateFlow<QuestionPaper?> = _questionPaper.asStateFlow()
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()
    
    fun setArticleText(text: String) {
        _articleText.value = text
    }
    
    fun setSelectedGrade(grade: Int) {
        _selectedGrade.value = grade
    }
    
    fun setSelectedQuestionCount(count: Int) {
        _selectedQuestionCount.value = count
    }
    
    fun generateQuestions(apiKey: String) {
        if (_articleText.value.isEmpty()) {
            _error.value = "文章內容為空"
            return
        }
        
        viewModelScope.launch {
            _isGenerating.value = true
            _error.value = null
            
            try {
                val questionGenerator = QuestionGenerator(getApplication())
                val questionPaper = questionGenerator.generateQuestions(
                    articleText = _articleText.value,
                    grade = _selectedGrade.value,
                    questionCount = _selectedQuestionCount.value,
                    apiKey = apiKey
                )
                
                if (questionPaper != null) {
                    _questionPaper.value = questionPaper
                } else {
                    _error.value = "問題生成失敗"
                }
            } catch (e: Exception) {
                _error.value = "生成問題時發生錯誤: ${e.message}"
            } finally {
                _isGenerating.value = false
            }
        }
    }

}