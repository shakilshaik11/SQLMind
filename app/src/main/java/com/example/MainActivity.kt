package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Quiz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.QuizScreen
import com.example.ui.screens.TranslateScreen
import com.example.ui.screens.WorkspaceScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.SqlViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: SqlViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Edge to edge immersive displays
        enableEdgeToEdge()

        setContent {
            val isDark = viewModel.isDarkMode.value
            val isBooting = remember { mutableStateOf(true) }

            MyApplicationTheme(darkTheme = isDark) {
                AnimatedContent(
                    targetState = isBooting.value,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "boot_trans"
                ) { booting ->
                    if (booting) {
                        FuturisticBootingScreen(onBootComplete = { isBooting.value = false })
                    } else {
                        Scaffold(
                            modifier = Modifier.fillMaxSize(),
                            topBar = { TopHeaderBar(viewModel) },
                            bottomBar = { BottomNavBar(viewModel) },
                            contentWindowInsets = WindowInsets(0, 0, 0, 0)
                        ) { innerPadding ->
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(top = innerPadding.calculateTopPadding())
                                    .background(MaterialTheme.colorScheme.background)
                            ) {
                                AnimatedContent(
                                    targetState = viewModel.currentScreen.value,
                                    transitionSpec = {
                                        if (targetState.ordinal > initialState.ordinal) {
                                            (slideInHorizontally { width -> width } + fadeIn()) togetherWith
                                                    (slideOutHorizontally { width -> -width } + fadeOut())
                                        } else {
                                            (slideInHorizontally { width -> -width } + fadeIn()) togetherWith
                                                    (slideOutHorizontally { width -> width } + fadeOut())
                                        }.using(SizeTransform(clip = false))
                                    },
                                    label = "screen_trans"
                                ) { targetScreen ->
                                    when (targetScreen) {
                                        AppScreen.TRANSLATE -> TranslateScreen(viewModel)
                                        AppScreen.LEARN -> LearnScreen(viewModel)
                                        AppScreen.QUIZ -> QuizScreen(viewModel)
                                        AppScreen.WORKSPACE -> WorkspaceScreen(viewModel)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 1-Second Futuristic Booting Splash Screen
 */
@Composable
fun FuturisticBootingScreen(onBootComplete: () -> Unit) {
    val progress = remember { androidx.compose.animation.core.Animatable(0f) }

    LaunchedEffect(Unit) {
        progress.animateTo(
            targetValue = 1f,
            animationSpec = androidx.compose.animation.core.tween(durationMillis = 1000, easing = androidx.compose.animation.core.LinearEasing)
        )
        onBootComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B0813)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_violet),
                contentDescription = "SQLMind Logo",
                modifier = Modifier.size(110.dp)
            )
            Spacer(Modifier.height(24.dp))
            Text(
                text = "SQLMIND",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White,
                letterSpacing = 3.sp
            )
            Text(
                text = "FUTURISTIC AI SQL ENGINE",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA855F7),
                letterSpacing = 2.sp
            )
            Spacer(Modifier.height(36.dp))
            
            // Neon progress bar
            Box(
                modifier = Modifier
                    .width(220.dp)
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(Color(0xFF241847))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress.value)
                        .background(
                            androidx.compose.ui.graphics.Brush.horizontalGradient(
                                colors = listOf(Color(0xFF8B5CF6), Color(0xFFA855F7), Color(0xFF38BDF8))
                            )
                        )
                )
            }
            
            Spacer(Modifier.height(16.dp))
            Text(
                text = "INITIALIZING CORE ENGINE... ${(progress.value * 100).toInt()}%",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                letterSpacing = 1.sp
            )
        }
    }
}

/**
 * Highly styled immersive top bar showing dark mode togglers and identity branding
 */
@Composable
fun TopHeaderBar(viewModel: SqlViewModel) {
    val isDark = viewModel.isDarkMode.value
    val statusBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("app_top_bar"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = statusBarPadding + 12.dp, bottom = 14.dp, start = 16.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Identity with Violet Logo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = R.drawable.logo_violet),
                    contentDescription = "SQLMind Logo",
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(12.dp))
                )
                Spacer(Modifier.width(12.dp))
                Column {
                    Text(
                        text = "SQLMind",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = FontFamily.SansSerif
                    )
                    Text(
                        text = "AI SQL Engine & Translator",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Dark/Light Mode Toggler
            IconButton(
                onClick = { viewModel.toggleDarkMode() },
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(if (isDark) Color(0xFF1E1E1E) else MaterialTheme.colorScheme.surfaceVariant)
                    .testTag("dark_mode_toggle")
            ) {
                Icon(
                    imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Toggle Dark Mode theme",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * Standard Material 3 dynamic Bottom Navigation Bar conforming with Notch and gesture safes
 */
@Composable
fun BottomNavBar(viewModel: SqlViewModel) {
    val navPadding = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    NavigationBar(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp + navPadding)
            .testTag("app_bottom_nav_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        val currentScreen = viewModel.currentScreen.value

        NavigationBarItem(
            icon = { Icon(Icons.Default.Code, contentDescription = "Translator") },
            label = { Text("Translator", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            selected = currentScreen == AppScreen.TRANSLATE,
            onClick = { viewModel.currentScreen.value = AppScreen.TRANSLATE },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.School, contentDescription = "Learn") },
            label = { Text("Learn", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            selected = currentScreen == AppScreen.LEARN,
            onClick = { viewModel.currentScreen.value = AppScreen.LEARN },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Quiz, contentDescription = "Quiz") },
            label = { Text("Quiz", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            selected = currentScreen == AppScreen.QUIZ,
            onClick = { viewModel.currentScreen.value = AppScreen.QUIZ },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Folder, contentDescription = "Workspace") },
            label = { Text("Workspace", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
            selected = currentScreen == AppScreen.WORKSPACE,
            onClick = { viewModel.currentScreen.value = AppScreen.WORKSPACE },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = MaterialTheme.colorScheme.primary,
                selectedTextColor = MaterialTheme.colorScheme.primary,
                indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
            )
        )
    }
}
