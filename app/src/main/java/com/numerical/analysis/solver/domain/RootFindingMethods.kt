package com.numerical.analysis.solver.domain

import com.numerical.analysis.solver.ui.screens.state.ToleranceMode
import kotlinx.coroutines.yield
import kotlin.math.abs

class RootFindingMethods {

    // =========================================================
    // BISECTION METHOD
    // =========================================================
    suspend fun bisection(
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
            // throw Exception("f(xl) and f(xu) must have opposite signs.")
        }

        while (iter <= maxIter) {
            yield() // Check for coroutine cancellation
            midPointOld = midPoint
            midPoint = (lowBound + highBound) / 2.0

            if (iter > 1) {
                error = if (mode == ToleranceMode.ABSOLUTE) {
                    abs(midPoint - midPointOld)
                } else {
                    if (abs(midPoint) < 1e-18) 0.0 else abs((midPoint - midPointOld) / midPoint) * 100.0
                }
            }

            val fXl = f(lowBound)
            val fXu = f(highBound)
            val fXr = f(midPoint)

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
            if (abs(fXr) < 1e-14) break

            if (fXl * fXr > 0) {
                lowBound = midPoint
            } else {
                highBound = midPoint
            }

            iter++
        }
        return steps
    }

    suspend fun falsePosition(
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
            // throw Exception("f(xl) and f(xu) must have opposite signs.")
        }

        while (iter <= maxIter) {
            yield()
            xrOld = xr

            val fXl = f(lowBound)
            val fXu = f(highBound)

            val denom = fXl - fXu
            if (abs(denom) < 1e-20) throw Exception("f(xl) equals f(xu). Method failed.")

            xr = highBound - (fXu * (lowBound - highBound)) / denom

            val fXr = f(xr)

            if (iter > 1) {
                error = if (mode == ToleranceMode.ABSOLUTE) {
                    abs(xr - xrOld)
                } else {
                    if (abs(xr) < 1e-18) 0.0 else abs((xr - xrOld) / xr) * 100.0
                }
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
            if (abs(fXr) < 1e-14) break

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
    suspend fun fixedPoint(
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
            yield()
            xiPlus1 = g(xi)

            if (iter != 0) {
                error = if (mode == ToleranceMode.ABSOLUTE) {
                    abs(xiPlus1 - xi)
                } else {
                    if (abs(xiPlus1) < 1e-18) 0.0 else abs((xiPlus1 - xi) / xiPlus1) * 100.0
                }
            }

            steps.add(OpenMethodsStep(iter, xi, xiPlus1, 0.0, if (iter == 0) 0.0 else error))

            if (error <= eps && iter != 0) break
            if (abs(xiPlus1 - xi) < 1e-14) break

            if (xiPlus1.isNaN() || xiPlus1.isInfinite() || abs(xiPlus1) > 1e20) {
                // Previously threw Exception; now continuing to allow user to see behavior as requested
                break 
            }

            xi = xiPlus1
            iter++
        }

        return steps
    }

    // =========================================================
    // NEWTON-RAPHSON METHOD
    // =========================================================
    suspend fun newton(
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
            yield()
            val derivative = fDash(xi)
            if (abs(derivative) < 1e-20) throw Exception("Derivative became zero. Method failed.")

            val fXi = f(xi)

            xiPlus1 = xi - (fXi / derivative)

            if (iter != 0) {
                error = if (mode == ToleranceMode.ABSOLUTE) {
                    abs(xiPlus1 - xi)
                } else {
                    if (abs(xiPlus1) < 1e-18) 0.0 else abs((xiPlus1 - xi) / xiPlus1) * 100.0
                }
            }

            steps.add(OpenMethodsStep(iter, xi, xiPlus1, fXi, if (iter == 0) 0.0 else error))

            if (error <= eps && iter != 0) break
            if (abs(fXi) < 1e-14) break

            if (xiPlus1.isNaN() || xiPlus1.isInfinite() || abs(xiPlus1) > 1e20) break

            xi = xiPlus1
            iter++
        }

        return steps
    }

    // =========================================================
    // SECANT METHOD
    // =========================================================
    suspend fun secant(
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
            yield()
            if (iter != 0) {
                error = if (mode == ToleranceMode.ABSOLUTE) {
                    abs(xi - xiMinus1)
                } else {
                    if (abs(xi) < 1e-18) 0.0 else abs((xi - xiMinus1) / xi) * 100.0
                }
            }
            
            val fXiMinus1 = f(xiMinus1)
            val fXi = f(xi)

            val denominator = fXiMinus1 - fXi
            if (abs(denominator) < 1e-20) throw Exception("Denominator became zero. Method failed.")

            var xiNext = xi - ((fXi * (xiMinus1 - xi)) / denominator)

            steps.add(OpenMethodsStep(iter, xiMinus1, xi, fXi, if (iter == 0) 0.0 else error))

            if (error <= eps && iter != 0) break
            if (abs(fXi) < 1e-14) break

            if (xiNext.isNaN() || xiNext.isInfinite() || abs(xiNext) > 1e20) break

            xiMinus1 = xi
            xi = xiNext
            iter++
        }

        return steps
    }
}