package com.numerical.analysis.solver.domain.solvers.linear

import com.numerical.analysis.solver.domain.methods.LinearSystemResult
import com.numerical.analysis.solver.domain.methods.SingularMatrixException
import kotlin.math.abs
import kotlin.math.round

/**
 * Gaussian Elimination with Partial Pivoting.
 * Logic mirrors the university C++ GJE() function.
 * Returns only the final solution — no intermediate steps stored.
 */
object GaussEliminationSolver {

    fun solve(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val n = bVector.size
        // Build augmented matrix [A | b]
        val a = Array(n) { r ->
            DoubleArray(n + 1) { c -> r5(if (c < n) aMatrix[r][c] else bVector[r]) }
        }

        return try {
            forwardElimination(a, n)
            val x = backSubstitution(a, n)
            LinearSystemResult(x, true)
        } catch (e: SingularMatrixException) {
            LinearSystemResult(DoubleArray(0), false, e.message)
        }
    }

    /** Forward elimination with partial pivoting — handles zero pivots */
    private fun forwardElimination(a: Array<DoubleArray>, n: Int) {
        for (k in 0 until n - 1) {
            // Partial Pivoting: find row with max |value| in column k
            var maxRow = k
            for (i in k + 1 until n) {
                if (abs(a[i][k]) > abs(a[maxRow][k])) maxRow = i
            }
            if (maxRow != k) {
                val tmp = a[k]; a[k] = a[maxRow]; a[maxRow] = tmp
            }

            if (abs(a[k][k]) < 1e-12) throw SingularMatrixException("Matrix is singular.")

            // Eliminate rows below pivot k
            for (i in k + 1 until n) {
                // m = a[i][k] / a[k][k]  (C++: m21, m31, m32)
                val m = r5(a[i][k] / a[k][k])
                for (j in k until n + 1) {
                    // C++: a[i][j] = e_i - (m * a[k][j])
                    a[i][j] = r5(a[i][j] - r5(m * a[k][j]))
                }
            }
        }
    }

    /** Back substitution — C++: x3 = a[2][3]/a[2][2], x2 = (a[1][3] - a[1][2]*x3)/a[1][1], etc. */
    private fun backSubstitution(a: Array<DoubleArray>, n: Int): DoubleArray {
        val x = DoubleArray(n)
        for (i in n - 1 downTo 0) {
            if (abs(a[i][i]) < 1e-12) throw SingularMatrixException("Matrix is singular.")
            var sum = a[i][n]
            for (j in i + 1 until n) {
                sum = r5(sum - r5(a[i][j] * x[j]))
            }
            x[i] = r5(sum / a[i][i])
        }
        return x
    }

    private fun r5(v: Double): Double = round(v * 1e5) / 1e5
}
