package com.numerical.analysis.solver.ui.theme.screens.optimization

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.Calculate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numerical.analysis.solver.ui.theme.components.KeypadKey
import com.numerical.analysis.solver.ui.theme.components.ScientificKeypad
import com.numerical.analysis.solver.ui.theme.components.handleKeypadInput
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.PrimaryColor
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.Slate200
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.Slate400
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.Slate50
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.Slate500
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.Slate700
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.Slate900
import com.numerical.analysis.solver.ui.theme.state.SolverViewModel

private enum class OptActiveField {
    EQUATION, XL, XU, EPS, NONE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoldenSectionScreen(
    viewModel: SolverViewModel,
    onBack: () -> Unit,
    onSolveComplete: () -> Unit
) {
    val state by viewModel.optimizationState.collectAsState()
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var activeField by remember { mutableStateOf(OptActiveField.NONE) }
    val isKeypadVisible = activeField != OptActiveField.NONE

    var equationValue by remember { mutableStateOf(TextFieldValue(state.equation)) }
    var xlValue by remember { mutableStateOf(TextFieldValue(state.xl)) }
    var xuValue by remember { mutableStateOf(TextFieldValue(state.xu)) }
    var epsValue by remember { mutableStateOf(TextFieldValue(state.eps)) }

    // When loadHistoryItem() pre-fills state.equation from a history entry,
    // this LaunchedEffect detects the change and syncs the TextField value
    // so the user sees the equation already populated when the screen opens.
    LaunchedEffect(state.equation) {
        if (state.equation != equationValue.text) {
            equationValue = TextFieldValue(state.equation)
        }
    }

    fun onKeyTapped(key: KeypadKey) {
        if (key == KeypadKey.HIDE) {
            activeField = OptActiveField.NONE
            focusManager.clearFocus()
            return
        }
        when (activeField) {
            OptActiveField.EQUATION -> {
                equationValue = handleKeypadInput(equationValue, key)
                viewModel.updateOptimizationInput(equation = equationValue.text)
            }
            OptActiveField.XL -> {
                xlValue = handleKeypadInput(xlValue, key)
                viewModel.updateOptimizationInput(xl = xlValue.text)
            }
            OptActiveField.XU -> {
                xuValue = handleKeypadInput(xuValue, key)
                viewModel.updateOptimizationInput(xu = xuValue.text)
            }
            OptActiveField.EPS -> {
                epsValue = handleKeypadInput(epsValue, key)
                viewModel.updateOptimizationInput(eps = epsValue.text)
            }
            OptActiveField.NONE -> {}
        }
    }

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
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(Modifier.height(8.dp))

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

                OutlinedTextField(
                    value = equationValue,
                    onValueChange = { equationValue = it; viewModel.updateOptimizationInput(equation = it.text) },
                    label = { Text("Function f(x)", color = Slate500) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { fs ->
                            if (fs.isFocused) {
                                keyboardController?.hide()
                                activeField = OptActiveField.EQUATION
                            }
                        },
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 15.sp, color = Slate900),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.None)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OptNumericField(
                        value = xlValue,
                        label = "x_l (Lower)",
                        onValueChange = { xlValue = it; viewModel.updateOptimizationInput(xl = it.text) },
                        isFocused = activeField == OptActiveField.XL,
                        onFocusGained = { keyboardController?.hide(); activeField = OptActiveField.XL },
                        modifier = Modifier.weight(1f)
                    )
                    OptNumericField(
                        value = xuValue,
                        label = "x_u (Upper)",
                        onValueChange = { xuValue = it; viewModel.updateOptimizationInput(xu = it.text) },
                        isFocused = activeField == OptActiveField.XU,
                        onFocusGained = { keyboardController?.hide(); activeField = OptActiveField.XU },
                        modifier = Modifier.weight(1f)
                    )
                }

                OptNumericField(
                    value = epsValue,
                    label = "Tolerance (%)",
                    onValueChange = { epsValue = it; viewModel.updateOptimizationInput(eps = it.text) },
                    isFocused = activeField == OptActiveField.EPS,
                    onFocusGained = { keyboardController?.hide(); activeField = OptActiveField.EPS },
                    modifier = Modifier.fillMaxWidth(0.5f)
                )

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

                Spacer(Modifier.height(4.dp))
            }

            Box(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.95f))
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        focusManager.clearFocus()
                        activeField = OptActiveField.NONE
                        viewModel.solveOptimization()
                        onSolveComplete()
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                    enabled = !state.isLoading
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Outlined.Calculate, null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Solve", fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                    }
                }
            }

            ScientificKeypad(
                visible = isKeypadVisible,
                onKey = { key -> onKeyTapped(key) }
            )
        }
    }
}

@Composable
private fun OptNumericField(
    value: TextFieldValue,
    label: String,
    onValueChange: (TextFieldValue) -> Unit,
    isFocused: Boolean,
    onFocusGained: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = Slate500) },
        modifier = modifier.onFocusChanged { fs ->
            if (fs.isFocused) onFocusGained()
        },
        shape = RoundedCornerShape(12.dp),
        textStyle = TextStyle(fontFamily = FontFamily.Monospace, color = Slate900),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = PrimaryColor,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.None)
    )
}

@Composable
fun TypeChoice(title: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (selected) PrimaryColor.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface)
            .border(2.dp, if (selected) PrimaryColor else MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(title, fontWeight = FontWeight.SemiBold, color = if (selected) PrimaryColor else MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
