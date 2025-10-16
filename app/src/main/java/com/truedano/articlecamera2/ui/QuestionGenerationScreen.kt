package com.truedano.articlecamera2.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.truedano.articlecamera2.ui.theme.BlueAccent
import com.truedano.articlecamera2.ui.theme.DarkCharcoalBackground
import com.truedano.articlecamera2.ui.theme.DarkGrayBackground
import com.truedano.articlecamera2.ui.theme.GrayText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionGenerationScreen(
    onBack: () -> Unit,
    onQuestionsGenerated: () -> Unit, // 修改為無參數
    articleQuestionViewModel: ArticleQuestionViewModel = viewModel(),
    questionViewModel: QuestionViewModel = viewModel(),
    apiKeyViewModel: ApiKeyViewModel = viewModel()
) {
    val grade by articleQuestionViewModel.selectedGrade.collectAsState()
    val questionCount by articleQuestionViewModel.selectedQuestionCount.collectAsState()
    val isGenerating by articleQuestionViewModel.isGenerating.collectAsState()
    val questionPaper by articleQuestionViewModel.questionPaper.collectAsState()
    val error by articleQuestionViewModel.error.collectAsState()
    
    // 當問題生成完成時，將其設置到QuestionViewModel並導航
    LaunchedEffect(questionPaper) {
        if (questionPaper != null) {
            // 重置QuestionViewModel以確保使用新的問題卷
            questionViewModel.reset()
            questionViewModel.setQuestionPaper(questionPaper!!)
            onQuestionsGenerated()
        }
    }
    
    // 開始生成問題
    LaunchedEffect(Unit) {
        val apiKey = apiKeyViewModel.getApiKey()
        if (apiKey.isNotEmpty()) {
            articleQuestionViewModel.generateQuestions(apiKey)
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("生成選擇題", color = Color.White) },
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
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                isGenerating -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = BlueAccent,
                            strokeWidth = 4.dp
                        )
                        Text(
                            text = "正在生成選擇題...",
                            color = Color.White,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "年級: $grade | 題數: $questionCount",
                            color = GrayText,
                            fontSize = 14.sp
                        )
                    }
                }
                error != null -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "生成失敗",
                            color = Color.Red,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = error ?: "",
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center
                        )
                        Button(
                            onClick = onBack,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = BlueAccent
                            )
                        ) {
                            Text(
                                text = "返回",
                                color = Color.White
                            )
                        }
                    }
                }
                questionPaper != null -> {
                    Text(
                        text = "問題生成完成",
                        color = Color.White,
                        fontSize = 20.sp
                    )
                }
                else -> {
                    Text(
                        text = "正在準備生成問題...",
                        color = Color.White,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
 }