package com.numerical.analysis.solver.domain

import kotlinx.coroutines.yield
import kotlin.math.abs

class LinearAlgebraMethods {

    suspend fun gaussElimination(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val n = bVector.size
        val a = Array(n) { i -> aMatrix[i].copyOf() }
        val b = bVector.copyOf()

        var pivot = 0
        while (pivot < n - 1) {
            yield()
            if (abs(a[pivot][pivot]) < 1e-22) return LinearSystemResult(DoubleArray(0), false, "Zero pivot encountered. Singular matrix.")

            var row = pivot + 1
            while (row < n) {
                val multiplier = a[row][pivot] / a[pivot][pivot]
                var col = pivot
                while (col < n) {
                    a[row][col] = a[row][col] - multiplier * a[pivot][col]
                    col++
                }
                b[row] = b[row] - multiplier * b[pivot]
                row++
            }
            pivot++
        }

        val x = DoubleArray(n)
        var i = n - 1
        while (i >= 0) {
            var sum = b[i]
            var j = i + 1
            while (j < n) {
                sum = sum - a[i][j] * x[j]
                j++
            }
            if (a[i][i] == 0.0) return LinearSystemResult(DoubleArray(0), false, "System has no unique solution.")
            x[i] = sum / a[i][i]
            i--
        }

        return LinearSystemResult(x, true)
    }

    suspend fun gaussJordan(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val n = bVector.size
        val a = Array(n) { i -> aMatrix[i].copyOf() }
        val b = bVector.copyOf()

        var k = 0
        while (k < n) {
            yield()
            val pivotValue = a[k][k]
            if (abs(pivotValue) < 1e-22) return LinearSystemResult(DoubleArray(0), false, "Zero pivot encountered.")

            var j = 0
            while (j < n) {
                a[k][j] = a[k][j] / pivotValue
                j++
            }
            b[k] = b[k] / pivotValue

            var i = 0
            while (i < n) {
                if (i != k) {
                    val factor = a[i][k]
                    j = 0
                    while (j < n) {
                        a[i][j] = a[i][j] - factor * a[k][j]
                        j++
                    }
                    b[i] = b[i] - factor * b[k]
                }
                i++
            }
            k++
        }

        return LinearSystemResult(b, true)
    }

    suspend fun luDecomposition(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val n = bVector.size
        val a = Array(n) { i -> aMatrix[i].copyOf() }
        val b = bVector.copyOf()

        // L and U matrices
        val lower = Array(n) { DoubleArray(n) }
        val upper = Array(n) { DoubleArray(n) }

        // LU Decomposition (Doolittle Algorithm)
        for (i in 0 until n) {
            yield()
            // Upper
            for (k in i until n) {
                var sum = 0.0
                for (j in 0 until i) {
                    sum += lower[i][j] * upper[j][k]
                }
                upper[i][k] = a[i][k] - sum
            }

            // Lower
            for (k in i until n) {
                if (i == k) {
                    lower[i][i] = 1.0
                } else {
                    var sum = 0.0
                    for (j in 0 until i) {
                        sum += lower[k][j] * upper[j][i]
                    }
                    if (abs(upper[i][i]) < 1e-20) return LinearSystemResult(DoubleArray(0), false, "Pivot in U is zero. LU fails.")
                    lower[k][i] = (a[k][i] - sum) / upper[i][i]
                }
            }
        }

        // Forward substitution to solve Ly = b
        val y = DoubleArray(n)
        for (i in 0 until n) {
            var sum = 0.0
            for (j in 0 until i) {
                sum += lower[i][j] * y[j]
            }
            y[i] = b[i] - sum
        }

        // Backward substitution to solve Ux = y
        val x = DoubleArray(n)
        for (i in n - 1 downTo 0) {
            var sum = 0.0
            for (j in i + 1 until n) {
                sum += upper[i][j] * x[j]
            }
            if (abs(upper[i][i]) < 1e-20) return LinearSystemResult(DoubleArray(0), false, "Singular Matrix.")
            x[i] = (y[i] - sum) / upper[i][i]
        }

        return LinearSystemResult(x, true)
    }

    suspend fun cramersRule(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val n = bVector.size
        val detA = getDeterminant(aMatrix)
        if (abs(detA) < 1e-15) return LinearSystemResult(DoubleArray(0), false, "Determinant is zero.")

        val solution = DoubleArray(n)
        for (i in 0 until n) {
            yield()
            val tempMatrix = Array(n) { r ->
                DoubleArray(n) { c ->
                    if (c == i) bVector[r] else aMatrix[r][c]
                }
            }
            solution[i] = getDeterminant(tempMatrix) / detA
        }
        return LinearSystemResult(solution, true)
    }

    private suspend fun getDeterminant(matrix: Array<DoubleArray>): Double {
        val n = matrix.size
        if (n == 1) return matrix[0][0]
        if (n == 2) return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0]
        
        // Use Gaussian elimination to find determinant (more efficient than recursive)
        val copy = Array(n) { i -> matrix[i].copyOf() }
        var det = 1.0
        for (i in 0 until n) {
            yield()
            var pivot = i
            for (j in i + 1 until n) {
                if (abs(copy[j][i]) > abs(copy[pivot][i])) pivot = j
            }
            if (pivot != i) {
                val temp = copy[i]
                copy[i] = copy[pivot]
                copy[pivot] = temp
                det *= -1.0
            }
            if (abs(copy[i][i]) < 1e-22) return 0.0
            det *= copy[i][i]
            for (j in i + 1 until n) {
                val factor = copy[j][i] / copy[i][i]
                for (k in i + 1 until n) {
                    copy[j][k] -= factor * copy[i][k]
                }
            }
        }
        return det
    }
}