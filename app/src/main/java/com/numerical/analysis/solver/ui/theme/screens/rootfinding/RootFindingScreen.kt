package com.numerical.analysis.solver.ui.theme.screens.rootfinding

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
import com.numerical.analysis.solver.ui.screens.state.RootFindingState
import com.numerical.analysis.solver.ui.theme.components.KeypadKey
import com.numerical.analysis.solver.ui.theme.components.ScientificKeypad
import com.numerical.analysis.solver.ui.theme.components.handleKeypadInput
import com.numerical.analysis.solver.ui.theme.state.SolverViewModel

val PrimaryColor @Composable get() = MaterialTheme.colorScheme.primary
val BackgroundLight @Composable get() = MaterialTheme.colorScheme.background
val Slate50 @Composable get() = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
val Slate100 @Composable get() = MaterialTheme.colorScheme.surfaceVariant
val Slate200 @Composable get() = MaterialTheme.colorScheme.outline
val Slate400 @Composable get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
val Slate500 @Composable get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
val Slate600 @Composable get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
val Slate700 @Composable get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
val Slate900 @Composable get() = MaterialTheme.colorScheme.onSurface

private enum class ActiveField {
    EQUATION, XL, XU, XI, X_MINUS_1, EPS, MAX_ITER, NONE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RootFindingScreen(
    viewModel: SolverViewModel,
    onBack: () -> Unit,
    onSolveComplete: (String) -> Unit
) {
    val state by viewModel.rootFindingState.collectAsState()
    val methods = listOf("Bisection", "False Position", "Newton", "Fixed Point", "Secant")
    var selectedMethodIndex by remember { mutableIntStateOf(0) }
    val selectedMethod = methods[selectedMethodIndex]

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var activeField by remember { mutableStateOf(ActiveField.NONE) }
    val isKeypadVisible = activeField != ActiveField.NONE

    var equationValue by remember { mutableStateOf(TextFieldValue(state.equation)) }
    var xlValue by remember { mutableStateOf(TextFieldValue(state.xl)) }
    var xuValue by remember { mutableStateOf(TextFieldValue(state.xu)) }
    var xiValue by remember { mutableStateOf(TextFieldValue(state.xi)) }
    var xMinus1Value by remember { mutableStateOf(TextFieldValue(state.xMinus1)) }
    var epsValue by remember { mutableStateOf(TextFieldValue(state.eps)) }
    var maxIterValue by remember { mutableStateOf(TextFieldValue(state.maxIterations)) }

    // Sync local TextFieldValue states whenever the ViewModel state changes externally.
    // This is what makes "Re-run from History" work:
    //   loadHistoryItem() updates state.equation in the ViewModel,
    //   this LaunchedEffect detects the change and updates the TextField so the
    //   user can SEE the pre-filled equation when the screen opens.
    LaunchedEffect(state.equation) {
        if (state.equation != equationValue.text) {
            equationValue = TextFieldValue(state.equation)
        }
    }

    fun onKeyTapped(key: KeypadKey) {
        if (key == KeypadKey.HIDE) {
            activeField = ActiveField.NONE
            focusManager.clearFocus()
            return
        }
        when (activeField) {
            ActiveField.EQUATION -> {
                equationValue = handleKeypadInput(equationValue, key)
                viewModel.updateRootFindingInput(equation = equationValue.text)
            }
            ActiveField.XL -> {
                xlValue = handleKeypadInput(xlValue, key)
                viewModel.updateRootFindingInput(xl = xlValue.text)
            }
            ActiveField.XU -> {
                xuValue = handleKeypadInput(xuValue, key)
                viewModel.updateRootFindingInput(xu = xuValue.text)
            }
            ActiveField.XI -> {
                xiValue = handleKeypadInput(xiValue, key)
                viewModel.updateRootFindingInput(xi = xiValue.text)
            }
            ActiveField.X_MINUS_1 -> {
                xMinus1Value = handleKeypadInput(xMinus1Value, key)
                viewModel.updateRootFindingInput(xMinus1 = xMinus1Value.text)
            }
            ActiveField.EPS -> {
                epsValue = handleKeypadInput(epsValue, key)
                viewModel.updateRootFindingInput(eps = epsValue.text)
            }
            ActiveField.MAX_ITER -> {
                maxIterValue = handleKeypadInput(maxIterValue, key)
                viewModel.updateRootFindingInput(maxIterations = maxIterValue.text)
            }
            ActiveField.NONE -> {}
        }
    }

    LaunchedEffect(state.isConverged, state.rootResult, state.isLoading) {
        if (!state.isLoading && (state.isConverged || state.rootResult != null) && state.errorMessage == null) {
            if (state.openMethodsResults.isNotEmpty() || state.bracketingResults.isNotEmpty()) {
                onSolveComplete(selectedMethod)
            }
        }
    }

    Scaffold(
        containerColor = BackgroundLight,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {
                CenterAlignedTopAppBar(
                    title = { Text("Root Finding", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Slate900) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Slate700)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                    windowInsets = WindowInsets.statusBars
                )

                LazyRow(
                    modifier = Modifier.fillMaxWidth().border(1.dp, Slate200),
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(methods) { index, method ->
                        val isSelected = index == selectedMethodIndex
                        Column(
                            modifier = Modifier
                                .clickable(
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                                ) {
                                    selectedMethodIndex = index
                                    activeField = ActiveField.NONE
                                    focusManager.clearFocus()
                                }
                                .padding(horizontal = 12.dp, vertical = 12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = method,
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) PrimaryColor else Slate500
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Box(
                                modifier = Modifier
                                    .height(3.dp)
                                    .fillMaxWidth(0.8f)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(if (isSelected) PrimaryColor else Color.Transparent)
                            )
                        }
                    }
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .navigationBarsPadding()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                if (state.errorMessage != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            state.errorMessage!!,
                            color = Color(0xFFB91C1C),
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp
                        )
                    }
                }

                SectionCard(title = "Function f(x)", icon = Icons.Outlined.Functions) {
                    KeypadTextField(
                        value = equationValue,
                        onValueChange = { equationValue = it; viewModel.updateRootFindingInput(equation = it.text) },
                        isFocused = activeField == ActiveField.EQUATION,
                        onFocusGained = {
                            keyboardController?.hide()
                            activeField = ActiveField.EQUATION
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = "e.g. x^3 - 2*x - 5",
                        isEquation = true
                    )
                    Text(
                        "Tap a field to open the scientific keypad",
                        fontSize = 12.sp,
                        color = Slate400,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                SectionCard(title = "$selectedMethod Parameters", icon = Icons.Outlined.Tune) {
                    when (selectedMethod) {
                        "Bisection", "False Position" -> {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                KeypadNumericField(
                                    modifier = Modifier.weight(1f),
                                    label = "Start (x_l)",
                                    value = xlValue,
                                    onValueChange = { xlValue = it; viewModel.updateRootFindingInput(xl = it.text) },
                                    isFocused = activeField == ActiveField.XL,
                                    onFocusGained = { keyboardController?.hide(); activeField = ActiveField.XL }
                                )
                                KeypadNumericField(
                                    modifier = Modifier.weight(1f),
                                    label = "End (x_u)",
                                    value = xuValue,
                                    onValueChange = { xuValue = it; viewModel.updateRootFindingInput(xu = it.text) },
                                    isFocused = activeField == ActiveField.XU,
                                    onFocusGained = { keyboardController?.hide(); activeField = ActiveField.XU }
                                )
                            }
                            Spacer(modifier = Modifier.height(12.dp))
                            InfoBox(text = "Ensure that f(x_l) * f(x_u) < 0. The root must lie within the interval.")
                        }
                        "Newton", "Fixed Point" -> {
                            KeypadNumericField(
                                modifier = Modifier.fillMaxWidth(),
                                label = "Initial Guess (x0)",
                                value = xiValue,
                                onValueChange = { xiValue = it; viewModel.updateRootFindingInput(xi = it.text) },
                                isFocused = activeField == ActiveField.XI,
                                onFocusGained = { keyboardController?.hide(); activeField = ActiveField.XI }
                            )
                            if (selectedMethod == "Fixed Point") {
                                Spacer(modifier = Modifier.height(12.dp))
                                InfoBox(text = "For Fixed Point, f(x) will be treated as g(x). Ensure equation is arranged as x = g(x).")
                            }
                        }
                        "Secant" -> {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                KeypadNumericField(
                                    modifier = Modifier.weight(1f),
                                    label = "x(i-1)",
                                    value = xMinus1Value,
                                    onValueChange = { xMinus1Value = it; viewModel.updateRootFindingInput(xMinus1 = it.text) },
                                    isFocused = activeField == ActiveField.X_MINUS_1,
                                    onFocusGained = { keyboardController?.hide(); activeField = ActiveField.X_MINUS_1 }
                                )
                                KeypadNumericField(
                                    modifier = Modifier.weight(1f),
                                    label = "x(i)",
                                    value = xiValue,
                                    onValueChange = { xiValue = it; viewModel.updateRootFindingInput(xi = it.text) },
                                    isFocused = activeField == ActiveField.XI,
                                    onFocusGained = { keyboardController?.hide(); activeField = ActiveField.XI }
                                )
                            }
                        }
                    }
                }

                SectionCard(title = "Solver Settings", icon = Icons.Outlined.Settings) {
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        KeypadNumericField(
                            modifier = Modifier.weight(1f),
                            label = "Tolerance (ε)",
                            value = epsValue,
                            onValueChange = { epsValue = it; viewModel.updateRootFindingInput(eps = it.text) },
                            isFocused = activeField == ActiveField.EPS,
                            onFocusGained = { keyboardController?.hide(); activeField = ActiveField.EPS }
                        )
                        KeypadNumericField(
                            modifier = Modifier.weight(1f),
                            label = "Max Iterations",
                            value = maxIterValue,
                            onValueChange = { maxIterValue = it; viewModel.updateRootFindingInput(maxIterations = it.text) },
                            isFocused = activeField == ActiveField.MAX_ITER,
                            onFocusGained = { keyboardController?.hide(); activeField = ActiveField.MAX_ITER }
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            activeField = ActiveField.NONE
                            equationValue = TextFieldValue("")
                            xlValue = TextFieldValue("")
                            xuValue = TextFieldValue("")
                            xiValue = TextFieldValue("")
                            xMinus1Value = TextFieldValue("")
                            epsValue = TextFieldValue("1e-6")
                            maxIterValue = TextFieldValue("100")
                            viewModel.updateRootFindingInput("", "", "", "", "", "1e-6", "100")
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Slate50, contentColor = Slate600),
                        border = BorderStroke(1.dp, Slate200)
                    ) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Reset", modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Clear", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }

                    Button(
                        onClick = {
                            focusManager.clearFocus()
                            activeField = ActiveField.NONE
                            viewModel.solveRootPath(selectedMethod)
                        },
                        modifier = Modifier.weight(2f).height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Outlined.Calculate, contentDescription = "Solve", modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("SOLVE", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
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
fun KeypadTextField(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    isFocused: Boolean,
    onFocusGained: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    isEquation: Boolean = false
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val primaryColor = PrimaryColor
    val slate50 = Slate50
    val slate200 = Slate200
    val slate400 = Slate400
    val slate900 = Slate900

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .onFocusChanged { focusState ->
                if (focusState.isFocused) {
                    keyboardController?.hide()
                    onFocusGained()
                }
            },
        placeholder = {
            Text(
                placeholder,
                fontFamily = FontFamily.Monospace,
                color = slate400
            )
        },
        textStyle = TextStyle(
            fontFamily = FontFamily.Monospace,
            fontSize = 16.sp,
            color = slate900
        ),
        shape = RoundedCornerShape(8.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = slate50,
            unfocusedContainerColor = slate50,
            focusedBorderColor = primaryColor,
            unfocusedBorderColor = slate200
        ),
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.None
        ),
        readOnly = false
    )
}

@Composable
fun KeypadNumericField(
    modifier: Modifier = Modifier,
    label: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    isFocused: Boolean,
    onFocusGained: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val primaryColor = PrimaryColor
    val slate50 = Slate50
    val slate200 = Slate200
    val slate600 = Slate600
    val slate900 = Slate900

    Column(modifier = modifier) {
        Text(
            label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = slate600,
            modifier = Modifier.padding(bottom = 4.dp)
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState ->
                    if (focusState.isFocused) {
                        keyboardController?.hide()
                        onFocusGained()
                    }
                },
            textStyle = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontSize = 14.sp,
                color = slate900
            ),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = slate50,
                unfocusedContainerColor = slate50,
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = slate200,
                cursorColor = primaryColor
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.None
            )
        )
    }
}

@Composable
fun SectionCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 12.dp)) {
                Icon(icon, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Slate900)
            }
            content()
        }
    }
}

@Composable
fun InputField(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    isNumber: Boolean = true
) {
    Column(modifier = modifier) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Slate600, modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, color = Slate900),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Slate50,
                unfocusedContainerColor = Slate50,
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = Slate200,
                cursorColor = PrimaryColor
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isNumber) KeyboardType.Number else KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )
    }
}

@Composable
fun InfoBox(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(PrimaryColor.copy(alpha = 0.1f))
            .border(1.dp, PrimaryColor.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(Icons.Outlined.Info, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(18.dp).padding(top = 2.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, color = Slate700, lineHeight = 18.sp)
    }
}
