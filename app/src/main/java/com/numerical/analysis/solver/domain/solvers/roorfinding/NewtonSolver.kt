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

            val fXi = round(f(xi))
            val dXi = round(fDash(xi))
            
            if (abs(dXi) < 1e-20) break

            // Calculate NEXT xi (Xi+1)
            val xiPlus1 = round(xi - (fXi / dXi))

            steps.add(
                OpenMethodsStep(
                    iter = iter,
                    xi = xi,
                    xiPlus1 = xiPlus1,
                    fXi = fXi,
                    error = error
                )
            )

            // Stopping Criteria uses the error of the current xi
            if (iter > 0 && error <= eps) break
            if (abs(fXi) < 1e-12) break

            xiMinus1 = xi
            xi = xiPlus1
            iter++
        }
        return steps
    }
}
