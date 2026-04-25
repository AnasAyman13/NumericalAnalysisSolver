package com.numerical.analysis.solver.domain

import com.numerical.analysis.solver.ui.screens.state.ToleranceMode
import kotlin.math.abs

class RootFindingMethods {

    private fun round5(value: Double): Double {
        if (value.isNaN() || value.isInfinite()) return value
        return Math.round(value * 100000.0) / 100000.0
    }

    // =========================================================
    // BISECTION METHOD
    // =========================================================
    fun bisection(
        lowerBound: Double,
        upperBound: Double,
        eps: Double,
        maxIter: Int = 100,
        mode: ToleranceMode,
        f: (Double) -> Double
    ): List<BracketingStep> {
        val steps = mutableListOf<BracketingStep>()
        var lowBound  = lowerBound
        var highBound = upperBound
        var midPoint  = 0.0
        var midPointOld = 0.0
        var error     = 0.0
        var iter      = 1

        if (f(lowBound) * f(highBound) >= 0) {
            throw Exception("f(xl) and f(xu) must have opposite signs.")
        }

        while (iter <= maxIter) {
            midPointOld = midPoint
            midPoint = (lowBound + highBound) / 2.0
            midPoint = round5(midPoint)

            if (iter > 1) {
                error = if (mode == ToleranceMode.ABSOLUTE) {
                    abs(midPoint - midPointOld)
                } else {
                    if (midPoint == 0.0) 0.0 else abs((midPoint - midPointOld) / midPoint) * 100.0
                }
                error = round5(error)
            }

            val fXl = round5(f(lowBound))
            val fXu = round5(f(highBound))
            val fXr = round5(f(midPoint))

            steps.add(
                BracketingStep(
                    iter  = iter,
                    xl    = lowBound,
                    fXl   = fXl,
                    xu    = highBound,
                    fXu   = fXu,
                    xr    = midPoint,
                    fXr   = fXr,
                    error = if (iter == 1) 0.0 else error
                )
            )

            if (error <= eps && iter > 1) break

            if (fXl * fXr > 0) {
                lowBound = midPoint
            } else {
                highBound = midPoint
            }

            iter++
        }
        return steps
    }

    fun falsePosition(
        lowerBound: Double,
        upperBound: Double,
        eps: Double,
        maxIter: Int = 100,
        mode: ToleranceMode,
        f: (Double) -> Double
    ): List<BracketingStep> {
        val steps = mutableListOf<BracketingStep>()
        var lowBound  = lowerBound
        var highBound = upperBound
        var xr        = 0.0
        var xrOld     = 0.0
        var error     = 0.0
        var iter      = 1

        if (f(lowBound) * f(highBound) >= 0) {
            throw Exception("f(xl) and f(xu) must have opposite signs.")
        }

        while (iter <= maxIter) {
            xrOld = xr

            val fXl = round5(f(lowBound))
            val fXu = round5(f(highBound))

            val denom = fXl - fXu
            if (denom == 0.0) throw Exception("f(xl) equals f(xu). Method failed.")

            xr = highBound - (fXu * (lowBound - highBound)) / denom
            xr = round5(xr)

            val fXr = round5(f(xr))

            if (iter > 1) {
                error = if (mode == ToleranceMode.ABSOLUTE) {
                    abs(xr - xrOld)
                } else {
                    if (xr == 0.0) 0.0 else abs((xr - xrOld) / xr) * 100.0
                }
                error = round5(error)
            }

            steps.add(
                BracketingStep(
                    iter  = iter,
                    xl    = lowBound,
                    fXl   = fXl,
                    xu    = highBound,
                    fXu   = fXu,
                    xr    = xr,
                    fXr   = fXr,
                    error = if (iter == 1) 0.0 else error
                )
            )

            if (error <= eps && iter > 1) break

            if (fXl * fXr > 0) {
                lowBound = xr
            } else {
                highBound = xr
            }

            iter++
        }

        return steps
    }

