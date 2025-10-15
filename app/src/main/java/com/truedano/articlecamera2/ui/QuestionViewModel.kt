package com.truedano.articlecamera2.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.truedano.articlecamera2.model.QuestionPaper
import com.truedano.articlecamera2.model.Question
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuestionViewModel(application: Application) : AndroidViewModel(application) {
    
    private val _questionPaper = MutableStateFlow<QuestionPaper?>(null)
    val questionPaper: StateFlow<QuestionPaper?> = _questionPaper.asStateFlow()
    
    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()
    
    private val _userAnswers = MutableStateFlow<List<String>>(emptyList())
    val userAnswers: StateFlow<List<String>> = _userAnswers.asStateFlow()
    
    private val _isAnswering = MutableStateFlow(false)
    val isAnswering: StateFlow<Boolean> = _isAnswering.asStateFlow()
    
    private val _isGrading = MutableStateFlow(false)
    val isGrading: StateFlow<Boolean> = _isGrading.asStateFlow()
    
    private val _gradeResult = MutableStateFlow<GradeResult?>(null)
    val gradeResult: StateFlow<GradeResult?> = _gradeResult.asStateFlow()
    
    fun setQuestionPaper(questionPaper: QuestionPaper) {
        _questionPaper.value = questionPaper
        _currentQuestionIndex.value = 0
        _userAnswers.value = List(questionPaper.questions.size) { "" }
        _isAnswering.value = true
        _isGrading.value = false
        _gradeResult.value = null
    }
    
    fun selectAnswer(option: String) {
        if (_isAnswering.value) {
            val currentAnswers = _userAnswers.value.toMutableList()
            val currentIndex = _currentQuestionIndex.value
            if (currentIndex < currentAnswers.size) {
                currentAnswers[currentIndex] = option
                _userAnswers.value = currentAnswers
            }
        }
    }
    
    fun nextQuestion() {
        if (_isAnswering.value) {
            val currentIndex = _currentQuestionIndex.value
            val totalQuestions = _questionPaper.value?.questions?.size ?: 0
            if (currentIndex < totalQuestions - 1) {
                _currentQuestionIndex.value = currentIndex + 1
            }
        }
    }
    
    fun previousQuestion() {
        if (_isAnswering.value) {
            val currentIndex = _currentQuestionIndex.value
            if (currentIndex > 0) {
                _currentQuestionIndex.value = currentIndex - 1
            }
        }
    }
    
    fun submitAnswers() {
        viewModelScope.launch {
            _isGrading.value = true
            val paper = _questionPaper.value
            val answers = _userAnswers.value
            
            if (paper != null) {
                val correctCount = calculateCorrectAnswers(paper.questions, answers)
                val total = paper.questions.size
                val score = (correctCount * 100) / total
                
                _gradeResult.value = GradeResult(
                    correctCount = correctCount,
                    totalCount = total,
                    score = score,
                    details = createGradeDetails(paper.questions, answers)
                )
                
                _isAnswering.value = false
                _isGrading.value = false
            }
        }
    }
    
    private fun calculateCorrectAnswers(questions: List<Question>, answers: List<String>): Int {
        return questions.zip(answers).count { (question, answer) ->
            question.correctAnswer == answer
        }
    }
    
    private fun createGradeDetails(questions: List<Question>, answers: List<String>): List<QuestionGradeDetail> {
        return questions.zip(answers).map { (question, answer) ->
            QuestionGradeDetail(
                questionId = question.id,
                questionText = question.questionText,
                userAnswer = answer,
                correctAnswer = question.correctAnswer,
                isCorrect = question.correctAnswer == answer,
                explanation = question.explanation
            )
        }
    }
    
    fun reset() {
        _questionPaper.value = null
        _currentQuestionIndex.value = 0
        _userAnswers.value = emptyList()
        _isAnswering.value = false
        _isGrading.value = false
        _gradeResult.value = null
    }
}

data class GradeResult(
    val correctCount: Int,
    val totalCount: Int,
    val score: Int,
    val details: List<QuestionGradeDetail>
)

data class QuestionGradeDetail(
    val questionId: Int,
    val questionText: String,
    val userAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean,
    val explanation: String
)