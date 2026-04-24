package com.numerical.analysis.solver.ui.theme.state

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.numerical.analysis.solver.domain.RootFindingMethods
import com.numerical.analysis.solver.ui.screens.state.LinearSystemState
import com.numerical.analysis.solver.ui.screens.state.RootFindingState
import com.numerical.analysis.solver.ui.screens.state.OptimizationState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.numerical.analysis.solver.domain.LinearAlgebraMethods
import com.numerical.analysis.solver.domain.MathParser
import com.numerical.analysis.solver.domain.OptimizationMethods
import com.numerical.analysis.solver.data.HistoryRepository
import com.numerical.analysis.solver.data.HistoryEntry
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.Dispatchers
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SolverViewModel(application: Application) : AndroidViewModel(application) {

    private val rootFindingMethods = RootFindingMethods()
    private val linearAlgebraMethods = LinearAlgebraMethods()
    private val optimizationMethods = OptimizationMethods()
    private val mathParser = MathParser()
    private val historyRepository = HistoryRepository(application)

    private val _history = MutableStateFlow<List<HistoryEntry>>(emptyList())
    val history: StateFlow<List<HistoryEntry>> = _history.asStateFlow()

    // Holds the history entry the user just tapped — read by HistoryDetailScreen
    private val _selectedHistoryEntry = MutableStateFlow<HistoryEntry?>(null)
    val selectedHistoryEntry: StateFlow<HistoryEntry?> = _selectedHistoryEntry.asStateFlow()

    fun selectHistoryEntry(entry: HistoryEntry) {
        _selectedHistoryEntry.value = entry
    }

    // -------------------------------------------------------------------------
    // loadHistoryItem
    // -------------------------------------------------------------------------
    // Called when the user taps a history card and wants to re-run it.
    //
    // What we CAN restore: the equation — it is saved in the subtitle field
    //   as  "f(x) = x^3 - 2*x - 5"  or  "Max of f(x) = x^2"
    //   We strip the prefix and put the equation back into the correct state.
    //
    // What we CANNOT restore: xl, xu, xi, tolerance — they were never stored
    //   in the database.  The user will need to re-enter those values.
    //
    // Returns the route string so the caller (NavGraph) knows where to navigate.
    // -------------------------------------------------------------------------
    fun loadHistoryItem(entry: HistoryEntry): String {

        // Step 1: Extract the equation text from the subtitle.
        //  Root Finding subtitle  → "f(x) = x^3 - 2*x - 5"
        //  Optimization subtitle  → "Max of f(x) = x^2"  or  "Min of f(x) = …"
        val equation = when {
            entry.subtitle.contains("f(x) = ") ->
                entry.subtitle.substringAfter("f(x) = ").trim()
            else ->
                ""   // unknown format — leave the field empty
        }

        // Step 2: Look at the title to decide which solver this belongs to,
        //         then pre-fill only the equation in the right ViewModel state.
        return when {

            // Root-finding methods — put equation in rootFindingState
            entry.title in listOf(
                "Bisection", "False Position",
                "Newton", "Fixed Point", "Secant"
            ) -> {
                // Use the existing updateRootFindingInput so we don't break any other logic
                updateRootFindingInput(equation = equation)
                "root_finding"   // the NavGraph will navigate here
            }

            // Optimization — put equation in optimizationState
            entry.title.contains("Golden", ignoreCase = true) -> {
                val isMax = entry.subtitle.startsWith("Max", ignoreCase = true)
                updateOptimizationInput(equation = equation, isMax = isMax)
                "golden_section"   // the NavGraph will navigate here
            }

            // Linear Systems — nothing useful to restore (no equation stored)
            else -> {
                "linear_systems"
            }
        }
    }

    private val _optimizationState = MutableStateFlow(OptimizationState())
    val optimizationState: StateFlow<OptimizationState> = _optimizationState.asStateFlow()

    private val _rootFindingState = MutableStateFlow(RootFindingState())
    val rootFindingState: StateFlow<RootFindingState> = _rootFindingState.asStateFlow()
    
    private val _linearSystemState = MutableStateFlow(LinearSystemState())
    val linearSystemState: StateFlow<LinearSystemState> = _linearSystemState.asStateFlow()

    fun updateRootFindingInput(
        equation: String? = null,
        xl: String? = null,
        xu: String? = null,
        xi: String? = null,
        xMinus1: String? = null,
        eps: String? = null,
        maxIterations: String? = null
    ) {
        _rootFindingState.update { currentState ->
            currentState.copy(
                equation = equation ?: currentState.equation,
                xl = xl ?: currentState.xl,
                xu = xu ?: currentState.xu,
                xi = xi ?: currentState.xi,
                xMinus1 = xMinus1 ?: currentState.xMinus1,
                eps = eps ?: currentState.eps,
                maxIterations = maxIterations ?: currentState.maxIterations,
                errorMessage = null
            )
        }
    }

    fun solveRootPath(method: String) {
        viewModelScope.launch {
            _rootFindingState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val state = _rootFindingState.value
                val eps = state.eps.toDoubleOrNull() ?: 1e-6
                val maxIter = state.maxIterations.toIntOrNull() ?: 100
                val f = mathParser.parseFunction(state.equation)

                val (step1, conv, res) = withContext(Dispatchers.Default) {
                    when (method) {
                        "Bisection" -> {
                            val xl = state.xl.toDouble()
                            val xu = state.xu.toDouble()
                            val results = rootFindingMethods.bisection(xl, xu, eps, f)
                            if (results.isEmpty()) throw Exception("Zero iterations computed.")
                            Triple(results, results.isNotEmpty() && results.last().error <= eps, results.lastOrNull()?.xr)
                        }
                        "False Position" -> {
                            val xl = state.xl.toDouble()
                            val xu = state.xu.toDouble()
                            val results = rootFindingMethods.falsePosition(xl, xu, eps, f)
                            if (results.isEmpty()) throw Exception("Zero iterations computed.")
                            Triple(results, results.isNotEmpty() && results.last().error <= eps, results.lastOrNull()?.xr)
                        }
                        "Newton" -> {
                            val xi = state.xi.toDouble()
                            val fDash = mathParser.parseDerivative(state.equation)
                            val results = rootFindingMethods.newton(xi, eps, maxIter, f, fDash)
                            if (results.isEmpty()) throw Exception("Zero iterations computed.")
                            Triple(results, results.isNotEmpty() && results.last().error <= eps, results.lastOrNull()?.xiPlus1)
                        }
                        "Fixed Point" -> {
                            val xi = state.xi.toDouble()
                            val results = rootFindingMethods.fixedPoint(xi, eps, f)
                            if (results.isEmpty()) throw Exception("Zero iterations computed.")
                            Triple(results, results.isNotEmpty() && results.last().error <= eps, results.lastOrNull()?.xiPlus1)
                        }
                        "Secant" -> {
                            val xi = state.xi.toDouble()
                            val xMinus1 = state.xMinus1.toDouble()
                            val results = rootFindingMethods.secant(xMinus1, xi, eps, f)
                            if (results.isEmpty()) throw Exception("Zero iterations computed.")
                            Triple(results, results.isNotEmpty() && results.last().error <= eps, results.lastOrNull()?.xiPlus1)
                        }
                        else -> throw Exception("Unknown method")
                    }
                }
                
                val resultEntry = HistoryEntry(
                    title = method,
                    subtitle = "f(x) = ${state.equation}",
                    result = "root ≈ ${String.format(Locale.US, "%.5f", res ?: 0.0)}",
                    timestamp = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(Date()),
                    accentColor = Color(0xFFE11D48)
                )

                _rootFindingState.update { 
                    if (method in listOf("Bisection", "False Position")) {
                        @Suppress("UNCHECKED_CAST")
                        val finalized = it.copy(bracketingResults = step1 as List<com.numerical.analysis.solver.domain.BracketingStep>, isConverged = conv, rootResult = res, isLoading = false)
                        if (conv) saveHistory(resultEntry)
                        finalized
                    } else {
                        @Suppress("UNCHECKED_CAST")
                        val finalized = it.copy(openMethodsResults = step1 as List<com.numerical.analysis.solver.domain.OpenMethodsStep>, isConverged = conv, rootResult = res, isLoading = false) 
                        if (conv) saveHistory(resultEntry)
                        finalized
                    }
                }
            } catch (e: Exception) {
                _rootFindingState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Invalid input") }
            }
        }
    }

    fun updateLinearSystemInput(matrixSize: Int? = null, method: String? = null) {
        _linearSystemState.update {
            val newSize = matrixSize ?: it.matrixSize
            val newA = if (matrixSize != null) Array(newSize) { DoubleArray(newSize) } else it.matrixA
            val newB = if (matrixSize != null) DoubleArray(newSize) else it.vectorB
            it.copy(
                matrixSize = newSize,
                matrixA = newA,
                vectorB = newB,
                method = method ?: it.method,
                errorMessage = null,
                result = null
            )
        }
    }

    fun updateMatrixElement(row: Int, col: Int, value: Double) {
        val currentA = _linearSystemState.value.matrixA
        currentA[row][col] = value
        _linearSystemState.update { it.copy(matrixA = currentA) }
    }

    fun updateVectorElement(index: Int, value: Double) {
        val currentB = _linearSystemState.value.vectorB
        currentB[index] = value
        _linearSystemState.update { it.copy(vectorB = currentB) }
    }

    fun solveLinearSystem() {
        viewModelScope.launch {
            _linearSystemState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val state = _linearSystemState.value
                val result = withContext(Dispatchers.Default) {
                    when (state.method) {
                        "gauss" -> linearAlgebraMethods.gaussElimination(state.matrixA, state.vectorB)
                        "lu" -> linearAlgebraMethods.luDecomposition(state.matrixA, state.vectorB)
                        "cramer" -> linearAlgebraMethods.cramersRule(state.matrixA, state.vectorB)
                        "gauss-jordan" -> linearAlgebraMethods.gaussJordan(state.matrixA, state.vectorB)
                        else -> linearAlgebraMethods.gaussElimination(state.matrixA, state.vectorB)
                    }
                }
                _linearSystemState.update { it.copy(result = result, isLoading = false, errorMessage = if (result.isSuccessful) null else result.errorMessage) }
                
                if (result.isSuccessful) {
                    val resultString = result.solution.mapIndexed { idx, v -> "x${idx + 1}=${String.format(Locale.US, "%.2f", v)}" }.joinToString(", ")
                    saveHistory(HistoryEntry(
                        title = state.method.replaceFirstChar { it.uppercase() },
                        subtitle = "${state.matrixA.size}x${state.matrixA.size} system",
                        result = resultString,
                        timestamp = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(Date()),
                        accentColor = Color(0xFF1586EF)
                    ))
                }
            } catch (e: Exception) {
                _linearSystemState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Invalid matrix setup") }
            }
        }
    }

    fun updateOptimizationInput(equation: String? = null, xl: String? = null, xu: String? = null, eps: String? = null, isMax: Boolean? = null) {
        _optimizationState.update {
            it.copy(
                equation = equation ?: it.equation,
                xl = xl ?: it.xl,
                xu = xu ?: it.xu,
                eps = eps ?: it.eps,
                isMax = isMax ?: it.isMax,
                errorMessage = null
            )
        }
    }

    fun solveOptimization() {
        viewModelScope.launch {
            _optimizationState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val state = _optimizationState.value
                val eps = state.eps.toDoubleOrNull() ?: 1e-6
                val xl = state.xl.toDouble()
                val xu = state.xu.toDouble()
                val f = mathParser.parseFunction(state.equation)

                val results = withContext(Dispatchers.Default) {
                    optimizationMethods.goldenSectionPoint(xl, xu, eps, state.isMax, f)
                }

                if (results.isEmpty()) throw Exception("Zero iterations computed.")
                val resOpt = results.last().xOpt

                _optimizationState.update {
                    it.copy(steps = results, resultOpt = resOpt, isConverged = true, isLoading = false)
                }
                
                saveHistory(HistoryEntry(
                    title = "Golden Section Search",
                    subtitle = "${if(state.isMax) "Max" else "Min"} of f(x) = ${state.equation}",
                    result = "x ≈ ${String.format(Locale.US, "%.5f", resOpt)}",
                    timestamp = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(Date()),
                    accentColor = Color(0xFFF59E0B)
                ))

            } catch (e: Exception) {
                _optimizationState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Invalid input") }
            }
        }
    }

    fun loadHistory() {
        viewModelScope.launch(Dispatchers.IO) {
            _history.value = historyRepository.getAllHistory()
        }
    }

    private fun saveHistory(entry: HistoryEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            historyRepository.addEntry(entry)
            loadHistory()
        }
    }
}