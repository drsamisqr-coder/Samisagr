package com.example.util

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import java.util.Locale

enum class AppLanguage(
    val code: String,
    val nativeName: String,
    val englishName: String,
    val layoutDirection: LayoutDirection
) {
    ARABIC(
        code = "ar",
        nativeName = "العربية",
        englishName = "Arabic",
        layoutDirection = LayoutDirection.Rtl
    ),
    ENGLISH(
        code = "en",
        nativeName = "English",
        englishName = "English",
        layoutDirection = LayoutDirection.Ltr
    );

    companion object {
        fun fromCode(code: String): AppLanguage {
            return values().firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ARABIC
        }
    }
}

val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.ARABIC }

object LocalizationManager {
    private const val PREFS_NAME = "mastercoder_locale_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun getSavedLanguage(context: Context): AppLanguage {
        val code = getPrefs(context).getString(KEY_LANGUAGE, AppLanguage.ARABIC.code) ?: AppLanguage.ARABIC.code
        return AppLanguage.fromCode(code)
    }

    fun saveLanguage(context: Context, language: AppLanguage) {
        getPrefs(context).edit().putString(KEY_LANGUAGE, language.code).apply()
    }

    fun updateContextLocale(context: Context, language: AppLanguage): Context {
        val locale = Locale.forLanguageTag(language.code)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        config.setLayoutDirection(locale)

        return context.createConfigurationContext(config)
    }

    fun setSavedLanguage(context: Context, language: AppLanguage) {
        saveLanguage(context, language)
    }

    fun createLocalizedContext(context: Context, language: AppLanguage): Context {
        return updateContextLocale(context, language)
    }

    fun getLocalizedResourceString(context: Context, language: AppLanguage, resId: Int, vararg formatArgs: Any): String {
        val localizedContext = updateContextLocale(context, language)
        return if (formatArgs.isNotEmpty()) {
            localizedContext.getString(resId, *formatArgs)
        } else {
            localizedContext.getString(resId)
        }
    }
}

@Composable
fun ProvideLocalizedApp(
    language: AppLanguage,
    content: @Composable () -> Unit
) {
    val currentContext = LocalContext.current
    val localizedContext = LocalizationManager.updateContextLocale(currentContext, language)

    CompositionLocalProvider(
        LocalContext provides localizedContext,
        LocalLayoutDirection provides language.layoutDirection,
        LocalAppLanguage provides language
    ) {
        content()
    }
}
