package com.numerical.analysis.solver.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.numerical.analysis.solver.ui.theme.screens.home.HomeScreen
import com.numerical.analysis.solver.ui.screens.splash.SplashScreen

import androidx.lifecycle.viewmodel.compose.viewModel
import com.numerical.analysis.solver.ui.theme.state.SolverViewModel
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.RootFindingScreen
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.RootFindingResultsScreen
import com.numerical.analysis.solver.ui.theme.screens.linearsystems.LinearSystemScreen
import com.numerical.analysis.solver.ui.theme.screens.linearsystems.LinearSystemResultsScreen
import com.numerical.analysis.solver.ui.theme.screens.optimization.GoldenSectionScreen
import com.numerical.analysis.solver.ui.theme.screens.optimization.GoldenSectionResultsScreen
import androidx.navigation.NavType
import androidx.navigation.navArgument

@Composable
fun AppNavGraph(
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {
    val navController = rememberNavController()
    val solverViewModel: SolverViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") {
            SplashScreen(onAnimationFinished = {
                navController.navigate("home") {
                    popUpTo("splash") { inclusive = true }
                }
            })
        }

        composable("home") {
            HomeScreen(
                currentRoute = "home",
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onNavigate = { route ->
                    if (route != "home") {
                        navController.navigate(route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                        }
                    }
                },
                onChapterClick = { chapterTitle ->
                    if (chapterTitle.contains("Root Finding", ignoreCase = true)) {
                        navController.navigate("root_finding")
                    } else if (chapterTitle.contains("Linear Systems", ignoreCase = true)) {
                        navController.navigate("linear_systems")
                    } else if (chapterTitle.contains("Golden Section", ignoreCase = true)) {
                        navController.navigate("golden_section")
                    }
                }
            )
        }

        composable("history") {
            com.numerical.analysis.solver.ui.theme.screens.history.HistoryScreen(
                isDarkTheme  = isDarkTheme,
                viewModel = solverViewModel,
                currentRoute = "history",
                onNavigate   = { route ->
                    if (route != "history") {
                        navController.navigate(route) {
                            popUpTo("home") { saveState = true }
                            launchSingleTop = true
                        }
                    }
                }
            )
        }

        composable("about") {
            com.numerical.analysis.solver.ui.screens.about.AboutScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        composable("root_finding") {
            RootFindingScreen(
                viewModel = solverViewModel,
                onBack = { navController.popBackStack() },
                onSolveComplete = { method ->
                    navController.navigate("root_finding_results/$method")
                }
            )
        }

        composable(
            route = "root_finding_results/{method}",
            arguments = listOf(navArgument("method") { type = NavType.StringType })
        ) { backStackEntry ->
            val method = backStackEntry.arguments?.getString("method") ?: "Bisection"
            RootFindingResultsScreen(
                viewModel = solverViewModel,
                method = method,
                onBack = { navController.popBackStack() }
            )
        }

        composable("linear_systems") {
            LinearSystemScreen(
                viewModel = solverViewModel,
                onBack = { navController.popBackStack() },
                onSolveComplete = {
                    navController.navigate("linear_systems_results")
                }
            )
        }

        composable("linear_systems_results") {
            LinearSystemResultsScreen(
                viewModel = solverViewModel,
                onBack = { navController.popBackStack() },
                onNewCalculation = {
                    navController.popBackStack("linear_systems", inclusive = false)
                }
            )
        }

        composable("golden_section") {
            GoldenSectionScreen(
                viewModel = solverViewModel,
                onBack = { navController.popBackStack() },
                onSolveComplete = {
                    navController.navigate("golden_section_results")
                }
            )
        }

        composable("golden_section_results") {
            GoldenSectionResultsScreen(
                viewModel = solverViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}