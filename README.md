# Article Camera 2

Article Camera 2 是一個 Android 應用程式，可以透過相機拍攝文章內容，並使用 Google Gemini AI 技術將圖片中的文字轉換成可編輯的文字。此外，它還能根據提取的文章內容生成選擇題，適合用於教育和學習目的。

## 功能特色

- **即時文字提取**：使用相機拍攝文章或文件，即時將圖片中的文字內容提取出來
- **AI 驅動**：使用 Google Gemini API 進行高品質的文字識別和提取
- **智慧題目生成**：根據提取的文章內容自動生成選擇題
- **年級設定**：可選擇 1-6 年級程度，調整題目難度
- **題數控制**：可設定生成題目的數量（2-10 題）
- **答題功能**：內建答題界面，可回答生成的選擇題
- **成績評分**：自動評分並顯示答題結果

## 技術架構

- **語言**：Kotlin
- **框架**：Jetpack Compose（現代化 Android UI 框架）
- **相機**：CameraX（現代化 Android 相機庫）
- **AI 服務**：Google Generative AI SDK（使用 Gemini 模型）
- **架構模式**：MVVM（Model-View-ViewModel）
- **版本**：0.0.2

## 安裝需求

- Android 10 (API 等級 29) 或更高版本
- Google Play 服務
- 相機權限
- 網路連線（用於 AI 服務）

## 使用方法

1. **設定 API 金鑰**：
   - 開啟應用程式後，點擊底部導覽列的 "API Key" 項目
   - 輸入您的 Google Gemini API 金鑰
   - 點擊儲存按鈕

2. **拍攝文章**：
   - 返回相機頁面
   - 對準要提取文字的文章或文件
   - 點擊中央的拍照按鈕

3. **等待處理**：
   - 應用程式會將拍攝的圖片發送到 Gemini API 進行文字提取
   - 處理完成後會自動導向問題設定頁面

4. **設定題目參數**：
   - 選擇適合的年級程度（1-6 年級）
   - 設定要生成的題目數量（2-10 題）
   - 點擊 "生成選擇題" 按鈕

5. **回答問題**：
   - 系統會根據文章內容生成選擇題
   - 選擇你認為正確的答案
   - 提交後查看評分結果

## 專案結構

```
app/
├── src/main/java/com/truedano/articlecamera2/
│   ├── model/                 # 資料模型和業務邏輯
│   │   ├── ApiConfig.kt       # API 配置
│   │   ├── ApiKeyManager.kt   # API 金鑰管理
│   │   ├── CameraUtils.kt     # 相機操作工具
│   │   ├── GeminiApiKeyValidator.kt # API 金鑰驗證
│   │   ├── ImageToTextConverter.kt # 圖片轉文字功能
│   │   ├── QuestionData.kt    # 問題資料結構
│   │   ├── QuestionGenerator.kt # 問題生成邏輯
│   │   └── VersionUtils.kt    # 版本資訊工具
│   ├── ui/                    # 使用者介面組件
│   │   ├── ApiKeyScreen.kt    # API 金鑰設定頁面
│   │   ├── ApiKeyViewModel.kt # API 金鑰檢視模型
│   │   ├── ArticleQuestionViewModel.kt # 文章問題檢視模型
│   │   ├── CameraScreen.kt    # 相機拍攝頁面
│   │   ├── GradeResultScreen.kt # 成績結果頁面
│   │   ├── QuestionGenerationScreen.kt # 問題生成頁面
│   │   ├── QuestionScreen.kt  # 答題頁面
│   │   ├── QuestionSettingsScreen.kt # 問題設定頁面
│   │   ├── QuestionViewModel.kt # 問題檢視模型
│   │   └── theme/             # 應用程式主題
│   └── MainActivity.kt        # 主活動入口點
```

## 依賴套件

- `androidx.activity:activity-compose` - Compose 支援
- `androidx.camera:camera-camera2` - CameraX 相機庫
- `androidx.camera:camera-lifecycle` - CameraX 生命週期支援
- `androidx.camera:camera-view` - CameraX 視圖支援
- `com.google.ai.client.generativeai:generativeai` - Google Generative AI SDK
- `androidx.compose.*` - Jetpack Compose UI 框架
- `androidx.lifecycle:*` - Android Lifecycle 組件
- `androidx.material3` - Material Design 3 支援

## 權限說明

應用程式需要以下權限：
- `INTERNET` - 用於 AI 服務連線
- `CAMERA` - 用於拍照功能
- `WRITE_EXTERNAL_STORAGE` - 用於儲存拍攝的圖片 (API 等級 28 以下)

## 貢獻

歡迎提交 Issue 和 Pull Request 來改善這個專案！

## 授權

此專案採用 MIT 授權條款。