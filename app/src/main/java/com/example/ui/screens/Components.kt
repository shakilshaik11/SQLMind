package com.example.ui.screens

import com.example.ui.theme.HighlightCyan

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Centered responsive container that limits width on wide screens (tablets) for visual polish.
 */
@Composable
fun ResponsiveContainer(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScopeWrapper.() -> Unit
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 680.dp)
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            val wrapper = ColumnScopeWrapper(this)
            wrapper.content()
        }
    }
}

class ColumnScopeWrapper(private val columnScope: ColumnScopeInstance) {
    // Expose column delegate powers if needed
}

typealias ColumnScopeInstance = androidx.compose.foundation.layout.ColumnScope

/**
 * Displays SQL code inside a glowing terminal window with code highlighting and a Copy button.
 */
@Composable
fun CodeTerminalBlock(
    code: String,
    modifier: Modifier = Modifier,
    label: String = "SQL QUERY",
    titleColor: Color = MaterialTheme.colorScheme.primary
) {
    val context = LocalContext.current
    val isDark = MaterialTheme.colorScheme.background == com.example.ui.theme.DarkBg

    val highlightedCode = buildAnnotatedString {
        val keywords = listOf(
            "SELECT", "FROM", "WHERE", "INNER JOIN", "LEFT JOIN", "RIGHT JOIN", "JOIN",
            "ON", "GROUP BY", "HAVING", "ORDER BY", "LIMIT", "OFFSET", "AND", "OR", "NOT",
            "IN", "BETWEEN", "LIKE", "AS", "INSERT", "INTO", "VALUES", "UPDATE", "SET", "DELETE",
            "COUNT", "SUM", "AVG", "MIN", "MAX", "WITH", "RECURSIVE", "EXECUTE", "EXPLAIN", "DISTINCT"
        )
        
        val lines = code.split("\n")
        lines.forEachIndexed { lineIdx, line ->
            var remaining = line
            while (remaining.isNotEmpty()) {
                var foundIdx = -1
                var foundWord = ""
                for (word in keywords) {
                    val idx = remaining.indexOf(word)
                    if (idx != -1) {
                        if (foundIdx == -1 || idx < foundIdx) {
                            foundIdx = idx
                            foundWord = word
                        }
                    }
                }

                if (foundIdx != -1) {
                    // Append raw text before the matched keyword
                    append(remaining.substring(0, foundIdx))
                    withStyle(style = SpanStyle(
                        color = if (isDark) HighlightCyan else MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )) {
                        append(foundWord)
                    }
                    remaining = remaining.substring(foundIdx + foundWord.length)
                } else {
                    append(remaining)
                    remaining = ""
                }
            }
            if (lineIdx < lines.size - 1) {
                append("\n")
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .testTag("code_terminal_block"),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color(0x66000000) else Color(0xFF1E293B)
        ),
        shape = RoundedCornerShape(12.dp),
        border = BorderStrokeWrapper(1.dp, if (isDark) Color(0xFF8B5CF6).copy(alpha = 0.45f) else Color(0xFF7C3AED).copy(alpha = 0.6f))
    ) {
        Column {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDark) Color(0x22000000) else Color(0xFF0F172A))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Three colored dots resembling a mac window
                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Box(Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFFF5F56)))
                        Box(Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFFFFBD2E)))
                        Box(Modifier.size(8.dp).clip(RoundedCornerShape(4.dp)).background(Color(0xFF27C93F)))
                    }
                    Spacer(Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = "Terminal",
                        tint = titleColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = label,
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
                IconButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("SQL_Code", code)
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, "Query copied to clipboard!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.size(24.dp).testTag("copy_code_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy SQL",
                        tint = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            // Code Content
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Text(
                    text = highlightedCode,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontFamily = FontFamily.Monospace,
                    lineHeight = 20.sp
                )
            }
        }
    }
}

/**
 * Custom function to bypass Border constructor types
 */
@Composable
fun BorderStrokeWrapper(width: androidx.compose.ui.unit.Dp = 1.dp, color: Color) = 
    androidx.compose.foundation.BorderStroke(width, color)

/**
 * Explains terms using a highlight format (e.g., converts custom markdown bold to beautiful spans).
 */
@Composable
fun RichExplanationText(
    text: String,
    modifier: Modifier = Modifier,
    textColor: Color = MaterialTheme.colorScheme.onSurface
) {
    val parsed = buildAnnotatedString {
        var cursor = 0
        val regex = Regex("(\\*\\*|`)(.*?)\\1") // matches **bold** or `code`
        
        val matches = regex.findAll(text)
        matches.forEach { match ->
            // Text before match
            append(text.substring(cursor, match.range.first))
            
            val type = match.groupValues[1]
            val value = match.groupValues[2]
            
            if (type == "**") {
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)) {
                    append(value)
                }
            } else if (type == "`") {
                withStyle(style = SpanStyle(
                    fontWeight = FontWeight.Bold, 
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.secondary,
                    background = MaterialTheme.colorScheme.surfaceVariant
                )) {
                    append(" $value ")
                }
            }
            cursor = match.range.last + 1
        }
        if (cursor < text.length) {
            append(text.substring(cursor))
        }
    }

    Text(
        text = parsed,
        color = textColor,
        fontSize = 14.sp,
        lineHeight = 22.sp,
        modifier = modifier
    )
}

/**
 * Animated expander/accordion helper
 */
@Composable
fun AccordionSection(
    title: String,
    expanded: Boolean,
    onHeaderClick: () -> Unit,
    modifier: Modifier = Modifier,
    statusBadge: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onHeaderClick() }
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = title,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (statusBadge != null) {
                        Spacer(Modifier.width(8.dp))
                        statusBadge()
                    }
                }
                Text(
                    text = if (expanded) "▲" else "▼",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                        .padding(14.dp)
                ) {
                    content()
                }
            }
        }
    }
}
