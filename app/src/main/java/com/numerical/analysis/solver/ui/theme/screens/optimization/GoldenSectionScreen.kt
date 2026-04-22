package com.numerical.analysis.solver.ui.theme.screens.optimization

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.Slate900
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.Slate700
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.Slate500
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.Slate400
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.Slate100
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.PrimaryColor
import com.numerical.analysis.solver.ui.theme.state.SolverViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoldenSectionScreen(
    viewModel: SolverViewModel,
    onBack: () -> Unit,
    onSolveComplete: () -> Unit
) {
    val state by viewModel.optimizationState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Golden Section Search", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.onSurface) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface)
            )
        },
        bottomBar = {
            Box(Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)).padding(16.dp).navigationBarsPadding()) {
                val isLoading = state.isLoading
                Button(
                    onClick = {
                        viewModel.solveOptimization()
                        onSolveComplete()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    enabled = !isLoading
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Outlined.Calculate, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Solve", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            
            // Error Message
            if (state.errorMessage != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5))
                ) {
                    Text(
                        text = state.errorMessage!!,
                        color = Color(0xFF991B1B),
                        modifier = Modifier.padding(16.dp),
                        fontSize = 14.sp
                    )
                }
            }

            // Equation Input
            OutlinedTextField(
                value = state.equation,
                onValueChange = { viewModel.updateOptimizationInput(equation = it) },
                label = { Text("Function f(x)", color = Slate500) },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryColor,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                ),
                singleLine = true
            )

            // Constraints
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = state.xl,
                    onValueChange = { viewModel.updateOptimizationInput(xl = it) },
                    label = { Text("x_l (Lower)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp)
                )
                OutlinedTextField(
                    value = state.xu,
                    onValueChange = { viewModel.updateOptimizationInput(xu = it) },
                    label = { Text("x_u (Upper)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Settings
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(
                    value = state.eps,
                    onValueChange = { viewModel.updateOptimizationInput(eps = it) },
                    label = { Text("Tolerance (%)") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(12.dp)
                )
            }

            // Type Selection
            Text("Optimization Goal", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                TypeChoice(
                    title = "Find Maximum",
                    selected = state.isMax,
                    onClick = { viewModel.updateOptimizationInput(isMax = true) },
                    modifier = Modifier.weight(1f)
                )
                TypeChoice(
                    title = "Find Minimum",
                    selected = !state.isMax,
                    onClick = { viewModel.updateOptimizationInput(isMax = false) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun TypeChoice(title: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) PrimaryColor.copy(alpha=0.1f) else MaterialTheme.colorScheme.surface)
            .border(2.dp, if (selected) PrimaryColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(title, fontWeight = FontWeight.SemiBold, color = if (selected) PrimaryColor else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
