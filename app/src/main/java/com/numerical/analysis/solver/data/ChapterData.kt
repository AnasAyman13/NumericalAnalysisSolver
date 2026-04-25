package com.numerical.analysis.solver.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dataset
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.ui.graphics.vector.ImageVector
import com.numerical.analysis.solver.R

data class ChapterData(
    val title: String,
    val description: String,
    val tags: List<String>,
    val imageRes: Int?,
    val icon: ImageVector,
    val isBonus: Boolean = false
)

fun getChaptersList(): List<ChapterData> {
    return listOf(
        ChapterData(
            title = "Chapter 1: Root Finding",
            description = "Find the roots of non-linear equations using five classical numerical methods.",
            tags = listOf("Bisection", "False Position", "Newton", "Fixed Point", "Secant"),
            imageRes = R.drawable.img_chapter1,
            icon = Icons.Outlined.GridOn
        ),
        ChapterData(
            title = "Chapter 2: Linear Systems",
            description = "Solve systems of linear equations using direct algebraic decomposition methods.",
            tags = listOf("Gauss Elimination", "LU Decomposition", "Cramer's Rule", "Gauss-Jordan"),
            imageRes = R.drawable.img_chapter2,
            icon = Icons.Outlined.GridOn
        ),
        ChapterData(
            title = "Chapter 3: Optimization",
            description = "Find the maximum or minimum of a single-variable function using the golden ratio.",
            tags = listOf("Golden Section", "Max / Min"),
            imageRes = null,
            icon = Icons.Outlined.Dataset,
            isBonus = false
        )
    )
}
