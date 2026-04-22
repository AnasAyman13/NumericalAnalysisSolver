package com.numerical.analysis.solver.data

import androidx.compose.ui.graphics.Color

data class HistoryEntry(
    val id: Long = 0,
    val title: String,
    val subtitle: String,
    val result: String,
    val timestamp: String,
    val accentColor: Color
)
