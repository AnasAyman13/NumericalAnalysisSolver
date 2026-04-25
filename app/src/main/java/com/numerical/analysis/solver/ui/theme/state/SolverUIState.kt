package com.numerical.analysis.solver.ui.screens.state

import com.numerical.analysis.solver.domain.BracketingStep
import com.numerical.analysis.solver.domain.LinearSystemResult
import com.numerical.analysis.solver.domain.OpenMethodsStep

enum class ToleranceMode {
    ABSOLUTE,
    PERCENTAGE
}

data class RootFindingState(
    val equation: String = "",
    val derivative: String = "",
    val xl: String = "",
    val xu: String = "",
    val xi: String = "",
    val xMinus1: String = "",
    val eps: String = "",
    val toleranceMode: ToleranceMode = ToleranceMode.PERCENTAGE,
    val bracketingResults: List<BracketingStep> = emptyList(),
    val openMethodsResults: List<OpenMethodsStep> = emptyList(),
    val errorMessage: String? = null,
    val maxIterations: String = "100",
    val isLoading: Boolean = false,
    val isConverged: Boolean = false,
    val rootResult: Double? = null,
    val stoppingReason: String? = null
)

data class LinearSystemState(
    val matrixSize: Int = 3,
    val matrixA: Array<DoubleArray> = Array(3) { DoubleArray(3) },
    val vectorB: DoubleArray = DoubleArray(3),
    val result: LinearSystemResult? = null,
    val errorMessage: String? = null,
    val method: String = "gauss",
    val isLoading: Boolean = false
)