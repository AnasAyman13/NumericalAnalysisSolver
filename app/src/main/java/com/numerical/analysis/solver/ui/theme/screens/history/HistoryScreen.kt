package com.numerical.analysis.solver.ui.theme.screens.history

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numerical.analysis.solver.data.HistoryEntry
import com.numerical.analysis.solver.ui.theme.screens.home.FloatingNavBar
import com.numerical.analysis.solver.ui.theme.screens.home.PrimaryBlue
import com.numerical.analysis.solver.ui.theme.state.SolverViewModel




// ─────────────────────────────────────────────────────────────
//  HistoryScreen
// ─────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    isDarkTheme: Boolean,
    viewModel: SolverViewModel,
    currentRoute: String,
    onNavigate: (String) -> Unit
) {
    val bgColor = MaterialTheme.colorScheme.background
    val textPrimary = MaterialTheme.colorScheme.onSurface
    val textMuted = MaterialTheme.colorScheme.onSurfaceVariant

    val historyEntries by viewModel.history.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadHistory()
    }

    Scaffold(
        containerColor = bgColor,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize()) {

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                contentPadding = PaddingValues(top = 24.dp, bottom = 120.dp)
            ) {
                // Header
                item {
                    Text(
                        text = "HISTORY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryBlue,
                        letterSpacing = 1.5.sp
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "Past Calculations",
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = textPrimary,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                }

                // Check if empty
                if (historyEntries.isEmpty()) {
                    item { EmptyHistoryState(textMuted) }
                } else {
                    itemsIndexed(historyEntries) { index, entry ->
                        HistoryCard(entry = entry, isDark = isDarkTheme, index = index)
                        Spacer(Modifier.height(12.dp))
                    }
                }

                item {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Showing last ${historyEntries.size} calculations",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = textMuted,
                        fontSize = 12.sp
                    )
                }
            }

            // Floating nav bar
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
//  Individual history entry card
// ─────────────────────────────────────────────────────────────
@Composable
private fun HistoryCard(entry: HistoryEntry, isDark: Boolean, index: Int) {
    val cardBg = MaterialTheme.colorScheme.surface
    val textPrimary = MaterialTheme.colorScheme.onSurface
    val textMuted = MaterialTheme.colorScheme.onSurfaceVariant
    val divider = MaterialTheme.colorScheme.surfaceVariant

    // Staggered entrance via alpha — cheap and effective
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { visible = true }
    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 350, delayMillis = index * 60),
        label = "entryAlpha"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(alpha)
            .shadow(
                elevation = if (isDark) 4.dp else 10.dp,
                shape = RoundedCornerShape(16.dp),
                spotColor = entry.accentColor.copy(alpha = 0.12f)
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Coloured icon badge
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(entry.accentColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Calculate,
                    contentDescription = null,
                    tint = entry.accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Text block
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = textPrimary
                )
                Text(
                    text = entry.subtitle,
                    fontSize = 12.sp,
                    color = textMuted
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = entry.result,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = entry.accentColor
                )
            }

            // Timestamp pill
            Box(
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = entry.timestamp,
                    fontSize = 10.sp,
                    color = textMuted,
                    lineHeight = 14.sp,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────
//  Empty state
// ─────────────────────────────────────────────────────────────
@Composable
private fun EmptyHistoryState(textMuted: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(PrimaryBlue.copy(alpha = 0.08f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.History,
                contentDescription = null,
                tint = PrimaryBlue.copy(alpha = 0.4f),
                modifier = Modifier.size(40.dp)
            )
        }
        Text(
            text = "No calculations yet",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = textMuted
        )
        Text(
            text = "Solve something and your results\nwill appear here.",
            fontSize = 13.sp,
            color = textMuted.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}
