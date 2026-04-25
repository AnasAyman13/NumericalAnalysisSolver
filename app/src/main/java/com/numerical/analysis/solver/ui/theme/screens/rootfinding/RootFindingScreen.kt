package com.numerical.analysis.solver.ui.theme.screens.rootfinding

import androidx.compose.animation.*
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusState
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.LocalTextInputService
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
import com.numerical.analysis.solver.ui.theme.state.SolverViewModel
import kotlinx.coroutines.launch

// ─────────────────────────────────────────────────────────────
// Colour aliases — theme-aware so both Light and Dark work
// ─────────────────────────────────────────────────────────────
val PrimaryColor    @Composable get() = MaterialTheme.colorScheme.primary
val BackgroundLight @Composable get() = MaterialTheme.colorScheme.background
val Slate50  @Composable get() = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
val Slate100 @Composable get() = MaterialTheme.colorScheme.surfaceVariant
val Slate200 @Composable get() = MaterialTheme.colorScheme.outline
val Slate400 @Composable get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
val Slate500 @Composable get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
val Slate600 @Composable get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
val Slate700 @Composable get() = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
val Slate900 @Composable get() = MaterialTheme.colorScheme.onSurface

// ─────────────────────────────────────────────────────────────
// Which field the custom keypad is currently writing to
// ─────────────────────────────────────────────────────────────
private enum class ActiveField {
    EQUATION, DERIVATIVE, XL, XU, XI, X_MINUS_1, EPS, MAX_ITER, NONE
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun RootFindingScreen(
    viewModel: SolverViewModel,
    onBack: () -> Unit,
    onSolveComplete: (String) -> Unit
) {
    val state by viewModel.rootFindingState.collectAsState()

    val methods = listOf("Bisection", "False Position", "Newton", "Fixed Point", "Secant")

    // HorizontalPager state — tracks the current page (= selected method)
    val pagerState  = rememberPagerState(pageCount = { methods.size })
    val coroutineScope = rememberCoroutineScope()
    val selectedMethod = methods[pagerState.currentPage]

    val focusManager       = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var activeField by remember { mutableStateOf(ActiveField.NONE) }
    val isKeypadVisible = activeField != ActiveField.NONE

    // Local TextFieldValue state for each input field
    var equationValue   by remember { mutableStateOf(TextFieldValue(state.equation)) }
    var derivativeValue by remember { mutableStateOf(TextFieldValue(state.derivative)) }
    var xlValue         by remember { mutableStateOf(TextFieldValue(state.xl)) }
    var xuValue       by remember { mutableStateOf(TextFieldValue(state.xu)) }
    var xiValue       by remember { mutableStateOf(TextFieldValue(state.xi)) }
    var xMinus1Value  by remember { mutableStateOf(TextFieldValue(state.xMinus1)) }
    var epsValue      by remember { mutableStateOf(TextFieldValue(state.eps)) }
    var maxIterValue  by remember { mutableStateOf(TextFieldValue(state.maxIterations)) }

    // Sync every field when loadHistoryItem() pre-fills the ViewModel from History.
    // Each LaunchedEffect fires only when its specific key changes, so normal
    // user typing (where VM and local state are always in sync) never triggers them.
    LaunchedEffect(state.equation)      { if (state.equation      != equationValue.text)     equationValue     = TextFieldValue(state.equation) }
    LaunchedEffect(state.derivative)    { if (state.derivative    != derivativeValue.text)   derivativeValue   = TextFieldValue(state.derivative) }
    LaunchedEffect(state.xl)            { if (state.xl            != xlValue.text)           xlValue           = TextFieldValue(state.xl) }
    LaunchedEffect(state.xu)            { if (state.xu            != xuValue.text)         xuValue        = TextFieldValue(state.xu) }
    LaunchedEffect(state.xi)            { if (state.xi            != xiValue.text)         xiValue        = TextFieldValue(state.xi) }
    LaunchedEffect(state.xMinus1)       { if (state.xMinus1       != xMinus1Value.text)    xMinus1Value   = TextFieldValue(state.xMinus1) }
    LaunchedEffect(state.eps)           { if (state.eps           != epsValue.text)        epsValue       = TextFieldValue(state.eps) }
    LaunchedEffect(state.maxIterations) { if (state.maxIterations != maxIterValue.text)    maxIterValue   = TextFieldValue(state.maxIterations) }

    // Distribute keypad key presses to the currently active field
    fun onKeyTapped(key: KeypadKey) {
        if (key == KeypadKey.HIDE) {
            activeField = ActiveField.NONE
            focusManager.clearFocus()
            return
        }
        when (activeField) {
            ActiveField.EQUATION  -> { equationValue   = handleKeypadInput(equationValue,  key); viewModel.updateRootFindingInput(equation      = equationValue.text) }
            ActiveField.DERIVATIVE-> { derivativeValue = handleKeypadInput(derivativeValue,key); viewModel.updateRootFindingInput(derivative    = derivativeValue.text) }
            ActiveField.XL        -> { xlValue         = handleKeypadInput(xlValue,        key); viewModel.updateRootFindingInput(xl            = xlValue.text) }
            ActiveField.XU        -> { xuValue        = handleKeypadInput(xuValue,        key); viewModel.updateRootFindingInput(xu            = xuValue.text) }
            ActiveField.XI        -> { xiValue        = handleKeypadInput(xiValue,        key); viewModel.updateRootFindingInput(xi            = xiValue.text) }
            ActiveField.X_MINUS_1 -> { xMinus1Value   = handleKeypadInput(xMinus1Value,  key); viewModel.updateRootFindingInput(xMinus1       = xMinus1Value.text) }
            ActiveField.EPS       -> { epsValue       = handleKeypadInput(epsValue,       key); viewModel.updateRootFindingInput(eps           = epsValue.text) }
            ActiveField.MAX_ITER  -> { maxIterValue   = handleKeypadInput(maxIterValue,   key); viewModel.updateRootFindingInput(maxIterations = maxIterValue.text) }
            ActiveField.NONE      -> {}
        }
    }

    // Navigate to results once the solver has finished
    LaunchedEffect(state.isConverged, state.rootResult, state.isLoading) {
        if (!state.isLoading && (state.isConverged || state.rootResult != null) && state.errorMessage == null) {
            if (state.openMethodsResults.isNotEmpty() || state.bracketingResults.isNotEmpty()) {
                onSolveComplete(selectedMethod)
            }
        }
    }

    LaunchedEffect(viewModel.selectedMethodIndex.value) {
        pagerState.scrollToPage(viewModel.selectedMethodIndex.value)
    }

    val accent = PrimaryColor

    CompositionLocalProvider(LocalTextInputService provides null) {
        Scaffold(
            containerColor      = BackgroundLight,
        contentWindowInsets = WindowInsets(0),
        topBar = {
            Column(modifier = Modifier.background(MaterialTheme.colorScheme.surface)) {

                CenterAlignedTopAppBar(
                    title = {
                        Text("Root Finding", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Slate900)
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Slate700)
                        }
                    },
                    colors        = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = MaterialTheme.colorScheme.surface),
                    windowInsets  = WindowInsets.statusBars
                )

                // ── TabRow synced with the HorizontalPager ─────────────────
                // Tapping a tab scrolls the pager; swiping the pager moves the tab indicator.
                ScrollableTabRow(
                    selectedTabIndex  = pagerState.currentPage,
                    containerColor    = MaterialTheme.colorScheme.surface,
                    contentColor      = accent,
                    edgePadding       = 16.dp,
                    indicator = { tabPositions ->
                        // Custom slim indicator bar below the selected tab
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                            height   = 3.dp,
                            color    = accent
                        )
                    },
                    divider = { HorizontalDivider(color = Slate200) }
                ) {
                    methods.forEachIndexed { index, method ->
                        val selected = pagerState.currentPage == index
                        Tab(
                            selected = selected,
                            onClick  = {
                                // Animate to the tapped page
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                                activeField = ActiveField.NONE
                                focusManager.clearFocus()
                            },
                            text = {
                                Text(
                                    text       = method,
                                    fontSize   = 13.sp,
                                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                    maxLines   = 1
                                )
                            },
                            selectedContentColor   = accent,
                            unselectedContentColor = Slate400
                        )
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
            // ── HorizontalPager — one page per method ──────────────────────
            // The user can swipe left/right to move between methods.
            // Each page shows the equation field + method-specific parameters
            // + solver settings — all in a verticalScroll for landscape safety.
            HorizontalPager(
                state             = pagerState,
                modifier          = Modifier.weight(1f),
                userScrollEnabled = !isKeypadVisible
            ) { page ->
                val method = methods[page]

                // Fade + scale animation as the page slides in
                val pageOffset = (pagerState.currentPage - page).toFloat()
                val alpha by animateFloatAsState(
                    targetValue   = if (pageOffset == 0f) 1f else 0.5f,
                    animationSpec = tween(220),
                    label         = "pageAlpha"
                )

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .alpha(alpha)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // ── Error banner ────────────────────────────────────────
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

                    // ── Function f(x) card ──────────────────────────────────
                    SectionCard(title = "Function f(x)", icon = Icons.Outlined.Functions) {
                        KeypadTextField(
                            value         = equationValue,
                            onValueChange = { equationValue = it; viewModel.updateRootFindingInput(equation = it.text) },
                            isFocused     = activeField == ActiveField.EQUATION,
                            onFocusGained = { keyboardController?.hide(); activeField = ActiveField.EQUATION },
                            modifier      = Modifier.fillMaxWidth(),
                            placeholder   = "e.g. x³ - 2x - 5"
                        )
                        Text(
                            "Tap a field → custom keypad appears",
                            fontSize = 12.sp,
                            color    = Slate400,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    // ── Method-specific parameters card ─────────────────────
                    SectionCard(title = "$method Parameters", icon = Icons.Outlined.Tune) {
                        when (method) {
                            "Bisection", "False Position" -> {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    KeypadNumericField(
                                        modifier      = Modifier.weight(1f),
                                        label         = "x_l  (Lower)",
                                        value         = xlValue,
                                        onValueChange = { xlValue = it; viewModel.updateRootFindingInput(xl = it.text) },
                                        isFocused     = activeField == ActiveField.XL,
                                        onFocusGained = { keyboardController?.hide(); activeField = ActiveField.XL }
                                    )
                                    KeypadNumericField(
                                        modifier      = Modifier.weight(1f),
                                        label         = "x_u  (Upper)",
                                        value         = xuValue,
                                        onValueChange = { xuValue = it; viewModel.updateRootFindingInput(xu = it.text) },
                                        isFocused     = activeField == ActiveField.XU,
                                        onFocusGained = { keyboardController?.hide(); activeField = ActiveField.XU }
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                InfoBox("Ensure f(x_l) × f(x_u) < 0. The root must lie within the interval.")
                            }

                            "Newton", "Fixed Point" -> {
                                KeypadNumericField(
                                    modifier      = Modifier.fillMaxWidth(),
                                    label         = "Initial Guess (x₀)",
                                    value         = xiValue,
                                    onValueChange = { xiValue = it; viewModel.updateRootFindingInput(xi = it.text) },
                                    isFocused     = activeField == ActiveField.XI,
                                    onFocusGained = { keyboardController?.hide(); activeField = ActiveField.XI }
                                )
                                if (method == "Newton") {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text("Derivative f'(x)", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Slate600, modifier = Modifier.padding(bottom = 4.dp))
                                    KeypadTextField(
                                        value         = derivativeValue,
                                        onValueChange = { derivativeValue = it; viewModel.updateRootFindingInput(derivative = it.text) },
                                        isFocused     = activeField == ActiveField.DERIVATIVE,
                                        onFocusGained = { keyboardController?.hide(); activeField = ActiveField.DERIVATIVE },
                                        modifier      = Modifier.fillMaxWidth(),
                                        placeholder   = "e.g. 3x² - 2"
                                    )
                                }
                                if (method == "Fixed Point") {
                                    Spacer(modifier = Modifier.height(12.dp))
                                    InfoBox("For Fixed Point, f(x) is treated as g(x). Arrange equation as x = g(x).")
                                }
                            }

                            "Secant" -> {
                                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                    KeypadNumericField(
                                        modifier      = Modifier.weight(1f),
                                        label         = "x(i−1)",
                                        value         = xMinus1Value,
                                        onValueChange = { xMinus1Value = it; viewModel.updateRootFindingInput(xMinus1 = it.text) },
                                        isFocused     = activeField == ActiveField.X_MINUS_1,
                                        onFocusGained = { keyboardController?.hide(); activeField = ActiveField.X_MINUS_1 }
                                    )
                                    KeypadNumericField(
                                        modifier      = Modifier.weight(1f),
                                        label         = "x(i)",
                                        value         = xiValue,
                                        onValueChange = { xiValue = it; viewModel.updateRootFindingInput(xi = it.text) },
                                        isFocused     = activeField == ActiveField.XI,
                                        onFocusGained = { keyboardController?.hide(); activeField = ActiveField.XI }
                                    )
                                }
                            }
                        }
                    }

                    // ── Solver settings card ────────────────────────────────
                    SectionCard(title = "Solver Settings", icon = Icons.Outlined.Settings) {
                        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                KeypadNumericField(
                                    modifier      = Modifier.weight(1f),
                                    label         = "Tolerance (ε)",
                                    value         = epsValue,
                                    onValueChange = { epsValue = it; viewModel.updateRootFindingInput(eps = it.text) },
                                    isFocused     = activeField == ActiveField.EPS,
                                    onFocusGained = { keyboardController?.hide(); activeField = ActiveField.EPS }
                                )
                                KeypadNumericField(
                                    modifier      = Modifier.weight(1f),
                                    label         = "Max Iterations",
                                    value         = maxIterValue,
                                    onValueChange = { maxIterValue = it; viewModel.updateRootFindingInput(maxIterations = it.text) },
                                    isFocused     = activeField == ActiveField.MAX_ITER,
                                    onFocusGained = { keyboardController?.hide(); activeField = ActiveField.MAX_ITER }
                                )
                            }
                            
                            // Tolerance Mode Toggle
                            Column {
                                Text("Stopping Criterion", fontSize = 13.sp, fontWeight = FontWeight.Medium, color = Slate600, modifier = Modifier.padding(bottom = 8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth().height(40.dp).clip(RoundedCornerShape(8.dp)).background(Slate50),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val isAbsolute = state.toleranceMode == com.numerical.analysis.solver.ui.screens.state.ToleranceMode.ABSOLUTE
                                    Box(
                                        modifier = Modifier.weight(1f).fillMaxHeight()
                                            .padding(2.dp).clip(RoundedCornerShape(6.dp))
                                            .background(if (isAbsolute) PrimaryColor else Color.Transparent)
                                            .clickable { viewModel.updateRootFindingInput(toleranceMode = com.numerical.analysis.solver.ui.screens.state.ToleranceMode.ABSOLUTE) },
                                        contentAlignment = Alignment.Center
                                    ) { Text("Absolute", color = if (isAbsolute) Color.White else Slate600, fontSize = 13.sp, fontWeight = FontWeight.Medium) }
                                    Box(
                                        modifier = Modifier.weight(1f).fillMaxHeight()
                                            .padding(2.dp).clip(RoundedCornerShape(6.dp))
                                            .background(if (!isAbsolute) PrimaryColor else Color.Transparent)
                                            .clickable { viewModel.updateRootFindingInput(toleranceMode = com.numerical.analysis.solver.ui.screens.state.ToleranceMode.PERCENTAGE) },
                                        contentAlignment = Alignment.Center
                                    ) { Text("Percentage (%)", color = if (!isAbsolute) Color.White else Slate600, fontSize = 13.sp, fontWeight = FontWeight.Medium) }
                                }
                            }
                        }
                    }
                }
            }

            // ── Bottom action bar ──────────────────────────────────────────
            Surface(
                modifier       = Modifier.fillMaxWidth(),
                color          = MaterialTheme.colorScheme.surface,
                shadowElevation = 8.dp
            ) {
                Row(
                    modifier              = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Clear button
                    OutlinedButton(
                        onClick = {
                            focusManager.clearFocus()
                            activeField    = ActiveField.NONE
                            equationValue   = TextFieldValue("")
                            derivativeValue = TextFieldValue("")
                            xlValue         = TextFieldValue("")
                            xuValue         = TextFieldValue("")
                            xiValue         = TextFieldValue("")
                            xMinus1Value    = TextFieldValue("")
                            epsValue        = TextFieldValue("1e-6")
                            maxIterValue    = TextFieldValue("100")
                            viewModel.updateRootFindingInput("", "", "", "", "", "", "1e-6", "100")
                        },
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape    = RoundedCornerShape(14.dp),
                        border   = BorderStroke(1.dp, Slate200)
                    ) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Reset", modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text("Clear", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    }

                    // Gradient SOLVE button
                    Box(
                        modifier = Modifier
                            .weight(2f)
                            .height(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF1586EF), Color(0xFF4AC29A))
                                )
                            )
                            .clickable(enabled = !state.isLoading) {
                                focusManager.clearFocus()
                                activeField = ActiveField.NONE
                                viewModel.solveRootPath(selectedMethod)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Row(
                                verticalAlignment     = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(Icons.Outlined.Calculate, null, tint = Color.White, modifier = Modifier.size(20.dp))
                                Text("SOLVE", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            }
                        }
                    }
                }
            }

            // ── Custom scientific keypad (slides up from the bottom) ───────
            ScientificKeypad(
                visible = isKeypadVisible,
                onKey   = { key -> onKeyTapped(key) }
            )
        }
    }
}
}

