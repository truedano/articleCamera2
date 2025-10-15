package com.truedano.articlecamera2.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.truedano.articlecamera2.ui.theme.BlueAccent
import com.truedano.articlecamera2.ui.theme.DarkCharcoalBackground
import com.truedano.articlecamera2.ui.theme.DarkGrayBackground
import com.truedano.articlecamera2.ui.theme.GrayText
import com.truedano.articlecamera2.ui.theme.LightGrayBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionScreen(
    onBack: () -> Unit,
    onGradeResult: () -> Unit, // 添加評分結果導航回調
    questionViewModel: QuestionViewModel = viewModel()
) {
    val questionPaper by questionViewModel.questionPaper.collectAsState()
    val currentQuestionIndex by questionViewModel.currentQuestionIndex.collectAsState()
    val userAnswers by questionViewModel.userAnswers.collectAsState()
    val isAnswering by questionViewModel.isAnswering.collectAsState()
    val gradeResult by questionViewModel.gradeResult.collectAsState()
    
    // 當評分完成時自動導航到結果頁面
    if (gradeResult != null && !isAnswering) {
        LaunchedEffect(gradeResult) {
            onGradeResult() // 導航到評分結果頁面
        }
    }
    
    if (!isAnswering || questionPaper == null) {
        // 如果不在答題狀態或沒有題目，可以顯示其他內容或返回
        Text(text = "沒有題目可答", color = Color.Red)
        return
    }
    
    val currentQuestion = questionPaper!!.questions[currentQuestionIndex]
    val currentAnswer = if (userAnswers.size > currentQuestionIndex) userAnswers[currentQuestionIndex] else ""
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("選擇題測驗", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkCharcoalBackground
                )
            )
        },
        containerColor = DarkGrayBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 進度條
            val progress = (currentQuestionIndex + 1).toFloat() / questionPaper!!.questions.size
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = BlueAccent,
                trackColor = LightGrayBorder
            )
            
            // 題號和進度
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "第 ${currentQuestionIndex + 1} 題",
                    color = Color.White,
                    fontSize = 16.sp
                )
                Text(
                    text = "${currentQuestionIndex + 1} / ${questionPaper!!.questions.size}",
                    color = GrayText,
                    fontSize = 16.sp
                )
            }
            
            // 問題內容
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = DarkCharcoalBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = currentQuestion.questionText,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                
                // 選項
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    currentQuestion.options.forEachIndexed { index, option ->
                        val optionLetter = ('A' + index).toString()
                        val isSelected = currentAnswer == optionLetter
                        
                        OptionButton(
                            option = option,
                            optionLetter = optionLetter,
                            isSelected = isSelected,
                            onClick = {
                                questionViewModel.selectAnswer(optionLetter)
                            }
                        )
                    }
                }
            }
            
            // 底部按鈕
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = {
                        if (currentQuestionIndex > 0) {
                            questionViewModel.previousQuestion()
                        }
                    },
                    enabled = currentQuestionIndex > 0,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkCharcoalBackground
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(text = "上一題", color = Color.White)
                }
                
                if (currentQuestionIndex == questionPaper!!.questions.size - 1) {
                    // 最後一題顯示提交按鈕
                    Button(
                        onClick = {
                            questionViewModel.submitAnswers()
                            // 提交答案後導航到評分結果頁面
                            // 使用LaunchedEffect來確保評分完成後再導航
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BlueAccent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "提交答案", color = Color.White)
                    }
                } else {
                    // 其他題目顯示下一題按鈕
                    Button(
                        onClick = {
                            questionViewModel.nextQuestion()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BlueAccent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = "下一題", color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
fun OptionButton(
    option: String,
    optionLetter: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) BlueAccent else LightGrayBorder,
                shape = RoundedCornerShape(8.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) BlueAccent.copy(alpha = 0.2f) else DarkCharcoalBackground
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(28.dp)
                    .height(28.dp)
                    .background(
                        color = if (isSelected) BlueAccent else Color.Transparent,
                        shape = RoundedCornerShape(6.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = if (isSelected) Color.White else LightGrayBorder,
                        shape = RoundedCornerShape(6.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = optionLetter,
                    color = if (isSelected) Color.White else GrayText,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Text(
                text = option.substringAfter("$optionLetter. ").trim(),
                color = Color.White,
                fontSize = 16.sp
            )
        }
    }
}
