package com.numerical.analysis.solver.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.numerical.analysis.solver.ui.theme.state.SolverViewModel
import com.numerical.analysis.solver.ui.screens.splash.SplashScreen
import com.numerical.analysis.solver.ui.screens.about.AboutScreen
import com.numerical.analysis.solver.ui.theme.screens.home.HomeScreen
import com.numerical.analysis.solver.ui.theme.screens.history.HistoryScreen
import com.numerical.analysis.solver.ui.theme.screens.history.HistoryDetailScreen
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.RootFindingScreen
import com.numerical.analysis.solver.ui.theme.screens.rootfinding.RootFindingResultsScreen
import com.numerical.analysis.solver.ui.theme.screens.linearsystems.LinearSystemScreen
import com.numerical.analysis.solver.ui.theme.screens.linearsystems.LinearSystemResultsScreen
import com.numerical.analysis.solver.ui.theme.screens.optimization.GoldenSectionScreen
import com.numerical.analysis.solver.ui.theme.screens.optimization.GoldenSectionResultsScreen

// ─────────────────────────────────────────────────────────────────────────────
//  Route Strings — keep them as simple constants so every screen refers to the
//  same string and typos are caught at compile time.
// ─────────────────────────────────────────────────────────────────────────────
private const val SPLASH               = "splash"
private const val HOME                 = "home"
private const val HISTORY              = "history"
private const val HISTORY_DETAIL       = "history_detail"
private const val ABOUT                = "about"
private const val ROOT_FINDING         = "root_finding"
private const val ROOT_FINDING_RESULTS = "root_finding_results/{method}"
private const val LINEAR_SYSTEMS       = "linear_systems"
private const val LINEAR_RESULTS       = "linear_systems_results"
private const val GOLDEN_SECTION       = "golden_section"
private const val GOLDEN_RESULTS       = "golden_section_results"

@Composable
fun AppNavGraph(
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {
    val navController = rememberNavController()

    // One shared ViewModel for the entire app — simple and easy to explain.
    val solverViewModel: SolverViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = SPLASH
    ) {

        // ── Splash ───────────────────────────────────────────────────────────
        composable(SPLASH) {
            SplashScreen(onAnimationFinished = {
                navController.navigate(HOME) {
                    popUpTo(SPLASH) { inclusive = true }
                }
            })
        }

        // ── Home ─────────────────────────────────────────────────────────────
        composable(HOME) {
            HomeScreen(
                currentRoute = HOME,
                isDarkTheme = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onNavigate = { route ->
                    if (route != HOME) {
                        navController.navigate(route) {
                            popUpTo(HOME) { saveState = true }
                            launchSingleTop = true
                        }
                    }
                },
                onChapterClick = { chapterTitle ->
                    when {
                        chapterTitle.contains("Root Finding",    ignoreCase = true) -> navController.navigate(ROOT_FINDING)
                        chapterTitle.contains("Linear Systems",  ignoreCase = true) -> navController.navigate(LINEAR_SYSTEMS)
                        chapterTitle.contains("Golden Section",  ignoreCase = true) -> navController.navigate(GOLDEN_SECTION)
                    }
                }
            )
        }

        // ── History list ─────────────────────────────────────────────────────
        composable(HISTORY) {
            HistoryScreen(
                isDarkTheme  = isDarkTheme,
                viewModel    = solverViewModel,
                currentRoute = HISTORY,
                onNavigate = { route ->
                    if (route != HISTORY) {
                        navController.navigate(route) {
                            popUpTo(HOME) { saveState = true }
                            launchSingleTop = true
                        }
                    }
                },
                // Step 1: store the tapped entry in the ViewModel
                // Step 2: navigate to the detail screen
                onEntryClick = { entry ->
                    solverViewModel.selectHistoryEntry(entry)
                    navController.navigate(HISTORY_DETAIL)
                }
            )
        }

        // ── History detail ───────────────────────────────────────────────────
        // Simple route — no arguments needed because the entry is in the ViewModel
        composable(HISTORY_DETAIL) {
            HistoryDetailScreen(
                viewModel = solverViewModel,
                onBack = { navController.popBackStack() },
                onRecalculate = { solverRoute ->
                    // Pop back to the history list, then open the correct solver
                    navController.popBackStack(HISTORY, inclusive = false)
                    navController.navigate(solverRoute)
                }
            )
        }

        // ── About ────────────────────────────────────────────────────────────
        composable(ABOUT) {
            AboutScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // ── Root Finding input ────────────────────────────────────────────────
        composable(ROOT_FINDING) {
            RootFindingScreen(
                viewModel = solverViewModel,
                onBack = { navController.popBackStack() },
                onSolveComplete = { method ->
                    navController.navigate("root_finding_results/$method")
                }
            )
        }

        // ── Root Finding results ──────────────────────────────────────────────
        composable(
            route = ROOT_FINDING_RESULTS,
            arguments = listOf(navArgument("method") { type = NavType.StringType })
        ) { backStackEntry ->
            val method = backStackEntry.arguments?.getString("method") ?: "Bisection"
            RootFindingResultsScreen(
                viewModel = solverViewModel,
                method    = method,
                onBack    = { navController.popBackStack() }
            )
        }

        // ── Linear Systems input ──────────────────────────────────────────────
        composable(LINEAR_SYSTEMS) {
            LinearSystemScreen(
                viewModel = solverViewModel,
                onBack    = { navController.popBackStack() },
                onSolveComplete = {
                    navController.navigate(LINEAR_RESULTS)
                }
            )
        }

        // ── Linear Systems results ────────────────────────────────────────────
        composable(LINEAR_RESULTS) {
            LinearSystemResultsScreen(
                viewModel        = solverViewModel,
                onBack           = { navController.popBackStack() },
                onNewCalculation = {
                    navController.popBackStack(LINEAR_SYSTEMS, inclusive = false)
                }
            )
        }

        // ── Golden Section input ──────────────────────────────────────────────
        composable(GOLDEN_SECTION) {
            GoldenSectionScreen(
                viewModel = solverViewModel,
                onBack    = { navController.popBackStack() },
                onSolveComplete = {
                    navController.navigate(GOLDEN_RESULTS)
                }
            )
        }

        // ── Golden Section results ────────────────────────────────────────────
        composable(GOLDEN_RESULTS) {
            GoldenSectionResultsScreen(
                viewModel = solverViewModel,
                onBack    = { navController.popBackStack() }
            )
        }
    }
}