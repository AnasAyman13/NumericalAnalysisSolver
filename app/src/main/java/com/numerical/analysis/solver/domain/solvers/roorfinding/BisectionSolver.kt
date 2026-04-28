package com.numerical.analysis.solver.domain.solvers

import com.numerical.analysis.solver.domain.methods.BracketingStep
import com.numerical.analysis.solver.ui.state.ToleranceMode
import kotlinx.coroutines.yield
import kotlin.math.abs

object BisectionSolver {

    private fun round(value: Double): Double {
        return Math.round(value * 100000.0) / 100000.0
    }

    suspend fun solve(
        lowerBound: Double,
        upperBound: Double,
        eps: Double,
        maxIter: Int,
        mode: ToleranceMode,
        f: (Double) -> Double
    ): List<BracketingStep> {
        val steps = mutableListOf<BracketingStep>()
        var xl = lowerBound
        var xu = upperBound
        var xr = 0.0
        var xrOld = 0.0
        var error = 0.0
        var iter = 1

        while (iter <= maxIter) {
            yield()
            xrOld = xr
            
            // XR Calculation + Rounding
            xr = round((xl + xu) / 2.0)

            if (iter > 1) {

                // Error Calculation + Rounding

                val rawError = if (mode == ToleranceMode.ABSOLUTE) {
                    abs(xr - xrOld)
                } else {
                    if (abs(xr) < 1e-18) 0.0 else abs((xr - xrOld) / xr) * 100.0
                }
                error = round(rawError)
            }

            // f(x) Calculations + Rounding

            val fXl = round(f(xl))
            val fXu = round(f(xu))
            val fXr = round(f(xr))

            steps.add(
                BracketingStep(
                    iter = iter,
                    xl = xl,
                    fXl = fXl,
                    xu = xu,
                    fXu = fXu,
                    xr = xr,
                    fXr = fXr,
                    error = if (iter == 1) 0.0 else error
                )
            )

            // Stopping Criteria

            if (iter > 1 && error <= eps) break
            if (abs(fXr) < 1e-12) break

            if (fXl * fXr > 0) {
                xl = xr
            } else {
                xu = xr
            }

            iter++
        }
        return steps
    }
}
