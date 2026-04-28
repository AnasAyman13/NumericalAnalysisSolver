package com.numerical.analysis.solver.ui.theme.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class SuperscriptTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val original = text.text
        val transformed = StringBuilder()
        val mapping = IntArray(original.length + 1)
        mapping[0] = 0
        var isExponent = false

        for (i in original.indices) {
            val ch = original[i]
            if (ch == '^') {
                isExponent = true
                mapping[i + 1] = transformed.length
                continue
            }
            if (isExponent && (ch.isDigit() || ch == '(')) {
                transformed.append(toSuperscript(ch))
                if (ch != '(') isExponent = true // keep going
            } else {
                if (!ch.isDigit() && ch != ')') isExponent = false
                transformed.append(ch)
            }
            mapping[i + 1] = transformed.length
        }

        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset < 0) return 0
                if (offset >= mapping.size) return transformed.length
                return mapping[offset]
            }

            override fun transformedToOriginal(offset: Int): Int {
                var best = 0
                for (i in mapping.indices) {
                    if (mapping[i] <= offset) best = i
                }
                return best
            }
        }

        return TransformedText(AnnotatedString(transformed.toString()), offsetMapping)
    }

    private fun toSuperscript(c: Char) = when (c) {
        '0' -> '⁰'; '1' -> '¹'; '2' -> '²'; '3' -> '³'; '4' -> '⁴'
        '5' -> '⁵'; '6' -> '⁶'; '7' -> '⁷'; '8' -> '⁸'; '9' -> '⁹'
        '(' -> '⁽'; ')' -> '⁾'
        else -> c
    }
}
