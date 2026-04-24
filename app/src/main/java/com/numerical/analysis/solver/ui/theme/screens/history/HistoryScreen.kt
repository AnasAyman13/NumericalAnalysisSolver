package com.numerical.analysis.solver.ui.theme.screens.history

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numerical.analysis.solver.data.HistoryEntry
import com.numerical.analysis.solver.ui.theme.screens.home.FloatingNavBar
import com.numerical.analysis.solver.ui.theme.screens.home.PrimaryBlue
import com.numerical.analysis.solver.ui.theme.state.SolverViewModel

// ─────────────────────────────────────────────────────────────────────────────
//  HistoryScreen — list of all past calculations
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun HistoryScreen(
    isDarkTheme: Boolean,
    viewModel: SolverViewModel,
    currentRoute: String,
    onNavigate: (String) -> Unit,
    // Called when the user taps a card; NavGraph navigates to "history_detail"
    onEntryClick: (HistoryEntry) -> Unit = {}
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
                // Page title
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

                if (historyEntries.isEmpty()) {
                    item { EmptyHistoryState(textMuted) }
                } else {
                    itemsIndexed(historyEntries) { index, entry ->
                        HistoryCard(
                            entry = entry,
                            isDark = isDarkTheme,
                            index = index,
                            // When the card is tapped, tell the NavGraph about it
                            onClick = { onEntryClick(entry) }
                        )
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

            FloatingNavBar(
                currentRoute = currentRoute,
                isDark = isDarkTheme,
                onNavigate = onNavigate,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  HistoryDetailScreen — shown when the user taps a history card
//
//  Shows all stored information from the database.
//  The "Re-run" button calls viewModel.loadHistoryItem(entry) which:
//    1. Parses the equation out of the subtitle string
//    2. Pre-fills the correct ViewModel state (rootFinding or optimization)
//    3. Returns the route string so we know where to navigate
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailScreen(
    viewModel: SolverViewModel,
    onBack: () -> Unit,
    // Called with the correct route after loadHistoryItem() pre-fills the state
    onRecalculate: (route: String) -> Unit
) {
    val entry by viewModel.selectedHistoryEntry.collectAsState()

    // If somehow we arrive here with nothing selected, go back immediately
    if (entry == null) {
        LaunchedEffect(Unit) { onBack() }
        return
    }

    val e = entry!!   // safe after the null check above

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Calculation Detail",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        // verticalScroll ensures nothing is cut off in landscape
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Spacer(Modifier.height(8.dp))

            // ── Accent banner ────────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(e.accentColor.copy(alpha = 0.1f))
                    .padding(20.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(e.accentColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Outlined.Calculate,
                            contentDescription = null,
                            tint = e.accentColor,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Column {
                        Text(
                            e.title,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            e.timestamp,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // ── Info card ────────────────────────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                    DetailRow(label = "Method", value = e.title, accent = e.accentColor)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    DetailRow(label = "Input", value = e.subtitle, accent = e.accentColor)
                    HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
                    DetailRow(label = "Result", value = e.result, accent = e.accentColor, highlight = true)
                }
            }

            // ── Note about iteration table ───────────────────────────────────
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = e.accentColor.copy(alpha = 0.08f)
                ),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        Icons.Outlined.History,
                        contentDescription = null,
                        tint = e.accentColor,
                        modifier = Modifier.size(20.dp).padding(top = 2.dp)
                    )
                    Text(
                        text = "The full iteration table is available by re-running the calculation. " +
                               "Tap the button below to pre-fill the solver and run it again.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 20.sp
                    )
                }
            }

            // ── Re-run button ────────────────────────────────────────────────
            // loadHistoryItem(e) does two things:
            //   1. Pre-fills the equation (and isMax for optimization) into the ViewModel
            //   2. Returns the correct route string ("root_finding", "golden_section", etc.)
            // We then call onRecalculate(route) so the NavGraph navigates there.
            Button(
                onClick = {
                    val route = viewModel.loadHistoryItem(e)
                    onRecalculate(route)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = e.accentColor)
            ) {
                Icon(Icons.Outlined.Calculate, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
                Text("Re-run Calculation", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }


            Spacer(Modifier.height(8.dp))
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  Individual row inside the detail card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun DetailRow(label: String, value: String, accent: Color, highlight: Boolean = false) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.2.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            value,
            fontSize = if (highlight) 22.sp else 15.sp,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
            color = if (highlight) accent else MaterialTheme.colorScheme.onSurface
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
//  History entry card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun HistoryCard(
    entry: HistoryEntry,
    isDark: Boolean,
    index: Int,
    onClick: () -> Unit
) {
    val cardBg = MaterialTheme.colorScheme.surface
    val textPrimary = MaterialTheme.colorScheme.onSurface
    val textMuted = MaterialTheme.colorScheme.onSurfaceVariant

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
            )
            // Makes the whole card tappable — shows ripple on press
            .clickable(onClick = onClick),
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

            Column(modifier = Modifier.weight(1f)) {
                Text(entry.title,    fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = textPrimary)
                Text(entry.subtitle, fontSize = 12.sp, color = textMuted)
                Spacer(Modifier.height(6.dp))
                Text(entry.result,   fontSize = 13.sp, fontWeight = FontWeight.Medium, color = entry.accentColor)
            }

            Box(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
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

// ─────────────────────────────────────────────────────────────────────────────
//  Empty state
// ─────────────────────────────────────────────────────────────────────────────
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
        Text("No calculations yet",      fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = textMuted)
        Text(
            "Solve something and your results\nwill appear here.",
            fontSize = 13.sp,
            color = textMuted.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
}
