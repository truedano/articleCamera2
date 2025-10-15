package com.truedano.articlecamera2.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
fun GradeResultScreen(
    onBack: () -> Unit,
    onRestart: () -> Unit,
    questionViewModel: QuestionViewModel = viewModel()
) {
    val gradeResult by questionViewModel.gradeResult.collectAsState()
    
    if (gradeResult == null) {
        // 如果沒有評分結果，可以顯示錯誤或返回
        return
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("測驗結果", color = Color.White) },
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 總體結果
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = DarkCharcoalBackground
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 分數圓環或進度條
                    Text(
                        text = "${gradeResult!!.score}%",
                        color = BlueAccent,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "總得分",
                        color = GrayText,
                        fontSize = 16.sp
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 進度條
                    val progress = gradeResult!!.correctCount.toFloat() / gradeResult!!.totalCount
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(8.dp),
                        color = BlueAccent,
                        trackColor = LightGrayBorder,
                        strokeCap = ProgressIndicatorDefaults.LinearStrokeCap,
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Text(
                        text = "${gradeResult!!.correctCount}/${gradeResult!!.totalCount} 題答對",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
            
            // 各題詳細結果
            Text(
                text = "題目詳情",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                gradeResult!!.details.forEach { detail ->
                    QuestionDetailCard(detail = detail)
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 重新測驗按鈕
            Button(
                onClick = onRestart,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueAccent
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "重新測驗",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun QuestionDetailCard(detail: QuestionGradeDetail) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (detail.isCorrect) 
                BlueAccent.copy(alpha = 0.1f) 
            else 
                Color.Red.copy(alpha = 0.1f)
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 問題
            Text(
                text = "第 ${detail.questionId} 題",
                color = if (detail.isCorrect) BlueAccent else Color.Red,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = detail.questionText,
                color = Color.White,
                fontSize = 16.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 使用者答案和正確答案
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "你的答案",
                        color = GrayText,
                        fontSize = 14.sp
                    )
                    Text(
                        text = detail.userAnswer,
                        color = if (detail.isCorrect) BlueAccent else Color.Red,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
                
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "正確答案",
                        color = GrayText,
                        fontSize = 14.sp
                    )
                    Text(
                        text = detail.correctAnswer,
                        color = Color.White,
                        fontWeight = FontWeight.Medium,
                        fontSize = 16.sp
                    )
                }
            }
            
            if (detail.explanation.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                
                Text(
                    text = "解析：",
                    color = GrayText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = detail.explanation,
                    color = Color.White,
                    fontSize = 14.sp
                )
            }
        }
    }
}