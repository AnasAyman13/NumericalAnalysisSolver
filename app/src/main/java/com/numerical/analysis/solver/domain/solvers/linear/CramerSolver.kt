package com.numerical.analysis.solver.domain.solvers.linear

import com.numerical.analysis.solver.domain.methods.LinearStep
import com.numerical.analysis.solver.domain.methods.LinearSystemResult
import com.numerical.analysis.solver.domain.methods.SingularMatrixException
import kotlin.math.abs
import kotlin.math.round

object CramerSolver {

 
    fun solve(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        val n = bVector.size
        val steps = mutableListOf<LinearStep>()
        
        try {
          
            val d = calculateDeterminant(aMatrix)
            if (abs(d) < 1e-12) {
                throw SingularMatrixException("The main determinant (D) is 0.0. System has no unique solution.")
            }

            val x = DoubleArray(n)
            for (i in 0 until n) {
                // 2. Create Bi matrix (replace i-th column with B vector)
                val biMatrix = Array(n) { r ->
                    DoubleArray(n) { c ->
                        if (c == i) bVector[r] else aMatrix[r][c]
                    }
                }
                
                val di = calculateDeterminant(biMatrix)
                x[i] = round5(di / d)
                
                // Detailed academic step message
                val stepTitle = "Calculating x${i+1}"
                val stepMessage = "Matrix with B in column ${i+1}. Determinant (D${i+1}) = ${round5(di)}. \n" +
                                 "x${i+1} = D${i+1} / D = ${round5(di)} / ${round5(d)} = ${x[i]}"
                
                steps.add(LinearStep(stepTitle, biMatrix, stepMessage))
            }

            return LinearSystemResult(x, true, steps = steps)

        } catch (e: SingularMatrixException) {
            return LinearSystemResult(DoubleArray(0), false, e.message, steps)
        }
    }

    /**
     * Calculates determinant. Uses explicit 3x3 formula for school compatibility,
     * otherwise uses Gaussian elimination with partial pivoting.
     */
    private fun calculateDeterminant(matrix: Array<DoubleArray>): Double {
        val n = matrix.size
        
        if (n == 3) {
            // EXACT 3x3 reference formula from book/C++: det = a00(a11*a22 - a12*a21) - a01(a10*a22 - a12*a20) + a02(a10*a21 - a11*a20)
            val a = matrix
            val t0 = round5(a[0][0] * round5(a[1][1] * a[2][2] - a[1][2] * a[2][1]))
            val t1 = round5(a[0][1] * round5(a[1][0] * a[2][2] - a[1][2] * a[2][0]))
            val t2 = round5(a[0][2] * round5(a[1][0] * a[2][1] - a[1][1] * a[2][0]))
            return round5(t0 - t1 + t2)
        }

        // Generalized Gaussian Determinant (for n != 3)
        val m = Array(n) { r -> matrix[r].copyOf() }
        var det = 1.0
        for (i in 0 until n) {
            var maxRow = i
            for (k in i + 1 until n) {
                if (abs(m[k][i]) > abs(m[maxRow][i])) maxRow = k
            }
            if (maxRow != i) {
                val temp = m[i]
                m[i] = m[maxRow]
                m[maxRow] = temp
                det *= -1.0
            }
            if (abs(m[i][i]) < 1e-15) return 0.0
            det = round5(det * m[i][i])
            for (k in i + 1 until n) {
                val factor = round5(m[k][i] / m[i][i])
                for (j in i + 1 until n) {
                    m[k][j] = round5(m[k][j] - round5(factor * m[i][j]))
                }
            }
        }
        return det
    }

    private fun round5(v: Double): Double {
        return round(v * 100000.0) / 100000.0
    }
}
