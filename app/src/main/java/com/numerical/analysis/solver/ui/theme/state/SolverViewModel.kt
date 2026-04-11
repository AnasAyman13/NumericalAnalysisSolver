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

class SolverViewModel : ViewModel() {

    private val rootFindingMethods = RootFindingMethods()
    private val linearAlgebraMethods = LinearAlgebraMethods()

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
        eps: String? = null
    ) {
        _rootFindingState.update { currentState ->
            currentState.copy(
                equation = equation ?: currentState.equation,
                xl = xl ?: currentState.xl,
                xu = xu ?: currentState.xu,
                xi = xi ?: currentState.xi,
                xMinus1 = xMinus1 ?: currentState.xMinus1,
                eps = eps ?: currentState.eps,
                errorMessage = null
            )
        }
    }

    fun solveBisection() {
        try {
            val state = _rootFindingState.value
            val xl = state.xl.toDouble()
            val xu = state.xu.toDouble()
            val eps = state.eps.toDouble()

            val f = parseEquation(state.equation)

            val results = rootFindingMethods.bisection(xl, xu, eps, f)
            _rootFindingState.update { it.copy(bracketingResults = results, errorMessage = null) }
        } catch (e: Exception) {
            _rootFindingState.update { it.copy(errorMessage = "Invalid input or equation format") }
        }
    }

    fun solveGaussElimination() {
        try {
            val state = _linearSystemState.value
            val result = linearAlgebraMethods.gaussElimination(state.matrixA, state.vectorB)
            _linearSystemState.update { it.copy(result = result, errorMessage = null) }
        } catch (e: Exception) {
            _linearSystemState.update { it.copy(errorMessage = "Error solving the system") }
        }
    }

    private fun parseEquation(equation: String): (Double) -> Double {
        return { x ->
            var result = 0.0
            result
        }
    }
}