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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import com.numerical.analysis.solver.domain.LinearAlgebraMethods
import com.numerical.analysis.solver.domain.MathParser
import com.numerical.analysis.solver.domain.GoldenSectionSolver
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

    private val rootFindingMethods  = RootFindingMethods()
    private val linearAlgebraMethods = LinearAlgebraMethods()
    private val optimizationMethods  = GoldenSectionSolver()
    private val mathParser           = MathParser()
    private val historyRepository    = HistoryRepository(application)
    private var calculationJob: kotlinx.coroutines.Job? = null

    private val _navigationEvents = MutableSharedFlow<String>()
    val navigationEvents = _navigationEvents.asSharedFlow()

    private val _history = MutableStateFlow<List<HistoryEntry>>(emptyList())
    val history: StateFlow<List<HistoryEntry>> = _history.asStateFlow()

    private val _selectedHistoryEntry = MutableStateFlow<HistoryEntry?>(null)
    val selectedHistoryEntry: StateFlow<HistoryEntry?> = _selectedHistoryEntry.asStateFlow()

    fun selectHistoryEntry(entry: HistoryEntry) {
        _selectedHistoryEntry.value = entry
    }

    fun cancelCalculation() {
        calculationJob?.cancel()
        _rootFindingState.update { it.copy(isLoading = false) }
        _linearSystemState.update { it.copy(isLoading = false) }
        _optimizationState.update { it.copy(isLoading = false) }
    }

    // -------------------------------------------------------------------------
    // loadHistoryItem
    // -------------------------------------------------------------------------
    // Restores ALL saved input fields into the correct ViewModel state so that
    // when the solver screen opens, every TextField is already populated.
    //
    // Returns the destination route string so the NavGraph knows where to go.
    // -------------------------------------------------------------------------
    fun loadHistoryItem(entry: HistoryEntry): String {
        return when {

            entry.title in listOf("Bisection", "False Position", "Newton", "Fixed Point", "Secant") -> {
                _rootFindingState.update { current ->
                    current.copy(
                        equation      = entry.equation,
                        derivative    = entry.derivative,
                        xl            = entry.xl,
                        xu            = entry.xu,
                        xi            = entry.xi,
                        xMinus1       = entry.xMinus1,
                        eps           = entry.eps,
                        maxIterations = entry.maxIterations,
                        isConverged   = false,
                        rootResult    = null,
                        stoppingReason = null,
                        errorMessage  = null
                    )
                }
                
                // Sync the pager index based on the saved method type
                when (entry.title) {
                    "Bisection"      -> _selectedMethodIndex.value = 0
                    "False Position" -> _selectedMethodIndex.value = 1
                    "Newton"         -> _selectedMethodIndex.value = 2
                    "Secant"         -> _selectedMethodIndex.value = 3
                    "Fixed Point"    -> _selectedMethodIndex.value = 4
                }
                "root_finding"
            }

            entry.title.contains("Golden", ignoreCase = true) -> {
                val isMax = entry.subtitle.startsWith("Max", ignoreCase = true)
                _optimizationState.update { current ->
                    current.copy(
                        equation = entry.equation,
                        xl       = entry.xl,
                        xu       = entry.xu,
                        numIterations = entry.maxIterations,
                        isMax    = isMax,
                        errorMessage = null
                    )
                }
                "golden_section"
            }

            else -> "linear_systems"
        }
    }

    private val _optimizationState = MutableStateFlow(OptimizationState())
    val optimizationState: StateFlow<OptimizationState> = _optimizationState.asStateFlow()

    private val _rootFindingState = MutableStateFlow(RootFindingState())
    val rootFindingState: StateFlow<RootFindingState> = _rootFindingState.asStateFlow()

    private val _selectedMethodIndex = androidx.compose.runtime.mutableStateOf(0)
    val selectedMethodIndex: androidx.compose.runtime.State<Int> = _selectedMethodIndex

    private val _linearSystemState = MutableStateFlow(LinearSystemState())
    val linearSystemState: StateFlow<LinearSystemState> = _linearSystemState.asStateFlow()

    fun updateRootFindingInput(
        equation: String? = null,
        derivative: String? = null,
        xl: String? = null,
        xu: String? = null,
        xi: String? = null,
        xMinus1: String? = null,
        eps: String? = null,
        maxIterations: String? = null,
        toleranceMode: com.numerical.analysis.solver.ui.screens.state.ToleranceMode? = null
    ) {
        _rootFindingState.update { s ->
            s.copy(
                equation      = equation      ?: s.equation,
                derivative    = derivative    ?: s.derivative,
                xl            = xl            ?: s.xl,
                xu            = xu            ?: s.xu,
                xi            = xi            ?: s.xi,
                xMinus1       = xMinus1       ?: s.xMinus1,
                eps           = eps           ?: s.eps,
                maxIterations = maxIterations ?: s.maxIterations,
                toleranceMode = toleranceMode ?: s.toleranceMode,
                errorMessage  = null
            )
        }
    }

    fun solveRootPath(method: String) {
        calculationJob?.cancel()
        calculationJob = viewModelScope.launch {
            _rootFindingState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val state = _rootFindingState.value
                val (step1, conv, res) = withContext(Dispatchers.Default) {
                    val eps     = state.eps.toDoubleOrNull() ?: 1e-6
                    val maxIter = state.maxIterations.toIntOrNull() ?: 100
                    val f       = mathParser.parseFunction(state.equation)

                    when (method) {
                        "Bisection" -> {
                            val xl = state.xl.toDouble()
                            val xu = state.xu.toDouble()
                            val results = rootFindingMethods.bisection(xl, xu, eps, maxIter, state.toleranceMode, f)
                            if (results.isEmpty()) throw Exception("Zero iterations computed.")
                            Triple(results, results.last().error <= eps, results.lastOrNull()?.xr)
                        }
                        "False Position" -> {
                            val xl = state.xl.toDouble()
                            val xu = state.xu.toDouble()
                            val results = rootFindingMethods.falsePosition(xl, xu, eps, maxIter, state.toleranceMode, f)
                            if (results.isEmpty()) throw Exception("Zero iterations computed.")
                            Triple(results, results.last().error <= eps, results.lastOrNull()?.xr)
                        }
                        "Newton" -> {
                            val xi    = state.xi.toDouble()
                            val fDash = mathParser.parseFunction(state.derivative)
                            val results = rootFindingMethods.newton(xi, eps, maxIter, state.toleranceMode, f, fDash)
                            if (results.isEmpty()) throw Exception("Zero iterations computed.")
                            Triple(results, results.last().error <= eps, results.lastOrNull()?.xiPlus1)
                        }
                        "Fixed Point" -> {
                            val xi = state.xi.toDouble()
                            val results = rootFindingMethods.fixedPoint(xi, eps, maxIter, state.toleranceMode, f)
                            if (results.isEmpty()) throw Exception("Zero iterations computed.")
                            Triple(results, results.last().error <= eps, results.lastOrNull()?.xiPlus1)
                        }
                        "Secant" -> {
                            val xi      = state.xi.toDouble()
                            val xMinus1 = state.xMinus1.toDouble()
                            val results = rootFindingMethods.secant(xMinus1, xi, eps, maxIter, state.toleranceMode, f)
                            if (results.isEmpty()) throw Exception("Zero iterations computed.")
                            Triple(results, results.last().error <= eps, results.lastOrNull()?.xiPlus1)
                        }
                        else -> throw Exception("Unknown method")
                    }
                }

                // Save ALL input fields so Re-run can fully restore them later
                val resultEntry = HistoryEntry(
                    title         = method,
                    subtitle      = "f(x) = ${state.equation}",
                    result        = "root ≈ ${String.format(Locale.US, "%.5f", res ?: 0.0)}",
                    timestamp     = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(Date()),
                    accentColor   = Color(0xFFE11D48),
                    equation      = state.equation,
                    derivative    = state.derivative,
                    xl            = state.xl,
                    xu            = state.xu,
                    xi            = state.xi,
                    xMinus1       = state.xMinus1,
                    eps           = state.eps,
                    maxIterations = state.maxIterations,
                    methodType    = method
                )

                // Compute the stopping reason
                val lastError = if (method in listOf("Bisection", "False Position")) {
                    (step1 as List<com.numerical.analysis.solver.domain.BracketingStep>).lastOrNull()?.error ?: 0.0
                } else {
                    (step1 as List<com.numerical.analysis.solver.domain.OpenMethodsStep>).lastOrNull()?.error ?: 0.0
                }
                
                val epsValStr = String.format(java.util.Locale.US, "%.5f", state.eps.toDoubleOrNull() ?: 1e-6)
                val errValStr = String.format(java.util.Locale.US, "%.5f", lastError)
                val isPercentage = state.toleranceMode == com.numerical.analysis.solver.ui.screens.state.ToleranceMode.PERCENTAGE
                
                val reason = if (conv) {
                    if (isPercentage) "|ε_a| % ($errValStr) ≤ ε_s % ($epsValStr) → Target Reached"
                    else              "|x_new - x_old| ($errValStr) ≤ ε ($epsValStr) → Target Reached"
                } else {
                    "Max Iterations (${step1.size}) Reached"
                }

                _rootFindingState.update {
                    if (method in listOf("Bisection", "False Position")) {
                        @Suppress("UNCHECKED_CAST")
                        val finalized = it.copy(
                            bracketingResults = step1 as List<com.numerical.analysis.solver.domain.BracketingStep>,
                            isConverged = conv, rootResult = res, stoppingReason = reason, isLoading = false
                        )
                        if (conv) saveHistory(resultEntry)
                        finalized
                    } else {
                        @Suppress("UNCHECKED_CAST")
                        val finalized = it.copy(
                            openMethodsResults = step1 as List<com.numerical.analysis.solver.domain.OpenMethodsStep>,
                            isConverged = conv, rootResult = res, stoppingReason = reason, isLoading = false
                        )
                        if (conv) saveHistory(resultEntry)
                        finalized
                    }
                }
                _navigationEvents.emit("root_finding_results/$method")
            } catch (e: Exception) {
                _rootFindingState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Invalid input") }
            }
        }
    }

    fun updateLinearSystemInput(matrixSize: Int? = null, method: String? = null) {
        _linearSystemState.update {
            val newSize = matrixSize ?: it.matrixSize
            val newA    = if (matrixSize != null) Array(newSize) { DoubleArray(newSize) } else it.matrixA
            val newB    = if (matrixSize != null) DoubleArray(newSize) else it.vectorB
            it.copy(matrixSize = newSize, matrixA = newA, vectorB = newB,
                    method = method ?: it.method, errorMessage = null, result = null)
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
        calculationJob?.cancel()
        calculationJob = viewModelScope.launch {
            _linearSystemState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val state = _linearSystemState.value
                val result = withContext(Dispatchers.Default) {
                    when (state.method) {
                        "gauss"        -> linearAlgebraMethods.gaussElimination(state.matrixA, state.vectorB)
                        "lu"           -> linearAlgebraMethods.luDecomposition(state.matrixA, state.vectorB)
                        "cramer"       -> linearAlgebraMethods.cramersRule(state.matrixA, state.vectorB)
                        "gauss-jordan" -> linearAlgebraMethods.gaussJordan(state.matrixA, state.vectorB)
                        else           -> linearAlgebraMethods.gaussElimination(state.matrixA, state.vectorB)
                    }
                }
                _linearSystemState.update {
                    it.copy(result = result, isLoading = false,
                            errorMessage = if (result.isSuccessful) null else result.errorMessage)
                }
                if (result.isSuccessful) {
                    _navigationEvents.emit("linear_systems_results")
                    val resultString = result.solution
                        .mapIndexed { idx, v -> "x${idx + 1}=${String.format(Locale.US, "%.2f", v)}" }
                        .joinToString(", ")
                    saveHistory(HistoryEntry(
                        title       = state.method.replaceFirstChar { it.uppercase() },
                        subtitle    = "${state.matrixA.size}x${state.matrixA.size} system",
                        result      = resultString,
                        timestamp   = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(Date()),
                        accentColor = Color(0xFF1586EF),
                        methodType  = state.method
                    ))
                }
            } catch (e: Exception) {
                _linearSystemState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Invalid matrix setup") }
            }
        }
    }

    fun updateOptimizationInput(
        equation: String? = null,
        xl: String? = null,
        xu: String? = null,
        numIterations: String? = null,
        isMax: Boolean? = null
    ) {
        _optimizationState.update {
            it.copy(
                equation     = equation      ?: it.equation,
                xl           = xl            ?: it.xl,
                xu           = xu            ?: it.xu,
                numIterations = numIterations ?: it.numIterations,
                isMax        = isMax         ?: it.isMax,
                errorMessage = null
            )
        }
    }

    fun solveOptimization() {
        calculationJob?.cancel()
        calculationJob = viewModelScope.launch {
            _optimizationState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                val state = _optimizationState.value
                val results = withContext(Dispatchers.Default) {
                    val iters   = state.numIterations.toIntOrNull() ?: 10
                    val xl      = state.xl.toDouble()
                    val xu      = state.xu.toDouble()
                    val f       = mathParser.parseFunction(state.equation)
                    optimizationMethods.goldenSectionSearch(state.equation, xl, xu, iters, state.isMax, f)
                }

                if (results.isEmpty()) throw Exception("Zero iterations computed.")
                val resOpt = results.last().xOpt

                _optimizationState.update {
                    it.copy(steps = results, resultOpt = resOpt, isConverged = true, isLoading = false)
                }

                _navigationEvents.emit("golden_section_results")

                saveHistory(HistoryEntry(
                    title       = "Golden Section Search",
                    subtitle    = "${if (state.isMax) "Max" else "Min"} of f(x) = ${state.equation}",
                    result      = "x ≈ ${String.format(Locale.US, "%.5f", resOpt)}",
                    timestamp   = SimpleDateFormat("dd MMM, hh:mm a", Locale.US).format(Date()),
                    accentColor = Color(0xFFF59E0B),
                    equation    = state.equation,
                    xl          = state.xl,
                    xu          = state.xu,
                    maxIterations = state.numIterations,
                    methodType  = "golden_section"
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