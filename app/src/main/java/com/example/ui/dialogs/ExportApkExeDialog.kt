package com.example.ui.dialogs

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Android
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.BrandCyan
import com.example.ui.theme.BrandGreen

@Composable
fun ExportApkExeDialog(
    isArabic: Boolean,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    fun copyToClipboard(label: String, text: String) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText(label, text)
        clipboard.setPrimaryClip(clip)
        val msg = if (isArabic) "تم نسخ المسار إلى الحافظة" else "Path copied to clipboard"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF0F172A),
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(BrandCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Download,
                        contentDescription = null,
                        tint = BrandCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = if (isArabic) "تحميل APK وتشغيل على ويندوز (EXE)" else "Download APK & Run on PC (EXE)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Free & Offline Banner
                Surface(
                    color = Color(0xFF064E3B).copy(alpha = 0.4f),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandGreen.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic)
                                "التطبيق مجاني 100% بدون أي رسوم أو اشتراكات، وجاهز للتحميل والتثبيت."
                            else
                                "App is 100% Free with no subscriptions, ready to install and run.",
                            color = Color(0xFFE2E8F0),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Section 1: Android APK
                Surface(
                    color = Color(0xFF131B2E),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Android, contentDescription = null, tint = BrandGreen, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "1. ملف تطبيق أندرويد (APK جاهز)" else "1. Android APK Package",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isArabic)
                                "تم تجميع وبناء ملف APK بنجاح! يمكنك تحميله وتثبيته مباشرة على أي هاتف أو جهاز لوحي:\n• من القائمة العلوية للتطبيق في AI Studio: اختر (Settings/Export -> Download APK).\n• أو من مسار البناء المباشر في المشروع."
                            else
                                "The APK is built and ready! You can install it directly on any Android smartphone or tablet:\n• In AI Studio top settings menu, select Export / Download APK.\n• Or grab the compiled APK from the build directory.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Surface(
                            color = Color(0xFF070B14),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "app/build/outputs/apk/debug/app-debug.apk",
                                    color = BrandCyan,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 10.sp,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { copyToClipboard("APK Path", "app/build/outputs/apk/debug/app-debug.apk") },
                                    modifier = Modifier.size(28.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = Color(0xFF94A3B8), modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                // Section 2: Windows PC / EXE
                Surface(
                    color = Color(0xFF131B2E),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Computer, contentDescription = null, tint = BrandCyan, modifier = Modifier.size(22.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isArabic) "2. التشغيل على ويندوز PC (EXE)" else "2. Run on Windows PC (EXE)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isArabic)
                                "لتشغيل التطبيق على أجهزة ويندوز الكمبيوتر بسهولة:\n" +
                                "أ) ويندوز 11 (مباشر بدون محاكي):\nاستخدم نظام Windows Subsystem for Android (WSA) أو أداة WSA Pacman لتثبيت ملف APK بنقرة واحدة وتشغيله كنافذة ويندوز أصلية.\n\n" +
                                "ب) ويندوز 10 و 11 (محاكي خفيف):\nافتح ملف الـ APK مباشرة عبر BlueStacks 5 أو LDPlayer أو NoxPlayer ليعمل بسلاسة فائقة.\n\n" +
                                "ج) توليد ملف EXE تنفيذي مستقل:\nيمكنك تصدير الكود المصدري كملف ZIP وتجميعه عبر Compose Multiplatform Desktop لإنشاء ملف .exe مباشر بنظام Windows Installer (MSI/EXE)."
                            else
                                "How to run on Windows PC:\n" +
                                "A) Windows 11 (Native):\nUse Windows Subsystem for Android (WSA) or WSA Pacman to install the APK directly as a native Windows desktop app window.\n\n" +
                                "B) Windows 10 & 11 (Fast Emulator):\nOpen the APK with 1 click using BlueStacks 5, LDPlayer, or Nox for instant desktop execution.\n\n" +
                                "C) Standalone EXE generation:\nExport the source code ZIP and package with Compose Multiplatform Desktop to generate a standalone .exe or .msi installer.",
                            color = Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = BrandCyan, contentColor = Color(0xFF04101E)),
                modifier = Modifier.testTag("export_dialog_ok")
            ) {
                Text(
                    text = if (isArabic) "حسناً، فهمت" else "Got it",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}
