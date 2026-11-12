package com.example.data

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: HistoryEntity)

    @Query("SELECT * FROM history_table ORDER BY timestamp DESC")
    fun getHistory(): Flow<List<HistoryEntity>>

    @Delete
    suspend fun deleteHistoryItem(item: HistoryEntity)

    @Query("DELETE FROM history_table")
    suspend fun clearHistory()

    @Query("SELECT * FROM history_table WHERE isSynced = 0")
    suspend fun getUnsyncedHistory(): List<HistoryEntity>

    @Query("UPDATE history_table SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markAsSynced(ids: List<Int>)
}

@Dao
interface ProgressDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgress(item: LessonProgressEntity)

    @Query("SELECT * FROM progress_table")
    fun getProgress(): Flow<List<LessonProgressEntity>>

    @Query("UPDATE progress_table SET isCompleted = 1, timestamp = :timestamp WHERE lessonId = :lessonId")
    suspend fun completeLesson(lessonId: String, timestamp: Long = System.currentTimeMillis())
}

@Dao
interface ScoresDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScore(item: UserScoreEntity)

    @Query("SELECT * FROM scores_table")
    fun getScores(): Flow<List<UserScoreEntity>>

    @Query("SELECT * FROM scores_table WHERE category = :category LIMIT 1")
    suspend fun getScoreForCategory(category: String): UserScoreEntity?
}

@Database(
    entities = [HistoryEntity::class, UserScoreEntity::class, LessonProgressEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun progressDao(): ProgressDao
    abstract fun scoresDao(): ScoresDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sql_translator_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
