package com.numerical.analysis.solver.domain.solvers

import com.numerical.analysis.solver.domain.methods.OpenMethodsStep
import com.numerical.analysis.solver.ui.state.ToleranceMode
import kotlinx.coroutines.yield
import kotlin.math.abs

object FixedPointSolver {

    private fun round(value: Double): Double {
        return Math.round(value * 100000.0) / 100000.0
    }

    suspend fun solve(
        x0: Double,
        eps: Double,
        maxIter: Int,
        mode: ToleranceMode,
        g: (Double) -> Double
    ): List<OpenMethodsStep> {
        val steps = mutableListOf<OpenMethodsStep>()
        var xiMinus1 = 0.0
        var xi = x0
        var iter = 0

        while (iter <= maxIter) {
            yield()

            // Calculate Error of the CURRENT xi (compared to xiMinus1)
            val error = if (iter == 0) {
                0.0
            } else {
                val rawError = if (mode == ToleranceMode.ABSOLUTE) {
                    abs(xi - xiMinus1)
                } else {
                    if (abs(xi) < 1e-18) 0.0 else abs((xi - xiMinus1) / xi) * 100.0
                }
                round(rawError)
            }

            // Calculate NEXT xi (Xi+1)
            val xiPlus1 = round(g(xi))

            steps.add(
                OpenMethodsStep(
                    iter = iter,
                    xi = xi,
                    xiPlus1 = xiPlus1,
                    fXi = 0.0, // not applicable for pure fixed point
                    error = error
                )
            )

            // Stopping Criteria uses the error of the current xi
            // This ensures the final step added to the table has error <= eps
            if (iter > 0 && error <= eps) break
            if (iter > 0 && abs(xi - xiMinus1) < 1e-12) break

            xiMinus1 = xi
            xi = xiPlus1
            iter++
        }
        return steps
    }
}
