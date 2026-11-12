package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val type: String, // "NL_TO_SQL" or "SQL_TO_NL"
    val inputText: String,
    val outputText: String,
    val explanation: String,
    val dbType: String,
    val isSynced: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "scores_table")
data class UserScoreEntity(
    @PrimaryKey val category: String, // "BEGINNER", "INTERMEDIATE", "ADVANCED", "MIXED"
    val highScore: Int,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "progress_table")
data class LessonProgressEntity(
    @PrimaryKey val lessonId: String, // Lesson A, B, etc.
    val isCompleted: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
