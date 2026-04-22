package com.numerical.analysis.solver.ui.theme.screens.linearsystems

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.*
import com.numerical.analysis.solver.ui.theme.state.SolverViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinearSystemResultsScreen(
    viewModel: SolverViewModel,
    onBack: () -> Unit,
    onNewCalculation: () -> Unit
) {
    val state by viewModel.linearSystemState.collectAsState()
    val isSuccess = state.result?.isSuccessful == true

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Results", fontWeight = FontWeight.SemiBold, fontSize = 18.sp, color = Slate900) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Slate700)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)).padding(16.dp).navigationBarsPadding()) {
                Button(
                    onClick = {
                        viewModel.updateLinearSystemInput()
                        onNewCalculation()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                ) {
                    Icon(Icons.Outlined.Add, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("New Calculation", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            
            // Status Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, Slate100),
                elevation = CardDefaults.cardElevation(0.dp)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 16.dp)) {
                        Box(
                            modifier = Modifier.size(40.dp).background(if (isSuccess) Color(0xFFD1FAE5) else Color(0xFFFEE2E2), RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(if (isSuccess) Icons.Filled.CheckCircle else Icons.Filled.Error, null, tint = if (isSuccess) Color(0xFF059669) else Color(0xFFDC2626))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("STATUS", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = Slate500)
                            Text(if (isSuccess) "Unique Solution Found" else "Error Found", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        }
                    }
                    Divider(color = Slate100)
                    Spacer(Modifier.height(16.dp))
                    Text(
                        text = if (isSuccess) "The system is consistent and has exactly one solution vector." else (state.result?.errorMessage ?: "Unknown error"),
                        fontSize = 14.sp, color = Slate600, lineHeight = 20.sp
                    )
                }
            }

            if (isSuccess && state.result?.solution != null) {
                Column {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Bottom) {
                        Text("Solution Vector", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Slate900)
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { /* Copy all */ }) {
                            Icon(Icons.Outlined.ContentCopy, null, tint = PrimaryColor, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Copy All", fontSize = 12.sp, fontWeight = FontWeight.Medium, color = PrimaryColor)
                        }
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, Slate100),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        LazyColumn {
                            val solution = state.result!!.solution
                            itemsIndexed(solution.toList()) { index, value ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier.size(32.dp).background(PrimaryColor.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("x${index + 1}", fontSize = 14.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic, fontWeight = FontWeight.Medium, color = PrimaryColor, fontFamily = FontFamily.Serif)
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Text("Variable ${index + 1}", fontSize = 14.sp, color = Slate500)
                                    }
                                    Text(
                                        text = String.format(Locale.US, "%.4f", value),
                                        fontSize = 16.sp, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Medium, color = Slate900
                                    )
                                }
                                if (index < solution.size - 1) {
                                    Divider(color = Slate100)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
