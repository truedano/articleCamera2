package com.truedano.articlecamera2.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truedano.articlecamera2.ui.theme.BlueAccent
import com.truedano.articlecamera2.ui.theme.DarkCharcoalBackground
import com.truedano.articlecamera2.ui.theme.DarkGrayBackground
import com.truedano.articlecamera2.ui.theme.GrayText
import com.truedano.articlecamera2.ui.theme.LightGrayBorder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestionSettingsScreen(
    articleText: String = "", // 添加文章內容參數
    onBack: () -> Unit,
    onGenerateQuestions: (grade: Int, questionCount: Int) -> Unit,
    articleQuestionViewModel: ArticleQuestionViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    LocalContext.current
    var selectedGrade by remember { mutableIntStateOf(1) }
    var selectedQuestionCount by remember { mutableIntStateOf(5) }

    // 初始化文章內容
    LaunchedEffect(articleText) {
        articleQuestionViewModel.setArticleText(articleText)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("設定題目參數", color = Color.White) },
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
                .padding(16.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 程度選擇標題
            Text(
                text = "選擇程度",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // 程度選擇按鈕
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (grade in 1..6) {
                    GradeButton(
                        grade = grade,
                        isSelected = selectedGrade == grade,
                        onClick = {
                            selectedGrade = grade
                            articleQuestionViewModel.setSelectedGrade(grade)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 題數選擇標題
            Text(
                text = "選擇題數",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )

            // 題數選擇滑桿
            Text(
                text = "$selectedQuestionCount 題",
                color = BlueAccent,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Slider(
                value = selectedQuestionCount.toFloat(),
                onValueChange = {
                    selectedQuestionCount = it.toInt()
                    articleQuestionViewModel.setSelectedQuestionCount(selectedQuestionCount)
                },
                valueRange = 2f..10f,
                steps = 7, // 2到10共9個數字，8個間隔，7個步驟
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = BlueAccent,
                    activeTrackColor = BlueAccent,
                    inactiveTrackColor = LightGrayBorder
                )
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = "2", color = GrayText)
                Text(text = "10", color = GrayText)
            }

            Spacer(modifier = Modifier.weight(1f))

            // 生成題目按鈕
            Button(
                onClick = {
                    // 更新ViewModel中的選擇值
                    articleQuestionViewModel.setSelectedGrade(selectedGrade)
                    articleQuestionViewModel.setSelectedQuestionCount(selectedQuestionCount)
                    onGenerateQuestions(selectedGrade, selectedQuestionCount)
                },
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
                    text = "生成選擇題",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun GradeButton(
    grade: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(48.dp)
            .height(48.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) BlueAccent else DarkCharcoalBackground
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "${grade}年級",
            fontSize = 12.sp,
            color = if (isSelected) Color.White else GrayText,
            textAlign = TextAlign.Center
        )
    }
}