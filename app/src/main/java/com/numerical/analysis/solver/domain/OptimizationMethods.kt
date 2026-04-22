package com.numerical.analysis.solver.domain

import kotlin.math.abs

class OptimizationMethods {

    fun goldenSectionPoint(
        initialXl: Double,
        initialXu: Double,
        eps: Double,
        isMax: Boolean,
        f: (Double) -> Double
    ): List<GoldenSectionStep> {
        val steps = mutableListOf<GoldenSectionStep>()
        var xl = initialXl
        var xu = initialXu
        val R = (Math.sqrt(5.0) - 1.0) / 2.0
        
        var d = R * (xu - xl)
        var x1 = xl + d
        var x2 = xu - d
        var f1 = f(x1)
        var f2 = f(x2)
        
        var iter = 0
        var xOpt: Double
        var error: Double
        
        while(true) {
            if(isMax) {
                if(f1 > f2) {
                    xl = x2
                    x2 = x1
                    f2 = f1
                    x1 = xl + R * (xu - xl)
                    f1 = f(x1)
                } else {
                    xu = x1
                    x1 = x2
                    f1 = f2
                    x2 = xu - R * (xu - xl)
                    f2 = f(x2)
                }
            } else {
                if(f1 < f2) {
                    xl = x2
                    x2 = x1
                    f2 = f1
                    x1 = xl + R * (xu - xl)
                    f1 = f(x1)
                } else {
                    xu = x1
                    x1 = x2
                    f1 = f2
                    x2 = xu - R * (xu - xl)
                    f2 = f(x2)
                }
            }
            
            xOpt = if (isMax) {
                if (f1 > f2) x1 else x2
            } else {
                if (f1 < f2) x1 else x2
            }
            
            error = (1.0 - R) * abs((xu - xl) / xOpt) * 100.0
            
            steps.add(GoldenSectionStep(iter, xl, f(xl), x2, f2, x1, f1, xu, f(xu), xOpt, f(xOpt), if (iter == 0) 100.0 else error))
            
            if (error <= eps) break
            
            iter++
            if (iter > 100) throw Exception("Failed to converge within 100 iterations")
        }
        
        return steps
    }
}