// ─────────────────────────────────────────────────────────────
// KeypadTextField — equation input blocked from system keyboard
// ─────────────────────────────────────────────────────────────
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
    val slate50      = Slate50
    val slate200     = Slate200
    val slate400     = Slate400
    val slate900     = Slate900

    // LocalTextInputService = null → the OS can never open the system keyboard.
    // readOnly = true → belt-and-suspenders approach.
    // Our custom keypad writes directly through onValueChange.
    CompositionLocalProvider(LocalTextInputService provides null) {
        OutlinedTextField(
            value         = value,
            onValueChange = onValueChange,
            modifier      = modifier
                .onFocusChanged { fs: FocusState ->
                    if (fs.isFocused) { keyboardController?.hide(); onFocusGained() }
                },
            placeholder = { Text(placeholder, fontFamily = FontFamily.Monospace, color = slate400) },
            textStyle   = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 16.sp, color = slate900),
            shape       = RoundedCornerShape(10.dp),
            colors      = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = slate50,
                unfocusedContainerColor = slate50,
                focusedBorderColor      = primaryColor,
                unfocusedBorderColor    = slate200
            ),
            singleLine           = true,
            readOnly             = true,
            keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.None),
            visualTransformation = com.numerical.analysis.solver.ui.theme.components.SuperscriptTransformation()
        )
    }
}

