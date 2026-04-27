package com.numerical.analysis.solver.ui.screens.rootfinding

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numerical.analysis.solver.domain.methods.BracketingStep
import com.numerical.analysis.solver.domain.methods.OpenMethodsStep
import com.numerical.analysis.solver.ui.state.SolverViewModel
import com.numerical.analysis.solver.ui.state.RootFindingState
import com.numerical.analysis.solver.ui.theme.*
import java.util.Locale

// ─────────────────────────────────────────────────────────────────────────────
// Fixed column widths for the 8-column bisection table.
// Using fixed Dp widths — combined with horizontalScroll — means columns never
// get squeezed regardless of screen width or orientation.
// ─────────────────────────────────────────────────────────────────────────────
private val COL_ITER = 42.dp
private val COL_NUM  = 100.dp
private val COL_ERR  = 100.dp

// ─────────────────────────────────────────────────────────────────────────────
// Screen
// ─────────────────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootFindingResultsScreen(
    viewModel: SolverViewModel,
    method: String,
    onBack: () -> Unit
) {
    val state by viewModel.rootFindingState.collectAsState()

    val isBracketing = method in listOf("Bisection", "False Position")
    val iterationsCount = if (isBracketing) state.bracketingResults.size
                          else              state.openMethodsResults.size

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "$method — Results",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Slate900
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Slate700
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->

        // ─────────────────────────────────────────────────────────────────────
        // ARCHITECTURE NOTE
        // ─────────────────────────────────────────────────────────────────────
        // We use a single LazyColumn for the whole screen so that:
        //   1. The summary card, title, AND all table rows scroll together.
        //   2. "stickyHeader" keeps the table column headers always visible
        //      even when the user scrolls deep into long iteration lists.
        //   3. In landscape mode, nothing is cut off — the list is infinite.
        //
        // For the 8-column bracketing table we wrap every row (header & data)
        // inside a shared horizontalScroll state so the header tracks with the
        // data columns perfectly when the user swipes sideways.
        // ─────────────────────────────────────────────────────────────────────

        val horizontalScrollState = rememberScrollState()  // shared by header + rows

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            state = rememberLazyListState()
        ) {
            // ── spacer ──────────────────────────────────────────────────────
            item { Spacer(Modifier.height(12.dp)) }

            // ── Summary card ────────────────────────────────────────────────
            item {
                SummaryCard(
                    method          = method,
                    root            = state.rootResult ?: 0.0,
                    isConverged     = state.isConverged,
                    iterationsCount = iterationsCount,
                    finalFx         = if (isBracketing)
                                          state.bracketingResults.lastOrNull()?.fXr ?: 0.0
                                      else
                                          state.openMethodsResults.lastOrNull()?.fXi ?: 0.0,
                    stoppingReason  = state.stoppingReason
                )
            }

            // ── Table title ─────────────────────────────────────────────────
            item {
                Text(
                    text = "Iteration Table",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                    modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
                )
            }

            // ── Sticky table header ──────────────────────────────────────────
            // stickyHeader = the header row stays pinned at the top of the
            // visible area while the user scrolls through data rows below it.
            stickyHeader {
                if (isBracketing) {
                    // 8-column header — must share the same horizontalScroll
                    // state as the data rows so they move together
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface) // opaque so rows underneath don't bleed through
                            .horizontalScroll(horizontalScrollState)
                            .background(Slate50)
                            .border(1.dp, Slate200)
                            .padding(vertical = 10.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HeaderCell("i",      COL_ITER)
                        HeaderCell("xl",     COL_NUM)
                        HeaderCell("f(xl)",  COL_NUM)
                        HeaderCell("xu",     COL_NUM)
                        HeaderCell("f(xu)",  COL_NUM)
                        HeaderCell("xr",     COL_NUM)
                        HeaderCell("f(xr)",  COL_NUM)
                        HeaderCell("Error%", COL_ERR)
                    }
                } else {
                    // 5-column header for open methods
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .background(Slate50)
                            .border(1.dp, Slate200)
                            .padding(vertical = 10.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("i",      modifier = Modifier.weight(0.12f), textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate500)
                        Text("xi",     modifier = Modifier.weight(0.22f), textAlign = TextAlign.End,    fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate500)
                        Text("xi+1",   modifier = Modifier.weight(0.24f), textAlign = TextAlign.End,    fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate500)
                        Text("f(xi)",  modifier = Modifier.weight(0.22f), textAlign = TextAlign.End,    fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate500)
                        Text("Error%", modifier = Modifier.weight(0.20f), textAlign = TextAlign.End,    fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Slate500)
                    }
                }
            }

            // ── Table data rows ──────────────────────────────────────────────
            if (isBracketing) {
                itemsIndexed(state.bracketingResults) { index, step ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(horizontalScrollState) // same state = synchronized
                            .background(
                                if (state.isConverged && index == state.bracketingResults.lastIndex) MaterialTheme.colorScheme.primaryContainer
                                else if (index % 2 == 0) MaterialTheme.colorScheme.surface
                                else Slate50.copy(alpha = 0.55f)
                            )
                            .border(0.5.dp, Slate100)
                            .padding(vertical = 9.dp, horizontal = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        DataCell(text = "${step.iter}", width = COL_ITER, color = PrimaryColor, bold = true, align = TextAlign.Center)
                        DataCell(text = fmt(step.xl),   width = COL_NUM)
                        DataCell(text = fmt(step.fXl),  width = COL_NUM, color = fColor(step.fXl))
                        DataCell(text = fmt(step.xu),   width = COL_NUM)
                        DataCell(text = fmt(step.fXu),  width = COL_NUM, color = fColor(step.fXu))
                        DataCell(text = fmt(step.xr),   width = COL_NUM, color = PrimaryColor, bold = true)
                        DataCell(text = fmt(step.fXr),  width = COL_NUM, color = fColor(step.fXr))
                        DataCell(
                            text  = if (step.iter == 1) "—" else {
                                val errStr = String.format(Locale.US, "%.5f", step.error)
                                if (state.isConverged && index == state.bracketingResults.lastIndex) "$errStr ✓" else errStr
                            },
                            width = COL_ERR,
                            color = if (state.isConverged && index == state.bracketingResults.lastIndex) MaterialTheme.colorScheme.primary else errColor(step.error)
                        )
                    }
                }
            } else {
                itemsIndexed(state.openMethodsResults) { index, step ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                if (state.isConverged && index == state.openMethodsResults.lastIndex) MaterialTheme.colorScheme.primaryContainer
                                else if (index % 2 == 0) MaterialTheme.colorScheme.surface
                                else Slate50.copy(alpha = 0.55f)
                            )
                            .border(0.5.dp, Slate100)
                            .padding(vertical = 9.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("${step.iter}",  modifier = Modifier.weight(0.12f), textAlign = TextAlign.Center, fontSize = 12.sp, color = PrimaryColor, fontWeight = FontWeight.Bold,       fontFamily = FontFamily.Monospace)
                        Text(fmt(step.xi),     modifier = Modifier.weight(0.22f), textAlign = TextAlign.End,    fontSize = 12.sp, color = Slate900,       fontFamily = FontFamily.Monospace)
                        Text(fmt(step.xiPlus1),modifier = Modifier.weight(0.24f), textAlign = TextAlign.End,    fontSize = 12.sp, color = PrimaryColor,    fontWeight = FontWeight.SemiBold, fontFamily = FontFamily.Monospace)
                        Text(fmt(step.fXi),    modifier = Modifier.weight(0.22f), textAlign = TextAlign.End,    fontSize = 12.sp, color = fColor(step.fXi),fontFamily = FontFamily.Monospace)
                        Text(
                            text = if (step.iter == 0) "—" else {
                                val errStr = String.format(Locale.US, "%.5f", step.error)
                                if (state.isConverged && index == state.openMethodsResults.lastIndex) "$errStr ✓" else errStr
                            },
                            modifier = Modifier.weight(0.20f),
                            textAlign = TextAlign.End,
                            fontSize = 12.sp,
                            color = if (state.isConverged && index == state.openMethodsResults.lastIndex) MaterialTheme.colorScheme.primary else errColor(step.error),
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (state.isConverged && index == state.openMethodsResults.lastIndex) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            // ── bottom spacer ────────────────────────────────────────────────
            item { Spacer(Modifier.height(24.dp)) }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Summary Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SummaryCard(
    method: String,
    root: Double,
    isConverged: Boolean,
    iterationsCount: Int,
    finalFx: Double,
    stoppingReason: String?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(16.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Filled.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = if (isConverged) "Converged" else "Did not converge",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(method, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Slate900)
            }

            Spacer(Modifier.height(14.dp))
            Text("Approximated Root", fontSize = 13.sp, color = Slate500)
            Text(
                text = String.format(Locale.US, "%.5f", root),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryColor
            )

            Spacer(Modifier.height(14.dp))
            Divider(color = Slate100)
            Spacer(Modifier.height(14.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("f(root)", fontSize = 12.sp, color = Slate500)
                    Text(String.format(Locale.US, "%.5f", finalFx), fontSize = 13.sp, fontFamily = FontFamily.Monospace, color = Slate900)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total Iterations", fontSize = 12.sp, color = Slate500)
                    Text("$iterationsCount", fontSize = 13.sp, fontFamily = FontFamily.Monospace, color = Slate900)
                }
            }
            
            if (stoppingReason != null) {
                Spacer(Modifier.height(14.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(if (isConverged) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer, RoundedCornerShape(8.dp))
                        .border(1.dp, if (isConverged) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error, RoundedCornerShape(8.dp))
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        if (isConverged) Icons.Filled.CheckCircle else Icons.Filled.CheckCircle,
                        contentDescription = null,
                        tint = if (isConverged) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stoppingReason,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isConverged) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Cell helpers
// ─────────────────────────────────────────────────────────────────────────────

@Composable
private fun HeaderCell(text: String, width: Dp) {
    Text(
        text = text,
        modifier = Modifier.width(width),
        textAlign = TextAlign.Center,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Slate500
    )
}

@Composable
private fun DataCell(
    text: String,
    width: Dp,
    color: Color = Slate900,
    bold: Boolean = false,
    align: TextAlign = TextAlign.End
) {
    Text(
        text = text,
        modifier = Modifier.width(width),
        textAlign = align,
        fontSize = 11.sp,
        fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
        fontFamily = FontFamily.Monospace,
        color = color,
        maxLines = 1
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Pure formatting helpers
// ─────────────────────────────────────────────────────────────────────────────

private fun fmt(value: Double): String = String.format(Locale.US, "%.5f", value)

@Composable
private fun fColor(value: Double): Color = when {
    value < -1e-8 -> MaterialTheme.colorScheme.error
    value >  1e-8 -> Color(0xFF16A34A)  // green is usually fine for positive
    else          -> Slate400
}

@Composable
private fun errColor(value: Double): Color =
    if (value < 1.0) Color(0xFF16A34A) else Slate600



