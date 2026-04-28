package com.numerical.analysis.solver.domain.solvers

import com.numerical.analysis.solver.domain.methods.SecantStep
import com.numerical.analysis.solver.ui.state.ToleranceMode
import kotlinx.coroutines.yield
import kotlin.math.abs

object SecantSolver {

    private fun round(value: Double): Double =
        Math.round(value * 100000.0) / 100000.0

    suspend fun solve(
        xMinus1: Double,
        x0: Double,
        eps: Double,
        maxIter: Int,
        mode: ToleranceMode,
        f: (Double) -> Double
    ): List<SecantStep> {
        val steps = mutableListOf<SecantStep>()
        var xiMinus1 = xMinus1
        var xi       = x0

        // Row 0: initial state — log both starting points, error = 0
        steps.add(
            SecantStep(
                iter     = 0,
                xMinus1  = round(xiMinus1),
                fXMinus1 = round(f(xiMinus1)),
                xi       = round(xi),
                fXi      = round(f(xi)),
                error    = 0.0
            )
        )

        var iter = 1
        while (iter <= maxIter) {
            yield()

            val fXiMinus1 = round(f(xiMinus1))
            val fXi       = round(f(xi))

            val denom = fXi - fXiMinus1
            if (abs(denom) < 1e-20) break

            // Formula: X_new = Xi - F(Xi)*(Xi - X(i-1)) / (F(Xi) - F(X(i-1)))
            val xNew  = round(xi - (fXi * (xi - xiMinus1)) / denom)
            val fXNew = round(f(xNew))

            // Error
            val rawError = if (mode == ToleranceMode.ABSOLUTE) {
                abs(xNew - xi)
            } else {
                if (abs(xNew) < 1e-18) 0.0 else abs((xNew - xi) / xNew) * 100.0
            }
            val error = round(rawError)

            // Each row shows the two points that PRODUCED xNew:
            //   X(i-1) = xi (old current), X(i) = xNew (newly computed root estimate)
            steps.add(
                SecantStep(
                    iter     = iter,
                    xMinus1  = round(xi),
                    fXMinus1 = fXi,
                    xi       = xNew,
                    fXi      = fXNew,
                    error    = error
                )
            )

            if (error <= eps)          break
            if (abs(fXNew) < 1e-12)   break

            xiMinus1 = xi
            xi       = xNew
            iter++
        }
        return steps
    }
}