// ─────────────────────────────────────────────────────────────
// KeypadNumericField — numeric parameter input
// ─────────────────────────────────────────────────────────────
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
    val slate50      = Slate50
    val slate200     = Slate200
    val slate600     = Slate600
    val slate900     = Slate900

    Column(modifier = modifier) {
        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = slate600, modifier = Modifier.padding(bottom = 4.dp))
        CompositionLocalProvider(LocalTextInputService provides null) {
            OutlinedTextField(
                value         = value,
                onValueChange = onValueChange,
                modifier      = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { fs: FocusState ->
                        if (fs.isFocused) { keyboardController?.hide(); onFocusGained() }
                    },
                textStyle   = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, color = slate900),
                colors      = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor   = slate50,
                    unfocusedContainerColor = slate50,
                    focusedBorderColor      = primaryColor,
                    unfocusedBorderColor    = slate200,
                    cursorColor             = primaryColor
                ),
                shape                = RoundedCornerShape(10.dp),
                singleLine           = true,
                readOnly             = true,
                keyboardOptions      = KeyboardOptions(keyboardType = KeyboardType.Text, imeAction = ImeAction.None),
                visualTransformation = com.numerical.analysis.solver.ui.theme.components.SuperscriptTransformation()
            )
        }
    }
}

// ─────────────────────────────────────────────────────────────
// SectionCard — glass-style card grouping inputs
// ─────────────────────────────────────────────────────────────
@Composable
fun SectionCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier  = Modifier.fillMaxWidth(),
        shape     = RoundedCornerShape(14.dp),
        colors    = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 14.dp)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryColor.copy(alpha = 0.10f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, tint = PrimaryColor, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Slate900)
            }
            content()
        }
    }
}

