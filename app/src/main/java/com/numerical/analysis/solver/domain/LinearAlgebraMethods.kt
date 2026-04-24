package com.numerical.analysis.solver.domain

import kotlin.math.abs

class LinearAlgebraMethods {

    fun gaussElimination(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val n = bVector.size
        val a = Array(n) { i -> aMatrix[i].copyOf() }
        val b = bVector.copyOf()

        var pivot = 0
        while (pivot < n - 1) {
            if (a[pivot][pivot] == 0.0) return LinearSystemResult(DoubleArray(0), false, "Zero pivot encountered. Singular matrix.")

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

    fun gaussJordan(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val n = bVector.size
        val a = Array(n) { i -> aMatrix[i].copyOf() }
        val b = bVector.copyOf()

        var k = 0
        while (k < n) {
            val pivotValue = a[k][k]
            if (pivotValue == 0.0) return LinearSystemResult(DoubleArray(0), false, "Zero pivot encountered.")

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

    fun luDecomposition(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val n = 3
        val a = Array(n) { i -> aMatrix[i].take(4).toDoubleArray() }

        val m21 = a[1][0] / a[0][0]
        val m31 = a[2][0] / a[0][0]

        var col = 0
        while (col < n) {
            a[1][col] = a[1][col] - m21 * a[0][col]
            col++
        }

        col = 0
        while (col < n) {
            a[2][col] = a[2][col] - m31 * a[0][col]
            col++
        }

        val m32 = a[2][1] / a[1][1]

        col = 0
        while (col < n) {
            a[2][col] = a[2][col] - m32 * a[1][col]
            col++
        }

        val u = Array(n) { DoubleArray(n) }
        var row = 0
        while (row < n) {
            col = 0
            while (col < n) {
                u[row][col] = a[row][col]
                col++
            }
            row++
        }

        val l = Array(n) { DoubleArray(n) }
        row = 0
        while (row < n) {
            col = 0
            while (col < n) {
                if (row == col) {
                    l[row][col] = 1.0
                } else {
                    l[row][col] = 0.0
                }
                col++
            }
            row++
        }
        l[1][0] = m21
        l[2][0] = m31
        l[2][1] = m32

        val b = bVector

        val c1 = b[0] / l[0][0]
        val c2 = (b[1] - l[1][0] * c1) / l[1][1]
        val c3 = (b[2] - (l[2][0] * c1 + l[2][1] * c2)) / l[2][2]

        val x3 = c3 / u[2][2]
        val x2 = (c2 - u[1][2] * x3) / u[1][1]
        val x1 = (c1 - (u[0][1] * x2 + u[0][2] * x3)) / u[0][0]

        val result = DoubleArray(3)
        result[0] = x1
        result[1] = x2
        result[2] = x3

        return LinearSystemResult(result, true)
    }

    fun cramersRule(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val a = aMatrix

        val r0 = a[0][0] * ((a[1][1] * a[2][2]) - (a[1][2] * a[2][1]))
        val r1 = a[0][1] * ((a[1][0] * a[2][2]) - (a[1][2] * a[2][0]))
        val r2 = a[0][2] * ((a[1][0] * a[2][1]) - (a[1][1] * a[2][0]))
        val detA = r0 - r1 + r2

        if (detA == 0.0) {
            return LinearSystemResult(DoubleArray(0), false, "Determinant is zero. No unique solution.")
        }

        val originalCol0 = DoubleArray(3)
        val originalCol1 = DoubleArray(3)
        val originalCol2 = DoubleArray(3)

        var rowIdx = 0
        while (rowIdx < 3) {
            originalCol0[rowIdx] = a[rowIdx][0]
            originalCol1[rowIdx] = a[rowIdx][1]
            originalCol2[rowIdx] = a[rowIdx][2]
            rowIdx++
        }

        rowIdx = 0
        while (rowIdx < 3) { a[rowIdx][0] = bVector[rowIdx]; rowIdx++ }
        val t00 = a[0][0] * ((a[1][1] * a[2][2]) - (a[1][2] * a[2][1]))
        val t01 = a[0][1] * ((a[1][0] * a[2][2]) - (a[1][2] * a[2][0]))
        val t02 = a[0][2] * ((a[1][0] * a[2][1]) - (a[1][1] * a[2][0]))
        val det1 = t00 - t01 + t02
        rowIdx = 0
        while (rowIdx < 3) { a[rowIdx][0] = originalCol0[rowIdx]; rowIdx++ }

        rowIdx = 0
        while (rowIdx < 3) { a[rowIdx][1] = bVector[rowIdx]; rowIdx++ }
        val t10 = a[0][0] * ((a[1][1] * a[2][2]) - (a[1][2] * a[2][1]))
        val t11 = a[0][1] * ((a[1][0] * a[2][2]) - (a[1][2] * a[2][0]))
        val t12 = a[0][2] * ((a[1][0] * a[2][1]) - (a[1][1] * a[2][0]))
        val det2 = t10 - t11 + t12
        rowIdx = 0
        while (rowIdx < 3) { a[rowIdx][1] = originalCol1[rowIdx]; rowIdx++ }

        rowIdx = 0
        while (rowIdx < 3) { a[rowIdx][2] = bVector[rowIdx]; rowIdx++ }
        val t20 = a[0][0] * ((a[1][1] * a[2][2]) - (a[1][2] * a[2][1]))
        val t21 = a[0][1] * ((a[1][0] * a[2][2]) - (a[1][2] * a[2][0]))
        val t22 = a[0][2] * ((a[1][0] * a[2][1]) - (a[1][1] * a[2][0]))
        val det3 = t20 - t21 + t22
        rowIdx = 0
        while (rowIdx < 3) { a[rowIdx][2] = originalCol2[rowIdx]; rowIdx++ }

        val result = DoubleArray(3)
        result[0] = det1 / detA
        result[1] = det2 / detA
        result[2] = det3 / detA

        return LinearSystemResult(result, true)
    }
}