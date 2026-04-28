package com.numerical.analysis.solver.domain.solvers.linear

import com.numerical.analysis.solver.domain.methods.LinearStep
import com.numerical.analysis.solver.domain.methods.LinearSystemResult
import com.numerical.analysis.solver.domain.methods.SingularMatrixException
import kotlin.math.abs
import kotlin.math.round

object GaussJordanSolver {

    /**
     * Solves a system of linear equations using Gauss-Jordan Elimination with Partial Pivoting.
     * Implements mid-loop rounding to 5 decimal places.
     */
    fun solve(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val n = bVector.size
        val steps = mutableListOf<LinearStep>()
        
        // 1. Create augmented matrix
        val aug = Array(n) { i ->
            DoubleArray(n + 1) { j ->
                if (j < n) round5(aMatrix[i][j]) else round5(bVector[i])
            }
        }
        steps.add(LinearStep("Initial Augmented Matrix", copyMatrix(aug)))

        try {
            for (k in 0 until n) {
                // PARTIAL PIVOTING
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
                    steps.add(LinearStep("Partial Pivoting: Swap R${k+1} with R${maxRow+1}", copyMatrix(aug)))
                }

                if (abs(aug[k][k]) < 1e-12) {
                    throw SingularMatrixException("Pivot at R${k+1} is 0.0 even after partial pivoting.")
                }

                // Normalization of Pivot Row
                val pivotValue = aug[k][k]
                for (j in k until n + 1) {
                    aug[k][j] = round5(aug[k][j] / pivotValue)
                }
                steps.add(LinearStep("Normalize R${k+1} (Divide by $pivotValue)", copyMatrix(aug)))

                // Eliminate all other rows
                for (i in 0 until n) {
                    if (i != k) {
                        val factor = aug[i][k] // No need to round factor yet as it's directly from matrix
                        for (j in k until n + 1) {
                            aug[i][j] = round5(aug[i][j] - round5(factor * aug[k][j]))
                        }
                        steps.add(LinearStep("R${i+1} = R${i+1} - (${factor}) * R${k+1}", copyMatrix(aug)))
                    }
                }
            }

            // Extract results from normalized augmented column
            val result = DoubleArray(n) { i -> aug[i][n] }
            return LinearSystemResult(result, true, steps = steps)

        } catch (e: SingularMatrixException) {
            return LinearSystemResult(DoubleArray(0), false, e.message, steps)
        }
    }

    private fun round5(v: Double): Double {
        return round(v * 100000.0) / 100000.0
    }

    private fun copyMatrix(m: Array<DoubleArray>): Array<DoubleArray> {
        return Array(m.size) { m[it].copyOf() }
    }
}
