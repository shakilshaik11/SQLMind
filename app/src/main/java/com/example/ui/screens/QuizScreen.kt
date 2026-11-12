package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.QuizData
import com.example.data.QuizQuestion
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SqlViewModel

@Composable
fun QuizScreen(viewModel: SqlViewModel) {
    val scrollState = rememberScrollState()
    val highScoresList by viewModel.highScores.collectAsState()
    val highScoresMap = remember(highScoresList) { highScoresList.associate { it.category to it.highScore } }

    val categoryConfig = listOf(
        QuizCategoryItem("BEGINNER", "Beginner Basics", "Basic filters, SELECT clauses, columns.", "3 Questions"),
        QuizCategoryItem("INTERMEDIATE", "Intermediate Operator", "JOINs, WHERE vs HAVING, groupings.", "3 Questions"),
        QuizCategoryItem("ADVANCED", "Advanced Administrator", "Correlated subqueries, CTEs, recursion.", "3 Questions")
    )

    ResponsiveContainer(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 64.dp)
    ) {
        if (!viewModel.quizActive.value && !viewModel.quizCompleted.value) {
            // --- Welcome Mode Selection ---
            Text(
                text = "Select Quiz category to test skills:",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 8.dp)
            )

            categoryConfig.forEach { cat ->
                val score = highScoresMap[cat.key] ?: 0

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clickable { viewModel.startQuiz(cat.key) }
                        .testTag("quiz_cat_${cat.key}"),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStrokeWrapper(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.15f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Quiz,
                                    contentDescription = "Quiz logo",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = cat.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = cat.desc,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // High Score display
                        Column(horizontalAlignment = Alignment.End) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Stars Score",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    text = "High: $score/3",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = cat.length,
                                fontSize = 9.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(30.dp))

            // Generic Sandbox Mixed Quiz Choice
            Button(
                onClick = { viewModel.startQuiz("MIXED") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("start_mixed_quiz"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Start Icon",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text("TAKE COMPLETE MIXED QUIZ", fontWeight = FontWeight.Bold)
            }
        } else if (viewModel.quizActive.value) {
            // --- Active Quiz Mode ---
            val activeQuestions = remember(viewModel.quizCategory.value) {
                if (viewModel.quizCategory.value == "MIXED") {
                    QuizData.questions
                } else {
                    QuizData.questions.filter { it.category == viewModel.quizCategory.value }
                }
            }

            val questionCount = activeQuestions.size
            val currentIndex = viewModel.currentQuestionIndex.value

            if (currentIndex < questionCount) {
                val question = activeQuestions[currentIndex]
                val currentProgress = (currentIndex + 1).toFloat() / questionCount.toFloat()

                // Question Header progress tracker
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "${viewModel.quizCategory.value} QUIZ",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Question ${currentIndex + 1} of $questionCount",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                LinearProgressIndicator(
                    progress = { currentProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MaterialTheme.colorScheme.primary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(Modifier.height(14.dp))

                // Question Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStrokeWrapper(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = question.questionText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            lineHeight = 22.sp
                        )

                        // Code snippet included (If Any)
                        if (question.codeSnippet != null) {
                            Spacer(Modifier.height(8.dp))
                            CodeTerminalBlock(
                                code = question.codeSnippet,
                                label = "QUESTION CODE",
                                titleColor = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Pill selections
                question.options.forEachIndexed { optIdx, option ->
                    val isSelected = viewModel.selectedOptionIndex.value == optIdx
                    val isChecked = viewModel.answerChecked.value
                    
                    val cardBgColor = when {
                        isChecked && optIdx == question.correctIndex -> MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                        isChecked && isSelected && optIdx != question.correctIndex -> Color(0xFFEF5350).copy(alpha = 0.15f)
                        isSelected -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f)
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val cardBorderColor = when {
                        isChecked && optIdx == question.correctIndex -> MaterialTheme.colorScheme.tertiary
                        isChecked && isSelected && optIdx != question.correctIndex -> Color(0xFFEF5350)
                        isSelected -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                    }

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable(enabled = !isChecked) { viewModel.selectQuizOption(optIdx) }
                            .testTag("option_$optIdx"),
                        colors = CardDefaults.cardColors(containerColor = cardBgColor),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStrokeWrapper(1.dp, cardBorderColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 48.dp) // meets 48dp target standard accessibility
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = when (optIdx) {
                                    0 -> "A"
                                    1 -> "B"
                                    2 -> "C"
                                    else -> "D"
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = option,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Answer Checked explanation alert box
                if (viewModel.answerChecked.value) {
                    val isCorrect = viewModel.selectedOptionIndex.value == question.correctIndex
                    
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCorrect) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.1f)
                            else Color(0xFFEF5350).copy(alpha = 0.1f)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStrokeWrapper(
                            1.dp,
                            if (isCorrect) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.5f)
                            else Color(0xFFEF5350).copy(alpha = 0.5f)
                        )
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (isCorrect) Icons.Default.CheckCircle
                                    else Icons.Default.HelpOutline,
                                    contentDescription = "Accuracy Icon",
                                    tint = if (isCorrect) MaterialTheme.colorScheme.tertiary else Color(0xFFEF5350),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = if (isCorrect) "Excellent! Correct Answer." else "Nice Try! Incorrect.",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCorrect) MaterialTheme.colorScheme.tertiary else Color(0xFFEF5350)
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = question.explanation,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(Modifier.height(14.dp))

                // Actions check/next
                if (!viewModel.answerChecked.value) {
                    Button(
                        onClick = { viewModel.checkAnswer(question.correctIndex) },
                        enabled = viewModel.selectedOptionIndex.value != null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("quiz_submit_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("CHECK ANSWER", fontWeight = FontWeight.Bold)
                    }
                } else {
                    Button(
                        onClick = { viewModel.nextQuestion(questionCount) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("quiz_next_btn"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = if (currentIndex + 1 < questionCount) "CONTINUE QUESTION" else "FINISH & VIEW SCORE",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        } else {
            // --- Quiz Completion Summary Dashboard ---
            val activeQuestions = remember(viewModel.quizCategory.value) {
                if (viewModel.quizCategory.value == "MIXED") {
                    QuizData.questions
                } else {
                    QuizData.questions.filter { it.category == viewModel.quizCategory.value }
                }
            }
            val questionCount = activeQuestions.size
            val correct = viewModel.quizCorrectAnswersCount.value
            val pct = if (questionCount > 0) (correct.toFloat() / questionCount.toFloat() * 100).toInt() else 0

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStrokeWrapper(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Celebration,
                        contentDescription = "Score medal",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(60.dp)
                    )

                    Spacer(Modifier.height(14.dp))

                    Text(
                        text = "Quiz Challenge Completed!",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(4.dp))

                    Text(
                        text = "Category: ${viewModel.quizCategory.value}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(Modifier.height(20.dp))

                    // SCORE DISPLAY
                    Text(
                        text = "$correct / $questionCount Correct",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "$pct% Score Rate",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )

                    Spacer(Modifier.height(14.dp))

                    // Motive text
                    Text(
                        text = when {
                            pct == 100 -> "Absolute Database master! Superb command of SQL aggregates and hierarchies."
                            pct >= 66 -> "Splendid work! You possess a robust handle on joins, metrics and core clauses."
                            else -> "Nice effort! Head back to the Course syllabus to review subqueries and join mechanics."
                        },
                        fontSize = 12.sp,
                        lineHeight = 18.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Return to course
                Button(
                    onClick = {
                        viewModel.quizCompleted.value = false
                        viewModel.quizActive.value = false
                        viewModel.currentScreen.value = AppScreen.LEARN
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("quiz_to_syllabus_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("LEARN COURSE", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Retry
                Button(
                    onClick = {
                        viewModel.quizCompleted.value = false
                        viewModel.quizActive.value = false
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("quiz_reset_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset Icon",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("RETAKE QUIZ", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

data class QuizCategoryItem(
    val key: String,
    val title: String,
    val desc: String,
    val length: String
)
