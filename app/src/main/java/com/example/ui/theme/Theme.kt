package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val MasterCoderDarkColorScheme = darkColorScheme(
  primary = BrandCyan,
  onPrimary = Color(0xFF04101E),
  primaryContainer = Color(0xFF0C2B4E),
  onPrimaryContainer = Color(0xFFBAE6FD),
  secondary = BrandIndigo,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFF262C5B),
  onSecondaryContainer = Color(0xFFE0E7FF),
  tertiary = BrandEmerald,
  onTertiary = Color(0xFF022C1A),
  background = BackgroundDark,
  onBackground = TextPrimary,
  surface = SurfaceDark,
  onSurface = TextPrimary,
  surfaceVariant = SurfaceCard,
  onSurfaceVariant = TextSecondary,
  outline = BorderSubtle,
  outlineVariant = Color(0xFF1E293B),
  error = BrandRose,
  onError = Color.White
)

private val MasterCoderLightColorScheme = lightColorScheme(
  primary = Color(0xFF0284C7),
  onPrimary = Color.White,
  primaryContainer = Color(0xFFE0F2FE),
  onPrimaryContainer = Color(0xFF0369A1),
  secondary = BrandIndigo,
  onSecondary = Color.White,
  secondaryContainer = Color(0xFFEEF2FF),
  onSecondaryContainer = Color(0xFF4338CA),
  tertiary = BrandEmerald,
  onTertiary = Color.White,
  background = Color(0xFFF8FAFC),
  onBackground = Color(0xFF0F172A),
  surface = Color.White,
  onSurface = Color(0xFF0F172A),
  surfaceVariant = Color(0xFFF1F5F9),
  onSurfaceVariant = Color(0xFF475569),
  outline = Color(0xFFCBD5E1),
  outlineVariant = Color(0xFFE2E8F0),
  error = BrandRose,
  onError = Color.White
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Developer tool defaults to sleek dark theme
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = if (darkTheme) MasterCoderDarkColorScheme else MasterCoderLightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}

