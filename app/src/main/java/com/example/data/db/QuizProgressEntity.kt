package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quiz_progress")
data class QuizProgressEntity(
    @PrimaryKey
    val id: Int = 1,
    val xp: Int = 0,
    val completedChallengesCount: Int = 0,
    val currentStreak: Int = 1,
    val lastCompletedDate: Long = 0L,
    val completedQuestionIds: String = "" // comma separated
)
