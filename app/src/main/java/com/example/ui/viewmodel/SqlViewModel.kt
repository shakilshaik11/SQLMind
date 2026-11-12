package com.example.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.FirebaseAuthManager
import com.example.auth.UserSession
import com.example.data.AppDatabase
import com.example.data.HistoryEntity
import com.example.data.LessonProgressEntity
import com.example.data.UserScoreEntity
import com.example.network.GeminiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppScreen {
    TRANSLATE,
    LEARN,
    QUIZ,
    WORKSPACE
}

enum class TranslationMode {
    NL_TO_SQL,
    SQL_TO_NL
}

object AppSettings {
    val isDarkMode = mutableStateOf(true) // default to dark mode for visual comfort!
}

class SqlViewModel(application: Application) : AndroidViewModel(application) {
    private val TAG = "SqlViewModel"
    private val database = AppDatabase.getDatabase(application)

    // Current navigation state
    val currentScreen = mutableStateOf(AppScreen.TRANSLATE)
    
    // Theme configuration
    val isDarkMode = AppSettings.isDarkMode

    // --- Translation Tab State ---
    val translationMode = mutableStateOf(TranslationMode.NL_TO_SQL)
    val selectedDbType = mutableStateOf("MySQL") // MySQL, PostgreSQL, SQLite, Oracle
    val nlInput = mutableStateOf("")
    val sqlInput = mutableStateOf("")
    
    // Translation Results
    val translateLoading = mutableStateOf(false)
    val resultSql = mutableStateOf("")
    val resultExplanation = mutableStateOf("")
    val resultNlTranslation = mutableStateOf("")
    val resultNlExplanation = mutableStateOf("")

    // List of supported databases
    val databaseTypes = listOf("MySQL", "PostgreSQL", "SQLite", "Oracle")

    // --- Room Database Flows ---
    val history: StateFlow<List<HistoryEntity>> = database.historyDao().getHistory()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val completedLessons: StateFlow<List<LessonProgressEntity>> = database.progressDao().getProgress()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val highScores: StateFlow<List<UserScoreEntity>> = database.scoresDao().getScores()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // --- Session State ---
    val userSession: StateFlow<UserSession?> = FirebaseAuthManager.userSession
    val authError: StateFlow<String?> = FirebaseAuthManager.authError

    // --- Quiz Mode State ---
    val quizActive = mutableStateOf(false)
    val quizCategory = mutableStateOf("BEGINNER") // BEGINNER, INTERMEDIATE, ADVANCED, MIXED
    val currentQuestionIndex = mutableStateOf(0)
    val selectedOptionIndex = mutableStateOf<Int?>(null)
    val answerChecked = mutableStateOf(false)
    val quizCorrectAnswersCount = mutableStateOf(0)
    val quizCompleted = mutableStateOf(false)

    init {
        // Initialize Firebase on start
        FirebaseAuthManager.initFirebase(application)
        
        // Setup sync observation if login state changes
        viewModelScope.launch {
            userSession.collect { session ->
                if (session != null) {
                    syncLocalHistoryToCloud(session.uid)
                }
            }
        }
    }

    fun toggleDarkMode() {
        isDarkMode.value = !isDarkMode.value
    }

