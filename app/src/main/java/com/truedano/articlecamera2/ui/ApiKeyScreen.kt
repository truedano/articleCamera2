package com.truedano.articlecamera2.ui

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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.truedano.articlecamera2.ui.theme.BlueAccent
import com.truedano.articlecamera2.ui.theme.DarkCharcoalBackground
import com.truedano.articlecamera2.ui.theme.DarkGrayBackground
import com.truedano.articlecamera2.ui.theme.GrayText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApiKeyScreen(
    onSave: (String) -> Unit,
    onBack: () -> Unit,
    onNavigateToCamera: () -> Unit,
    onNavigateToApiKey: () -> Unit,
    selectedScreen: String
) {
    var apiKey by remember { mutableStateOf("") }

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(DarkCharcoalBackground)
                    .windowInsetsPadding(WindowInsets.navigationBars),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val isCameraSelected = selectedScreen == "Camera"
                val isApiKeySelected = selectedScreen == "API Key"

                // API Key item
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onNavigateToApiKey() }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Key,
                        contentDescription = "API Key",
                        tint = if (isApiKeySelected) BlueAccent else GrayText,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "API Key",
                        color = if (isApiKeySelected) BlueAccent else GrayText,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // Camera item
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable { onNavigateToCamera() }
                        .padding(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PhotoCamera,
                        contentDescription = "Camera",
                        tint = if (isCameraSelected) BlueAccent else GrayText,
                        modifier = Modifier.size(32.dp)
                    )
                    Text(
                        text = "Camera",
                        color = if (isCameraSelected) BlueAccent else GrayText,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
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
            Spacer(modifier = Modifier.height(16.dp))
            Text("Gemini API Key", color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text("Enter your API key") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { /* TODO: Check API Key Valid */ },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Check API Key Valid")
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "You can obtain your Gemini API key from the Google AI Studio website.",
                color = GrayText,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            
            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { onSave(apiKey) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            ) {
                Text("Save")
            }
        }
    }
}