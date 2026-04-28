package com.numerical.analysis.solver.domain.solvers.linear

import com.numerical.analysis.solver.domain.methods.LinearSystemResult
import com.numerical.analysis.solver.domain.methods.SingularMatrixException
import kotlin.math.abs
import kotlin.math.round

object LUSolver {

    fun solve(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        if (aMatrix.size == 3) return solve3x3(aMatrix, bVector)
        return solveNxN(aMatrix, bVector)
    }

    private fun solve3x3(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val a = Array(3) { r -> DoubleArray(4) { c -> if (c < 3) round5(aMatrix[r][c]) else round5(bVector[r]) } }
        val p = intArrayOf(0, 1, 2)

        var maxRow = 0
        if (abs(a[1][0]) > abs(a[maxRow][0])) maxRow = 1
        if (abs(a[2][0]) > abs(a[maxRow][0])) maxRow = 2

        if (maxRow != 0) {
            val tA = a[0]; a[0] = a[maxRow]; a[maxRow] = tA
            val tP = p[0]; p[0] = p[maxRow]; p[maxRow] = tP
        }

        if (abs(a[0][0]) < 1e-12) throw SingularMatrixException("Pivot a[0][0] is zero after pivoting. System is singular.")

        var m21 = round5(a[1][0] / a[0][0])
        var m31 = round5(a[2][0] / a[0][0])

        for (j in 0 until 4) {
            a[1][j] = round5(a[1][j] - round5(m21 * a[0][j]))
            a[2][j] = round5(a[2][j] - round5(m31 * a[0][j]))
        }

        // Second pivot
        if (abs(a[2][1]) > abs(a[1][1])) {
            val tA = a[1]; a[1] = a[2]; a[2] = tA
            val tP = p[1]; p[1] = p[2]; p[2] = tP
            val tm = m21; m21 = m31; m31 = tm
        }

        if (abs(a[1][1]) < 1e-12) throw SingularMatrixException("Pivot a[1][1] is zero after pivoting. System is singular.")

        val m32 = round5(a[2][1] / a[1][1])

        for (j in 0 until 4) {
            a[2][j] = round5(a[2][j] - round5(m32 * a[1][j]))
        }

        // --- 2. Build L & U Matrices ---
        val u = Array(3) { r -> DoubleArray(3) { c -> a[r][c] } }
        val l = Array(3) { r -> DoubleArray(3) { c -> if (r == c) 1.0 else 0.0 } }
        l[1][0] = m21
        l[2][0] = m31
        l[2][1] = m32

        // --- 3. Solve Lc = b (Using Permuted B) ---
        val permutedB = DoubleArray(3) { r -> round5(bVector[p[r]]) }

        val c1 = round5(permutedB[0] / l[0][0])
        val c2 = round5(round5(permutedB[1] - round5(l[1][0] * c1)) / l[1][1])
        val c3 = round5(round5(permutedB[2] - round5(round5(l[2][0] * c1) + round5(l[2][1] * c2))) / l[2][2])

        // --- 4. Solve Ux = c ---
        val x3 = round5(c3 / u[2][2])
        val x2 = round5(round5(c2 - round5(u[1][2] * x3)) / u[1][1])
        val x1 = round5(round5(c1 - round5(round5(u[0][1] * x2) + round5(u[0][2] * x3))) / u[0][0])

        return LinearSystemResult(doubleArrayOf(x1, x2, x3), true, steps = emptyList())
    }

    private fun solveNxN(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val n = bVector.size
        val a = Array(n) { r -> aMatrix[r].copyOf() }
        val b = bVector.copyOf()
        val p = IntArray(n) { it }
        val l = Array(n) { r -> DoubleArray(n) { c -> if (r == c) 1.0 else 0.0 } }

        try {
            for (i in 0 until n) {
                var max = i
                for (k in i + 1 until n) if (abs(a[k][i]) > abs(a[max][i])) max = k
                if (max != i) {
                    val t = a[i]; a[i] = a[max]; a[max] = t
                    val tb = b[i]; b[i] = b[max]; b[max] = tb
                    val tp = p[i]; p[i] = p[max]; p[max] = tp
                    for (k in 0 until i) {
                        val tl = l[i][k]; l[i][k] = l[max][k]; l[max][k] = tl
                    }
                }
                if (abs(a[i][i]) < 1e-12) throw SingularMatrixException("System is singular.")
                for (k in i + 1 until n) {
                    val factor = round5(a[k][i] / a[i][i])
                    l[k][i] = factor
                    for (j in i until n) a[k][j] = round5(a[k][j] - round5(factor * a[i][j]))
                }
            }
            val c = DoubleArray(n)
            for (i in 0 until n) {
                var sum = 0.0
                for (j in 0 until i) sum = round5(sum + round5(l[i][j] * c[j]))
                c[i] = round5(b[i] - sum)
            }
            val x = DoubleArray(n)
            for (i in n - 1 downTo 0) {
                var sum = 0.0
                for (j in i + 1 until n) sum = round5(sum + round5(a[i][j] * x[j]))
                x[i] = round5(round5(c[i] - sum) / a[i][i])
            }
            return LinearSystemResult(x, true, steps = emptyList())
        } catch (e: Exception) {
            return LinearSystemResult(DoubleArray(0), false, e.message, emptyList())
        }
    }

    private fun round5(v: Double): Double = round(v * 100000.0) / 100000.0
}