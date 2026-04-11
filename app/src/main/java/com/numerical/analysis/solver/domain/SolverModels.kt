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