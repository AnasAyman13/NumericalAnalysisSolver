package com.numerical.analysis.solver

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.numerical.analysis.solver.navigation.AppNavGraph
import com.numerical.analysis.solver.ui.theme.NumericalAnalysisSolverTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Let Compose manage all insets (including IME / keyboard)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            // Hoist dark mode state at the very top so the toggle can flip the entire theme
            var darkTheme by rememberSaveable { mutableStateOf(false) }

            NumericalAnalysisSolverTheme(darkTheme = darkTheme) {
                AppNavGraph(
                    isDarkTheme = darkTheme,
                    onToggleTheme = { darkTheme = !darkTheme }
                )
            }
        }
    }
}