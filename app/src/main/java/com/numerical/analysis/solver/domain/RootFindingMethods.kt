package com.numerical.analysis.solver.domain

import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class RootFindingMethods {

    // =========================================================
    // BISECTION METHOD
    // =========================================================
    // The idea: the root lies between lowBound and highBound.
    // We cut the interval in half every iteration.
    // We keep the half that still contains the sign change.
    // =========================================================
    fun bisection(
        lowerBound: Double,
        upperBound: Double,
        eps: Double,
        f: (Double) -> Double
    ): List<BracketingStep> {

        // This list will hold the data for every row of the table
        val steps = mutableListOf<BracketingStep>()

        var lowBound = lowerBound    // xl  — left bracket
        var highBound = upperBound   // xu  — right bracket
        var midPoint = 0.0           // xr  — midpoint (our estimate of the root)
        var midPointOld = 0.0        // previous midpoint — needed to calculate error
        var error = 0.0              // approximate relative error in %
        var iter = 0                 // iteration counter

        // Step 1: Check that a sign change exists in the interval.
        // If f(xl) and f(xu) have the same sign, no root is guaranteed.
        if (f(lowBound) * f(highBound) >= 0) {
            throw Exception("f(xl) and f(xu) must have opposite signs.")
        }

        // Step 2: Main loop — keep iterating until the error is small enough
        while (true) {

            // Step 3: Save the previous midpoint so we can calculate error later
            midPointOld = midPoint

            // Step 4: Calculate the new midpoint — the core bisection formula
            midPoint = (lowBound + highBound) / 2.0

            // Step 5: Calculate the approximate relative error (skip iteration 0)
            // Formula: |( xr_new - xr_old ) / xr_new| * 100
            if (iter != 0) {
                error = abs((midPoint - midPointOld) / midPoint) * 100.0
            }

            // Step 6: Save this iteration's data as one row in the results table
            steps.add(
                BracketingStep(
                    iter    = iter,
                    xl      = lowBound,
                    fXl     = f(lowBound),
                    xu      = highBound,
                    fXu     = f(highBound),
                    xr      = midPoint,
                    fXr     = f(midPoint),
                    error   = if (iter == 0) 0.0 else error
                )
            )

            // Step 7: Stop if the error is within tolerance (and it's not the first iteration)
            if (error <= eps && iter != 0) break

            // Step 8: Decide which half of the interval to keep.
            // If f(xl) * f(xr) > 0, the root is in the RIGHT half → move xl to xr
            // Otherwise the root is in the LEFT half  → move xu to xr
            if (f(lowBound) * f(midPoint) > 0) {
                lowBound = midPoint   // root is in [xr, xu]
            } else {
                highBound = midPoint  // root is in [xl, xr]
            }

            iter++
            if (iter > 100) break // safety limit
        }

        return steps
    }

    // =========================================================
    // FALSE POSITION METHOD
    // =========================================================
    fun falsePosition(
        lowerBound: Double,
        upperBound: Double,
        eps: Double,
        f: (Double) -> Double
    ): List<BracketingStep> {
        val steps = mutableListOf<BracketingStep>()
        var lowBound = lowerBound
        var highBound = upperBound
        var midPoint = 0.0
        var midPointOld = 0.0
        var error = 0.0
        var iter = 0

        if (f(lowBound) * f(highBound) >= 0) {
            throw Exception("f(xl) and f(xu) must have opposite signs.")
        }

        while (true) {
            midPointOld = midPoint

            // False Position formula — uses the slope between the two bracket points
            midPoint = highBound - (f(highBound) * (lowBound - highBound)) / (f(lowBound) - f(highBound))

            if (iter != 0) {
                error = abs((midPoint - midPointOld) / midPoint) * 100.0
            }

            steps.add(
                BracketingStep(
                    iter  = iter,
                    xl    = lowBound,
                    fXl   = f(lowBound),
                    xu    = highBound,
                    fXu   = f(highBound),
                    xr    = midPoint,
                    fXr   = f(midPoint),
                    error = if (iter == 0) 0.0 else error
                )
            )

            if (error <= eps && iter != 0) break

            if (f(lowBound) * f(midPoint) > 0) {
                lowBound = midPoint
            } else {
                highBound = midPoint
            }

            iter++
            if (iter > 100) throw Exception("Failed to converge within 100 iterations")
        }

        return steps
    }

    // =========================================================
    // FIXED POINT METHOD
    // =========================================================
    fun fixedPoint(
        x0: Double,
        eps: Double,
        g: (Double) -> Double
    ): List<OpenMethodsStep> {
        val steps = mutableListOf<OpenMethodsStep>()
        var xi = x0
        var xiPlus1: Double
        var error = 0.0
        var iter = 0

        while (true) {
            // Apply the fixed-point iteration: xi+1 = g(xi)
            xiPlus1 = g(xi)

            if (iter != 0) {
                error = abs((xiPlus1 - xi) / xiPlus1) * 100.0
            }

            steps.add(OpenMethodsStep(iter, xi, xiPlus1, 0.0, if (iter == 0) 0.0 else error))

            if (error <= eps && iter != 0) break

            if (xiPlus1.isNaN() || xiPlus1.isInfinite()) throw Exception("Method diverged.")

            xi = xiPlus1
            iter++
            if (iter > 100) throw Exception("Failed to converge within 100 iterations")
        }

        return steps
    }

    // =========================================================
    // NEWTON-RAPHSON METHOD
    // =========================================================
    fun newton(
        x0: Double,
        eps: Double,
        maxIter: Int = 50,
        f: (Double) -> Double,
        fDash: (Double) -> Double
    ): List<OpenMethodsStep> {
        val steps = mutableListOf<OpenMethodsStep>()
        var xi = x0
        var xiPlus1: Double
        var error = 0.0
        var iter = 0

        while (true) {
            val derivative = fDash(xi)
            if (derivative == 0.0) throw Exception("Derivative became zero. Method failed.")

            // Newton formula: xi+1 = xi - f(xi) / f'(xi)
            xiPlus1 = xi - (f(xi) / derivative)

            if (iter != 0) {
                error = abs((xiPlus1 - xi) / xiPlus1) * 100.0
            }

            steps.add(OpenMethodsStep(iter, xi, xiPlus1, f(xi), if (iter == 0) 0.0 else error))

            if (error <= eps && iter != 0) break

            if (xiPlus1.isNaN() || xiPlus1.isInfinite()) throw Exception("Method diverged.")

            xi = xiPlus1
            iter++
            if (iter > maxIter) throw Exception("Failed to converge within $maxIter iterations")
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
        f: (Double) -> Double
    ): List<OpenMethodsStep> {
        val steps = mutableListOf<OpenMethodsStep>()
        var xiMinus1 = xMinus1
        var xi = x0
        var error = 0.0
        var iter = 0

        while (true) {
            if (iter != 0) {
                error = abs((xi - xiMinus1) / xi) * 100.0
            }

            val denominator = f(xiMinus1) - f(xi)
            if (denominator == 0.0) throw Exception("Denominator became zero. Method failed.")

            val xiNext = xi - ((f(xi) * (xiMinus1 - xi)) / denominator)

            steps.add(OpenMethodsStep(iter, xiMinus1, xi, f(xi), if (iter == 0) 0.0 else error))

            if (error <= eps && iter != 0) break

            if (xiNext.isNaN() || xiNext.isInfinite()) throw Exception("Method diverged.")

            xiMinus1 = xi
            xi = xiNext
            iter++
            if (iter > 100) throw Exception("Failed to converge within 100 iterations")
        }

        return steps
    }
}