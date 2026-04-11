package com.numerical.analysis.solver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.numerical.analysis.solver.navigation.AppNavGraph
import com.numerical.analysis.solver.ui.theme.NumericalAnalysisSolverTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NumericalAnalysisSolverTheme {
                // Call the unified navigation graph
                AppNavGraph()
            }
        }
    }
}