package com.example.data.repository

import com.example.data.db.QuizProgressDao
import com.example.data.db.QuizProgressEntity
import com.example.data.db.SnippetDao
import com.example.data.db.SnippetEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class MasterCoderRepository(
    private val snippetDao: SnippetDao,
    private val quizProgressDao: QuizProgressDao
) {
    val allSnippets: Flow<List<SnippetEntity>> = snippetDao.getAllSnippets()
    val quizProgress: Flow<QuizProgressEntity?> = quizProgressDao.getProgress()

    fun getSnippetsByLanguage(language: String): Flow<List<SnippetEntity>> {
        return if (language == "All" || language.isEmpty()) {
            snippetDao.getAllSnippets()
        } else {
            snippetDao.getSnippetsByLanguage(language)
        }
    }

    fun searchSnippets(query: String): Flow<List<SnippetEntity>> {
        return snippetDao.searchSnippets(query)
    }

    suspend fun saveSnippet(snippet: SnippetEntity): Long {
        return snippetDao.insertSnippet(snippet)
    }

    suspend fun deleteSnippet(id: Long) {
        snippetDao.deleteSnippetById(id)
    }

    suspend fun toggleFavorite(id: Long, currentFavorite: Boolean) {
        snippetDao.updateFavorite(id, !currentFavorite)
    }

    suspend fun addQuizXP(earnedXP: Int, questionId: String) {
        val current = quizProgressDao.getProgress().firstOrNull() ?: QuizProgressEntity(id = 1)
        val completedIds = current.completedQuestionIds
            .split(",")
            .filter { it.isNotBlank() }
            .toMutableSet()

        val isNew = completedIds.add(questionId)
        val newXp = current.xp + earnedXP
        val newCount = if (isNew) current.completedChallengesCount + 1 else current.completedChallengesCount

        quizProgressDao.saveProgress(
            current.copy(
                xp = newXp,
                completedChallengesCount = newCount,
                lastCompletedDate = System.currentTimeMillis(),
                completedQuestionIds = completedIds.joinToString(",")
            )
        )
    }
}
