package com.numerical.analysis.solver.domain

import kotlin.math.abs

class RootFindingMethods {

    fun bisection(
        initialXl: Double,
        initialXu: Double,
        eps: Double,
        f: (Double) -> Double
    ): List<BracketingStep> {
        val steps = mutableListOf<BracketingStep>()
        var xl = initialXl
        var xu = initialXu
        var xrOld = 0.0
        var iter = 0
        var error = 100.0

        if (f(xl) * f(xu) >= 0) return steps

        while (true) {
            val xr = (xl + xu) / 2.0

            if (iter != 0) {
                error = abs((xr - xrOld) / xr) * 100.0
            }

            steps.add(BracketingStep(iter, xl, f(xl), xu, f(xu), xr, f(xr), if (iter == 0) 0.0 else error))

            if (error <= eps && iter != 0) break

            if (f(xl) * f(xr) < 0) {
                xu = xr
            } else {
                xl = xr
            }

            xrOld = xr
            iter++
            if (iter > 100) break
        }
        return steps
    }

    fun falsePosition(
        initialXl: Double,
        initialXu: Double,
        eps: Double,
        f: (Double) -> Double
    ): List<BracketingStep> {
        val steps = mutableListOf<BracketingStep>()
        var xl = initialXl
        var xu = initialXu
        var xrOld = 0.0
        var iter = 0
        var error = 100.0

        if (f(xl) * f(xu) >= 0) return steps

        while (true) {
            val denominator = f(xl) - f(xu)
            if (denominator == 0.0) break

            val xr = xu - (f(xu) * (xl - xu)) / denominator

            if (iter != 0) {
                error = abs((xr - xrOld) / xr) * 100.0
            }

            steps.add(BracketingStep(iter, xl, f(xl), xu, f(xu), xr, f(xr), if (iter == 0) 0.0 else error))

            if (error <= eps && iter != 0) break

            if (f(xl) * f(xr) < 0) {
                xu = xr
            } else {
                xl = xr
            }

            xrOld = xr
            iter++
            if (iter > 100) break
        }
        return steps
    }

    fun fixedPoint(
        x0: Double,
        eps: Double,
        g: (Double) -> Double
    ): List<OpenMethodsStep> {
        val steps = mutableListOf<OpenMethodsStep>()
        var xi = x0
        var iter = 0
        var error = 100.0

        while (true) {
            val xiPlus1 = g(xi)

            if (iter != 0) {
                error = abs((xiPlus1 - xi) / xiPlus1) * 100.0
            }

            steps.add(OpenMethodsStep(iter, xi, xiPlus1, 0.0, if (iter == 0) 0.0 else error))

            if (error <= eps && iter != 0) break

            xi = xiPlus1
            iter++
            if (iter > 100) break
        }
        return steps
    }

    fun newton(
        x0: Double,
        eps: Double,
        maxIter: Int = 50,
        f: (Double) -> Double,
        fDash: (Double) -> Double
    ): List<OpenMethodsStep> {
        val steps = mutableListOf<OpenMethodsStep>()
        var xi = x0
        var iter = 0
        var error = 100.0

        while (iter <= maxIter) {
            val derivative = fDash(xi)
            if (derivative == 0.0) break

            val xiPlus1 = xi - (f(xi) / derivative)

            if (iter != 0) {
                error = abs((xiPlus1 - xi) / xiPlus1) * 100.0
            }

            steps.add(OpenMethodsStep(iter, xi, xiPlus1, f(xi), if (iter == 0) 0.0 else error))

            if (error <= eps && iter != 0) break

            xi = xiPlus1
            iter++
        }
        return steps
    }

    fun secant(
        xMinus1: Double,
        x0: Double,
        eps: Double,
        f: (Double) -> Double
    ): List<OpenMethodsStep> {
        val steps = mutableListOf<OpenMethodsStep>()
        var xiMinus1 = xMinus1
        var xi = x0
        var iter = 0
        var error = 100.0

        while (true) {
            if (iter != 0) {
                error = abs((xi - xiMinus1) / xi) * 100.0
            }

            val denominator = f(xiMinus1) - f(xi)
            if (denominator == 0.0) break

            val xiNext = xi - ((f(xi) * (xiMinus1 - xi)) / denominator)

            steps.add(OpenMethodsStep(iter, xiMinus1, xi, f(xi), if (iter == 0) 0.0 else error))

            if (error <= eps && iter != 0) break

            xiMinus1 = xi
            xi = xiNext
            iter++
            if (iter > 100) break
        }
        return steps
    }
}