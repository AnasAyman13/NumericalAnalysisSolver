package com.numerical.analysis.solver.ui.theme.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Backspace
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// ---------------------------------------------------------------------------
// Key definitions
// ---------------------------------------------------------------------------

enum class KeypadKey(
    val label: String,
    val insert: String = ""
) {
    // Row 1 — Scientific functions
    SIN("sin()", "sin("),
    COS("cos()", "cos("),
    TAN("tan()", "tan("),
    LOG("log()", "log("),
    EXP("exp()", "exp("),

    // Row 2 — Powers / variable / parentheses
    X_SQUARED("x²", "x^2"),
    X_POWER("xⁿ", "x^"),
    VAR_X("x", "x"),
    LEFT_PAREN("(", "("),
    RIGHT_PAREN(")", ")"),

    // Row 3 — 7 8 9 ÷ Backspace
    NUM_7("7", "7"),
    NUM_8("8", "8"),
    NUM_9("9", "9"),
    DIVIDE("÷", "/"),
    BACKSPACE("", ""),

    // Row 4 — 4 5 6 × −
    NUM_4("4", "4"),
    NUM_5("5", "5"),
    NUM_6("6", "6"),
    MULTIPLY("×", "*"),
    MINUS("−", "-"),

    // Row 5 — 1 2 3 + C
    NUM_1("1", "1"),
    NUM_2("2", "2"),
    NUM_3("3", "3"),
    PLUS("+", "+"),
    CLEAR("C", ""),

    // Row 6 — 0  .  [Hide]
    NUM_0("0", "0"),
    DECIMAL(".", "."),
    ARROW_RIGHT("→", ""),
    HIDE("", "")
}

// ---------------------------------------------------------------------------
// Visual classification
// ---------------------------------------------------------------------------

private enum class KeyType { NUMBER, OPERATOR, FUNCTION, POWER, CONTROL_CLEAR, CONTROL_BACK, CONTROL_HIDE }

private fun classifyKey(key: KeypadKey): KeyType = when (key) {
    KeypadKey.NUM_0, KeypadKey.NUM_1, KeypadKey.NUM_2, KeypadKey.NUM_3,
    KeypadKey.NUM_4, KeypadKey.NUM_5, KeypadKey.NUM_6, KeypadKey.NUM_7,
    KeypadKey.NUM_8, KeypadKey.NUM_9, KeypadKey.DECIMAL,
    KeypadKey.LEFT_PAREN, KeypadKey.RIGHT_PAREN -> KeyType.NUMBER

    KeypadKey.PLUS, KeypadKey.MINUS, KeypadKey.MULTIPLY, KeypadKey.DIVIDE -> KeyType.OPERATOR

    KeypadKey.SIN, KeypadKey.COS, KeypadKey.TAN, KeypadKey.LOG, KeypadKey.EXP,
    KeypadKey.VAR_X -> KeyType.FUNCTION

    KeypadKey.X_SQUARED, KeypadKey.X_POWER -> KeyType.POWER

    KeypadKey.CLEAR -> KeyType.CONTROL_CLEAR
    KeypadKey.BACKSPACE -> KeyType.CONTROL_BACK
    KeypadKey.ARROW_RIGHT -> KeyType.OPERATOR
    KeypadKey.HIDE -> KeyType.CONTROL_HIDE
}

// ---------------------------------------------------------------------------
// Input handler — cursor-aware, appends the correct parser string
// ---------------------------------------------------------------------------

