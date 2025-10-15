package com.truedano.articlecamera2.ui

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.compose.viewModel
import com.truedano.articlecamera2.model.ApiConfig
import com.truedano.articlecamera2.ui.theme.BlueAccent
import com.truedano.articlecamera2.ui.theme.DarkCharcoalBackground
import com.truedano.articlecamera2.ui.theme.DarkGrayBackground
import com.truedano.articlecamera2.ui.theme.GrayText

// API Key 申請指南組件
@Composable
fun ApiKeyGuideDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("如何申請 Gemini API Key") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "步驟 1: 訪問 Google AI Studio",
                    style = MaterialTheme.typography.titleMedium,
                    color = BlueAccent
                )
                Text(
                    text = "點擊連結前往 Google AI Studio 網站。",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp)
                )
                
                Text(
                    text = "步驟 2: 登入或註冊",
                    style = MaterialTheme.typography.titleMedium,
                    color = BlueAccent,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = "使用您的 Google 帳戶登入，或註冊一個新帳戶。",
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Text(
                    text = "步驟 3: 啟用 Gemini API",
                    style = MaterialTheme.typography.titleMedium,
                    color = BlueAccent,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = "在 AI Studio 中找到 API Keys 頁面，啟用 Gemini API。",
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Text(
                    text = "步驟 4: 建立 API Key",
                    style = MaterialTheme.typography.titleMedium,
                    color = BlueAccent,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = "點擊「Create API Key」按鈕建立新的 API 金鑰。",
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Text(
                    text = "步驟 5: 複製並貼上",
                    style = MaterialTheme.typography.titleMedium,
                    color = BlueAccent,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = "複製生成的 API 金鑰，並貼上到上方的輸入框中。",
                    modifier = Modifier.padding(top = 4.dp)
                )
                
                Text(
                    text = "注意事項:",
                    style = MaterialTheme.typography.titleMedium,
                    color = BlueAccent,
                    modifier = Modifier.padding(top = 16.dp)
                )
                Text(
                    text = "• 請妥善保管您的 API 金鑰，不要分享給他人\n" +
                           "• API 金鑰僅會顯示一次，請立即複製\n" +
                           "• 申請 API 金鑰可能需要 Google Cloud 帳戶驗證",
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("關閉")
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeyScreen(
    onSave: () -> Unit,
    onBack: () -> Unit,
    onNavigateToCamera: () -> Unit,
    onNavigateToApiKey: () -> Unit,
    selectedScreen: String,
    apiKeyViewModel: ApiKeyViewModel = viewModel()
) {
    val apiKey by apiKeyViewModel.apiKey.collectAsState()
    val selectedModel by apiKeyViewModel.selectedModel.collectAsState()
    val validationState by apiKeyViewModel.validationState.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    var showApiKeyGuide by remember { mutableStateOf(false) }
    
    // 添加打開瀏覽器的函數

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("API Key", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkCharcoalBackground
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = selectedScreen,
                onNavigateToApiKey = onNavigateToApiKey,
                onNavigateToCamera = onNavigateToCamera
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
        ) {
            // 表單區域
            FormSection(
                apiKey = apiKey,
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = { passwordVisible = !passwordVisible },
                onApiKeyChange = { apiKeyViewModel.onApiKeyChange(it) },
                selectedModel = selectedModel,
                expanded = expanded,
                onExpandedChange = { newExpanded -> expanded = newExpanded },
                onModelChange = { apiKeyViewModel.onModelChange(it) },
                validationState = validationState,
                onValidateClick = { apiKeyViewModel.validateApiKey() },
                onShowGuide = { showApiKeyGuide = true },
                onSaveClick = {
                    apiKeyViewModel.saveApiKey()
                    onSave()
                }
            )
            
            // 顯示 API Key 指南對話框
            if (showApiKeyGuide) {
                ApiKeyGuideDialog(
                    onDismiss = { showApiKeyGuide = false }
                )
            }
        }
    }
}

// API 金鑰輸入區域組件
@Composable
private fun ApiKeyInputSection(
    apiKey: String,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    onApiKeyChange: (String) -> Unit
) {
    Text("Gemini API Key", color = Color.White)
    Spacer(modifier = Modifier.height(8.dp))
    OutlinedTextField(
        value = apiKey,
        onValueChange = onApiKeyChange,
        label = { Text("Enter your API key") },
        modifier = Modifier.fillMaxWidth(),
        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
        trailingIcon = {
            val image = if (passwordVisible)
                Icons.Filled.Visibility
            else Icons.Filled.VisibilityOff

            IconButton(onClick = onPasswordVisibilityChange) {
                Icon(imageVector = image, contentDescription = "Toggle password visibility")
            }
        },
        colors = TextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            cursorColor = Color.White,
            focusedContainerColor = DarkGrayBackground,
            unfocusedContainerColor = DarkGrayBackground,
            focusedLabelColor = GrayText,
            unfocusedLabelColor = GrayText,
            focusedIndicatorColor = BlueAccent,
            unfocusedIndicatorColor = GrayText,
            focusedTrailingIconColor = GrayText,
            unfocusedTrailingIconColor = GrayText
        )
    )
}

// 模型選擇區域組件
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModelSelectionSection(
    selectedModel: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onModelChange: (String) -> Unit
) {
    Text("Gemini Model", color = Color.White)
    Spacer(modifier = Modifier.height(8.dp))
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { bool -> onExpandedChange(bool) },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedModel.ifEmpty { ApiConfig.GEMINI_MODELS.first() },
            onValueChange = { },
            readOnly = true,
            label = { Text("Select a model") },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.ArrowDropDown,
                    contentDescription = null
                )
            },
            modifier = Modifier.menuAnchor(ExposedDropdownMenuAnchorType.PrimaryEditable, true).fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                focusedContainerColor = DarkGrayBackground,
                unfocusedContainerColor = DarkGrayBackground,
                focusedLabelColor = GrayText,
                unfocusedLabelColor = GrayText,
                focusedIndicatorColor = BlueAccent,
                unfocusedIndicatorColor = GrayText
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
            modifier = Modifier.fillMaxWidth()
        ) {
            ApiConfig.GEMINI_MODELS.forEach { model ->
                DropdownMenuItem(
                    text = { Text(text = model) },
                    onClick = {
                        onModelChange(model)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

// 驗證按鈕組件
@Composable
private fun ValidateApiKeyButton(
    validationState: ValidationState,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        enabled = validationState !is ValidationState.Loading
    ) {
        when (validationState) {
            ValidationState.Loading -> {
                CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
            }
            else -> {
                Text("驗證 API 金鑰")
            }
        }
    }
}

// 驗證結果訊息組件
@Composable
private fun ValidationResultMessage(validationState: ValidationState) {
    when (validationState) {
        ValidationState.Success -> {
            Text("API 金鑰有效！", color = Color.Green)
        }
        is ValidationState.Failure -> {
            Text(validationState.message, color = Color.Red)
        }
        else -> { /* No text to show */ }
    }
}

// 保存按鈕組件
@Composable
private fun SaveButton(
    validationState: ValidationState,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth(),
        enabled = validationState is ValidationState.Success
    ) {
        Text("儲存")
    }
}

// 底部導航欄組件
@Composable
private fun BottomNavigationBar(
    selectedScreen: String,
    onNavigateToApiKey: () -> Unit,
    onNavigateToCamera: () -> Unit
) {
    val navigationItems = listOf(
        NavigationItemData(
            icon = Icons.Default.Key,
            text = "API Key",
            isSelected = selectedScreen == "API Key",
            onClick = onNavigateToApiKey
        ),
        NavigationItemData(
            icon = Icons.Default.PhotoCamera,
            text = "Camera",
            isSelected = selectedScreen == "Camera",
            onClick = onNavigateToCamera
        )
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .background(DarkCharcoalBackground)
            .windowInsetsPadding(WindowInsets.navigationBars),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        navigationItems.forEach { item ->
            NavigationItem(item)
        }
    }
}

// 導航項目數據類
data class NavigationItemData(
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val text: String,
    val isSelected: Boolean,
    val onClick: () -> Unit
)

// 導航項目組件
@Composable
private fun NavigationItem(item: NavigationItemData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable { item.onClick() }
            .padding(8.dp)
    ) {
        Icon(
            imageVector = item.icon,
            contentDescription = item.text,
            tint = if (item.isSelected) BlueAccent else GrayText,
            modifier = Modifier.size(32.dp)
        )
        Text(
            text = item.text,
            color = if (item.isSelected) BlueAccent else GrayText,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

// 表單區域組件
@Composable
private fun FormSection(
    apiKey: String,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: () -> Unit,
    onApiKeyChange: (String) -> Unit,
    selectedModel: String,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onModelChange: (String) -> Unit,
    validationState: ValidationState,
    onValidateClick: () -> Unit,
    onShowGuide: () -> Unit,
    onSaveClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            // API Key 輸入區域
            ApiKeyInputSection(
                apiKey = apiKey,
                passwordVisible = passwordVisible,
                onPasswordVisibilityChange = onPasswordVisibilityChange,
                onApiKeyChange = onApiKeyChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 模型選擇區域
            ModelSelectionSection(
                selectedModel = selectedModel,
                expanded = expanded,
                onExpandedChange = onExpandedChange,
                onModelChange = onModelChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 驗證按鈕和結果
            ValidationSection(
                validationState = validationState,
                onClick = onValidateClick
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // API 金鑰說明
            Text(
                text = "您可以從以下網站取得您的 Gemini API 金鑰：",
                color = GrayText,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            // 可點擊的連結文字
            val context = LocalContext.current
            ClickableLinkText(
                context = context
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // 如何申請 API Key 按鈕
            HelpButton(
                onClick = onShowGuide
            )
            
            // 為驗證成功後的保存按鈕留出空間
            Spacer(modifier = Modifier.weight(1f, fill = false))
        }

        // 保存按鈕
        SaveButton(
            validationState = validationState,
            onClick = onSaveClick
        )
    }
}

// 可點擊連結組件
@Composable
private fun ClickableLinkText(context: android.content.Context = LocalContext.current) {
    Text(
        text = buildAnnotatedString {
            withStyle(style = androidx.compose.ui.text.SpanStyle(color = BlueAccent)) {
                append(ApiConfig.GOOGLE_AI_STUDIO_WEBSITE)
            }
        },
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, ApiConfig.GOOGLE_AI_STUDIO_URL.toUri())
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
    )
}

// 幫助按鈕組件
@Composable
private fun HelpButton(onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.Help,
                contentDescription = "Help",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("如何申請 API Key？")
        }
    }
}

// 驗證區域組件
@Composable
private fun ValidationSection(
    validationState: ValidationState,
    onClick: () -> Unit
) {
    ValidateApiKeyButton(
        validationState = validationState,
        onClick = onClick
    )
    Spacer(modifier = Modifier.height(8.dp))
    ValidationResultMessage(validationState = validationState)
}
