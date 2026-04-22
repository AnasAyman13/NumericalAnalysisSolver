package com.numerical.analysis.solver.ui.theme.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numerical.analysis.solver.data.ChapterData
import com.numerical.analysis.solver.data.getChaptersList
import com.numerical.analysis.solver.ui.theme.LocalDarkTheme

val PrimaryBlue = Color(0xFF1586EF)
val GradientEnd = Color(0xFF4AC29A)

// ─────────────────────────────────────────────────────────────
//  Main screen
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentRoute: String,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onNavigate: (String) -> Unit,
    onChapterClick: (String) -> Unit
) {
    val chapters = getChaptersList()

    // Adaptive colours
    val bgColor = MaterialTheme.colorScheme.background
    val textPrimary = MaterialTheme.colorScheme.onSurface

    Scaffold(
        containerColor = bgColor,
        // Remove bottomBar from Scaffold — we'll draw the floating bar ourselves
    ) { padding ->

        Box(modifier = Modifier.fillMaxSize()) {

            // ── Scrollable content ──────────────────────────────────────
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 120.dp) // bottom room for floating bar
            ) {
                // ── Header row with title + theme toggle ─────────────────
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "DASHBOARD",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = PrimaryBlue,
                                letterSpacing = 1.5.sp
                            )
                            Text(
                                text = "Numerical Solver",
                                fontSize = 30.sp,
                                fontWeight = FontWeight.Bold,
                                color = textPrimary
                            )
                        }

                        // ── Dark / Light Toggle ───────────────────────────
                        ThemeToggleSwitch(isDark = isDarkTheme, onToggle = onToggleTheme)
                    }
                    Spacer(Modifier.height(20.dp))
                }

                items(chapters) { chapter ->
                    PremiumChapterCard(
                        chapter = chapter,
                        isDark  = isDarkTheme,
                        onClick = { onChapterClick(chapter.title) }
                    )
                    Spacer(Modifier.height(20.dp))
                }

                item {
                    Text(
                        text = "MTI University - CS-252",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }

            // ── Floating Pill Navigation Bar ────────────────────────────
            FloatingNavBar(
                currentRoute = currentRoute,
                isDark = isDarkTheme,
                onNavigate = onNavigate,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Floating pill nav bar
// ─────────────────────────────────────────────────────────────
private data class NavItem(val route: String, val icon: ImageVector, val label: String)

@Composable
fun FloatingNavBar(
    currentRoute: String,
    isDark: Boolean,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        NavItem("home",    Icons.Outlined.Home,    "Home"),
        NavItem("history", Icons.Outlined.History, "History"),
        NavItem("about",   Icons.Outlined.Info,    "About")
    )

    val pillBg = MaterialTheme.colorScheme.surface
    val unselectedIcon = MaterialTheme.colorScheme.onSurfaceVariant
    val unselectedText = unselectedIcon

    Box(
        modifier = modifier
            .padding(horizontal = 24.dp)
            .padding(bottom = 28.dp)
            .navigationBarsPadding()
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(50),
                    spotColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    ambientColor = Color.Black.copy(alpha = 0.12f)
                )
                .clip(RoundedCornerShape(50))
                .background(pillBg)
                .padding(horizontal = 8.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val selected = currentRoute == item.route

                    val iconTint by animateColorAsState(
                        targetValue = if (selected) PrimaryBlue else unselectedIcon,
                        animationSpec = tween(250), label = "iconTint"
                    )
                    val labelAlpha by animateFloatAsState(
                        targetValue = if (selected) 1f else 0.75f,
                        animationSpec = tween(250), label = "labelAlpha"
                    )

                    // Indicator + icon + label column
                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(40))
                            .clickable { onNavigate(item.route) }
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        // Blue pill indicator dot
                        Box(
                            modifier = Modifier
                                .size(width = if (selected) 24.dp else 6.dp, height = 4.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (selected) PrimaryBlue else Color.Transparent)
                        )

                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            tint = iconTint,
                            modifier = Modifier.size(24.dp)
                        )

                        Text(
                            text = item.label,
                            fontSize = 11.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (selected) PrimaryBlue else unselectedText,
                            modifier = Modifier.alpha(labelAlpha)
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Dark / Light toggle switch
// ─────────────────────────────────────────────────────────────
@Composable
fun ThemeToggleSwitch(isDark: Boolean, onToggle: () -> Unit) {
    val trackColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(300), label = "trackColor"
    )
    val thumbColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.primary,
        animationSpec = tween(300), label = "thumbColor"
    )
    val thumbOffset by animateDpAsState(
        targetValue = if (isDark) 24.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label = "thumbOffset"
    )

    Box(
        modifier = Modifier
            .width(56.dp)
            .height(32.dp)
            .shadow(4.dp, RoundedCornerShape(50), spotColor = PrimaryBlue.copy(alpha = 0.2f))
            .clip(RoundedCornerShape(50))
            .background(trackColor)
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.CenterStart
    ) {
        // Thumb
        Box(
            modifier = Modifier
                .padding(start = 3.dp)
                .offset(x = thumbOffset)
                .size(26.dp)
                .clip(CircleShape)
                .background(thumbColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = if (isDark) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                contentDescription = if (isDark) "Switch to Light Mode" else "Switch to Dark Mode",
                tint = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Chapter card
// ─────────────────────────────────────────────────────────────
@Composable
fun PremiumChapterCard(chapter: ChapterData, isDark: Boolean, onClick: () -> Unit) {
    val cardBg = MaterialTheme.colorScheme.surface
    val textPrimary = MaterialTheme.colorScheme.onSurface
    val textSecondary = MaterialTheme.colorScheme.onSurfaceVariant
    val tagBg = MaterialTheme.colorScheme.surfaceVariant

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = if (isDark) 8.dp else 16.dp,
                shape = RoundedCornerShape(24.dp),
                spotColor = PrimaryBlue.copy(alpha = if (isDark) 0.25f else 0.15f)
            )
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .background(cardBg),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            if (!chapter.isBonus && chapter.imageRes != null) {
                Box(
                    modifier = Modifier
                        .height(140.dp)
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = chapter.imageRes),
                        contentDescription = chapter.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                )
                            )
                    )
                    Text(
                        text = chapter.title,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PrimaryBlue, GradientEnd)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(chapter.icon, null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
                Text(
                    text = chapter.title,
                    modifier = Modifier.padding(start = 24.dp, top = 20.dp),
                    color = textPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Column(Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                Text(
                    text = chapter.description,
                    color = textSecondary,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                Spacer(Modifier.height(20.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    chapter.tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .background(tagBg, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                tag,
                                fontSize = 12.sp,
                                color = PrimaryBlue,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}