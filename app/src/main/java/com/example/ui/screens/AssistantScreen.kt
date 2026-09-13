package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.BugReport
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.model.ChatMessage
import com.example.model.MessageSender
import com.example.model.SpecializationMode
import com.example.ui.MasterCoderViewModel
import com.example.ui.components.CodeCard
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandIndigo

@Composable
fun AssistantScreen(
    viewModel: MasterCoderViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isArabic by viewModel.isArabic.collectAsStateWithLifecycle()
    val messages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val selectedMode by viewModel.selectedMode.collectAsStateWithLifecycle()
    val isGenerating by viewModel.isGenerating.collectAsStateWithLifecycle()
    val isOfflineMode by viewModel.isOfflineMode.collectAsStateWithLifecycle()

    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, isGenerating) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.lastIndex)
        }
    }

    val quickPrompts = if (isArabic) {
        listOf(
            "حل مشكلة NullPointerException في كوتلن",
            "نموذج معماري متكامل لـ Jetpack Compose",
            "كيف يعمل derivedStateOf بالتفصيل؟",
            "خوارزمية Binary Search بلغة بايثون",
            "إنشاء جدول وفهرس مركب في SQL"
        )
    } else {
        listOf(
            "Fix NullPointerException in Kotlin",
            "Clean Jetpack Compose Architecture",
            "Explain derivedStateOf vs remember",
            "Python Fast Binary Search Algorithm",
            "SQL Composite Index best practice"
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Specialization Mode Selector Bar
        SpecializationSelectorBar(
            selectedMode = selectedMode,
            isArabic = isArabic,
            onSelectMode = { viewModel.selectMode(it) }
        )

        // Free & Offline Mode Status Bar
        Surface(
            color = if (isOfflineMode) Color(0xFF064E3B).copy(alpha = 0.6f) else Color(0xFF131B2E).copy(alpha = 0.7f),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { viewModel.toggleOfflineMode() }
                .testTag("assistant_offline_banner")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isOfflineMode) Icons.Default.Bolt else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isOfflineMode) Color(0xFF10B981) else BrandCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isOfflineMode)
                            (if (isArabic) "وضع أوفلاين مفعّل (استجابة فورية 0ms • مجاني بالكامل)" else "Offline Mode Active (0ms Latency • 100% Free)")
                        else
                            (if (isArabic) "تطبيق مجاني 100% • يعمل بدون إنترنت مع محرك ذكي" else "100% Free • Built-in smart offline code engine"),
                        color = if (isOfflineMode) Color(0xFF6EE7B7) else Color(0xFF94A3B8),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Text(
                    text = if (isArabic) "تبديل ⚡" else "Toggle ⚡",
                    color = if (isOfflineMode) Color(0xFF10B981) else BrandCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Chat Message List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Quick suggestions header when conversation is fresh
            if (messages.size <= 2) {
                item {
                    Column(modifier = Modifier.padding(vertical = 4.dp)) {
                        Text(
                            text = stringResource(R.string.quick_prompts_title),
                            style = MaterialTheme.typography.labelMedium,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            quickPrompts.forEach { prompt ->
                                Surface(
                                    color = Color(0xFF162036),
                                    shape = RoundedCornerShape(16.dp),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E3D5C)),
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.sendMessage(prompt)
                                        }
                                        .testTag("quick_prompt_chip")
                                ) {
                                    Text(
                                        text = prompt,
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 12.sp,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            items(messages, key = { it.id }) { msg ->
                ChatMessageItem(
                    message = msg,
                    isArabic = isArabic,
                    onSendToPlayground = { code, lang ->
                        viewModel.copyCodeToPlayground(code, lang)
                        Toast.makeText(context, context.getString(R.string.opened_in_playground), Toast.LENGTH_SHORT).show()
                    },
                    onSaveSnippet = { code, lang ->
                        viewModel.saveSnippet("Snippet from MasterCoder", code, lang, "AI, Saved")
                        Toast.makeText(context, context.getString(R.string.saved_to_snippets), Toast.LENGTH_SHORT).show()
                    }
                )
            }

            if (isGenerating) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(vertical = 8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = BrandCyan,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = stringResource(R.string.chat_ai_generating),
                            color = Color(0xFF94A3B8),
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        // Input bottom bar
        Surface(
            color = Color(0xFF0F172A),
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.clearChat() },
                    modifier = Modifier.testTag("clear_chat_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Clear chat",
                        tint = Color(0xFF64748B)
                    )
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.chat_input_placeholder),
                            color = Color(0xFF64748B),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    maxLines = 4,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF131B2E),
                        unfocusedContainerColor = Color(0xFF131B2E),
                        focusedBorderColor = BrandCyan,
                        unfocusedBorderColor = Color(0xFF2E3D5C),
                        focusedTextColor = Color(0xFFF8FAFC),
                        unfocusedTextColor = Color(0xFFF8FAFC)
                    ),
                    shape = RoundedCornerShape(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            viewModel.sendMessage(inputText.trim())
                            inputText = ""
                        }
                    },
                    enabled = inputText.isNotBlank() && !isGenerating,
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank() && !isGenerating)
                                Brush.linearGradient(listOf(BrandCyan, BrandIndigo))
                            else
                                Brush.linearGradient(listOf(Color(0xFF2E3D5C), Color(0xFF1E293B)))
                        )
                        .testTag("send_message_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank() && !isGenerating) Color(0xFF04101E) else Color(0xFF64748B),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun SpecializationSelectorBar(
    selectedMode: SpecializationMode,
    isArabic: Boolean,
    onSelectMode: (SpecializationMode) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFF0C1322))
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SpecializationMode.values().forEach { mode ->
            val isSelected = mode == selectedMode
            val icon = getModeIcon(mode)
            val title = stringResource(mode.titleRes)

            Surface(
                color = if (isSelected) Color(0xFF0C2B4E) else Color(0xFF131B2E),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    if (isSelected) BrandCyan else Color(0xFF2E3D5C)
                ),
                modifier = Modifier
                    .clickable { onSelectMode(mode) }
                    .testTag("mode_chip_${mode.id}")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = if (isSelected) BrandCyan else Color(0xFF94A3B8),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = title,
                        color = if (isSelected) Color(0xFFF8FAFC) else Color(0xFF94A3B8),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }
    }
}

