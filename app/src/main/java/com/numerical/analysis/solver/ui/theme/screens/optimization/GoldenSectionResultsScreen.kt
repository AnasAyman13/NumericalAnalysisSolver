package com.numerical.analysis.solver.ui.theme.screens.optimization

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
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
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.BackgroundLight
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.Slate900
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.Slate700
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.Slate600
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.Slate500
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.Slate400
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.Slate200
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.Slate100
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.Slate50
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.PrimaryColor
import com.numerical.analysis.solver.ui.theme.state.SolverViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoldenSectionResultsScreen(
    viewModel: SolverViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.optimizationState.collectAsState()
    val iterationsCount = state.steps.size

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Optimization Results", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Slate900) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Slate700)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            
            // Results Summary Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Box(Modifier.fillMaxWidth()) {
                    Box(Modifier.matchParentSize().padding(end = 4.dp).background(PrimaryColor).width(4.dp))
                    
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("STATUS", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.background(Color(0xFFD1FAE5), RoundedCornerShape(16.dp)).padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Filled.CheckCircle, null, tint = Color(0xFF059669), modifier = Modifier.size(16.dp))
                                    Spacer(Modifier.width(4.dp))
                                    Text(if (state.isConverged) "Converged" else "Diverged", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF059669))
                                }
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("METHOD", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Slate400)
                                Text("Golden Section", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Slate900)
                            }
                        }
                        
                        Spacer(Modifier.height(16.dp))
                        
                        Text(if (state.isMax) "Maximum Point" else "Minimum Point", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Slate500)
                        Text(String.format(Locale.US, "%.5f", state.resultOpt ?: 0.0), fontSize = 32.sp, fontWeight = FontWeight.Bold, color = PrimaryColor)
                        
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider(color = Slate100)
                        Spacer(Modifier.height(16.dp))
                        
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Column {
                                Text("f(x_opt)", fontSize = 12.sp, color = Slate500)
                                val finalF = state.steps.lastOrNull()?.fOpt ?: 0.0
                                Text(String.format(Locale.US, "%.5e", finalF), fontSize = 14.sp, fontFamily = FontFamily.Monospace, color = Slate900)
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text("Total Iterations", fontSize = 12.sp, color = Slate500)
                                Text("$iterationsCount", fontSize = 14.sp, fontFamily = FontFamily.Monospace, color = Slate900)
                            }
                        }
                    }
                }
            }

            Text("Iteration History", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate900)
            
            // Table
            Card(
                modifier = Modifier.fillMaxWidth().weight(1f).padding(bottom = 80.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, Slate200)
            ) {
                Column(Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().background(Slate50).border(1.dp, Slate200).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Iter", modifier = Modifier.weight(0.15f), textAlign = TextAlign.Center, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate500)
                        Text("x_l", modifier = Modifier.weight(0.2f), textAlign = TextAlign.Right, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate500)
                        Text("x_u", modifier = Modifier.weight(0.2f), textAlign = TextAlign.Right, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate500)
                        Text("x_opt", modifier = Modifier.weight(0.25f), textAlign = TextAlign.Right, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate500)
                        Text("Error", modifier = Modifier.weight(0.2f), textAlign = TextAlign.Right, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Slate500)
                    }
                    
                    LazyColumn(Modifier.fillMaxSize()) {
                        itemsIndexed(state.steps) { i, step ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (i % 2 == 0) MaterialTheme.colorScheme.surface else Slate50.copy(alpha = 0.5f))
                                    .border(0.5.dp, Slate100)
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("${step.iter}", modifier = Modifier.weight(0.15f), textAlign = TextAlign.Center, fontSize = 12.sp, color = Slate500)
                                Text(String.format(Locale.US, "%.3f", step.xl), modifier = Modifier.weight(0.2f), textAlign = TextAlign.Right, fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = Slate900)
                                Text(String.format(Locale.US, "%.3f", step.xu), modifier = Modifier.weight(0.2f), textAlign = TextAlign.Right, fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = Slate900)
                                Text(String.format(Locale.US, "%.4f", step.xOpt), modifier = Modifier.weight(0.25f), textAlign = TextAlign.Right, fontSize = 12.sp, fontFamily = FontFamily.Monospace, color = Slate900)
                                Text(if (step.iter == 0) "-" else String.format(Locale.US, "%.1f%%", step.error), modifier = Modifier.weight(0.2f), textAlign = TextAlign.Right, fontSize = 12.sp, color = Slate500)
                            }
                        }
                    }
                }
            }
        }
    }
}
