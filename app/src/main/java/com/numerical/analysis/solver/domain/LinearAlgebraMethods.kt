package com.numerical.analysis.solver.domain

import kotlin.math.abs

class LinearAlgebraMethods {

    fun gaussElimination(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val n = bVector.size
        val a = Array(n) { i -> aMatrix[i].copyOf() }
        val b = bVector.copyOf()

        // Forward Elimination
        for (k in 0 until n - 1) {
            for (i in k + 1 until n) {
                if (a[k][k] == 0.0) return LinearSystemResult(DoubleArray(0), false, "Division by zero detected")
                val factor = a[i][k] / a[k][k]
                for (j in k until n) {
                    a[i][j] -= factor * a[k][j]
                }
                b[i] -= factor * b[k]
            }
        }

        // Back Substitution
        val x = DoubleArray(n)
        for (i in n - 1 downTo 0) {
            var sum = b[i]
            for (j in i + 1 until n) {
                sum -= a[i][j] * x[j]
            }
            if (a[i][i] == 0.0) return LinearSystemResult(DoubleArray(0), false, "System has no unique solution")
            x[i] = sum / a[i][i]
        }

        return LinearSystemResult(x, true)
    }

    fun gaussJordan(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val n = bVector.size
        val a = Array(n) { i -> aMatrix[i].copyOf() }
        val b = bVector.copyOf()

        for (k in 0 until n) {
            val pivot = a[k][k]
            if (pivot == 0.0) return LinearSystemResult(DoubleArray(0), false, "Zero pivot encountered")

            for (j in 0 until n) {
                a[k][j] /= pivot
            }
            b[k] /= pivot

            for (i in 0 until n) {
                if (i != k) {
                    val factor = a[i][k]
                    for (j in 0 until n) {
                        a[i][j] -= factor * a[k][j]
                    }
                    b[i] -= factor * b[k]
                }
            }
        }

        return LinearSystemResult(b, true)
    }

    fun luDecomposition(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val n = bVector.size
        val l = Array(n) { DoubleArray(n) }
        val u = Array(n) { DoubleArray(n) }

        // Doolittle Algorithm
        for (i in 0 until n) {
            // Upper Triangular
            for (k in i until n) {
                var sum = 0.0
                for (j in 0 until i) {
                    sum += l[i][j] * u[j][k]
                }
                u[i][k] = aMatrix[i][k] - sum
            }

            // Lower Triangular
            for (k in i until n) {
                if (i == k) {
                    l[i][i] = 1.0
                } else {
                    var sum = 0.0
                    for (j in 0 until i) {
                        sum += l[k][j] * u[j][i]
                    }
                    if (u[i][i] == 0.0) return LinearSystemResult(DoubleArray(0), false, "LU decomposition failed")
                    l[k][i] = (aMatrix[k][i] - sum) / u[i][i]
                }
            }
        }

        // Forward Substitution (Ly = b)
        val y = DoubleArray(n)
        for (i in 0 until n) {
            var sum = bVector[i]
            for (j in 0 until i) {
                sum -= l[i][j] * y[j]
            }
            y[i] = sum / l[i][i]
        }

        // Back Substitution (Ux = y)
        val x = DoubleArray(n)
        for (i in n - 1 downTo 0) {
            var sum = y[i]
            for (j in i + 1 until n) {
                sum -= u[i][j] * x[j]
            }
            if (u[i][i] == 0.0) return LinearSystemResult(DoubleArray(0), false, "No unique solution")
            x[i] = sum / u[i][i]
        }

        return LinearSystemResult(x, true)
    }

    fun cramersRule(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val n = bVector.size
        val d = calculateDeterminant(aMatrix)

        if (d == 0.0) return LinearSystemResult(DoubleArray(0), false, "Determinant is zero. No unique solution.")

        val x = DoubleArray(n)
        for (i in 0 until n) {
            val modifiedMatrix = Array(n) { row -> aMatrix[row].copyOf() }
            for (row in 0 until n) {
                modifiedMatrix[row][i] = bVector[row]
            }
            x[i] = calculateDeterminant(modifiedMatrix) / d
        }

        return LinearSystemResult(x, true)
    }

    private fun calculateDeterminant(matrix: Array<DoubleArray>): Double {
        val n = matrix.size
        val a = Array(n) { i -> matrix[i].copyOf() }
        var det = 1.0

        for (i in 0 until n) {
            var pivot = i
            for (j in i + 1 until n) {
                if (abs(a[j][i]) > abs(a[pivot][i])) {
                    pivot = j
                }
            }

            if (pivot != i) {
                val temp = a[i]
                a[i] = a[pivot]
                a[pivot] = temp
                det *= -1.0
            }

            if (a[i][i] == 0.0) return 0.0

            det *= a[i][i]

            for (j in i + 1 until n) {
                val factor = a[j][i] / a[i][i]
                for (k in i until n) {
                    a[j][k] -= factor * a[i][k]
                }
            }
        }
        return det
    }
}