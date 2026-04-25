package com.numerical.analysis.solver.domain

import net.objecthunter.exp4j.ExpressionBuilder

class MathParser {

    /**
     * Parses a string representation of a mathematical function (e.g., "x^2 - 4")
     * into a Kotlin lambda function (Double) -> Double.
     */
    fun parseFunction(expression: String, variableName: String = "x"): (Double) -> Double {
        // 1. Remove visual artifacts and normalize
        var sanitized = expression.replace(" ", "")
            .replace("²", "^2")
            .replace("ⁿ", "^")
            .replace("−", "-") 
            .replace("×", "*")
            .replace("÷", "/")
            .replace("**", "^") // Support python-style power

        // Normalize ln(x) -> log(x) [exp4j uses log for natural log]
        // Normalize log(x) -> log10(x) [Some users expect base 10 for log]
        sanitized = sanitized.replace("ln(", "log(")
            .replace("log10(", "log10(") // already good
            // We should be careful about log() vs log10(). 
            // I'll leave log() as log (natural) unless it's explicitly log10.

        // 2. Explicitly handle implicit multiplication using regex
        // Number followed by variable: 2x -> 2*x
        sanitized = sanitized.replace("(\\d)([a-zA-Z])".toRegex(), "$1*$2")
        // Number followed by paren: 2( -> 2*(
        sanitized = sanitized.replace("(\\d)(\\()".toRegex(), "$1*$2")
        // Number followed by 'e' or 'pi' (if not part of a word)
        // Wait, e and pi are letters, so it's already covered by ([a-zA-Z]) regex.

        // Variable followed by paren: x( -> x*(
        sanitized = sanitized.replace("([a-zA-Z])(\\()".toRegex(), "$1*$2")
        // Paren followed by letter: )x -> )*x
        sanitized = sanitized.replace("(\\))([a-zA-Z])".toRegex(), "$1*$2")
        // Paren followed by digit: )2 -> )*2
        sanitized = sanitized.replace("(\\))(\\d)".toRegex(), "$1*$2")
        // Paren followed by paren: )( -> )*(
        sanitized = sanitized.replace("(\\))(\\()".toRegex(), "$1*$2")
        
        // Scientific notation: 10^-4 in 3.993*10^-4 is fine, 
        // but if they write 3.993e-4, exp4j handles it.
        // If they write 3.993*10^-4, it's also fine.

        try {
            val expr = ExpressionBuilder(sanitized)
                .variables(variableName)
                .build()

            return { value ->
                expr.setVariable(variableName, value)
                // Remove the restrictive round5 here; we want full Double precision during calculation.
                // Rounding should only happen at the final presentation layer if needed.
                expr.evaluate()
            }
        } catch (e: Exception) {
            // Return a function that throws if the expression is invalid
            return { throw Exception("Invalid syntax: ${e.message}") }
        }
    }
}
