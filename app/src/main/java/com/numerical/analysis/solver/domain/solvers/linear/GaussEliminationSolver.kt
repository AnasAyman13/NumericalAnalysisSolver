package com.numerical.analysis.solver.domain.solvers.linear

import com.numerical.analysis.solver.domain.methods.LinearStep
import com.numerical.analysis.solver.domain.methods.LinearSystemResult
import com.numerical.analysis.solver.domain.methods.SingularMatrixException
import kotlin.math.abs
import kotlin.math.round

object GaussEliminationSolver {

    /**
     * Solves a system of linear equations using Gaussian Elimination.
     * Rewritten to exactly match the university C++ GJE algorithm formulas for 3x3 matrices.
     */
    fun solve(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        if (aMatrix.size == 3) return solve3x3(aMatrix, bVector)
        return solveNxN(aMatrix, bVector)
    }

    private fun solve3x3(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        // Prepare augmented matrix similar to C++ a[][4]
        val a = Array(3) { r -> DoubleArray(4) { c -> if (c < 3) round5(aMatrix[r][c]) else round5(bVector[r]) } }
        val steps = mutableListOf<LinearStep>()
        
        steps.add(LinearStep("Initial Matrix", copyAugmented(a)))

        // 1. Partial Pivoting for the first column
        var maxRow = 0
        if (abs(a[1][0]) > abs(a[maxRow][0])) maxRow = 1
        if (abs(a[2][0]) > abs(a[maxRow][0])) maxRow = 2
        
        if (maxRow != 0) {
            val temp = a[0]; a[0] = a[maxRow]; a[maxRow] = temp
            steps.add(LinearStep("Partial Pivoting: Swapped Row 1 with Row ${maxRow+1}", copyAugmented(a)))
        }

        if (abs(a[0][0]) < 1e-12) throw SingularMatrixException("Pivot a[0][0] is zero after pivoting. System is singular.")

        // C++: m21 = a[1][0] / a[0][0]; m31 = a[2][0] / a[0][0];
        val m21 = round5(a[1][0] / a[0][0])
        val m31 = round5(a[2][0] / a[0][0])

        // C++ Row 2 calculation
        for (j in 0 until 4) {
            val e2 = a[1][j]
            val e1 = round5(m21 * a[0][j])
            a[1][j] = round5(e2 - e1)
        }
        steps.add(LinearStep("Row2 = Row2 - ($m21 * Row1)", copyAugmented(a)))

        // C++ Row 3 calculation
        for (j in 0 until 4) {
            val e3 = a[2][j]
            val e1 = round5(m31 * a[0][j])
            a[2][j] = round5(e3 - e1)
        }
        steps.add(LinearStep("Row3 = Row3 - ($m31 * Row1)", copyAugmented(a)))

        // 2. Partial Pivoting for the second column
        if (abs(a[2][1]) > abs(a[1][1])) {
            val temp = a[1]; a[1] = a[2]; a[2] = temp
            steps.add(LinearStep("Partial Pivoting: Swapped Row 2 with Row 3", copyAugmented(a)))
        }

        if (abs(a[1][1]) < 1e-12) throw SingularMatrixException("Pivot a[1][1] is zero after pivoting. System is singular.")

        // C++: m32 = a[2][1] / a[1][1];
        val m32 = round5(a[2][1] / a[1][1])

        for (j in 0 until 4) {
            val e3 = a[2][j]
            val e1 = round5(m32 * a[1][j])
            a[2][j] = round5(e3 - e1)
        }
        steps.add(LinearStep("Row3 = Row3 - ($m32 * Row2)", copyAugmented(a)))

        // C++ Back substitution
        val x3 = round5(a[2][3] / a[2][2])
        val x2 = round5(round5(a[1][3] - round5(a[1][2] * x3)) / a[1][1])
        val x1 = round5(round5(a[0][3] - round5(round5(a[0][1] * x2) + round5(a[0][2] * x3))) / a[0][0])

        return LinearSystemResult(doubleArrayOf(x1, x2, x3), true, steps = steps)
    }

    private fun solveNxN(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val n = bVector.size
        val steps = mutableListOf<LinearStep>()
        val aug = Array(n) { i -> DoubleArray(n + 1) { j -> if (j < n) round5(aMatrix[i][j]) else round5(bVector[i]) } }
        steps.add(LinearStep("Initial Matrix N x N", copyAugmented(aug)))

        try {
            for (k in 0 until n - 1) {
                var maxRow = k
                for (i in k + 1 until n) {
                    if (abs(aug[i][k]) > abs(aug[maxRow][k])) maxRow = i
                }

                if (maxRow != k) {
                    val temp = aug[k]; aug[k] = aug[maxRow]; aug[maxRow] = temp
                    steps.add(LinearStep("Partial Pivoting", copyAugmented(aug)))
                }

                if (abs(aug[k][k]) < 1e-12) throw SingularMatrixException("Pivot is 0.0")

                for (i in k + 1 until n) {
                    val multiplier = round5(aug[i][k] / aug[k][k])
                    for (j in k until n + 1) aug[i][j] = round5(aug[i][j] - round5(multiplier * aug[k][j]))
                }
            }

            val x = DoubleArray(n)
            for (i in n - 1 downTo 0) {
                if (abs(aug[i][i]) < 1e-12) throw SingularMatrixException("Division by zero")
                var sum = 0.0
                for (j in i + 1 until n) sum = round5(sum + round5(aug[i][j] * x[j]))
                x[i] = round5(round5(aug[i][n] - sum) / aug[i][i])
            }
            return LinearSystemResult(x, true, steps = steps)
        } catch (e: SingularMatrixException) {
            return LinearSystemResult(DoubleArray(0), false, e.message, steps)
        }
    }

    private fun round5(v: Double): Double = round(v * 100000.0) / 100000.0

    private fun copyAugmented(a: Array<DoubleArray>): Array<DoubleArray> = Array(a.size) { r -> a[r].copyOf() }
}
