import net.objecthunter.exp4j.ExpressionBuilder

fun main() {
    val FUNCTIONS = listOf(
        "sqrt", "cbrt", "sinh", "cosh", "tanh",
        "asin", "acos", "atan", "log10", "log2",
        "sin", "cos", "tan", "log", "ln", "exp", "abs", "ceil", "floor"
    )
    val FN_PATTERN = FUNCTIONS.joinToString("|")

    fun preprocessEquation(input: String): String {
        var s = input
        s = s.replace("([\\dx])($FN_PATTERN)".toRegex()) { m -> "${m.groupValues[1]}*${m.groupValues[2]}" }
        s = s.replace("(\\d)(?!$FN_PATTERN)([a-zA-Z])".toRegex()) { m -> "${m.groupValues[1]}*${m.groupValues[2]}" }
        s = s.replace("(\\d)(\\()".toRegex(), "$1*$2")
        s = s.replace("([a-wyzA-Z])(\\()".toRegex(), "$1*$2")
        for (fn in FUNCTIONS) {
            val broken = fn.zipWithNext().joinToString("") { (a, b) -> "$a*$b" }
            if (broken in s) s = s.replace(broken, fn)
        }
        s = s.replace("(\\))([a-zA-Z])".toRegex(), "$1*$2")
        s = s.replace("(\\))(\\d)".toRegex(), "$1*$2")
        s = s.replace("(\\))(\\()".toRegex(), "$1*$2")
        return s
    }

    var sanitized = "sqrt(1.9x+2.8)"
        .replace(" ", "")
        .replace("²", "^2")
        .replace("ⁿ", "^")
        .replace("−", "-")
        .replace("×", "*")
        .replace("÷", "/")
        .replace("**", "^")
    sanitized = sanitized.replace("ln(", "log(")
    
    val before = sanitized
    sanitized = preprocessEquation(sanitized)
    
    println("Original: sqrt(1.9x+2.8)")
    println("Before: $before")
    println("After:  $sanitized")
}
