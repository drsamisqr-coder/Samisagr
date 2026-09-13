package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SnippetDao {
    @Query("SELECT * FROM snippets ORDER BY isFavorite DESC, timestamp DESC")
    fun getAllSnippets(): Flow<List<SnippetEntity>>

    @Query("SELECT * FROM snippets WHERE language = :language ORDER BY timestamp DESC")
    fun getSnippetsByLanguage(language: String): Flow<List<SnippetEntity>>

    @Query("SELECT * FROM snippets WHERE title LIKE '%' || :query || '%' OR code LIKE '%' || :query || '%' OR tags LIKE '%' || :query || '%'")
    fun searchSnippets(query: String): Flow<List<SnippetEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSnippet(snippet: SnippetEntity): Long

    @Update
    suspend fun updateSnippet(snippet: SnippetEntity)

    @Delete
    suspend fun deleteSnippet(snippet: SnippetEntity)

    @Query("DELETE FROM snippets WHERE id = :id")
    suspend fun deleteSnippetById(id: Long)

    @Query("UPDATE snippets SET isFavorite = :isFavorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, isFavorite: Boolean)
}

@Dao
interface QuizProgressDao {
    @Query("SELECT * FROM quiz_progress WHERE id = 1 LIMIT 1")
    fun getProgress(): Flow<QuizProgressEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveProgress(progress: QuizProgressEntity)
}
