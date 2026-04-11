package com.numerical.analysis.solver.ui.theme.state

import androidx.lifecycle.ViewModel
import com.numerical.analysis.solver.domain.RootFindingMethods
import com.numerical.analysis.solver.ui.screens.state.LinearSystemState
import com.numerical.analysis.solver.ui.screens.state.RootFindingState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import com.numerical.analysis.solver.domain.LinearAlgebraMethods
import com.numerical.analysis.solver.domain.MathParser
import kotlinx.coroutines.delay
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SolverViewModel : ViewModel() {

    private val rootFindingMethods = RootFindingMethods()
    private val linearAlgebraMethods = LinearAlgebraMethods()
    private val mathParser = MathParser()

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
            delay(500) // Cinematic delay
            try {
                val state = _rootFindingState.value
                val eps = state.eps.toDoubleOrNull() ?: 1e-6
                val maxIter = state.maxIterations.toIntOrNull() ?: 100
                val f = mathParser.parseFunction(state.equation)

                when (method) {
                    "Bisection" -> {
                        val xl = state.xl.toDouble()
                        val xu = state.xu.toDouble()
                        val results = rootFindingMethods.bisection(xl, xu, eps, f)
                        _rootFindingState.update { 
                            it.copy(bracketingResults = results, isConverged = results.isNotEmpty() && results.last().error <= eps, rootResult = results.lastOrNull()?.xr, isLoading = false) 
                        }
                    }
                    "False Position" -> {
                        val xl = state.xl.toDouble()
                        val xu = state.xu.toDouble()
                        val results = rootFindingMethods.falsePosition(xl, xu, eps, f)
                        _rootFindingState.update { 
                            it.copy(bracketingResults = results, isConverged = results.isNotEmpty() && results.last().error <= eps, rootResult = results.lastOrNull()?.xr, isLoading = false) 
                        }
                    }
                    "Newton" -> {
                        val xi = state.xi.toDouble()
                        val fDash = mathParser.parseDerivative(state.equation)
                        val results = rootFindingMethods.newton(xi, eps, maxIter, f, fDash)
                        _rootFindingState.update { 
                            it.copy(openMethodsResults = results, isConverged = results.isNotEmpty() && results.last().error <= eps, rootResult = results.lastOrNull()?.xiPlus1, isLoading = false) 
                        }
                    }
                    "Fixed Point" -> {
                        val xi = state.xi.toDouble()
                        // fixedPoint expects g(x), if user put g(x) in equation field then f acts as g.
                        val results = rootFindingMethods.fixedPoint(xi, eps, f)
                        _rootFindingState.update { 
                            it.copy(openMethodsResults = results, isConverged = results.isNotEmpty() && results.last().error <= eps, rootResult = results.lastOrNull()?.xiPlus1, isLoading = false) 
                        }
                    }
                    "Secant" -> {
                        val xi = state.xi.toDouble()
                        val xMinus1 = state.xMinus1.toDouble()
                        val results = rootFindingMethods.secant(xMinus1, xi, eps, f)
                        _rootFindingState.update { 
                            it.copy(openMethodsResults = results, isConverged = results.isNotEmpty() && results.last().error <= eps, rootResult = results.lastOrNull()?.xiPlus1, isLoading = false) 
                        }
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
            delay(500)
            try {
                val state = _linearSystemState.value
                val result = when (state.method) {
                    "gauss" -> linearAlgebraMethods.gaussElimination(state.matrixA, state.vectorB)
                    "lu" -> linearAlgebraMethods.luDecomposition(state.matrixA, state.vectorB)
                    "cramer" -> linearAlgebraMethods.cramersRule(state.matrixA, state.vectorB)
                    "gauss-jordan" -> linearAlgebraMethods.gaussJordan(state.matrixA, state.vectorB)
                    else -> linearAlgebraMethods.gaussElimination(state.matrixA, state.vectorB)
                }
                _linearSystemState.update { it.copy(result = result, isLoading = false, errorMessage = if (result.isSuccessful) null else result.errorMessage) }
            } catch (e: Exception) {
                _linearSystemState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Error solving system") }
            }
        }
    }
}