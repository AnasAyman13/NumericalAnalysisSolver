package com.numerical.analysis.solver.ui.theme.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class SuperscriptTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        val originalText = text.text
        val transformed = StringBuilder()
        val mapping = IntArray(originalText.length + 1)
        mapping[0] = 0
        
        var isExponent = false
        for (i in originalText.indices) {
            val char = originalText[i]
            if (char == '^') {
                isExponent = true
                mapping[i + 1] = transformed.length
                continue
            }
            if (isExponent) {
                if (char.isDigit()) {
                    transformed.append(toSuperscript(char))
                    mapping[i + 1] = transformed.length
                } else {
                    isExponent = false
                    transformed.append(char)
                    mapping[i + 1] = transformed.length
                }
            } else {
                transformed.append(char)
                mapping[i + 1] = transformed.length
            }
        }
        
        val offsetMapping = object : OffsetMapping {
            override fun originalToTransformed(offset: Int): Int {
                if (offset < 0) return 0
                if (offset >= mapping.size) return transformed.length
                return mapping[offset]
            }
            override fun transformedToOriginal(offset: Int): Int {
                // Return the largest original offset that maps to this transformed offset
                // This ensures the cursor placed after '^' (which visually maps to the same place as before '^')
                // ends up jumping the invisible '^'.
                var best = 0
                for (i in mapping.indices) {
                    if (mapping[i] == offset) {
                         best = i
                    }
                }
                return best
            }
        }
        
        return TransformedText(AnnotatedString(transformed.toString()), offsetMapping)
    }
    
    private fun toSuperscript(char: Char): Char = when(char) {
        '0' -> '⁰'
        '1' -> '¹'
        '2' -> '²'
        '3' -> '³'
        '4' -> '⁴'
        '5' -> '⁵'
        '6' -> '⁶'
        '7' -> '⁷'
        '8' -> '⁸'
        '9' -> '⁹'
        else -> char
    }
}
