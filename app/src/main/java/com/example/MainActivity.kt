package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.MainTab
import com.example.ui.MasterCoderViewModel
import com.example.ui.dialogs.ExportApkExeDialog
import com.example.ui.dialogs.LanguageSelectionDialog
import com.example.ui.dialogs.SettingsDialog
import com.example.ui.screens.AcademyScreen
import com.example.ui.screens.AssistantScreen
import com.example.ui.screens.PlaygroundScreen
import com.example.ui.screens.SnippetsScreen
import com.example.ui.theme.BrandAmber
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandIndigo
import com.example.ui.theme.MyApplicationTheme
import com.example.util.AppLanguage
import com.example.util.ProvideLocalizedApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: MasterCoderViewModel = viewModel()
                MasterCoderApp(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MasterCoderApp(viewModel: MasterCoderViewModel) {
    val appLanguage by viewModel.appLanguage.collectAsStateWithLifecycle()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val progress by viewModel.quizProgress.collectAsStateWithLifecycle()
    val customApiKey by viewModel.customApiKey.collectAsStateWithLifecycle()
    val isOfflineMode by viewModel.isOfflineMode.collectAsStateWithLifecycle()
    val showExportDialog by viewModel.showExportDialog.collectAsStateWithLifecycle()

    var showSettingsDialog by remember { mutableStateOf(false) }
    var showLanguageDialog by remember { mutableStateOf(false) }

    ProvideLocalizedApp(language = appLanguage) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color(0xFF0B0F19),
                        titleContentColor = Color.White
                    ),
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF131B2E)),
                                contentAlignment = Alignment.Center
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_launcher_logo),
                                    contentDescription = stringResource(R.string.app_name),
                                    modifier = Modifier.size(28.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = stringResource(R.string.app_name),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = stringResource(R.string.app_subtitle),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = BrandCyan,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    },
                    actions = {
                        // Offline Mode Quick Pill Toggle
                        Surface(
                            color = if (isOfflineMode) Color(0xFF064E3B) else Color(0xFF131B2E),
                            shape = RoundedCornerShape(14.dp),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isOfflineMode) Color(0xFF10B981) else Color(0xFF2E3D5C)
                            ),
                            modifier = Modifier
                                .padding(end = 4.dp)
                                .clickable { viewModel.toggleOfflineMode() }
                                .testTag("offline_toggle_btn")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = if (isOfflineMode) Icons.Default.Bolt else Icons.Default.CloudQueue,
                                    contentDescription = stringResource(R.string.offline_mode_title),
                                    tint = if (isOfflineMode) Color(0xFF10B981) else Color(0xFF94A3B8),
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = if (isOfflineMode) (if (appLanguage == AppLanguage.ARABIC) "أوفلاين" else "Offline") else (if (appLanguage == AppLanguage.ARABIC) "أونلاين" else "Online"),
                                    color = if (isOfflineMode) Color(0xFF10B981) else Color(0xFF94A3B8),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Export APK & Windows EXE Dialog Button
                        IconButton(
                            onClick = { viewModel.setExportDialogVisible(true) },
                            modifier = Modifier.testTag("export_dialog_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = stringResource(R.string.btn_apk_exe),
                                tint = BrandCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Explicit Language Dialog / Switcher Button
                        IconButton(
                            onClick = { showLanguageDialog = true },
                            modifier = Modifier.testTag("lang_toggle_btn")
                        ) {
                            Surface(
                                color = Color(0xFF162036),
                                shape = RoundedCornerShape(8.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BrandCyan)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Language,
                                        contentDescription = stringResource(R.string.language_switch),
                                        tint = BrandCyan,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (appLanguage == AppLanguage.ARABIC) "عربي" else "EN",
                                        color = BrandCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        // Settings Icon
                        IconButton(
                            onClick = { showSettingsDialog = true },
                            modifier = Modifier.testTag("settings_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = stringResource(R.string.settings_title),
                                tint = Color(0xFF94A3B8)
                            )
                        }
                    }
                )
            },
            bottomBar = {
                NavigationBar(
                    containerColor = Color(0xFF0F172A),
                    contentColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    val navItemColors = NavigationBarItemDefaults.colors(
                        selectedIconColor = BrandCyan,
                        selectedTextColor = BrandCyan,
                        unselectedIconColor = Color(0xFF64748B),
                        unselectedTextColor = Color(0xFF64748B),
                        indicatorColor = Color(0xFF0C2B4E)
                    )

                    // 1. Assistant Tab
                    NavigationBarItem(
                        selected = currentTab == MainTab.CHAT,
                        onClick = { viewModel.selectTab(MainTab.CHAT) },
                        icon = { Icon(Icons.AutoMirrored.Filled.Chat, contentDescription = stringResource(R.string.nav_assistant)) },
                        label = { Text(stringResource(R.string.nav_assistant), fontSize = 11.sp) },
                        colors = navItemColors,
                        modifier = Modifier.testTag("nav_assistant")
                    )

                    // 2. Playground Tab
                    NavigationBarItem(
                        selected = currentTab == MainTab.PLAYGROUND,
                        onClick = { viewModel.selectTab(MainTab.PLAYGROUND) },
                        icon = { Icon(Icons.Default.Terminal, contentDescription = stringResource(R.string.nav_playground)) },
                        label = { Text(stringResource(R.string.nav_playground), fontSize = 11.sp) },
                        colors = navItemColors,
                        modifier = Modifier.testTag("nav_playground")
                    )

                    // 3. Snippets Tab
                    NavigationBarItem(
                        selected = currentTab == MainTab.SNIPPETS,
                        onClick = { viewModel.selectTab(MainTab.SNIPPETS) },
                        icon = { Icon(Icons.Default.Bookmark, contentDescription = stringResource(R.string.nav_snippets)) },
                        label = { Text(stringResource(R.string.nav_snippets), fontSize = 11.sp) },
                        colors = navItemColors,
                        modifier = Modifier.testTag("nav_snippets")
                    )

                    // 4. Academy Tab
                    NavigationBarItem(
                        selected = currentTab == MainTab.ACADEMY,
                        onClick = { viewModel.selectTab(MainTab.ACADEMY) },
                        icon = { Icon(Icons.Default.School, contentDescription = stringResource(R.string.nav_academy)) },
                        label = { Text(stringResource(R.string.nav_academy), fontSize = 11.sp) },
                        colors = navItemColors,
                        modifier = Modifier.testTag("nav_academy")
                    )
                }
            }
        ) { innerPadding ->
            Crossfade(
                targetState = currentTab,
                label = "tab_fade",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) { tab ->
                when (tab) {
                    MainTab.CHAT -> AssistantScreen(viewModel = viewModel)
                    MainTab.PLAYGROUND -> PlaygroundScreen(viewModel = viewModel)
                    MainTab.SNIPPETS -> SnippetsScreen(viewModel = viewModel)
                    MainTab.ACADEMY -> AcademyScreen(viewModel = viewModel)
                }
            }
        }

        if (showLanguageDialog) {
            LanguageSelectionDialog(
                currentLanguage = appLanguage,
                onLanguageSelected = { viewModel.setAppLanguage(it) },
                onDismiss = { showLanguageDialog = false }
            )
        }

        if (showExportDialog) {
            ExportApkExeDialog(
                isArabic = appLanguage == AppLanguage.ARABIC,
                onDismiss = { viewModel.setExportDialogVisible(false) }
            )
        }

        if (showSettingsDialog) {
            SettingsDialog(
                currentApiKey = customApiKey,
                currentLanguage = appLanguage,
                isOfflineMode = isOfflineMode,
                onDismiss = { showSettingsDialog = false },
                onSaveApiKey = { viewModel.setCustomApiKey(it) },
                onLanguageSelected = { viewModel.setAppLanguage(it) },
                onToggleOfflineMode = { viewModel.setOfflineMode(it) },
                onOpenExportApkExe = { viewModel.setExportDialogVisible(true) }
            )
        }
    }
}

