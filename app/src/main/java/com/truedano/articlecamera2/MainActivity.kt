package com.truedano.articlecamera2

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.truedano.articlecamera2.ui.ApiKeyScreen
import com.truedano.articlecamera2.ui.ApiKeyViewModel
import com.truedano.articlecamera2.ui.CameraScreen
import com.truedano.articlecamera2.ui.QuestionSettingsScreen
import com.truedano.articlecamera2.ui.QuestionGenerationScreen
import com.truedano.articlecamera2.ui.QuestionScreen
import com.truedano.articlecamera2.ui.GradeResultScreen
import com.truedano.articlecamera2.ui.theme.ArticleCamera2Theme

class MainActivity : ComponentActivity() {
    private val apiKeyViewModel: ApiKeyViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        WindowCompat.setDecorFitsSystemWindows(window, false)
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        val requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            // Callback for permission result - handled by the composable
        }

        setContent {
            ArticleCamera2Theme {
                var selectedScreen by remember { mutableStateOf("Camera") }
                var capturedArticleText by remember { mutableStateOf("") } // 添加狀態變量來存儲拍攝獲得的文章內容
                
                when (selectedScreen) {
                    "Camera" -> CameraScreen(
                        onNeedPermission = {
                            if (ContextCompat.checkSelfPermission(
                                    this,
                                    Manifest.permission.CAMERA
                                ) != PackageManager.PERMISSION_GRANTED) {
                                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                            }
                        },
                        hasCameraPermission = ContextCompat.checkSelfPermission(
                            this,
                            Manifest.permission.CAMERA
                        ) == PackageManager.PERMISSION_GRANTED,
                        onNavigateToApiKey = { selectedScreen = "API Key" },
                        onNavigateToCamera = { selectedScreen = "Camera" },
                        onNavigateToQuestionSettings = { articleText ->
                            capturedArticleText = articleText // 保存拍攝獲得的文章內容
                            selectedScreen = "Question Settings" // 新增導航到問題設定
                        },
                        selectedScreen = selectedScreen
                    )
                    "API Key" -> {
                        ApiKeyScreen(
                            onSave = {
                                selectedScreen = "Camera"
                            },
                            onBack = { selectedScreen = "Camera" },
                            onNavigateToApiKey = { selectedScreen = "API Key" },
                            onNavigateToCamera = { selectedScreen = "Camera" },
                            selectedScreen = selectedScreen,
                            apiKeyViewModel = apiKeyViewModel
                        )
                    }
                    "Question Settings" -> {
                        // 問題設定屏幕，需要傳遞提取的文章內容和導航回調
                        QuestionSettingsScreen(
                            articleText = capturedArticleText, // 使用從拍照獲得的實際文章內容
                            onBack = { selectedScreen = "Camera" },
                            onGenerateQuestions = { grade: Int, questionCount: Int ->
                                selectedScreen = "Question Generation" // 導航到問題生成頁面
                            }
                        )
                    }
                    "Question Generation" -> {
                        // 問題生成屏幕
                        QuestionGenerationScreen(
                            onBack = { selectedScreen = "Question Settings" },
                            onQuestionsGenerated = {
                                selectedScreen = "Question Screen" // 生成成功後導航到答題頁面
                            }
                        )
                    }
                    "Question Screen" -> {
                        // 答題屏幕
                        QuestionScreen(
                            onBack = { selectedScreen = "Camera" }, // 應該導航回結果頁面或相機頁面
                            onGradeResult = { selectedScreen = "Grade Result" } // 提交後導航到評分結果
                        )
                    }
                    "Grade Result" -> {
                        // 評分結果屏幕
                        GradeResultScreen(
                            onBack = { selectedScreen = "Camera" },
                            onRestart = { selectedScreen = "Question Settings" } // 重新開始測驗
                        )
                    }
                }
            }
        }
    }
}