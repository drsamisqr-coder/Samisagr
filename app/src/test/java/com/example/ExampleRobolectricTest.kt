package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.SpecializationMode
import com.example.util.AppLanguage
import com.example.util.LocalizationManager
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.util.Locale

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("MasterCoder", appName)
  }

  @Test
  fun `verify localization manager saves and reads language`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    LocalizationManager.setSavedLanguage(context, AppLanguage.ARABIC)
    val savedLang = LocalizationManager.getSavedLanguage(context)
    assertEquals(AppLanguage.ARABIC, savedLang)

    LocalizationManager.setSavedLanguage(context, AppLanguage.ENGLISH)
    val englishLang = LocalizationManager.getSavedLanguage(context)
    assertEquals(AppLanguage.ENGLISH, englishLang)
  }

  @Test
  fun `verify arabic localized context returns arabic strings`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val arabicContext = LocalizationManager.createLocalizedContext(context, AppLanguage.ARABIC)
    val navAssistant = arabicContext.getString(R.string.nav_assistant)
    assertEquals("المساعد", navAssistant)

    val navAcademy = arabicContext.getString(R.string.nav_academy)
    assertEquals("الأكاديمية", navAcademy)
  }

  @Test
  fun `verify specialization modes have valid titles`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    SpecializationMode.values().forEach { mode ->
      val title = context.getString(mode.titleRes)
      assertTrue(title.isNotBlank())
    }
  }

  @Test
  fun `verify offline code engine returns responses in arabic and english`() {
    val arabicResponse = com.example.util.OfflineCodeEngine.generateOfflineResponse(
      prompt = "عندي مشكلة null pointer crash",
      mode = SpecializationMode.DEBUGGER,
      forceArabic = true
    )
    assertTrue(arabicResponse.isNotBlank())
    assertTrue(arabicResponse.contains("المبرمج المحترف") || arabicResponse.contains("MasterCoder"))

    val englishResponse = com.example.util.OfflineCodeEngine.generateOfflineResponse(
      prompt = "explain binary search algorithm",
      mode = SpecializationMode.INTERVIEW,
      forceArabic = false
    )
    assertTrue(englishResponse.isNotBlank())
    assertTrue(englishResponse.contains("binarySearch") || englishResponse.contains("Binary Search") || englishResponse.contains("Algorithm"))
  }
}

