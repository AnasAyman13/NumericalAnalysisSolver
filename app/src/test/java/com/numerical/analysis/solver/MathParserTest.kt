package com.numerical.analysis.solver

import com.numerical.analysis.solver.domain.MathParser
import org.junit.Test
import org.junit.Assert.*

class MathParserTest {
    @Test
    fun testPreprocess() {
        val FUNCTIONS = listOf(
            "sqrt", "cbrt", "sinh", "cosh", "tanh",
            "asin", "acos", "atan", "log10", "log2",
            "sin", "cos", "tan", "log", "ln", "exp", "abs", "ceil", "floor"
        )
        val FN_PATTERN = FUNCTIONS.joinToString("|")

        fun preprocessEquation(input: String): String {
            var s = input
            
            val placeholders = mutableMapOf<String, String>()
            FUNCTIONS.forEachIndexed { i, fn ->
                val placeholder = "@@F$i@@"
                placeholders[placeholder] = fn
                s = s.replace(fn, placeholder)
            }

            s = s.replace("([\\dx])(@@F\\d+@@)".toRegex(), "$1*$2")
            s = s.replace("(\\))(@@F\\d+@@)".toRegex(), "$1*$2")
            s = s.replace("(\\d)([a-zA-Z])".toRegex(), "$1*$2")
            s = s.replace("(\\d)(\\()".toRegex(), "$1*$2")
            s = s.replace("([a-zA-Z])(\\()".toRegex(), "$1*$2")
            s = s.replace("(\\))([a-zA-Z])".toRegex(), "$1*$2")
            s = s.replace("(\\))(\\d)".toRegex(), "$1*$2")
            s = s.replace("(\\))(\\()".toRegex(), "$1*$2")

            placeholders.forEach { (placeholder, fn) ->
                s = s.replace(placeholder, fn)
            }

            return s
        }

        val original = "sqrt(1.9x+2.8)"
        val after = preprocessEquation(original)
        println("original: $original")
        println("after: $after")
    }
}
