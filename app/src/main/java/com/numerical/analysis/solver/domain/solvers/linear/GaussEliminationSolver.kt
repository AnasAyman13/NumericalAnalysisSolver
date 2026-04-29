package com.numerical.analysis.solver.domain.solvers.linear

import com.numerical.analysis.solver.domain.methods.LinearSystemResult
import com.numerical.analysis.solver.domain.methods.SingularMatrixException
import kotlin.math.abs
import kotlin.math.round

object GaussEliminationSolver {

    fun solve(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        if (aMatrix.size == 3) return solve3x3(aMatrix, bVector)
        return solveNxN(aMatrix, bVector)
    }

    private fun solve3x3(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val a = Array(3) { r -> DoubleArray(4) { c -> if (c < 3) round5(aMatrix[r][c]) else round5(bVector[r]) } }

        try {
            // Partial Pivoting for Column 0
            var maxRow = 0
            if (abs(a[1][0]) > abs(a[maxRow][0])) maxRow = 1
            if (abs(a[2][0]) > abs(a[maxRow][0])) maxRow = 2

            if (maxRow != 0) {
                val temp = a[0]; a[0] = a[maxRow]; a[maxRow] = temp
            }

            if (abs(a[0][0]) < 1e-12) throw SingularMatrixException("System is singular.")

            // Forward Elimination matching C++ GJE()
            val m21 = round5(a[1][0] / a[0][0])
            val m31 = round5(a[2][0] / a[0][0])

            for (j in 0 until 4) {
                a[1][j] = round5(a[1][j] - round5(m21 * a[0][j]))
                a[2][j] = round5(a[2][j] - round5(m31 * a[0][j]))
            }

            // Partial Pivoting for Column 1
            if (abs(a[2][1]) > abs(a[1][1])) {
                val temp = a[1]; a[1] = a[2]; a[2] = temp
            }

            if (abs(a[1][1]) < 1e-12) throw SingularMatrixException("System is singular.")

            val m32 = round5(a[2][1] / a[1][1])

            for (j in 0 until 4) {
                a[2][j] = round5(a[2][j] - round5(m32 * a[1][j]))
            }

            if (abs(a[2][2]) < 1e-12) throw SingularMatrixException("System is singular.")

            // Back Substitution exactly as C++ GJE()
            val x3 = round5(a[2][3] / a[2][2])
            val x2 = round5((a[1][3] - round5(a[1][2] * x3)) / a[1][1])
            val x1 = round5((a[0][3] - round5(round5(a[0][1] * x2) + round5(a[0][2] * x3))) / a[0][0])

            return LinearSystemResult(doubleArrayOf(x1, x2, x3), true, steps = emptyList())
        } catch (e: Exception) {
            return LinearSystemResult(DoubleArray(0), false, e.message, emptyList())
        }
    }

    private fun solveNxN(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val n = bVector.size
        val a = Array(n) { r -> DoubleArray(n + 1) { c -> if (c < n) round5(aMatrix[r][c]) else round5(bVector[r]) } }

        try {
            for (k in 0 until n - 1) {
                var maxRow = k
                for (i in k + 1 until n) {
                    if (abs(a[i][k]) > abs(a[maxRow][k])) maxRow = i
                }
                if (maxRow != k) {
                    val tmp = a[k]; a[k] = a[maxRow]; a[maxRow] = tmp
                }

                if (abs(a[k][k]) < 1e-12) throw SingularMatrixException("Matrix is singular.")

                for (i in k + 1 until n) {
                    val m = round5(a[i][k] / a[k][k])
                    for (j in k until n + 1) {
                        a[i][j] = round5(a[i][j] - round5(m * a[k][j]))
                    }
                }
            }

            val x = DoubleArray(n)
            for (i in n - 1 downTo 0) {
                if (abs(a[i][i]) < 1e-12) throw SingularMatrixException("Matrix is singular.")
                var sum = 0.0
                for (j in i + 1 until n) {
                    sum = round5(sum + round5(a[i][j] * x[j]))
                }
                x[i] = round5(round5(a[i][n] - sum) / a[i][i])
            }
            return LinearSystemResult(x, true, steps = emptyList())
        } catch (e: Exception) {
            return LinearSystemResult(DoubleArray(0), false, e.message, emptyList())
        }
    }

    private fun round5(v: Double): Double = round(v * 100000.0) / 100000.0
}