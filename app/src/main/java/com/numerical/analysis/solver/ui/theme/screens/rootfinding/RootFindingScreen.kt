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
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numerical.analysis.solver.ui.screens.state.RootFindingState
import com.numerical.analysis.solver.ui.theme.state.SolverViewModel

val PrimaryColor = Color(0xFF1586EF)
val BackgroundLight = Color(0xFFF6F7F8)
val Slate50 = Color(0xFFF8FAFC)
val Slate100 = Color(0xFFF1F5F9)
val Slate200 = Color(0xFFE2E8F0)
val Slate400 = Color(0xFF94A3B8)
val Slate500 = Color(0xFF64748B)
val Slate600 = Color(0xFF475569)
val Slate700 = Color(0xFF334155)
val Slate900 = Color(0xFF0F172A)

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
    
    LaunchedEffect(state.isConverged, state.rootResult, state.isLoading) {
        if (!state.isLoading && (state.isConverged || state.rootResult != null) && state.errorMessage == null) {
            if (state.openMethodsResults.isNotEmpty() || state.bracketingResults.isNotEmpty()) {
                onSolveComplete(selectedMethod)
            }
        }
    }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            Column(modifier = Modifier.background(Color.White)) {
                CenterAlignedTopAppBar(
                    title = { Text("Root Finding", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Slate900) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Slate700)
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
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
                                ) { selectedMethodIndex = index }
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
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White.copy(alpha = 0.9f))
                    .border(1.dp, Slate200)
                    .padding(16.dp)
                    .navigationBarsPadding()
            ) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Slate100)
                            .border(1.dp, Slate200, RoundedCornerShape(12.dp))
                            .clickable {
                                viewModel.updateRootFindingInput("", "", "", "", "", "1e-6", "100")
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Reset", tint = Slate600)
                    }

                    Button(
                        onClick = { 
                            focusManager.clearFocus()
                            viewModel.solveRootPath(selectedMethod)
                        },
                        modifier = Modifier.fillMaxWidth().height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryColor),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                        } else {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Outlined.Calculate, contentDescription = "Solve", modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("SOLVE", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            
            if (state.errorMessage != null) {
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2)), modifier = Modifier.fillMaxWidth()) {
                    Text(state.errorMessage!!, color = Color(0xFFB91C1C), modifier = Modifier.padding(16.dp), fontSize = 14.sp)
                }
            }

            SectionCard(title = "Function f(x)", icon = Icons.Outlined.Functions) {
                OutlinedTextField(
                    value = state.equation,
                    onValueChange = { viewModel.updateRootFindingInput(equation = it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g. x^3 - 2x - 5", fontFamily = FontFamily.Monospace, color = Slate400) },
                    textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 16.sp, color = Slate900),
                    shape = RoundedCornerShape(8.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Slate50,
                        unfocusedContainerColor = Slate50,
                        focusedBorderColor = PrimaryColor,
                        unfocusedBorderColor = Slate200
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                Text("Supports standard math syntax: sin(x), log(x), etc.", fontSize = 12.sp, color = Slate400, modifier = Modifier.padding(top = 8.dp))
            }

            SectionCard(title = "$selectedMethod Parameters", icon = Icons.Outlined.Tune) {
                when (selectedMethod) {
                    "Bisection", "False Position" -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            InputField(modifier = Modifier.weight(1f), label = "Start (x_l)", value = state.xl, onValueChange = { viewModel.updateRootFindingInput(xl = it) })
                            InputField(modifier = Modifier.weight(1f), label = "End (x_u)", value = state.xu, onValueChange = { viewModel.updateRootFindingInput(xu = it) })
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        InfoBox(text = "Ensure that f(x_l) * f(x_u) < 0. The root must lie within the interval.")
                    }
                    "Newton", "Fixed Point" -> {
                        InputField(modifier = Modifier.fillMaxWidth(), label = "Initial Guess (x0)", value = state.xi, onValueChange = { viewModel.updateRootFindingInput(xi = it) })
                        if (selectedMethod == "Fixed Point") {
                             Spacer(modifier = Modifier.height(12.dp))
                             InfoBox(text = "For Fixed Point, f(x) will be treated as g(x). Ensure equation is arranged as x = g(x).")
                        }
                    }
                    "Secant" -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            InputField(modifier = Modifier.weight(1f), label = "x(i-1)", value = state.xMinus1, onValueChange = { viewModel.updateRootFindingInput(xMinus1 = it) })
                            InputField(modifier = Modifier.weight(1f), label = "x(i)", value = state.xi, onValueChange = { viewModel.updateRootFindingInput(xi = it) })
                        }
                    }
                }
            }

            SectionCard(title = "Solver Settings", icon = Icons.Outlined.Settings) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    InputField(modifier = Modifier.weight(1f), label = "Tolerance (ε)", value = state.eps, onValueChange = { viewModel.updateRootFindingInput(eps = it) })
                    InputField(modifier = Modifier.weight(1f), label = "Max Iterations", value = state.maxIterations, onValueChange = { viewModel.updateRootFindingInput(maxIterations = it) }, isNumber = true)
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun SectionCard(title: String, icon: ImageVector, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
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
fun InputField(modifier: Modifier = Modifier, label: String, value: String, onValueChange: (String) -> Unit, isNumber: Boolean = true) {
    var isFocused by remember { mutableStateOf(false) }
    Column(modifier = modifier) {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Slate600, modifier = Modifier.padding(bottom = 4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().onFocusChanged { isFocused = it.isFocused },
            textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 14.sp, color = Slate900),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Slate50,
                unfocusedContainerColor = Slate50,
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = Slate200,
                cursorColor = PrimaryColor
            ),
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = if (isNumber) KeyboardType.Number else KeyboardType.Text, imeAction = ImeAction.Next)
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
