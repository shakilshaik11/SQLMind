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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CheckCircleOutline
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.LearningData
import com.example.data.Lesson
import com.example.data.LessonLevel
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SqlViewModel
import com.example.ui.viewmodel.TranslationMode

@Composable
fun LearnScreen(viewModel: SqlViewModel) {
    val completedList by viewModel.completedLessons.collectAsState()
    val completedIds = remember(completedList) { completedList.filter { it.isCompleted }.map { it.lessonId }.toSet() }
    
    val totalLessons = LearningData.lessons.size
    val completedCount = completedIds.size
    val progressFraction = if (totalLessons > 0) completedCount.toFloat() / totalLessons.toFloat() else 0f

    // Calculate level-specific completion statistics
    val basicLessons = remember { LearningData.lessons.filter { it.level == LessonLevel.BASIC } }
    val moderateLessons = remember { LearningData.lessons.filter { it.level == LessonLevel.MODERATE } }
    val advancedLessons = remember { LearningData.lessons.filter { it.level == LessonLevel.ADVANCED } }

    val basicCompleted = basicLessons.count { completedIds.contains(it.id) }
    val moderateCompleted = moderateLessons.count { completedIds.contains(it.id) }
    val advancedCompleted = advancedLessons.count { completedIds.contains(it.id) }

    var selectedLevelFilter by remember { mutableStateOf<LessonLevel?>(null) } // null = All
    var activeLesson by remember { mutableStateOf<Lesson?>(null) }
    val scrollState = rememberScrollState()

    val displayedLessons = remember(selectedLevelFilter) {
        if (selectedLevelFilter == null) LearningData.lessons
        else LearningData.lessons.filter { it.level == selectedLevelFilter }
    }

    ResponsiveContainer(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 64.dp)
    ) {
        if (activeLesson == null) {
            // --- Course Summary & Progress Header Card ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f)
                ),
                shape = RoundedCornerShape(18.dp),
                border = BorderStrokeWrapper(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text(
                                text = "SQL Master Trail",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "24 Comprehensive Sessions: Basic, Moderate & Advanced",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.School,
                            contentDescription = "Learning Academy",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Overall Progress",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "$completedCount of $totalLessons Completed (${(progressFraction * 100).toInt()}%)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                    }

                    Spacer(Modifier.height(8.dp))

                    LinearProgressIndicator(
                        progress = { progressFraction },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MaterialTheme.colorScheme.secondary,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(Modifier.height(14.dp))

                    // Level Progress Badges
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        LevelMiniStatCard(
                            title = "Basic",
                            completed = basicCompleted,
                            total = basicLessons.size,
                            color = Color(LessonLevel.BASIC.badgeColor),
                            modifier = Modifier.weight(1f)
                        )
                        LevelMiniStatCard(
                            title = "Moderate",
                            completed = moderateCompleted,
                            total = moderateLessons.size,
                            color = Color(LessonLevel.MODERATE.badgeColor),
                            modifier = Modifier.weight(1f)
                        )
                        LevelMiniStatCard(
                            title = "Advanced",
                            completed = advancedCompleted,
                            total = advancedLessons.size,
                            color = Color(LessonLevel.ADVANCED.badgeColor),
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            // --- Level Filter Selector Tabs ---
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                LevelFilterChip(
                    label = "All (${totalLessons})",
                    isSelected = selectedLevelFilter == null,
                    activeColor = MaterialTheme.colorScheme.primary,
                    onClick = { selectedLevelFilter = null },
                    modifier = Modifier.weight(1f)
                )
                LevelFilterChip(
                    label = "Basic (${basicLessons.size})",
                    isSelected = selectedLevelFilter == LessonLevel.BASIC,
                    activeColor = Color(LessonLevel.BASIC.badgeColor),
                    onClick = { selectedLevelFilter = LessonLevel.BASIC },
                    modifier = Modifier.weight(1f)
                )
                LevelFilterChip(
                    label = "Moderate (${moderateLessons.size})",
                    isSelected = selectedLevelFilter == LessonLevel.MODERATE,
                    activeColor = Color(LessonLevel.MODERATE.badgeColor),
                    onClick = { selectedLevelFilter = LessonLevel.MODERATE },
                    modifier = Modifier.weight(1.05f)
                )
                LevelFilterChip(
                    label = "Advanced (${advancedLessons.size})",
                    isSelected = selectedLevelFilter == LessonLevel.ADVANCED,
                    activeColor = Color(LessonLevel.ADVANCED.badgeColor),
                    onClick = { selectedLevelFilter = LessonLevel.ADVANCED },
                    modifier = Modifier.weight(1.05f)
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = if (selectedLevelFilter == null) "All Learning Sessions"
                       else "${selectedLevelFilter!!.displayName} Level Sessions (${displayedLessons.size})",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 4.dp)
            )

            // --- Lesson Cards List ---
            displayedLessons.forEach { lesson ->
                val isCompleted = completedIds.contains(lesson.id)
                val badgeColor = Color(lesson.level.badgeColor)

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { activeLesson = lesson }
                        .testTag("lesson_card_${lesson.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCompleted)
                            MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStrokeWrapper(
                        1.dp,
                        if (isCompleted) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.4f)
                        else badgeColor.copy(alpha = 0.25f)
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Circular Indicator for Module ID
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isCompleted) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.15f)
                                        else badgeColor.copy(alpha = 0.18f)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = lesson.id,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isCompleted) MaterialTheme.colorScheme.tertiary else badgeColor
                                )
                            }

                            Spacer(Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Level tag pill
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(badgeColor.copy(alpha = 0.15f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = lesson.level.displayName.uppercase(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = badgeColor
                                        )
                                    }
                                    Spacer(Modifier.width(6.dp))
                                    Text(
                                        text = lesson.category,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                    )
                                }

                                Spacer(Modifier.height(3.dp))

                                Text(
                                    text = lesson.title,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = lesson.summary,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1
                                )
                            }
                        }

                        // Completed Checkbox status icon
                        Icon(
                            imageVector = if (isCompleted) Icons.Default.CheckCircle
                            else Icons.Default.CheckCircleOutline,
                            contentDescription = "Completed mark status",
                            tint = if (isCompleted) MaterialTheme.colorScheme.tertiary
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            modifier = Modifier
                                .size(22.dp)
                                .clickable {
                                    viewModel.toggleLessonCompleted(lesson.id, isCompleted)
                                }
                                .testTag("lesson_toggle_${lesson.id}")
                        )
                    }
                }
            }
        } else {
            // --- Lesson Detail View ---
            val lesson = activeLesson!!
            val isCompleted = completedIds.contains(lesson.id)
            val badgeColor = Color(lesson.level.badgeColor)

            // Back button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { activeLesson = null }
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "◀  Back to course syllabus",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.testTag("lesson_detail_back")
                )
            }

            Spacer(Modifier.height(6.dp))

            // Theory Details Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(18.dp),
                border = BorderStrokeWrapper(1.dp, badgeColor.copy(alpha = 0.25f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(badgeColor.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "${lesson.level.displayName.uppercase()} LEVEL",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = badgeColor
                                    )
                                }
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = lesson.category,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Spacer(Modifier.height(6.dp))
                            Text(
                                text = "Session ${lesson.id}: ${lesson.title}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(Modifier.height(10.dp))

                    RichExplanationText(
                        text = lesson.theory,
                        textColor = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Spacer(Modifier.height(12.dp))

            // Code Example Card
            Text(
                text = "Practical Execution Query",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            CodeTerminalBlock(
                code = lesson.exampleQuery,
                label = "SQL QUERY (${lesson.id})",
                titleColor = badgeColor
            )

            // Example Result description
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.2f)
                ),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Result bulb",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Execution Use & Results:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = lesson.exampleResultExplanation,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 16.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(18.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // TRY IN TRANSLATOR
                Button(
                    onClick = {
                        viewModel.sqlInput.value = lesson.exampleQuery
                        viewModel.translationMode.value = TranslationMode.SQL_TO_NL
                        viewModel.currentScreen.value = AppScreen.TRANSLATE
                        viewModel.performTranslation()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("try_in_translator_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Try Icon",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text("TRY QUERY", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // MARK AS COMPLETE
                Button(
                    onClick = {
                        viewModel.toggleLessonCompleted(lesson.id, isCompleted)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("lesson_dialog_complete_btn"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isCompleted) MaterialTheme.colorScheme.surfaceVariant
                        else MaterialTheme.colorScheme.tertiary,
                        contentColor = if (isCompleted) MaterialTheme.colorScheme.onSurfaceVariant
                        else Color.White
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(
                        imageVector = if (isCompleted) Icons.Default.Check
                        else Icons.Default.CheckCircleOutline,
                        contentDescription = "Check Icon",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = if (isCompleted) "COMPLETED" else "MARK COMPLETE",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun LevelMiniStatCard(
    title: String,
    completed: Int,
    total: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.1f))
            .border(1.dp, color.copy(alpha = 0.25f), RoundedCornerShape(10.dp))
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "$completed/$total",
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
fun LevelFilterChip(
    label: String,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) activeColor.copy(alpha = 0.18f)
                else MaterialTheme.colorScheme.surface
            )
            .border(
                width = 1.dp,
                color = if (isSelected) activeColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                shape = RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) activeColor else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
