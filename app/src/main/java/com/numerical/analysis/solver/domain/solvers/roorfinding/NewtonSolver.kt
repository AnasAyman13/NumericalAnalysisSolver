package com.numerical.analysis.solver.domain.solvers

import com.numerical.analysis.solver.domain.methods.OpenMethodsStep
import com.numerical.analysis.solver.ui.state.ToleranceMode
import kotlinx.coroutines.yield
import kotlin.math.abs

object NewtonSolver {

    private fun round(value: Double): Double {
        return Math.round(value * 100000.0) / 100000.0
    }

    suspend fun solve(
        x0: Double,
        eps: Double,
        maxIter: Int,
        mode: ToleranceMode,
        f: (Double) -> Double,
        fDash: (Double) -> Double
    ): List<OpenMethodsStep> {
        val steps = mutableListOf<OpenMethodsStep>()
        var xi = x0
        var xiPlus1 = 0.0
        var error = 0.0
        var iter = 0

        while (iter <= maxIter) {
            yield()
            val fXi = round(f(xi))
            val dXi = round(fDash(xi))
            
            if (abs(dXi) < 1e-20) break

            // Xi+1 Calculation + Rounding
            xiPlus1 = round(xi - (fXi / dXi))

            if (iter > 0) {
                // Error Calculation + Rounding
                val rawError = if (mode == ToleranceMode.ABSOLUTE) {
                    abs(xiPlus1 - xi)
                } else {
                    if (abs(xiPlus1) < 1e-18) 0.0 else abs((xiPlus1 - xi) / xiPlus1) * 100.0
                }
                error = round(rawError)
            }

            steps.add(
                OpenMethodsStep(
                    iter = iter,
                    xi = xi,
                    xiPlus1 = xiPlus1,
                    fXi = fXi,
                    error = if (iter == 0) 0.0 else error
                )
            )

            // Stopping Criteria
            if (iter > 0 && error <= eps) break
            if (abs(fXi) < 1e-12) break

            xi = xiPlus1
            iter++
        }
        return steps
    }
}
