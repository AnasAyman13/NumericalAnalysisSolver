package com.numerical.analysis.solver.domain

import net.objecthunter.exp4j.ExpressionBuilder

class MathParser {

    /**
     * Parses a string representation of a mathematical function (e.g., "x^2 - 4")
     * into a Kotlin lambda function (Double) -> Double.
     */
    fun parseFunction(expression: String, variableName: String = "x"): (Double) -> Double {
        // Translate visual superscripts into standard operators for the mathematical parser
        val sanitizedExpression = expression
            .replace("²", "^2")
            .replace("ⁿ", "^")

        val expr = ExpressionBuilder(sanitizedExpression)
            .variables(variableName)
            .build()

        return { value ->
            expr.setVariable(variableName, value)
            val raw = expr.evaluate()
            Math.round(raw * 100000.0) / 100000.0
        }
    }
}
