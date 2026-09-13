package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiApiClient
import com.example.data.db.AppDatabase
import com.example.data.db.QuizProgressEntity
import com.example.data.db.SnippetEntity
import com.example.data.repository.MasterCoderRepository
import com.example.model.AcademyRepository
import com.example.model.ChatMessage
import com.example.model.MessageSender
import com.example.model.QuizChallenge
import com.example.model.SpecializationMode
import com.example.util.AppLanguage
import com.example.util.CodeExecutionEngine
import com.example.util.ExecutionResult
import com.example.util.LocalizationManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab {
    CHAT,
    PLAYGROUND,
    SNIPPETS,
    ACADEMY
}

class MasterCoderViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: MasterCoderRepository

    init {
        val database = AppDatabase.getDatabase(application, viewModelScope)
        repository = MasterCoderRepository(database.snippetDao(), database.quizProgressDao())
    }

    // App Language managed via LocalizationManager with persistent storage
    private val _appLanguage = MutableStateFlow(LocalizationManager.getSavedLanguage(application))
    val appLanguage: StateFlow<AppLanguage> = _appLanguage.asStateFlow()

    private val _isArabic = MutableStateFlow(_appLanguage.value == AppLanguage.ARABIC)
    val isArabic: StateFlow<Boolean> = _isArabic.asStateFlow()

    fun setAppLanguage(language: AppLanguage) {
        _appLanguage.value = language
        _isArabic.value = (language == AppLanguage.ARABIC)
        LocalizationManager.saveLanguage(getApplication(), language)
    }

    fun toggleLanguage() {
        val nextLang = if (_appLanguage.value == AppLanguage.ARABIC) AppLanguage.ENGLISH else AppLanguage.ARABIC
        setAppLanguage(nextLang)
    }

    // Active Navigation Tab
    private val _currentTab = MutableStateFlow(MainTab.CHAT)
    val currentTab: StateFlow<MainTab> = _currentTab.asStateFlow()

    fun selectTab(tab: MainTab) {
        _currentTab.value = tab
    }

    // Settings
    private val _customApiKey = MutableStateFlow("")
    val customApiKey: StateFlow<String> = _customApiKey.asStateFlow()

    fun setCustomApiKey(key: String) {
        _customApiKey.value = key
    }

    // Offline Mode State (100% Free & Works without Internet)
    private val _isOfflineMode = MutableStateFlow(false)
    val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

    fun toggleOfflineMode() {
        _isOfflineMode.value = !_isOfflineMode.value
    }

    fun setOfflineMode(enabled: Boolean) {
        _isOfflineMode.value = enabled
    }

    // Export APK & Windows EXE Guide Dialog State
    private val _showExportDialog = MutableStateFlow(false)
    val showExportDialog: StateFlow<Boolean> = _showExportDialog.asStateFlow()

    fun setExportDialogVisible(visible: Boolean) {
        _showExportDialog.value = visible
    }

    // -------------------------------------------------------------
    // CHAT TAB STATE & LOGIC
    // -------------------------------------------------------------
    private val _selectedMode = MutableStateFlow(SpecializationMode.GENERAL)
    val selectedMode: StateFlow<SpecializationMode> = _selectedMode.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                sender = MessageSender.AI,
                text = "مرحباً بك في MasterCoder (المبرمج المحترف)!\nأنا مساعدك الهندسي لكتابة وتدقيق الأكواد، حل المشاكل، وشرح المعمارية البرمجية.\nالتطبيق مجاني 100% ويعمل بكفاءة عالية بدون الحاجة للإنترنت.\n\nHello! I am MasterCoder, your senior AI pair programmer. The app is 100% Free and works completely offline without Internet.",
                codeBlocks = emptyList()
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    fun selectMode(mode: SpecializationMode) {
        _selectedMode.value = mode
    }

    fun sendMessage(userText: String) {
        if (userText.isBlank() || _isGenerating.value) return

        val userMessage = ChatMessage(
            sender = MessageSender.USER,
            text = userText
        )
        _chatMessages.value = _chatMessages.value + userMessage
        _isGenerating.value = true

        val currentHistory = _chatMessages.value.map {
            (if (it.sender == MessageSender.USER) "USER" else "AI") to it.text
        }

        viewModelScope.launch {
            val result = GeminiApiClient.generateCodeResponse(
                prompt = userText,
                mode = _selectedMode.value,
                customApiKey = _customApiKey.value,
                conversationHistory = currentHistory,
                isOfflineOnly = _isOfflineMode.value,
                isArabic = _isArabic.value
            )

            result.onSuccess { responseText ->
                val codeBlocks = ChatMessage.extractCodeBlocks(responseText)
                val aiMessage = ChatMessage(
                    sender = MessageSender.AI,
                    text = responseText,
                    codeBlocks = codeBlocks
                )
                _chatMessages.value = _chatMessages.value + aiMessage
            }.onFailure { err ->
                val errorMessage = ChatMessage(
                    sender = MessageSender.AI,
                    text = "⚠️ Note: ${err.message}",
                    isError = true
                )
                _chatMessages.value = _chatMessages.value + errorMessage
            }
            _isGenerating.value = false
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            ChatMessage(
                sender = MessageSender.AI,
                text = if (_isArabic.value)
                    "تم مسح المحادثة. كيف يمكنني مساعدتك برمجياً الآن؟"
                else
                    "Conversation cleared. What shall we build or solve next?",
                codeBlocks = emptyList()
            )
        )
    }

    // -------------------------------------------------------------
    // PLAYGROUND TAB STATE & LOGIC
    // -------------------------------------------------------------
    private val _playgroundLanguage = MutableStateFlow("JavaScript")
    val playgroundLanguage: StateFlow<String> = _playgroundLanguage.asStateFlow()

    private val _playgroundCode = MutableStateFlow(
        """
// MasterCoder Interactive JS Sandbox
function calculateFibonacci(n) {
    if (n <= 1) return n;
    let a = 0, b = 1;
    for (let i = 2; i <= n; i++) {
        let temp = a + b;
        a = b;
        b = temp;
    }
    return b;
}

console.log("Fibonacci(10) =", calculateFibonacci(10));
console.log("MasterCoder Ready for Execution! 🚀");
""".trimIndent()
    )
    val playgroundCode: StateFlow<String> = _playgroundCode.asStateFlow()

    private val _executionResult = MutableStateFlow<ExecutionResult?>(null)
    val executionResult: StateFlow<ExecutionResult?> = _executionResult.asStateFlow()

    private val _isExecuting = MutableStateFlow(false)
    val isExecuting: StateFlow<Boolean> = _isExecuting.asStateFlow()

    fun updatePlaygroundCode(code: String) {
        _playgroundCode.value = code
    }

    fun selectPlaygroundLanguage(lang: String) {
        _playgroundLanguage.value = lang
        _executionResult.value = null
        when (lang) {
            "JavaScript" -> _playgroundCode.value = """
// JavaScript Sandbox
const numbers = [12, 45, 2, 99, 34, 67];
const sorted = numbers.slice().sort((a, b) => a - b);
console.log("Original:", numbers);
console.log("Sorted:", sorted);
console.log("Max item:", Math.max(...numbers));
""".trimIndent()

            "HTML" -> _playgroundCode.value = """
<!DOCTYPE html>
<html>
<head>
  <style>
    body { font-family: sans-serif; background: #0b0f19; color: #f8fafc; padding: 20px; text-align: center; }
    h1 { color: #00e5ff; }
    .badge { background: #6366f1; padding: 8px 16px; border-radius: 20px; display: inline-block; font-weight: bold; }
    button { margin-top: 15px; padding: 10px 20px; background: #10b981; color: white; border: none; border-radius: 8px; cursor: pointer; }
  </style>
</head>
<body>
  <h1>⚡ MasterCoder Studio</h1>
  <div class="badge">Live HTML & CSS Preview</div>
  <p>Render interactive UI prototypes directly on mobile.</p>
  <button onclick="alert('Hello from MasterCoder!')">Tap Me</button>
</body>
</html>
""".trimIndent()

            "Kotlin" -> _playgroundCode.value = """
// Kotlin Logic Sandbox
fun main() {
    val languages = listOf("Kotlin", "Python", "TypeScript", "Rust", "Go")
    println("Active Polyglot Stack:")
    languages.forEachIndexed { index, lang ->
        println("${'$'}{index + 1}. ${'$'}lang")
    }
}
""".trimIndent()

            "Python" -> _playgroundCode.value = """
# Python Sandbox
def get_user_stats(username: str) -> dict:
    return {
        "user": username,
        "role": "Master Architect",
        "xp": 4500,
        "status": "Online"
    }

stats = get_user_stats("DevMaster")
print(f"User: {stats['user']} | Role: {stats['role']}")
print(f"Current XP: {stats['xp']}")
""".trimIndent()

            "SQL" -> _playgroundCode.value = """
-- SQL Query Sandbox
SELECT 
    p.project_id,
    p.title,
    COUNT(c.commit_id) AS total_commits,
    MAX(c.created_at) AS last_active
FROM projects p
LEFT JOIN commits c ON p.project_id = c.project_id
GROUP BY p.project_id, p.title
HAVING COUNT(c.commit_id) > 10
ORDER BY total_commits DESC
LIMIT 5;
""".trimIndent()
        }
    }

    fun executePlaygroundCode(context: Context) {
        val code = _playgroundCode.value
        val lang = _playgroundLanguage.value
        _isExecuting.value = true

        viewModelScope.launch {
            val res = if (lang == "JavaScript") {
                CodeExecutionEngine.executeJavaScript(context, code)
            } else {
                CodeExecutionEngine.executeSimulated(code, lang)
            }
            _executionResult.value = res
            _isExecuting.value = false
        }
    }

    fun askAiAboutPlaygroundCode(actionType: String) {
        val code = _playgroundCode.value
        val lang = _playgroundLanguage.value
        val prompt = when (actionType) {
            "DEBUG" -> "Please analyze this $lang code, find any bugs or edge case failures, and provide the fixed code:\n```$lang\n$code\n```"
            "OPTIMIZE" -> "Analyze the time and space complexity of this $lang code and rewrite it for optimal performance:\n```$lang\n$code\n```"
            else -> "Explain step by step how this $lang code works and its key patterns:\n```$lang\n$code\n```"
        }
        _currentTab.value = MainTab.CHAT
        sendMessage(prompt)
    }

    fun copyCodeToPlayground(code: String, lang: String) {
        _playgroundLanguage.value = lang
        _playgroundCode.value = code
        _currentTab.value = MainTab.PLAYGROUND
    }

    // -------------------------------------------------------------
    // SNIPPETS TAB STATE & LOGIC
    // -------------------------------------------------------------
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedLanguageFilter = MutableStateFlow("All")
    val selectedLanguageFilter: StateFlow<String> = _selectedLanguageFilter.asStateFlow()

    val filteredSnippets: StateFlow<List<SnippetEntity>> = combine(
        repository.allSnippets,
        _searchQuery,
        _selectedLanguageFilter
    ) { snippets, query, langFilter ->
        snippets.filter { snippet ->
            val matchesLang = langFilter == "All" || snippet.language.equals(langFilter, ignoreCase = true)
            val matchesQuery = query.isBlank() ||
                    snippet.title.contains(query, ignoreCase = true) ||
                    snippet.code.contains(query, ignoreCase = true) ||
                    snippet.tags.contains(query, ignoreCase = true)
            matchesLang && matchesQuery
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setLanguageFilter(filter: String) {
        _selectedLanguageFilter.value = filter
    }

    fun toggleSnippetFavorite(snippet: SnippetEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(snippet.id, snippet.isFavorite)
        }
    }

    fun deleteSnippet(id: Long) {
        viewModelScope.launch {
            repository.deleteSnippet(id)
        }
    }

    fun saveSnippet(title: String, code: String, language: String, tags: String) {
        viewModelScope.launch {
            repository.saveSnippet(
                SnippetEntity(
                    title = title,
                    code = code,
                    language = language,
                    tags = tags,
                    timestamp = System.currentTimeMillis()
                )
            )
        }
    }

    // -------------------------------------------------------------
    // ACADEMY / QUIZ TAB STATE & LOGIC
    // -------------------------------------------------------------
    val quizProgress: StateFlow<QuizProgressEntity?> = repository.quizProgress
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = QuizProgressEntity()
        )

    private val _currentQuestionIndex = MutableStateFlow(0)
    val currentQuestionIndex: StateFlow<Int> = _currentQuestionIndex.asStateFlow()

    private val _selectedOption = MutableStateFlow<Int?>(null)
    val selectedOption: StateFlow<Int?> = _selectedOption.asStateFlow()

    private val _isSubmitted = MutableStateFlow(false)
    val isSubmitted: StateFlow<Boolean> = _isSubmitted.asStateFlow()

    val currentChallenge: QuizChallenge
        get() = AcademyRepository.challenges[_currentQuestionIndex.value.coerceIn(0, AcademyRepository.challenges.lastIndex)]

    fun selectOption(index: Int) {
        if (!_isSubmitted.value) {
            _selectedOption.value = index
        }
    }

    fun submitAnswer() {
        val selected = _selectedOption.value ?: return
        _isSubmitted.value = true
        if (selected == currentChallenge.correctIndex) {
            viewModelScope.launch {
                repository.addQuizXP(currentChallenge.xpReward, currentChallenge.id)
            }
        }
    }

    fun nextQuestion() {
        _selectedOption.value = null
        _isSubmitted.value = false
        _currentQuestionIndex.value = (_currentQuestionIndex.value + 1) % AcademyRepository.challenges.size
    }
}
