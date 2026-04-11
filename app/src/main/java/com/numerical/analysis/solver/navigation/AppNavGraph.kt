package com.numerical.analysis.solver.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.numerical.analysis.solver.ui.screens.dashboard.HomeScreen
import com.numerical.analysis.solver.ui.screens.splash.SplashScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "splash" // Starts with your "Explosion" Splash
    ) {
        composable("splash") {
            SplashScreen(onAnimationFinished = {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true } // Kill splash so back button doesn't go back to it
                }
            })
        }

        composable("home") {
            HomeScreen(onChapterClick = { chapterTitle ->
                // Future: navController.navigate("chapter_detail/$chapterTitle")
            })
        }
    }
}