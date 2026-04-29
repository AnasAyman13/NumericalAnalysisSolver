package com.numerical.analysis.solver.domain.solvers.linear

import com.numerical.analysis.solver.domain.methods.LinearStep
import com.numerical.analysis.solver.domain.methods.LinearSystemResult
import com.numerical.analysis.solver.domain.methods.SingularMatrixException
import kotlin.math.abs
import kotlin.math.round

object GaussJordanSolver {

    /**
     * Solves matching the university's "GJE" C++ logic:
     * Forward Elimination (making lower triangle zeros) followed by Back Substitution.
     * Includes Partial Pivoting to prevent division by zero.
     */
    fun solve(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val n = bVector.size
        val steps = mutableListOf<LinearStep>()

        // 1. Create augmented matrix [A | b]
        val aug = Array(n) { i ->
            DoubleArray(n + 1) { j ->
                if (j < n) round5(aMatrix[i][j]) else round5(bVector[i])
            }
        }

        try {
            // --- Forward Elimination
            for (k in 0 until n - 1) {
                // Partial Pivoting
                var maxRow = k
                var maxVal = abs(aug[k][k])
                for (i in k + 1 until n) {
                    if (abs(aug[i][k]) > maxVal) {
                        maxVal = abs(aug[i][k])
                        maxRow = i
                    }
                }

                if (maxRow != k) {
                    val temp = aug[k]
                    aug[k] = aug[maxRow]
                    aug[maxRow] = temp
                }

                if (abs(aug[k][k]) < 1e-12) {
                    throw SingularMatrixException("System is singular (division by zero).")
                }

                //  m21, m31, m32
                for (i in k + 1 until n) {
                    val m = round5(aug[i][k] / aug[k][k])
                    for (j in k until n + 1) {
                        aug[i][j] = round5(aug[i][j] - round5(m * aug[k][j]))
                    }
                }
            }

            if (abs(aug[n - 1][n - 1]) < 1e-12) {
                throw SingularMatrixException("System is singular.")
            }

            val x = DoubleArray(n)
            for (i in n - 1 downTo 0) {
                var sum = 0.0
                for (j in i + 1 until n) {
                    sum = round5(sum + round5(aug[i][j] * x[j]))
                }
                // x = (b - sum) / a[i][i]
                x[i] = round5(round5(aug[i][n] - sum) / aug[i][i])
            }

            return LinearSystemResult(x, true, steps = steps)

        } catch (e: SingularMatrixException) {
            return LinearSystemResult(DoubleArray(0), false, e.message, steps)
        }
    }

    private fun round5(v: Double): Double {
        return round(v * 100000.0) / 100000.0
    }
}