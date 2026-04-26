package com.numerical.analysis.solver.domain

// Models for Chapter 1: Root Finding Methods
data class BracketingStep(
    val iter: Int,
    val xl: Double,
    val fXl: Double,
    val xu: Double,
    val fXu: Double,
    val xr: Double,
    val fXr: Double,
    val error: Double
)

data class OpenMethodsStep(
    val iter: Int,
    val xi: Double,
    val xiPlus1: Double,
    val fXi: Double,
    val error: Double
)

// Models for Chapter 2: Linear Algebraic Equations
data class LinearSystemResult(
    val solution: DoubleArray,
    val isSuccessful: Boolean,
    val errorMessage: String? = null
)

// Models for Chapter 3: Optimization
data class GoldenSectionStep(
    val iter: Int,
    val xl: Double,
    val xu: Double,
    val d: Double,
    val x1: Double,
    val x2: Double,
    val fX1: Double,
    val fX2: Double,
    val fXl: Double,
    val fXu: Double,
    val xOpt: Double,
    val fOpt: Double
)