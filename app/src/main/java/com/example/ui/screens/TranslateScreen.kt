package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SettingsBackupRestore
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SqlViewModel
import com.example.ui.viewmodel.TranslationMode

@Composable
fun TranslateScreen(viewModel: SqlViewModel) {
    val scrollState = rememberScrollState()
    val isDark = viewModel.isDarkMode.value
    var dbDropdownExpanded by remember { mutableStateOf(false) }

    ResponsiveContainer(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(bottom = 64.dp) // buffer spacing above bottom navigation
    ) {
        // --- Dialect & Tab Mode Selector ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab Toggle
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (viewModel.translationMode.value == TranslationMode.NL_TO_SQL)
                                MaterialTheme.colorScheme.surfaceVariant
                            else Color.Transparent
                        )
                        .clickable { viewModel.translationMode.value = TranslationMode.NL_TO_SQL }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("tab_nl_to_sql")
                ) {
                    Text(
                        text = "NL to SQL",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (viewModel.translationMode.value == TranslationMode.NL_TO_SQL)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (viewModel.translationMode.value == TranslationMode.SQL_TO_NL)
                                MaterialTheme.colorScheme.surfaceVariant
                            else Color.Transparent
                        )
                        .clickable { viewModel.translationMode.value = TranslationMode.SQL_TO_NL }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .testTag("tab_sql_to_nl")
                ) {
                    Text(
                        text = "SQL to NL",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (viewModel.translationMode.value == TranslationMode.SQL_TO_NL)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // DB Dropdown Choice
            Box {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable { dbDropdownExpanded = true }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = "Database Icon",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = viewModel.selectedDbType.value,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                DropdownMenu(
                    expanded = dbDropdownExpanded,
                    onDismissRequest = { dbDropdownExpanded = false }
                ) {
                    viewModel.databaseTypes.forEach { db ->
                        DropdownMenuItem(
                            text = { Text(db, fontSize = 13.sp) },
                            onClick = {
                                viewModel.selectedDbType.value = db
                                dbDropdownExpanded = false
                            },
                            modifier = Modifier.testTag("db_option_$db")
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(12.dp))

        // --- Translation Text Fields ---
        if (viewModel.translationMode.value == TranslationMode.NL_TO_SQL) {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Describe your query in plain language:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = viewModel.nlInput.value,
                    onValueChange = { viewModel.nlInput.value = it },
                    placeholder = {
                        Text(
                            "e.g. Find employee IDs, names and project names for workers who completed more than 5 assignments this quarter, ordering by name.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("nl_input_field"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Input raw SQL code:",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 6.dp)
                )

                OutlinedTextField(
                    value = viewModel.sqlInput.value,
                    onValueChange = { viewModel.sqlInput.value = it },
                    placeholder = {
                        Text(
                            "e.g. SELECT u.id, SUM(o.amount) FROM users u INNER JOIN orders o ON u.id = o.user_id GROUP BY u.id HAVING SUM(o.amount) > 1500;",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("sql_input_field"),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                )
            }
        }

        Spacer(Modifier.height(14.dp))

        // --- SUBMIT COMPILER BUTTON ---
        Button(
            onClick = { viewModel.performTranslation() },
            enabled = !viewModel.translateLoading.value,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("translate_submit_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            if (viewModel.translateLoading.value) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.White
                )
                Spacer(Modifier.width(8.dp))
                Text("Analyzing database constraints...", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            } else {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Translate",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = if (viewModel.translationMode.value == TranslationMode.NL_TO_SQL) "COMPILE TO SQL" else "TRANSLATE TO ENGLISH",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(Modifier.height(18.dp))

        // --- OUTPUT GRAPHIC RESULTS ---
        AnimatedVisibility(
            visible = (viewModel.translationMode.value == TranslationMode.NL_TO_SQL && viewModel.resultSql.value.isNotEmpty()) ||
                    (viewModel.translationMode.value == TranslationMode.SQL_TO_NL && viewModel.resultNlTranslation.value.isNotEmpty()),
            enter = fadeIn() + slideInVertically(initialOffsetY = { 60 }),
            exit = fadeOut()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                if (viewModel.translationMode.value == TranslationMode.NL_TO_SQL) {
                    // SQL output
                    CodeTerminalBlock(
                        code = viewModel.resultSql.value,
                        label = "GENERATED ${viewModel.selectedDbType.value.uppercase()} QUERY",
                        titleColor = MaterialTheme.colorScheme.primary
                    )

                    Spacer(Modifier.height(10.dp))

                    // Exploit Use
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStrokeWrapper(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Explanation",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Explanation & Mechanics",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            RichExplanationText(
                                text = viewModel.resultExplanation.value,
                                textColor = MaterialTheme.colorScheme.onSurface
                                    .copy(alpha = 0.9f)
                            )
                        }
                    }
                } else {
                    // SQL TO NL Output
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStrokeWrapper(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Translate,
                                    contentDescription = "Interpretation",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Natural Language Translation",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(Modifier.height(8.dp))
                            Text(
                                text = viewModel.resultNlTranslation.value,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // SQL TO NL Detailed Breakdown
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStrokeWrapper(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.18f)),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.HelpOutline,
                                    contentDescription = "Breakdown",
                                    tint = MaterialTheme.colorScheme.secondary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    text = "Detailed Breakdown",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(Modifier.height(10.dp))
                            RichExplanationText(
                                text = viewModel.resultNlExplanation.value,
                                textColor = MaterialTheme.colorScheme.onSurface
                                    .copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }

        // Default Tutorial Tip if results empty
        if ((viewModel.translationMode.value == TranslationMode.NL_TO_SQL && viewModel.resultSql.value.isEmpty()) ||
            (viewModel.translationMode.value == TranslationMode.SQL_TO_NL && viewModel.resultNlTranslation.value.isEmpty())) {
            
            Spacer(Modifier.height(40.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(12.dp),
                border = BorderStrokeWrapper(1.dp, MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Translation Tip",
                        tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                        modifier = Modifier.size(36.dp)
                    )
                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = "Pro Tip: Dialect Support & Subqueries",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "SQL Translator supports complex nested queries, multitable joins, and aggregations (GROUP BY / HAVING) for MySQL, PostgreSQL, SQLite, and Oracle databases. Simply describe the tables, columns, and target output clearly above.",
                        fontSize = 11.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
