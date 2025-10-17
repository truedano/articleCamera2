# Article Camera 2 - Qwen Context

## 專案概覽

Article Camera 2 是一款 Android 應用程式，使用 Google 的 Gemini AI 技術通過相機拍攝文章內容，並將圖像中的文字轉換為可編輯的文字。此外，它還可以根據提取的文章內容生成選擇題，適合教育和學習用途。

### 主要功能
- **即時文字提取**: 使用相機拍攝文章或文件，並即時提取文字內容
- **AI 驅動**: 使用 Google Gemini API 進行高品質的文字識別和提取
- **智慧題目生成**: 根據提取的文章內容自動生成選擇題
- **年級設定**: 選擇 1-6 年級以調整題目難度
- **題目數量控制**: 設置要生成的題目數量 (2-10 題)
- **答題功能**: 內建答題介面以回答生成的選擇題
- **成績評分**: 自動評分並顯示答題結果
- **連續拍攝模式**: 支援連續拍攝多張照片，便於捕獲更多內容
- **多張圖片處理**: 支援一次選擇多張圖片進行文字提取
- **相片庫選取**: 可以直接從相片庫選取圖片進行文字提取
- **長按功能**: 長按相機按鈕可開啟選單，提供更多選項
- **快速說明**: 提供長按提示說明，幫助用戶了解功能
- **改進的UI體驗**: 優化的相機界面和更流暢的用戶體驗

### 技術棧
- **程式語言**: Kotlin
- **框架**: Jetpack Compose (現代 Android UI 框架)
- **相機**: CameraX (現代 Android 相機庫)
- **AI 服務**: Google Generative AI SDK (使用 Gemini 模型)
- **架構模式**: MVVM (Model-View-ViewModel)
- **目前版本**: 0.0.2

### 專案結構
```
app/
├── src/main/java/com/truedano/articlecamera2/
│   ├── model/                 # 數據模型和業務邏輯
│   │   ├── ApiConfig.kt       # API 配置
│   │   ├── ApiKeyManager.kt   # API 金鑰管理
│   │   ├── CameraUtils.kt     # 相機操作工具
│   │   ├── GeminiApiKeyValidator.kt # API 金鑰驗證
│   │   ├── ImageToTextConverter.kt # 圖像轉文字功能
│   │   ├── QuestionData.kt    # 題目數據結構
│   │   ├── QuestionGenerator.kt # 題目生成邏輯
│   │   └── VersionUtils.kt    # 版本資訊工具
│   ├── ui/                    # 用戶界面組件
│   │   ├── ApiKeyScreen.kt    # API 金鑰設定頁面
│   │   ├── ApiKeyViewModel.kt # API 金鑰 ViewModel
│   │   ├── ArticleQuestionViewModel.kt # 文章題目 ViewModel
│   │   ├── CameraScreen.kt    # 相機拍攝頁面
│   │   ├── GradeResultScreen.kt # 成績結果頁面
│   │   ├── QuestionGenerationScreen.kt # 題目生成頁面
│   │   ├── QuestionScreen.kt  # 答題頁面
│   │   ├── QuestionSettingsScreen.kt # 題目設定頁面
│   │   ├── QuestionViewModel.kt # 題目 ViewModel
│   │   ├── theme/             # 應用程式主題
│   │   │   ├── Color.kt       # 色彩配置
│   │   │   ├── Theme.kt       # 應用程式主題
│   │   │   └── Type.kt        # 字體配置
│   └── MainActivity.kt        # 主活動入口點
```

## 相依性

專案使用以下關鍵相依性：
- `androidx.activity:activity-compose` - Compose 支援
- `androidx.camera:camera-camera2` - CameraX 相機庫
- `androidx.camera:camera-lifecycle` - CameraX 生命周期支援
- `androidx.camera:camera-view` - CameraX 視圖支援
- `com.google.ai.client.generativeai:generativeai` - Google 生成式 AI SDK
- `androidx.compose.*` - Jetpack Compose UI 框架
- `androidx.lifecycle:*` - Android 生命周期組件
- `androidx.material3` - Material Design 3 支援
- `androidx.core.ktx` - Android Core KTX
- `androidx.lifecycle.runtime.ktx` - Lifecycle Runtime KTX
- `androidx.lifecycle.runtime.compose` - Lifecycle Compose Integration
- `androidx.lifecycle.viewmodel.compose` - ViewModel Compose Integration
- `androidx.compose.material` - Material Design Components
- `androidx.compose.material.icons.extended` - Extended Material Icons

## 建置和運行

### 需求
- Android Studio (建議使用最新版本)
- JDK 11 或更高版本
- Android 10 (API 等級 29) 或更高版本於目標設備
- Google Play 服務
- 相機權限
- 網際網路連線 (用於 AI 服務)

### 建置設定
- **編譯 SDK**：36
- **最低 SDK**：29
- **目標 SDK**：36
- **Kotlin 版本**：2.2.20
- **AGP 版本**：8.13.0
- **JVM 工具鏈**：11
- **Compose**：已啟用
- **BuildConfig**：已啟用

### 建置指令
要建置專案，請使用以下 Gradle 指令：

```bash
# 組建除錯 APK
./gradlew assembleDebug

# 組建發布 APK
./gradlew assembleRelease

# 在連接的設備上安裝除錯 APK
./gradlew installDebug
```

