package com.example.data.api

import android.util.Log
import com.example.BuildConfig
import com.example.model.SpecializationMode
import com.example.util.OfflineCodeEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private const val MODEL_NAME = "gemini-3.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()

    suspend fun generateCodeResponse(
        prompt: String,
        mode: SpecializationMode,
        customApiKey: String? = null,
        conversationHistory: List<Pair<String, String>> = emptyList(), // sender, text
        isOfflineOnly: Boolean = false,
        isArabic: Boolean = false
    ): Result<String> = withContext(Dispatchers.IO) {
        // If the user enabled pure offline mode, respond instantly via the local engine
        if (isOfflineOnly) {
            return@withContext Result.success(OfflineCodeEngine.generateOfflineResponse(prompt, mode, isArabic))
        }

        val apiKey = customApiKey?.takeIf { it.isNotBlank() }
            ?: BuildConfig.GEMINI_API_KEY.takeIf { it.isNotBlank() && it != "MY_GEMINI_API_KEY" }
            ?: ""

        if (apiKey.isEmpty()) {
            return@withContext Result.success(OfflineCodeEngine.generateOfflineResponse(prompt, mode, isArabic))
        }

        try {
            val url = "$BASE_URL/$MODEL_NAME:generateContent?key=$apiKey"
            val jsonBody = JSONObject()

            // System instructions
            val systemObj = JSONObject()
            val systemParts = JSONArray()
            systemParts.put(JSONObject().put("text", mode.systemPrompt))
            systemObj.put("parts", systemParts)
            jsonBody.put("systemInstruction", systemObj)

            // Contents array (context + current prompt)
            val contentsArray = JSONArray()
            for ((sender, text) in conversationHistory.takeLast(6)) {
                val role = if (sender == "USER") "user" else "model"
                val contentObj = JSONObject()
                contentObj.put("role", role)
                val parts = JSONArray()
                parts.put(JSONObject().put("text", text))
                contentObj.put("parts", parts)
                contentsArray.put(contentObj)
            }

            // Current prompt
            val currentContent = JSONObject()
            currentContent.put("role", "user")
            val currentParts = JSONArray()
            currentParts.put(JSONObject().put("text", prompt))
            currentContent.put("parts", currentParts)
            contentsArray.put(currentContent)

            jsonBody.put("contents", contentsArray)

            // Generation config
            val genConfig = JSONObject()
            genConfig.put("temperature", 0.4)
            genConfig.put("topP", 0.95)
            genConfig.put("topK", 40)
            jsonBody.put("generationConfig", genConfig)

            val mediaType = "application/json; charset=utf-8".toMediaType()
            val requestBody = jsonBody.toString().toRequestBody(mediaType)

            val request = Request.Builder()
                .url(url)
                .post(requestBody)
                .build()

            val response = client.newCall(request).execute()
            val responseString = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "API call failed (${response.code}): $responseString")
                return@withContext Result.success(
                    OfflineCodeEngine.generateOfflineResponse(prompt, mode, isArabic)
                )
            }

            val jsonResponse = JSONObject(responseString)
            val candidates = jsonResponse.optJSONArray("candidates")
            if (candidates != null && candidates.length() > 0) {
                val firstCandidate = candidates.getJSONObject(0)
                val content = firstCandidate.optJSONObject("content")
                val parts = content?.optJSONArray("parts")
                if (parts != null && parts.length() > 0) {
                    val text = parts.getJSONObject(0).optString("text", "")
                    if (text.isNotBlank()) {
                        return@withContext Result.success(text)
                    }
                }
            }

            Result.success(OfflineCodeEngine.generateOfflineResponse(prompt, mode, isArabic))
        } catch (e: Exception) {
            Log.e(TAG, "Network exception calling Gemini: ${e.message}", e)
            Result.success(
                OfflineCodeEngine.generateOfflineResponse(prompt, mode, isArabic)
            )
        }
    }
}
