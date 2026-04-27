package com.numerical.analysis.solver.domain.solvers

import com.numerical.analysis.solver.domain.methods.OpenMethodsStep
import com.numerical.analysis.solver.ui.state.ToleranceMode
import kotlinx.coroutines.yield
import kotlin.math.abs

object SecantSolver {

    private fun round(value: Double): Double {
        return Math.round(value * 100000.0) / 100000.0
    }

    suspend fun solve(
        xMinus1: Double,
        x0: Double,
        eps: Double,
        maxIter: Int,
        mode: ToleranceMode,
        f: (Double) -> Double
    ): List<OpenMethodsStep> {
        val steps = mutableListOf<OpenMethodsStep>()
        var xiMinus1 = xMinus1
        var xi = x0
        var error = 0.0
        var iter = 0

        while (iter <= maxIter) {
            yield()
            
            val fXiMinus1 = round(f(xiMinus1))
            val fXi = round(f(xi))

            val denom = fXiMinus1 - fXi
            if (abs(denom) < 1e-20) break

            // Xi+1 Calculation + Rounding
            val xiNext = round(xi - (fXi * (xiMinus1 - xi)) / denom)

            if (iter > 0) {
                // Error Calculation + Rounding
                val rawError = if (mode == ToleranceMode.ABSOLUTE) {
                    abs(xi - xiMinus1)
                } else {
                    if (abs(xi) < 1e-18) 0.0 else abs((xi - xiMinus1) / xi) * 100.0
                }
                error = round(rawError)
            }

            steps.add(
                OpenMethodsStep(
                    iter = iter,
                    xi = xiMinus1,
                    xiPlus1 = xi,
                    fXi = fXi,
                    error = if (iter == 0) 0.0 else error
                )
            )

            // Stopping Criteria
            if (iter > 0 && error <= eps) break
            if (abs(fXi) < 1e-12) break

            xiMinus1 = xi
            xi = xiNext
            iter++
        }
        return steps
    }
}