// ─────────────────────────────────────────────────────────────
// InfoBox — coloured hint inside a parameter card
// ─────────────────────────────────────────────────────────────
@Composable
fun InfoBox(text: String) {
    Row(
        modifier  = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(PrimaryColor.copy(alpha = 0.08f))
            .border(1.dp, PrimaryColor.copy(alpha = 0.18f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(Icons.Outlined.Info, null, tint = PrimaryColor, modifier = Modifier.size(16.dp).padding(top = 2.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text, fontSize = 12.sp, color = Slate700, lineHeight = 18.sp)
    }
}

// ─────────────────────────────────────────────────────────────
// InputField — legacy helper kept for backward compatibility
// ─────────────────────────────────────────────────────────────
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
            value         = value,
            onValueChange = onValueChange,
            modifier      = Modifier.fillMaxWidth(),
            textStyle     = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, color = Slate900),
            colors        = OutlinedTextFieldDefaults.colors(
                focusedContainerColor   = Slate50,
                unfocusedContainerColor = Slate50,
                focusedBorderColor      = PrimaryColor,
                unfocusedBorderColor    = Slate200,
                cursorColor             = PrimaryColor
            ),
            shape           = RoundedCornerShape(10.dp),
            singleLine      = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = if (isNumber) KeyboardType.Number else KeyboardType.Text,
                imeAction    = ImeAction.Next
            )
        )
    }
}
