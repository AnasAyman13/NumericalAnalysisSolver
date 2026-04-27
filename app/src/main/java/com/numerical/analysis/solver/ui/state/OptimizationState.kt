package com.numerical.analysis.solver.ui.state

import com.numerical.analysis.solver.domain.methods.GoldenSectionStep

data class OptimizationState(
    val equation: String = "x^2 - 4*x + 4",
    val xl: String = "0",
    val xu: String = "4",
    val numIterations: String = "10",
    val isMax: Boolean = true,
    
    val steps: List<GoldenSectionStep> = emptyList(),
    val resultOpt: Double? = null,
    val isConverged: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)


