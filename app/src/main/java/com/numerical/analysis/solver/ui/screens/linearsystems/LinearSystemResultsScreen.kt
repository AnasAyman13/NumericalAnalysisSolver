package com.numerical.analysis.solver.ui.screens.linearsystems

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numerical.analysis.solver.ui.theme.*
import com.numerical.analysis.solver.ui.state.SolverViewModel
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
                title = {
                    Text(
                        "Results",
                        fontWeight = FontWeight.SemiBold,
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .navigationBarsPadding()
        ) {
            // Scrollable content area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                Spacer(Modifier.height(8.dp))

                // Status card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, Slate100),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(Modifier.padding(20.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 16.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        if (isSuccess) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                                        RoundedCornerShape(20.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isSuccess) Icons.Filled.CheckCircle else Icons.Filled.Error,
                                    contentDescription = null,
                                    tint = if (isSuccess) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                                )
                            }
                            Spacer(Modifier.width(12.dp))
                            Column {
                                Text(
                                    "STATUS",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Slate500
                                )
                                Text(
                                    if (isSuccess) "Unique Solution Found" else "Error Found",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Slate900
                                )
                            }
                        }
                        Divider(color = Slate100)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = if (isSuccess)
                                "The system is consistent and has exactly one solution vector."
                            else
                                (state.result?.errorMessage ?: "Unknown error"),
                            fontSize = 14.sp,
                            color = Slate600,
                            lineHeight = 20.sp
                        )
                    }
                }

                // Solution vector
                if (isSuccess && state.result?.solution != null) {
                    val solution = state.result!!.solution
                    Text(
                        "Solution Vector",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, Slate100),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column {
                            solution.forEachIndexed { index, value ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(
                                                    PrimaryColor.copy(alpha = 0.1f),
                                                    RoundedCornerShape(8.dp)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "x${index + 1}",
                                                fontSize = 14.sp,
                                                fontStyle = FontStyle.Italic,
                                                fontWeight = FontWeight.Medium,
                                                color = PrimaryColor,
                                                fontFamily = FontFamily.Serif
                                            )
                                        }
                                        Spacer(Modifier.width(12.dp))
                                        Text(
                                            "Variable ${index + 1}",
                                            fontSize = 14.sp,
                                            color = Slate500
                                        )
                                    }
                                    Text(
                                        text = String.format(Locale.US, "%.5f", value),
                                        fontSize = 18.sp,
                                        fontFamily = FontFamily.Monospace,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Slate900
                                    )
                                }
                                if (index < solution.size - 1) {
                                    Divider(color = Slate100, modifier = Modifier.padding(horizontal = 16.dp))
                                }
                            }
                        }
                    }
                }

                // Step-by-step visualization
                if (isSuccess && !state.result?.steps.isNullOrEmpty()) {
                    Text(
                        "Step-by-Step Visualization",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900,
                        modifier = Modifier.padding(top = 12.dp)
                    )

                    state.result?.steps?.forEach { step ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, Slate100),
                            elevation = CardDefaults.cardElevation(0.dp)
                        ) {
                            Column(Modifier.padding(16.dp)) {
                                Text(
                                    step.title,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryColor
                                )
                                if (step.message != null) {
                                    Text(
                                        step.message,
                                        fontSize = 12.sp,
                                        color = Slate500,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                                Spacer(Modifier.height(12.dp))
                                MatrixDisplay(step.matrix)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // Pinned "New Calculation" button
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Button(
                        onClick = {
                            viewModel.updateLinearSystemInput()
                            onNewCalculation()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                    ) {
                        Icon(Icons.Outlined.Add, contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("New Calculation", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun MatrixDisplay(matrix: Array<DoubleArray>) {
    val n = matrix.size
    val m = if (n > 0) matrix[0].size else 0

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .border(1.dp, Slate100, RoundedCornerShape(8.dp))
            .background(Slate50.copy(alpha = 0.5f))
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        for (i in 0 until n) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                for (j in 0 until m) {
                    val value = matrix[i][j]
                    val isAugmented = j == m - 1
                    Box(
                        modifier = Modifier
                            .width(70.dp)
                            .height(30.dp)
                            .background(
                                if (isAugmented) PrimaryColor.copy(alpha = 0.05f) else Color.Transparent,
                                RoundedCornerShape(4.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = String.format(Locale.US, "%.3f", value),
                            fontSize = 12.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = if (isAugmented) FontWeight.Bold else FontWeight.Normal,
                            color = if (isAugmented) PrimaryColor else Slate700
                        )
                    }
                }
            }
        }
    }
}
