package com.numerical.analysis.solver.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.collect
import com.numerical.analysis.solver.ui.state.SolverViewModel
import com.numerical.analysis.solver.ui.screens.splash.SplashScreen
import com.numerical.analysis.solver.ui.screens.about.AboutScreen
import com.numerical.analysis.solver.ui.screens.home.HomeScreen
import com.numerical.analysis.solver.ui.screens.history.HistoryScreen
import com.numerical.analysis.solver.ui.screens.history.HistoryDetailScreen
import com.numerical.analysis.solver.ui.screens.rootfinding.RootFindingScreen
import com.numerical.analysis.solver.ui.screens.rootfinding.RootFindingResultsScreen
import com.numerical.analysis.solver.ui.screens.linearsystems.LinearSystemScreen
import com.numerical.analysis.solver.ui.screens.linearsystems.LinearSystemResultsScreen
import com.numerical.analysis.solver.ui.screens.optimization.GoldenSectionScreen
import com.numerical.analysis.solver.ui.screens.optimization.GoldenSectionResultsScreen

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

// Shared animation duration used on every transition
private const val ANIM_MS = 250

@Composable
fun AppNavGraph(
    isDarkTheme: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {
    val navController    = rememberNavController()
    val solverViewModel: SolverViewModel = viewModel()

    val getTabIndex = { route: String? ->
        when (route) {
            HOME -> 0
            HISTORY -> 1
            ABOUT -> 2
            else -> 3
        }
    }

    androidx.compose.runtime.LaunchedEffect(Unit) {
        solverViewModel.navigationEvents.collect { route ->
            navController.navigate(route)
        }
    }

    NavHost(
        navController    = navController,
        startDestination = SPLASH,
        enterTransition  = {
            val initialIndex = getTabIndex(initialState.destination.route)
            val targetIndex = getTabIndex(targetState.destination.route)
            val direction = if (targetIndex >= initialIndex) {
                AnimatedContentTransitionScope.SlideDirection.Left
            } else {
                AnimatedContentTransitionScope.SlideDirection.Right
            }
            slideIntoContainer(direction, animationSpec = tween(ANIM_MS)) + fadeIn(animationSpec = tween(ANIM_MS))
        },
        exitTransition   = {
            val initialIndex = getTabIndex(initialState.destination.route)
            val targetIndex = getTabIndex(targetState.destination.route)
            val direction = if (targetIndex >= initialIndex) {
                AnimatedContentTransitionScope.SlideDirection.Left
            } else {
                AnimatedContentTransitionScope.SlideDirection.Right
            }
            slideOutOfContainer(direction, animationSpec = tween(ANIM_MS)) + fadeOut(animationSpec = tween(ANIM_MS))
        },
        popEnterTransition = {
            val initialIndex = getTabIndex(initialState.destination.route)
            val targetIndex = getTabIndex(targetState.destination.route)
            val direction = if (targetIndex >= initialIndex) {
                AnimatedContentTransitionScope.SlideDirection.Left
            } else {
                AnimatedContentTransitionScope.SlideDirection.Right
            }
            slideIntoContainer(direction, animationSpec = tween(ANIM_MS)) + fadeIn(animationSpec = tween(ANIM_MS))
        },
        popExitTransition = {
            val initialIndex = getTabIndex(initialState.destination.route)
            val targetIndex = getTabIndex(targetState.destination.route)
            val direction = if (targetIndex >= initialIndex) {
                AnimatedContentTransitionScope.SlideDirection.Left
            } else {
                AnimatedContentTransitionScope.SlideDirection.Right
            }
            slideOutOfContainer(direction, animationSpec = tween(ANIM_MS)) + fadeOut(animationSpec = tween(ANIM_MS))
        }
    ) {

        composable(SPLASH) {
            SplashScreen(onAnimationFinished = {
                navController.navigate(HOME) {
                    popUpTo(SPLASH) { inclusive = true }
                }
            })
        }

        composable(HOME) {
            HomeScreen(
                currentRoute  = HOME,
                isDarkTheme   = isDarkTheme,
                onToggleTheme = onToggleTheme,
                onNavigate    = { route ->
                    if (route != HOME) {
                        navController.navigate(route) {
                            popUpTo(HOME) { saveState = true }
                            launchSingleTop = true
                        }
                    }
                },
                onChapterClick = { chapterTitle ->
                    when {
                        chapterTitle.contains("Root Finding",   ignoreCase = true) -> navController.navigate(ROOT_FINDING)
                        chapterTitle.contains("Linear Systems", ignoreCase = true) -> navController.navigate(LINEAR_SYSTEMS)
                        chapterTitle.contains("Optimization",  ignoreCase = true) -> navController.navigate(GOLDEN_SECTION)
                        chapterTitle.contains("Golden",        ignoreCase = true) -> navController.navigate(GOLDEN_SECTION)
                    }
                }
            )
        }

        composable(HISTORY) {
            HistoryScreen(
                isDarkTheme  = isDarkTheme,
                viewModel    = solverViewModel,
                currentRoute = HISTORY,
                onNavigate   = { route ->
                    if (route != HISTORY) {
                        navController.navigate(route) {
                            popUpTo(HOME) { saveState = true }
                            launchSingleTop = true
                        }
                    }
                },
                onEntryClick = { entry ->
                    solverViewModel.selectHistoryEntry(entry)
                    navController.navigate(HISTORY_DETAIL)
                }
            )
        }

        composable(HISTORY_DETAIL) {
            HistoryDetailScreen(
                viewModel     = solverViewModel,
                onBack        = { navController.popBackStack() },
                onRecalculate = { solverRoute ->
                    navController.popBackStack(HISTORY, inclusive = false)
                    navController.navigate(solverRoute)
                }
            )
        }

        composable(ABOUT) {
            AboutScreen(onNavigateBack = { navController.popBackStack() })
        }

        composable(ROOT_FINDING) {
            RootFindingScreen(
                viewModel       = solverViewModel,
                onBack          = { 
                    solverViewModel.cancelCalculation()
                    navController.popBackStack() 
                },
                onSolveComplete = { method ->
                    navController.navigate("root_finding_results/$method")
                }
            )
        }

        composable(
            route     = ROOT_FINDING_RESULTS,
            arguments = listOf(navArgument("method") { type = NavType.StringType })
        ) { backStackEntry ->
            val method = backStackEntry.arguments?.getString("method") ?: "Bisection"
            RootFindingResultsScreen(
                viewModel = solverViewModel,
                method    = method,
                onBack    = { navController.popBackStack() }
            )
        }

        composable(LINEAR_SYSTEMS) {
            LinearSystemScreen(
                viewModel       = solverViewModel,
                onBack          = { 
                    solverViewModel.cancelCalculation()
                    navController.popBackStack() 
                },
                onSolveComplete = { navController.navigate(LINEAR_RESULTS) }
            )
        }

        composable(LINEAR_RESULTS) {
            LinearSystemResultsScreen(
                viewModel        = solverViewModel,
                onBack           = { navController.popBackStack() },
                onNewCalculation = { navController.popBackStack(LINEAR_SYSTEMS, inclusive = false) }
            )
        }

        composable(GOLDEN_SECTION) {
            GoldenSectionScreen(
                viewModel       = solverViewModel,
                onBack          = { 
                    solverViewModel.cancelCalculation()
                    navController.popBackStack() 
                },
                onSolveComplete = { navController.navigate(GOLDEN_RESULTS) }
            )
        }

        composable(GOLDEN_RESULTS) {
            GoldenSectionResultsScreen(
                viewModel = solverViewModel,
                onBack    = { navController.popBackStack() }
            )
        }
    }
}

