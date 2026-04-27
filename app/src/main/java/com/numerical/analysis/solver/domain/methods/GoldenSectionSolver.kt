package com.numerical.analysis.solver.domain.methods

import kotlinx.coroutines.yield

class GoldenSectionSolver {

    suspend fun goldenSectionSearch(
        function: String,
        initialXl: Double,
        initialXu: Double,
        numIterations: Int,
        isMax: Boolean,
        f: (Double) -> Double
    ): List<GoldenSectionStep> {
        val steps = mutableListOf<GoldenSectionStep>()
        var xl = initialXl
        var xu = initialXu
        val R = 0.618
        
        for (i in 1..numIterations) {
            yield() // Check for coroutine cancellation
            
            val d = R * (xu - xl)
            val x1 = xl + d
            val x2 = xu - d
            
            val fXl = f(xl)
            val fXu = f(xu)
            val f1 = f(x1)
            val f2 = f(x2)
            
            val xOpt = if (isMax) {
                if (f1 > f2) x1 else x2
            } else {
                if (f1 < f2) x1 else x2
            }
            
            val fOpt = f(xOpt)
            
            steps.add(
                GoldenSectionStep(
                    iter = i,
                    xl = xl,
                    xu = xu,
                    d = d,
                    x1 = x1,
                    x2 = x2,
                    fX1 = f1,
                    fX2 = f2,
                    fXl = fXl,
                    fXu = fXu,
                    xOpt = xOpt,
                    fOpt = fOpt
                )
            )
            
            // Update bounds for next iteration
            if (isMax) {
                if (f2 > f1) {
                    xu = x1
                } else {
                    xl = x2
                }
            } else {
                if (f2 < f1) {
                    xu = x1
                } else {
                    xl = x2
                }
            }
        }
        
        return steps
    }
}


