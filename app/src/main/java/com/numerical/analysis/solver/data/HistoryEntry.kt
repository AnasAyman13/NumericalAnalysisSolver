package com.numerical.analysis.solver.data

import androidx.compose.ui.graphics.Color

data class HistoryEntry(
    val id: Long = 0,
    val title: String,
    val subtitle: String,
    val result: String,
    val timestamp: String,
    val accentColor: Color,
    // Full input parameters saved for re-run restoration
    val equation: String = "",
    val derivative: String = "",
    val xl: String = "",
    val xu: String = "",
    val xi: String = "",
    val xMinus1: String = "",
    val eps: String = "",
    val maxIterations: String = "100",
    val methodType: String = ""
)