### 配置
1. **API 金鑰設定**: 
   - 開啟應用程式並點擊底部導航列的 "API 金鑰" 項目
   - 輸入您的 Google Gemini API 金鑰
   - 點擊儲存

2. **模型配置**:
   - 應用程式預設使用 "gemini-2.5-flash" 模型，但可以在 API 金鑰頁面更改

### 權限
應用程式需要以下權限：
- `INTERNET` - 用於 AI 服務連線
- `CAMERA` - 用於相機功能
- `WRITE_EXTERNAL_STORAGE` - 用於儲存拍攝的圖像 (API 等級 28 及以下)
- `android.hardware.camera` - 硬體功能需求 (非必要)

## 開發約定

- **架構**: MVVM (Model-View-ViewModel) 模式
- **UI**: Jetpack Compose 用於現代 UI 開發
- **導航**: 在 MainActivity 中管理的基於螢幕的導航
- **狀態管理**: Jetpack Compose 狀態管理原則
- **非同步操作**: Kotlin Coroutines 用於非同步操作
- **程式碼風格**: 官方 Kotlin 程式碼風格

## 關鍵組件

### 圖像處理流程
1. 用戶使用相機拍攝圖像
2. 圖像由 `ImageToTextConverter` 使用 Gemini API 處理
3. 提取的文字傳遞給 `QuestionGenerator` 
4. 根據文章內容生成題目
5. 用戶可以回答題目並獲得評分

### API 金鑰管理
- `ApiKeyManager` 處理使用 SharedPreferences 存儲和檢索 API 金鑰
- `GeminiApiKeyValidator` 在使用前驗證提供的 API 金鑰
- API 金鑰存儲在本地，從不必要的傳輸

### 相機實現
- 使用 CameraX 庫進行相機功能
- `CameraScreen` 處理相機預覽和拍攝
- `CameraUtils` 提供相機操作的實用函數

### 題目生成
- `QuestionData` 定義題目和試卷的結構
- `QuestionGenerator` 處理從文章內容生成題目的邏輯
- 生成帶有選項和解釋的選擇題

## 測試

專案包含標準 Android 測試相依性：
- 使用 JUnit 的單元測試
- 使用 Espresso 的儀器測試
- Compose UI 測試

運行測試：
```bash
# 運行單元測試
./gradlew test

# 運行儀器測試
./gradlew connectedAndroidTest
```

## 專案配置

### 建置設置
- 編譯 SDK: 36
- 最低 SDK: 29
- 目標 SDK: 36
- Kotlin JVM toolchain: 11
- 啟用 Compose
- 啟用 BuildConfig

### Gradle 相依性
專案在 `gradle/libs.versions.toml` 中使用版本目錄管理相依性，其中包括：
- AndroidX 核心和生命周期庫
- Compose BOM 和 UI 組件
- CameraX 庫
- Google 生成式 AI SDK
- Material Design 組件

版本目錄詳細配置：
- AGP (Android Gradle Plugin): 8.13.0
- Kotlin: 2.2.20
- AndroidX Core KTX: 1.17.0
- JUnit: 4.13.2
- AndroidX JUnit: 1.3.0
- Espresso Core: 3.7.0
- Lifecycle Runtime KTX: 2.9.4
- Activity Compose: 1.11.0
- Compose BOM: 2025.10.00
- CameraX: 1.5.1
- Google Generative AI SDK: 0.9.0
- Material3: 1.4.0

## 常見任務

### 新增功能
1. 在 `ui/` 目錄中新增 UI 頁面
2. 在 `ui/` 目錄中創建相應的 ViewModels
3. 在 `model/` 目錄中新增模型邏輯（如需要）
4. 在 MainActivity 導航中註冊新頁面
5. 如有必要，更新 README

### 修改題目生成
1. 更新 `QuestionData.kt` 以修改題目結構
2. 修改 `QuestionGenerator.kt` 以修改題目生成邏輯
3. 更新相應的 ViewModel 和 UI 組件
4. 根據需要更新對 Gemini 模型的 API 調用

### 更新 AI 模型
1. 在 `ApiKeyManager.kt` 中修改預設模型
2. 在 `GeminiApiKeyValidator.kt` 中更新模型驗證
3. 在 `ImageToTextConverter.kt` 中進行必要的更新
4. 使用新模型進行測試以確保相容性

## 近期功能更新

### 最新版本功能 (自 commit 76cb71c4 以來)

1. **連續拍攝與多圖處理**
   - 新增連續拍攝模式，可在相機界面進行連續拍照
   - 支援多張圖片同時處理，一次性提取多張圖片的文字內容
   - 優化的 `ImageToTextConverter` 支援單圖或多圖處理

2. **相片庫選取功能**
   - 新增從相片庫選取圖片的功能
   - 支援多張圖片選取與處理
   - 改進的圖片載入與處理流程

3. **相機界面優化**
   - 長按相機按鈕可開啟功能選單
   - 新增長按提示說明，5秒後自動消失
   - 改進的UI/UX設計與互動體驗

4. **底層架構改進**
   - 新增 `onImageCaptured` 回調功能
   - 優化的 `CameraUtils` 包含 `capturePhotoOnly` 方法
   - 改進的圖片URI處理機制

5. **程式碼結構優化**
   - 改進的錯誤處理與異常管理
   - 優化的UI狀態管理
   - 改進的線程處理與API調用

## 回覆規則

- **一律以繁體中文回復**: 所有與此專案相關的回覆都應使用繁體中文