fun handleKeypadInput(
    current: TextFieldValue,
    key: KeypadKey
): TextFieldValue {
    val text = current.text
    val cursor = current.selection.end.coerceIn(0, text.length)

    return when (key) {
        KeypadKey.HIDE -> current

        KeypadKey.CLEAR -> TextFieldValue("")

        KeypadKey.ARROW_RIGHT -> {
            if (cursor < text.length) {
                current.copy(selection = androidx.compose.ui.text.TextRange(cursor + 1))
            } else current
        }

        KeypadKey.BACKSPACE -> {
            if (cursor > 0) {
                val newText = text.removeRange(cursor - 1, cursor)
                current.copy(
                    text = newText,
                    selection = androidx.compose.ui.text.TextRange(cursor - 1)
                )
            } else current
        }

        else -> {
            val ins = key.insert
            val newText = text.substring(0, cursor) + ins + text.substring(cursor)
            current.copy(
                text = newText,
                selection = androidx.compose.ui.text.TextRange(cursor + ins.length)
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Keypad composable
// ---------------------------------------------------------------------------

@Composable
fun ScientificKeypad(
    visible: Boolean,
    onKey: (KeypadKey) -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically(initialOffsetY = { it }),
        exit = slideOutVertically(targetOffsetY = { it }),
        modifier = modifier
    ) {
        Surface(
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 20.dp,
            tonalElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Drag handle
                Box(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .width(40.dp)
                            .height(4.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                    )
                }

                // Row 1 — sin  cos  tan  log  exp
                KeyRow {
                    Btn(KeypadKey.SIN, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.COS, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.TAN, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.LOG, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.EXP, onKey, Modifier.weight(1f))
                }

                // Row 2 — x²  xⁿ  x  (  )
                KeyRow {
                    Btn(KeypadKey.X_SQUARED, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.X_POWER, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.VAR_X, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.LEFT_PAREN, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.RIGHT_PAREN, onKey, Modifier.weight(1f))
                }

                // Row 3 — 7  8  9  ÷  ⌫
                KeyRow {
                    Btn(KeypadKey.NUM_7, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.NUM_8, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.NUM_9, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.DIVIDE, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.BACKSPACE, onKey, Modifier.weight(1f))
                }

                // Row 4 — 4  5  6  ×  −
                KeyRow {
                    Btn(KeypadKey.NUM_4, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.NUM_5, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.NUM_6, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.MULTIPLY, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.MINUS, onKey, Modifier.weight(1f))
                }

                // Row 5 — 1  2  3  +  C
                KeyRow {
                    Btn(KeypadKey.NUM_1, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.NUM_2, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.NUM_3, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.PLUS, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.CLEAR, onKey, Modifier.weight(1f))
                }

                // Row 6 — 0 (wide)  .  [→] [Hide]
                KeyRow {
                    Btn(KeypadKey.NUM_0, onKey, Modifier.weight(2f))
                    Btn(KeypadKey.DECIMAL, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.ARROW_RIGHT, onKey, Modifier.weight(1f))
                    Btn(KeypadKey.HIDE, onKey, Modifier.weight(1f))
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Private helpers
// ---------------------------------------------------------------------------

@Composable
private fun KeyRow(content: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        content = { content() }
    )
}

@Composable
private fun Btn(
    key: KeypadKey,
    onKey: (KeypadKey) -> Unit,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current
    val keyType = classifyKey(key)

    // --- container fill colors ---
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    val primary = MaterialTheme.colorScheme.primary
    val secondary = MaterialTheme.colorScheme.secondary
    val tertiary = MaterialTheme.colorScheme.tertiary

    val containerColor: Color = when (keyType) {
        KeyType.NUMBER -> surfaceVariant.copy(alpha = 0.7f)
        KeyType.OPERATOR -> Color(0xFFDBEAFD)                   // light blue
        KeyType.FUNCTION -> secondary.copy(alpha = 0.13f)
        KeyType.POWER -> tertiary.copy(alpha = 0.15f)
        KeyType.CONTROL_CLEAR -> Color(0xFFFFE4E4)              // soft red bg
        KeyType.CONTROL_BACK -> Color(0xFFFFF3E0)               // amber bg
        KeyType.CONTROL_HIDE -> surfaceVariant.copy(alpha = 0.5f)
    }

    // --- icon / text colors ---
    val contentColor: Color = when (keyType) {
        KeyType.NUMBER -> MaterialTheme.colorScheme.onSurface
        KeyType.OPERATOR -> Color(0xFF1565C0)                   // deep blue
        KeyType.FUNCTION -> secondary
        KeyType.POWER -> tertiary
        KeyType.CONTROL_CLEAR -> Color(0xFFB91C1C)              // deep red
        KeyType.CONTROL_BACK -> Color(0xFFD97706)               // amber
        KeyType.CONTROL_HIDE -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    // --- border colors ---
    val borderColor: Color = contentColor.copy(alpha = 0.18f)

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(46.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(containerColor)
            .border(1.dp, borderColor, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = interactionSource,
                indication = ripple(bounded = true)
            ) {
                haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                onKey(key)
            },
        contentAlignment = Alignment.Center
    ) {
        when (key) {
            KeypadKey.HIDE -> Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = "Hide keypad",
                tint = contentColor,
                modifier = Modifier.size(22.dp)
            )
            KeypadKey.BACKSPACE -> Icon(
                imageVector = Icons.Outlined.Backspace,
                contentDescription = "Backspace",
                tint = contentColor,
                modifier = Modifier.size(20.dp)
            )
            else -> Text(
                text = key.label,
                fontSize = when (keyType) {
                    KeyType.FUNCTION -> 11.sp
                    KeyType.POWER -> 13.sp
                    else -> 15.sp
                },
                fontWeight = when (keyType) {
                    KeyType.NUMBER -> FontWeight.Normal
                    KeyType.OPERATOR -> FontWeight.ExtraBold
                    KeyType.POWER -> FontWeight.SemiBold
                    KeyType.FUNCTION -> FontWeight.SemiBold
                    else -> FontWeight.Bold
                },
                fontFamily = FontFamily.Monospace,
                color = contentColor,
                maxLines = 1
            )
        }
    }
}
