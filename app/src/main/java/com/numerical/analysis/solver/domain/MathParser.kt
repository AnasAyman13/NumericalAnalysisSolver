package com.numerical.analysis.solver.domain

import net.objecthunter.exp4j.ExpressionBuilder

class MathParser {

    /**
     * Parses a string representation of a mathematical function (e.g., "x^2 - 4")
     * into a Kotlin lambda function (Double) -> Double.
     */
    fun parseFunction(expression: String, variableName: String = "x"): (Double) -> Double {
        val expr = ExpressionBuilder(expression)
            .variables(variableName)
            .build()
        
        return { value ->
            expr.setVariable(variableName, value)
            expr.evaluate()
        }
    }

    /**
     * Evaluates the derivative numerically using the central difference formula.
     * f'(x) ≈ (f(x + h) - f(x - h)) / 2h
     */
    fun parseDerivative(expression: String, variableName: String = "x"): (Double) -> Double {
        val f = parseFunction(expression, variableName)
        val h = 1e-6 // Small step size
        return { value ->
            (f(value + h) - f(value - h)) / (2 * h)
        }
    }
}