    // =========================================================
    // FIXED POINT METHOD
    // =========================================================
    fun fixedPoint(
        x0: Double,
        eps: Double,
        maxIter: Int = 100,
        mode: ToleranceMode,
        g: (Double) -> Double
    ): List<OpenMethodsStep> {
        val steps = mutableListOf<OpenMethodsStep>()
        var xi = x0
        var xiPlus1: Double
        var error = 0.0
        var iter = 0

        while (iter <= maxIter) {
            xiPlus1 = g(xi)
            xiPlus1 = round5(xiPlus1)

            if (iter != 0) {
                error = if (mode == ToleranceMode.ABSOLUTE) {
                    abs(xiPlus1 - xi)
                } else {
                    if (xiPlus1 == 0.0) 0.0 else abs((xiPlus1 - xi) / xiPlus1) * 100.0
                }
                error = round5(error)
            }

            steps.add(OpenMethodsStep(iter, xi, xiPlus1, 0.0, if (iter == 0) 0.0 else error))

            if (error <= eps && iter != 0) break

            if (xiPlus1.isNaN() || xiPlus1.isInfinite()) throw Exception("Method diverged.")

            xi = xiPlus1
            iter++
        }

        return steps
    }

    // =========================================================
    // NEWTON-RAPHSON METHOD
    // =========================================================
    fun newton(
        x0: Double,
        eps: Double,
        maxIter: Int = 100,
        mode: ToleranceMode,
        f: (Double) -> Double,
        fDash: (Double) -> Double
    ): List<OpenMethodsStep> {
        val steps = mutableListOf<OpenMethodsStep>()
        var xi = x0
        var xiPlus1: Double
        var error = 0.0
        var iter = 0

        while (iter <= maxIter) {
            val derivative = round5(fDash(xi))
            if (derivative == 0.0) throw Exception("Derivative became zero. Method failed.")

            val fXi = round5(f(xi))

            xiPlus1 = xi - (fXi / derivative)
            xiPlus1 = round5(xiPlus1)

            if (iter != 0) {
                error = if (mode == ToleranceMode.ABSOLUTE) {
                    abs(xiPlus1 - xi)
                } else {
                    if (xiPlus1 == 0.0) 0.0 else abs((xiPlus1 - xi) / xiPlus1) * 100.0
                }
                error = round5(error)
            }

            steps.add(OpenMethodsStep(iter, xi, xiPlus1, fXi, if (iter == 0) 0.0 else error))

            if (error <= eps && iter != 0) break

            if (xiPlus1.isNaN() || xiPlus1.isInfinite()) throw Exception("Method diverged.")

            xi = xiPlus1
            iter++
        }

        return steps
    }

    // =========================================================
    // SECANT METHOD
    // =========================================================
    fun secant(
        xMinus1: Double,
        x0: Double,
        eps: Double,
        maxIter: Int = 100,
        mode: ToleranceMode,
        f: (Double) -> Double
    ): List<OpenMethodsStep> {
        val steps = mutableListOf<OpenMethodsStep>()
        var xiMinus1 = xMinus1
        var xi = x0
        var error = 0.0
        var iter = 0

        while (iter <= maxIter) {
            if (iter != 0) {
                error = if (mode == ToleranceMode.ABSOLUTE) {
                    abs(xi - xiMinus1)
                } else {
                    if (xi == 0.0) 0.0 else abs((xi - xiMinus1) / xi) * 100.0
                }
                error = round5(error)
            }
            
            val fXiMinus1 = round5(f(xiMinus1))
            val fXi = round5(f(xi))

            val denominator = fXiMinus1 - fXi
            if (denominator == 0.0) throw Exception("Denominator became zero. Method failed.")

            var xiNext = xi - ((fXi * (xiMinus1 - xi)) / denominator)
            xiNext = round5(xiNext)

            steps.add(OpenMethodsStep(iter, xiMinus1, xi, fXi, if (iter == 0) 0.0 else error))

            if (error <= eps && iter != 0) break

            if (xiNext.isNaN() || xiNext.isInfinite()) throw Exception("Method diverged.")

            xiMinus1 = xi
            xi = xiNext
            iter++
        }

        return steps
    }
}