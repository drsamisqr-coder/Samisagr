package com.example.model

enum class MessageSender {
    USER,
    AI,
    SYSTEM
}

data class CodeBlock(
    val language: String,
    val code: String
)

data class ChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val codeBlocks: List<CodeBlock> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false,
    val isError: Boolean = false
) {
    companion object {
        fun extractCodeBlocks(text: String): List<CodeBlock> {
            val list = mutableListOf<CodeBlock>()
            val regex = "```([a-zA-Z0-9_+-]*)\\n([\\s\\S]*?)```".toRegex()
            val matches = regex.findAll(text)
            for (m in matches) {
                val lang = m.groupValues[1].ifBlank { "code" }
                val code = m.groupValues[2].trim()
                if (code.isNotBlank()) {
                    list.add(CodeBlock(language = lang, code = code))
                }
            }
            return list
        }
    }
}