fun getModeIcon(mode: SpecializationMode): ImageVector {
    return when (mode) {
        SpecializationMode.GENERAL -> Icons.Default.Code
        SpecializationMode.DEBUGGER -> Icons.Default.BugReport
        SpecializationMode.EXPLAINER -> Icons.Default.Lightbulb
        SpecializationMode.OPTIMIZER -> Icons.Default.Speed
        SpecializationMode.INTERVIEW -> Icons.Default.Psychology
    }
}

@Composable
fun ChatMessageItem(
    message: ChatMessage,
    isArabic: Boolean,
    onSendToPlayground: (String, String) -> Unit,
    onSaveSnippet: (String, String) -> Unit
) {
    val isUser = message.sender == MessageSender.USER

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (!isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(BrandCyan, BrandIndigo))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "MasterCoder AI",
                        tint = Color(0xFF04101E),
                        modifier = Modifier.size(14.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.assistant_sender_title),
                    color = BrandCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Message text bubble
        Surface(
            color = if (isUser) Color(0xFF1E293B) else Color(0xFF131B2E),
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (isUser) Color(0xFF334155) else Color(0xFF1E293B)
            ),
            modifier = Modifier.padding(horizontal = 2.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // If message contains markdown code blocks, render explanation and cards
                val cleanText = message.text.replace("```[a-zA-Z0-9_+-]*\\n[\\s\\S]*?```".toRegex(), "").trim()

                if (cleanText.isNotBlank()) {
                    Text(
                        text = cleanText,
                        color = Color(0xFFF1F5F9),
                        fontSize = 14.sp,
                        lineHeight = 22.sp
                    )
                }

                // Render each extracted code block
                if (message.codeBlocks.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    message.codeBlocks.forEach { block ->
                        CodeCard(
                            code = block.code,
                            language = block.language,
                            modifier = Modifier.padding(vertical = 4.dp),
                            onSendToPlayground = { onSendToPlayground(block.code, block.language) },
                            onSaveSnippet = { onSaveSnippet(block.code, block.language) }
                        )
                    }
                }
            }
        }
    }
}
