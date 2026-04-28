package com.numerical.analysis.solver.domain

import net.objecthunter.exp4j.ExpressionBuilder

class MathParser {

    companion object {
        // All function names recognised by exp4j (order longest-first to avoid partial matches)
        private val FUNCTIONS = listOf(
            "sqrt", "cbrt", "sinh", "cosh", "tanh",
            "asin", "acos", "atan", "log10", "log2",
            "sin", "cos", "tan", "log", "ln", "exp", "abs", "ceil", "floor"
        )
        // Pre-built alternation pattern, e.g.  sqrt|cbrt|sinh|...
        private val FN_PATTERN = FUNCTIONS.joinToString("|")
    }

    /**
     * Inserts '*' for implicit multiplication WITHOUT breaking function names.
     *
     * Rules applied (in order):
     *  1. number | x  before a function name  → insert *    (3sqrt → 3*sqrt)
     *  2. number before x or other letter     → insert *    (3x → 3*x)
     *  3. number before '('                   → insert *    (3( → 3*()
     *  4. x/letter before '(' NOT preceded by a function name → insert *  (x( → x*()
     *  5. ')' before x/letter                 → insert *    ()x → )*x)
     *  6. ')' before digit                    → insert *    ()2 → )*2)
     *  7. ')' before '('                      → insert *    ()( → )*(
     */
    private fun preprocessEquation(input: String): String {
        var s = input
        
        // 1. Temporarily replace all functions with placeholders so they aren't mangled
        val placeholders = mutableMapOf<String, String>()
        FUNCTIONS.forEachIndexed { i, fn ->
            val placeholder = "@@F$i@@"
            placeholders[placeholder] = fn
            s = s.replace(fn, placeholder)
        }

        // 2. Insert '*' between number/x and a function placeholder (e.g., 3sqrt -> 3*sqrt, xsin -> x*sin)
        s = s.replace("([\\dx])(@@F\\d+@@)".toRegex(), "$1*$2")
        
        // 3. Insert '*' between ')' and a function placeholder (e.g., )sqrt -> )*sqrt)
        s = s.replace("(\\))(@@F\\d+@@)".toRegex(), "$1*$2")

        // 4. Insert '*' between a number and a letter (e.g., 3x -> 3*x)
        s = s.replace("(\\d)([a-zA-Z])".toRegex(), "$1*$2")

        // 5. Insert '*' between a number and '(' (e.g., 3( -> 3*()
        s = s.replace("(\\d)(\\()".toRegex(), "$1*$2")

        // 6. Insert '*' between a letter and '(' (e.g., x( -> x*()
        s = s.replace("([a-zA-Z])(\\()".toRegex(), "$1*$2")

        // 7. Insert '*' between ')' and a letter (e.g., )x -> )*x)
        s = s.replace("(\\))([a-zA-Z])".toRegex(), "$1*$2")

        // 8. Insert '*' between ')' and a number (e.g., )2 -> )*2)
        s = s.replace("(\\))(\\d)".toRegex(), "$1*$2")

        // 9. Insert '*' between ')' and '(' (e.g., )( -> )*()
        s = s.replace("(\\))(\\()".toRegex(), "$1*$2")

        // 10. Restore the function names from placeholders
        placeholders.forEach { (placeholder, fn) ->
            s = s.replace(placeholder, fn)
        }

        return s
    }

    /**
     * Parses a string mathematical function into a Kotlin lambda (Double) -> Double.
     */
    fun parseFunction(expression: String, variableName: String = "x"): (Double) -> Double {
        // Step 1 — visual / encoding normalisations
        var sanitized = expression
            .replace(" ", "")
            .replace("²", "^2")
            .replace("ⁿ", "^")
            .replace("−", "-")
            .replace("×", "*")
            .replace("÷", "/")
            .replace("**", "^")

        // Step 2 — normalise ln → log  (exp4j uses "log" for the natural logarithm)
        sanitized = sanitized.replace("ln(", "log(")

        // Step 3 — inject implicit multiplication operators
        sanitized = preprocessEquation(sanitized)

        try {
            val expr = ExpressionBuilder(sanitized)
                .variables(variableName)
                .build()

            return { value ->
                expr.setVariable(variableName, value)
                expr.evaluate()
            }
        } catch (e: Exception) {
            return { throw Exception("Invalid syntax: ${e.message}") }
        }
    }
}
