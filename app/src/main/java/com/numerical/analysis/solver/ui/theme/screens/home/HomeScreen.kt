package com.numerical.analysis.solver.ui.theme.screens.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.Dataset
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numerical.analysis.solver.data.ChapterData
import com.numerical.analysis.solver.data.getChaptersList
import com.numerical.analysis.solver.ui.theme.components.MathBackground

import com.numerical.analysis.solver.ui.theme.*

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
    val bgColor  = MaterialTheme.colorScheme.background
    val textPrimary = MaterialTheme.colorScheme.onSurface

    Scaffold(containerColor = bgColor) { padding ->

        Box(modifier = Modifier.fillMaxSize()) {
            MathBackground()

            LazyColumn(
                modifier        = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 20.dp),
                contentPadding  = PaddingValues(top = 24.dp, bottom = 120.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Row(
                        modifier              = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text          = "DASHBOARD",
                                fontSize      = 11.sp,
                                fontWeight    = FontWeight.Bold,
                                color         = PrimaryColor,
                                letterSpacing = 2.sp
                            )
                            Text(
                                text       = "Numerical Solver",
                                fontSize   = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color      = textPrimary
                            )
                        }
                        ThemeToggleSwitch(isDark = isDarkTheme, onToggle = onToggleTheme)
                    }
                }

                items(chapters) { chapter ->
                    GlassChapterCard(
                        chapter   = chapter,
                        isDark    = isDarkTheme,
                        onClick   = { onChapterClick(chapter.title) }
                    )
                }

                item {
                    Text(
                        text      = "MTI University · CS-252",
                        modifier  = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color     = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize  = 12.sp
                    )
                }
            }

            FloatingNavBar(
                currentRoute = currentRoute,
                isDark       = isDarkTheme,
                onNavigate   = onNavigate,
                modifier     = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Glassmorphism Chapter Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun GlassChapterCard(chapter: ChapterData, isDark: Boolean, onClick: () -> Unit) {

    // Scale animation on press — shrinks to 0.95 and springs back
    var isPressed by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue    = if (isPressed) 0.95f else 1f,
        animationSpec  = spring(dampingRatio = 0.5f, stiffness = 600f),
        label          = "cardScale"
    )

    val glassColor  = if (isDark) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
                      else        MaterialTheme.colorScheme.surface.copy(alpha = 0.80f)
    val borderColor = if (isDark) MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
                      else        MaterialTheme.colorScheme.outline.copy(alpha = 0.9f)
    val onSurface   = MaterialTheme.colorScheme.onSurface
    val onVariant   = MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .scale(cardScale)
            .shadow(
                elevation  = if (isDark) 6.dp else 14.dp,
                shape      = RoundedCornerShape(20.dp),
                spotColor  = PrimaryColor.copy(alpha = 0.18f),
                ambientColor = Color.Black.copy(alpha = 0.10f)
            )
            .clip(RoundedCornerShape(20.dp))
            .background(glassColor)
            .border(1.dp, borderColor, RoundedCornerShape(20.dp))
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress   = { isPressed = true; tryAwaitRelease(); isPressed = false },
                    onTap     = { onClick() }
                )
            }
    ) {
        Column {
            // ── Soft Gradient Banner with Large Math Symbol ──────────────────
            val bannerGradient = when {
                chapter.title.contains("Root", true) -> listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                chapter.title.contains("Linear", true) -> listOf(MaterialTheme.colorScheme.secondary, MaterialTheme.colorScheme.tertiary)
                else -> listOf(MaterialTheme.colorScheme.tertiary, MaterialTheme.colorScheme.error)
            }
            val bgText = when {
                chapter.title.contains("Root", true) -> "∫"
                chapter.title.contains("Linear", true) -> "∑"
                else -> "ƒ"
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp)
                    .background(Brush.linearGradient(bannerGradient))
                    .clipToBounds(),
                contentAlignment = Alignment.Center
            ) {
                // Large semi-transparent math icon in background
                Text(
                    text = bgText,
                    fontSize = 120.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.15f),
                    modifier = Modifier.offset(x = 60.dp, y = 20.dp)
                )

                // Foreground icon
                Icon(chapter.icon, null, tint = Color.White, modifier = Modifier.size(36.dp))
            }

            // ── Body ────────────────────────────────────────────────────────
            Column(Modifier.padding(horizontal = 18.dp, vertical = 14.dp)) {

                Text(
                    text       = chapter.title,
                    fontSize   = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color      = onSurface
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    text       = chapter.description,
                    color      = onVariant,
                    fontSize   = 13.sp,
                    lineHeight = 18.sp
                )

                Spacer(Modifier.height(12.dp))

                // ── Method chips (FlowRow — all visible, no scrolling) ─────
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement   = Arrangement.spacedBy(6.dp)
                ) {
                    chapter.tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .background(PrimaryColor.copy(alpha = 0.10f), RoundedCornerShape(6.dp))
                                .border(1.dp, PrimaryColor.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text       = tag,
                                fontSize   = 11.sp,
                                color      = PrimaryColor,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(Modifier.height(10.dp))

                // ── Open arrow ───────────────────────────────────────────────
                Row(
                    modifier              = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment     = Alignment.CenterVertically
                ) {
                    Text(
                        text       = "Open",
                        fontSize   = 13.sp,
                        color      = PrimaryColor,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(Modifier.width(4.dp))
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint               = PrimaryColor,
                        modifier           = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Floating pill nav bar
// ─────────────────────────────────────────────────────────────────────────────
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

    val pillBg        = MaterialTheme.colorScheme.surface
    val unselectedIcon = MaterialTheme.colorScheme.onSurfaceVariant

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
                    elevation    = 24.dp,
                    shape        = RoundedCornerShape(50),
                    spotColor    = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                    ambientColor = Color.Black.copy(alpha = 0.12f)
                )
                .clip(RoundedCornerShape(50))
                .background(pillBg)
                .padding(horizontal = 8.dp, vertical = 10.dp)
        ) {
            Row(
                modifier              = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment     = Alignment.CenterVertically
            ) {
                items.forEach { item ->
                    val selected = currentRoute == item.route

                    val iconTint by animateColorAsState(
                        targetValue   = if (selected) PrimaryColor else unselectedIcon,
                        animationSpec = tween(250), label = "iconTint"
                    )
                    val labelAlpha by animateFloatAsState(
                        targetValue   = if (selected) 1f else 0.75f,
                        animationSpec = tween(250), label = "labelAlpha"
                    )

                    Column(
                        modifier = Modifier
                            .clip(RoundedCornerShape(40))
                            .clickable { onNavigate(item.route) }
                            .padding(horizontal = 20.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(width = if (selected) 24.dp else 6.dp, height = 4.dp)
                                .clip(RoundedCornerShape(50))
                                .background(if (selected) PrimaryColor else Color.Transparent)
                        )
                        Icon(
                            imageVector   = item.icon,
                            contentDescription = item.label,
                            tint          = iconTint,
                            modifier      = Modifier.size(24.dp)
                        )
                        Text(
                            text       = item.label,
                            fontSize   = 11.sp,
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                            color      = if (selected) PrimaryColor else unselectedIcon,
                            modifier   = Modifier.alpha(labelAlpha)
                        )
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Dark / Light toggle switch
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun ThemeToggleSwitch(isDark: Boolean, onToggle: () -> Unit) {
    val trackColor by animateColorAsState(
        targetValue   = MaterialTheme.colorScheme.surfaceVariant,
        animationSpec = tween(300), label = "trackColor"
    )
    val thumbColor by animateColorAsState(
        targetValue   = MaterialTheme.colorScheme.primary,
        animationSpec = tween(300), label = "thumbColor"
    )
    val thumbOffset by animateDpAsState(
        targetValue   = if (isDark) 24.dp else 0.dp,
        animationSpec = spring(dampingRatio = 0.6f, stiffness = 300f),
        label         = "thumbOffset"
    )

    Box(
        modifier = Modifier
            .width(56.dp)
            .height(32.dp)
            .shadow(4.dp, RoundedCornerShape(50), spotColor = PrimaryColor.copy(alpha = 0.2f))
            .clip(RoundedCornerShape(50))
            .background(trackColor)
            .clickable(onClick = onToggle),
        contentAlignment = Alignment.CenterStart
    ) {
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
                imageVector        = if (isDark) Icons.Outlined.DarkMode else Icons.Outlined.LightMode,
                contentDescription = if (isDark) "Switch to Light" else "Switch to Dark",
                tint               = MaterialTheme.colorScheme.onPrimary,
                modifier           = Modifier.size(16.dp)
            )
        }
    }
}