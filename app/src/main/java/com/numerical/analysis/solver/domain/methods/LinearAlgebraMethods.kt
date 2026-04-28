package com.numerical.analysis.solver.domain.methods

import com.numerical.analysis.solver.domain.solvers.linear.GaussEliminationSolver
import com.numerical.analysis.solver.domain.solvers.linear.GaussJordanSolver
import com.numerical.analysis.solver.domain.solvers.linear.CramerSolver
import com.numerical.analysis.solver.domain.solvers.linear.LUSolver
import kotlinx.coroutines.yield

class LinearAlgebraMethods {

    suspend fun gaussElimination(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        yield()
        return GaussEliminationSolver.solve(aMatrix, bVector)
    }

    suspend fun gaussJordan(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        yield()
        return GaussJordanSolver.solve(aMatrix, bVector)
    }

    suspend fun cramersRule(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        yield()
        return CramerSolver.solve(aMatrix, bVector)
    }

    suspend fun luDecomposition(aMatrix: Array<DoubleArray>, bVector: DoubleArray): LinearSystemResult {
        yield()
        return LUSolver.solve(aMatrix, bVector)
    }
}