    // --- Translate Logic ---
    fun performTranslation() {
        if (translationMode.value == TranslationMode.NL_TO_SQL) {
            val prompt = nlInput.value.trim()
            if (prompt.isEmpty()) return

            viewModelScope.launch {
                translateLoading.value = true
                try {
                    val result = GeminiClient.translateNlToSql(prompt, selectedDbType.value)
                    resultSql.value = result.first
                    resultExplanation.value = result.second

                    // Save to Room DB automatically
                    val newItem = HistoryEntity(
                        type = "NL_TO_SQL",
                        inputText = prompt,
                        outputText = result.first,
                        explanation = result.second,
                        dbType = selectedDbType.value
                    )
                    database.historyDao().insertHistory(newItem)

                    // Sync to Firestore if user active
                    userSession.value?.let { session ->
                        syncLocalHistoryToCloud(session.uid)
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Fail translating NL to SQL", e)
                } finally {
                    translateLoading.value = false
                }
            }
        } else {
            val query = sqlInput.value.trim()
            if (query.isEmpty()) return

            viewModelScope.launch {
                translateLoading.value = true
                try {
                    val result = GeminiClient.translateSqlToNl(query)
                    resultNlTranslation.value = result.first
                    resultNlExplanation.value = result.second

                    // Save to Room DB
                    val newItem = HistoryEntity(
                        type = "SQL_TO_NL",
                        inputText = query,
                        outputText = result.first,
                        explanation = result.second,
                        dbType = selectedDbType.value
                    )
                    database.historyDao().insertHistory(newItem)

                    // Sync to Firestore if user active
                    userSession.value?.let { session ->
                        syncLocalHistoryToCloud(session.uid)
                    }

                } catch (e: Exception) {
                    Log.e(TAG, "Fail translating SQL to NL", e)
                } finally {
                    translateLoading.value = false
                }
            }
        }
    }

    fun deleteHistoryItem(item: HistoryEntity) {
        viewModelScope.launch {
            database.historyDao().deleteHistoryItem(item)
            // If synced, optionally we can expand to delete from Firestore
            userSession.value?.let { session ->
                if (FirebaseAuthManager.isFirebaseInitialized() && item.isSynced) {
                    try {
                        val fs = FirebaseAuthManager.getFirestore()
                        fs?.collection("users")?.document(session.uid)
                            ?.collection("history")?.document(item.id.toString())?.delete()
                    } catch (e: Exception) {
                        Log.w(TAG, "Fail firestore item remove", e)
                    }
                }
            }
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            database.historyDao().clearHistory()
            userSession.value?.let { session ->
                if (FirebaseAuthManager.isFirebaseInitialized()) {
                    try {
                        val fs = FirebaseAuthManager.getFirestore()
                        // Firestore deletions are typically done individually in batches
                        fs?.collection("users")?.document(session.uid)
                            ?.collection("history")?.get()?.addOnSuccessListener { snapshot ->
                                val batch = fs.batch()
                                snapshot.documents.forEach { doc ->
                                    batch.delete(doc.reference)
                                }
                                batch.commit()
                            }
                    } catch (e: Exception) {
                        Log.w(TAG, "Fail clear firestore history", e)
                    }
                }
            }
        }
    }

    // --- Learn Logic ---
    fun toggleLessonCompleted(lessonId: String, currentCompleted: Boolean) {
        viewModelScope.launch {
            if (currentCompleted) {
                database.progressDao().insertProgress(LessonProgressEntity(lessonId, false))
            } else {
                database.progressDao().completeLesson(lessonId)
            }
        }
    }

    // --- Quiz Logic ---
    fun startQuiz(category: String) {
        quizCategory.value = category
        currentQuestionIndex.value = 0
        selectedOptionIndex.value = null
        answerChecked.value = false
        quizCorrectAnswersCount.value = 0
        quizCompleted.value = false
        quizActive.value = true
    }

    fun selectQuizOption(index: Int) {
        if (!answerChecked.value) {
            selectedOptionIndex.value = index
        }
    }

    fun checkAnswer(correctIndex: Int) {
        if (selectedOptionIndex.value == null || answerChecked.value) return
        answerChecked.value = true
        if (selectedOptionIndex.value == correctIndex) {
            quizCorrectAnswersCount.value += 1
        }
    }

    fun nextQuestion(totalQuestionsCount: Int) {
        selectedOptionIndex.value = null
        answerChecked.value = false
        
        if (currentQuestionIndex.value + 1 < totalQuestionsCount) {
            currentQuestionIndex.value += 1
        } else {
            quizCompleted.value = true
            quizActive.value = false
            saveQuizHighScore()
        }
    }

    @Suppress("DEPRECATION")
    private fun saveQuizHighScore() {
        val score = quizCorrectAnswersCount.value
        val categoryKey = quizCategory.value
        viewModelScope.launch {
            val existing = database.scoresDao().getScoreForCategory(categoryKey)
            if (existing == null || score > existing.highScore) {
                database.scoresDao().insertScore(UserScoreEntity(categoryKey, score))
                
                // Save to Firestore so report can be retained in account
                userSession.value?.let { session ->
                    val fs = FirebaseAuthManager.getFirestore()
                    if (fs != null) {
                        val scoreData = hashMapOf(
                            "category" to categoryKey,
                            "highScore" to score,
                            "timestamp" to System.currentTimeMillis()
                        )
                        fs.collection("users").document(session.uid)
                            .collection("scores").document(categoryKey)
                            .set(scoreData)
                    }
                }
            }
        }
    }

    // --- Authentication Actions ---
    fun loginSimulatedUser() {
        FirebaseAuthManager.performBackupLocalSession()
    }

    fun signOutUser() {
        FirebaseAuthManager.signOut()
    }

    // --- Firestore Sync Engine ---
    private fun syncLocalHistoryToCloud(userId: String) {
        if (!FirebaseAuthManager.isFirebaseInitialized()) return
        val db = FirebaseAuthManager.getFirestore() ?: return

        viewModelScope.launch {
            val unsynced = database.historyDao().getUnsyncedHistory()
            if (unsynced.isEmpty()) return@launch

            Log.i(TAG, "Found ${unsynced.size} unsynced translations. Pushing to Cloud Firestore.")
            val batch = db.batch()
            unsynced.forEach { item ->
                // Use a composite custom or stable document ID
                val docRef = db.collection("users").document(userId)
                    .collection("history").document(item.id.toString())
                
                val payload = hashMapOf(
                    "type" to item.type,
                    "inputText" to item.inputText,
                    "outputText" to item.outputText,
                    "explanation" to item.explanation,
                    "dbType" to item.dbType,
                    "timestamp" to item.timestamp
                )
                batch.set(docRef, payload)
            }

            batch.commit()
                .addOnSuccessListener {
                    viewModelScope.launch {
                        database.historyDao().markAsSynced(unsynced.map { it.id })
                        Log.d(TAG, "Cloud Firestore sync of history successful.")
                    }
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Failed syncing history to cloud", e)
                }
        }
    }
}
