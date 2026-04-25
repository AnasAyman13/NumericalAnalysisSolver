package com.numerical.analysis.solver.ui.theme.screens.linearsystems

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.focus.onFocusChanged
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.*
import com.numerical.analysis.solver.ui.theme.state.SolverViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinearSystemScreen(
    viewModel: SolverViewModel,
    onBack: () -> Unit,
    onSolveComplete: () -> Unit
) {
    val state by viewModel.linearSystemState.collectAsState()
    val focusManager = LocalFocusManager.current

    val methodsMap = mapOf(
        "gauss"        to "Gauss Elimination",
        "lu"           to "LU Decomposition",
        "cramer"       to "Cramer's Rule",
        "gauss-jordan" to "Gauss-Jordan"
    )

    LaunchedEffect(state.result, state.isLoading) {
        if (!state.isLoading && state.result != null && state.errorMessage == null) {
            onSolveComplete()
        }
    }

    // ── Outer scaffold provides only the top bar; the bottom button bar is
    //   managed manually inside the content lambda so that imePadding() on
    //   the Column naturally shifts both the scrollable area AND the button
    //   bar together — no overlap, no double-inset counting.
    Scaffold(
        containerColor = BackgroundLight,
        contentWindowInsets = WindowInsets(0),          // we handle ALL insets ourselves
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Linear Algebraic Equations",
                        fontWeight = FontWeight.Bold,
                        fontSize  = 16.sp,
                        color     = Slate900
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
                ),
                windowInsets = WindowInsets.statusBars   // keep status bar padding on the top bar
            )
        }
        // NO bottomBar — it lives inside the content Column so imePadding works
    ) { scaffoldPadding ->

        // ── Root column: fills screen, handles insets, tracks keyboard ──────
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(scaffoldPadding)               // moves below the top bar
                .navigationBarsPadding()                // respects bottom gesture bar
                .imePadding()                           // ← entire column (scroll + buttons)
                                                        //   rises when keyboard appears
        ) {

            // ── Scrollable input content ────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)                          // takes all available space
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp, bottom = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Error banner
                if (state.errorMessage != null) {
                    Card(
                        colors   = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            state.errorMessage!!,
                            color    = Color(0xFFB91C1C),
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp
                        )
                    }
                }

                // ── Method Selector ─────────────────────────────────────────
                Card(
                    shape     = RoundedCornerShape(12.dp),
                    colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border    = BorderStroke(1.dp, Slate100),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(
                        Modifier
                            .padding(16.dp)
                            .fillMaxWidth()
                    ) {
                        Text(
                            "Select Method",
                            fontSize     = 14.sp,
                            fontWeight   = FontWeight.Medium,
                            color        = Slate500,
                            modifier     = Modifier.padding(bottom = 8.dp)
                        )
                        var expanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded        = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value       = methodsMap[state.method] ?: "Gauss Elimination",
                                onValueChange = {},
                                readOnly    = true,
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .menuAnchor(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    unfocusedContainerColor = Slate50,
                                    focusedContainerColor   = Slate50,
                                    unfocusedBorderColor    = Slate200,
                                    focusedBorderColor      = PrimaryColor
                                ),
                                shape     = RoundedCornerShape(8.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(
                                    fontSize = 16.sp, color = Slate900
                                )
                            )
                            ExposedDropdownMenu(
                                expanded        = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                methodsMap.forEach { (key, display) ->
                                    DropdownMenuItem(
                                        text    = { Text(display) },
                                        onClick = {
                                            viewModel.updateLinearSystemInput(method = key)
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // ── System Size Stepper ─────────────────────────────────────
                Card(
                    shape     = RoundedCornerShape(12.dp),
                    colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border    = BorderStroke(1.dp, Slate100),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Row(
                        Modifier
                            .padding(16.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "System Size",
                                fontSize   = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color      = Slate500
                            )
                            Text(
                                "Matrix dimension (n × n)",
                                fontSize = 12.sp,
                                color    = Slate400
                            )
                        }
                        Row(
                            modifier = Modifier
                                .background(Slate50, RoundedCornerShape(8.dp))
                                .border(1.dp, Slate200, RoundedCornerShape(8.dp))
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick  = {
                                    if (state.matrixSize > 2)
                                        viewModel.updateLinearSystemInput(matrixSize = state.matrixSize - 1)
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(6.dp))
                                    .border(1.dp, Slate200, RoundedCornerShape(6.dp))
                            ) {
                                Icon(Icons.Outlined.Remove, null, modifier = Modifier.size(16.dp))
                            }
                            Text(
                                "${state.matrixSize}",
                                modifier   = Modifier.width(32.dp),
                                textAlign  = TextAlign.Center,
                                fontWeight = FontWeight.SemiBold,
                                fontSize   = 16.sp
                            )
                            IconButton(
                                onClick  = {
                                    if (state.matrixSize < 8)
                                        viewModel.updateLinearSystemInput(matrixSize = state.matrixSize + 1)
                                },
                                modifier = Modifier
                                    .size(32.dp)
                                    .background(PrimaryColor, RoundedCornerShape(6.dp))
                            ) {
                                Icon(
                                    Icons.Outlined.Add, null,
                                    tint     = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }

                // ── Matrix A Input ──────────────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment     = Alignment.CenterVertically
                    ) {
                        Text(
                            "Matrix A (Coefficients)",
                            fontSize   = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color      = Slate900
                        )
                        Text(
                            "${state.matrixSize} × ${state.matrixSize}",
                            fontSize   = 12.sp,
                            fontWeight = FontWeight.Medium,
                            color      = PrimaryColor,
                            modifier   = Modifier
                                .background(PrimaryColor.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Card(
                        shape     = RoundedCornerShape(12.dp),
                        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border    = BorderStroke(1.dp, Slate100),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(
                            Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            for (r in 0 until state.matrixSize) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    modifier              = Modifier.padding(
                                        bottom = if (r < state.matrixSize - 1) 8.dp else 0.dp
                                    )
                                ) {
                                    for (c in 0 until state.matrixSize) {
                                        val current = state.matrixA[r][c]
                                        var textVal by remember(r, c) {
                                            mutableStateOf(if (current == 0.0) "" else current.toString())
                                        }
                                        OutlinedTextField(
                                            value         = textVal,
                                            onValueChange = {
                                                textVal = it
                                                it.toDoubleOrNull()?.let { d ->
                                                    viewModel.updateMatrixElement(r, c, d)
                                                }
                                            },
                                            modifier      = Modifier.width(80.dp),
                                            placeholder   = {
                                                Text(
                                                    "a${r + 1}${c + 1}",
                                                    textAlign = TextAlign.Center,
                                                    modifier  = Modifier.fillMaxWidth()
                                                )
                                            },
                                            textStyle     = androidx.compose.ui.text.TextStyle(
                                                textAlign  = TextAlign.Center,
                                                fontFamily = FontFamily.Monospace,
                                                fontSize   = 14.sp
                                            ),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                unfocusedContainerColor = Slate50,
                                                focusedContainerColor   = Slate50,
                                                unfocusedBorderColor    = Slate200,
                                                focusedBorderColor      = PrimaryColor
                                            ),
                                            shape          = RoundedCornerShape(8.dp),
                                            singleLine     = true,
                                            keyboardOptions = KeyboardOptions(
                                                keyboardType = KeyboardType.Number,
                                                imeAction    = ImeAction.Next
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // ── Vector b Input ──────────────────────────────────────────
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Vector b (Constants)",
                        fontSize   = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color      = Slate900,
                        modifier   = Modifier.padding(horizontal = 4.dp)
                    )

                    Card(
                        shape     = RoundedCornerShape(12.dp),
                        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border    = BorderStroke(1.dp, Slate100),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(
                            Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState())
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                for (r in 0 until state.matrixSize) {
                                    val current = state.vectorB[r]
                                    var textVal by remember(r) {
                                        mutableStateOf(if (current == 0.0) "" else current.toString())
                                    }
                                    OutlinedTextField(
                                        value         = textVal,
                                        onValueChange = {
                                            textVal = it
                                            it.toDoubleOrNull()?.let { d ->
                                                viewModel.updateVectorElement(r, d)
                                            }
                                        },
                                        modifier      = Modifier.width(80.dp),
                                        placeholder   = {
                                            Text(
                                                "b${r + 1}",
                                                textAlign = TextAlign.Center,
                                                modifier  = Modifier.fillMaxWidth(),
                                                color     = PrimaryColor.copy(alpha = 0.5f)
                                            )
                                        },
                                        textStyle     = androidx.compose.ui.text.TextStyle(
                                            textAlign  = TextAlign.Center,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize   = 14.sp,
                                            color      = PrimaryColor,
                                            fontWeight = FontWeight.Bold
                                        ),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            unfocusedContainerColor = PrimaryColor.copy(alpha = 0.05f),
                                            focusedContainerColor   = PrimaryColor.copy(alpha = 0.10f),
                                            unfocusedBorderColor    = PrimaryColor.copy(alpha = 0.20f),
                                            focusedBorderColor      = PrimaryColor
                                        ),
                                        shape          = RoundedCornerShape(8.dp),
                                        singleLine     = true,
                                        keyboardOptions = KeyboardOptions(
                                            keyboardType = KeyboardType.Number,
                                            imeAction    = ImeAction.Done   // dismisses keyboard cleanly
                                        )
                                    )
                                }
                            }
                        }
                    }
                }

                // Bottom breathing room — not strictly needed because weight(1f) pushes
                // the button bar below, but gives a visual gap above the button area.
                Spacer(Modifier.height(8.dp))
            }

            // ── Pinned button bar — never overlaps content ──────────────────
            // Because this is a sibling of the scroll column (not a Scaffold bottomBar),
            // the content column above automatically stops where this begins.
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color    = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick  = {
                            focusManager.clearFocus()
                            viewModel.updateLinearSystemInput(matrixSize = 3, method = "gauss")
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(
                            containerColor = Slate50,
                            contentColor   = Slate600
                        ),
                        border = BorderStroke(1.dp, Slate200)
                    ) {
                        Icon(
                            Icons.Outlined.Refresh,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("Clear", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }

                    Button(
                        onClick  = {
                            focusManager.clearFocus()
                            viewModel.solveLinearSystem()
                        },
                        modifier = Modifier
                            .weight(2f)
                            .height(48.dp),
                        shape    = RoundedCornerShape(12.dp),
                        colors   = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                color       = Color.White,
                                modifier    = Modifier.size(24.dp),
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Outlined.Calculate,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                "SOLVE SYSTEM",
                                fontWeight = FontWeight.Bold,
                                fontSize   = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
