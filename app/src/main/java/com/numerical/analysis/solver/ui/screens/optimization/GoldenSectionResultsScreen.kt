package com.numerical.analysis.solver.ui.screens.optimization

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numerical.analysis.solver.ui.theme.*
import com.numerical.analysis.solver.ui.state.SolverViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoldenSectionResultsScreen(
    viewModel: SolverViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.optimizationState.collectAsState()
    val iterationsCount = state.steps.size
    val tableScrollState = rememberScrollState()

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Optimization Results",
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

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .navigationBarsPadding()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(0.dp),
            state = rememberLazyListState()
        ) {
            item { Spacer(Modifier.height(12.dp)) }

            // ── Summary card ─────────────────────────────────────────────────
            item {
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
                                Icon(
                                    Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(Modifier.width(4.dp))
                                Text(
                                    if (state.isConverged) "Converged" else "Diverged",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                "Golden Section",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Slate900
                            )
                        }

                        Spacer(Modifier.height(14.dp))

                        Text(
                            if (state.isMax) "Maximum Point" else "Minimum Point",
                            fontSize = 13.sp,
                            color = Slate500
                        )
                        Text(
                            String.format(Locale.US, "%.5f", state.resultOpt ?: 0.0),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryColor
                        )

                        Spacer(Modifier.height(14.dp))
                        HorizontalDivider(color = Slate100)
                        Spacer(Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("f(x_opt)", fontSize = 12.sp, color = Slate500)
                                val finalF = state.steps.lastOrNull()?.fOpt ?: 0.0
                                Text(
                                    String.format(Locale.US, "%.5e", finalF),
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Slate900
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Total Iterations", fontSize = 12.sp, color = Slate500)
                                Text(
                                    "$iterationsCount",
                                    fontSize = 13.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = Slate900
                                )
                            }
                        }
                    }
                }
            }

            // ── Table title ──────────────────────────────────────────────────
            item {
                Text(
                    "Iteration History",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Slate900,
                    modifier = Modifier.padding(top = 20.dp, bottom = 8.dp)
                )
            }

            // ── The Table with Horizontal Scroll ──────────────────────────────
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(tableScrollState)
                        .border(1.dp, Slate200, RoundedCornerShape(4.dp))
                ) {
                    // Header
                    Row(
                        modifier = Modifier
                            .width(800.dp) // Fixed width for horizontal scrolling
                            .background(Slate50)
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("i",    modifier = Modifier.weight(0.4f), textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate500)
                        Text("xl",   modifier = Modifier.weight(1f),   textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate500)
                        Text("f(xl)",modifier = Modifier.weight(1.1f),   textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate500)
                        Text("xu",   modifier = Modifier.weight(1f),   textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate500)
                        Text("f(xu)",modifier = Modifier.weight(1.1f),   textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate500)
                        Text("x1",   modifier = Modifier.weight(1f),   textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate500)
                        Text("f(x1)",modifier = Modifier.weight(1.1f),   textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate500)
                        Text("x2",   modifier = Modifier.weight(1f),   textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate500)
                        Text("f(x2)",modifier = Modifier.weight(1.1f),   textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate500)
                        Text("d",    modifier = Modifier.weight(1f),   textAlign = TextAlign.Center, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate500)
                    }

                    // Data Rows
                    state.steps.forEachIndexed { index, step ->
                        HorizontalDivider(color = Slate100, thickness = 0.5.dp)
                        Row(
                            modifier = Modifier
                                .width(800.dp)
                                .background(if (index % 2 == 0) Color.White else Slate50.copy(alpha = 0.3f))
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${step.iter}", modifier = Modifier.weight(0.4f), textAlign = TextAlign.Center, fontSize = 9.sp, color = PrimaryColor, fontWeight = FontWeight.Bold)
                            Text(String.format(Locale.US, "%.5f", step.xl), modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Slate900)
                            Text(String.format(Locale.US, "%.5f", step.fXl),modifier = Modifier.weight(1.1f), textAlign = TextAlign.Center, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Slate700)
                            Text(String.format(Locale.US, "%.5f", step.xu), modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Slate900)
                            Text(String.format(Locale.US, "%.5f", step.fXu),modifier = Modifier.weight(1.1f), textAlign = TextAlign.Center, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Slate700)
                            Text(String.format(Locale.US, "%.5f", step.x1), modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Slate900)
                            Text(String.format(Locale.US, "%.5f", step.fX1),modifier = Modifier.weight(1.1f), textAlign = TextAlign.Center, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Slate700)
                            Text(String.format(Locale.US, "%.5f", step.x2), modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Slate900)
                            Text(String.format(Locale.US, "%.5f", step.fX2),modifier = Modifier.weight(1.1f), textAlign = TextAlign.Center, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = Slate700)
                            Text(String.format(Locale.US, "%.5f", step.d),  modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 9.sp, fontFamily = FontFamily.Monospace, color = PrimaryColor, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}



