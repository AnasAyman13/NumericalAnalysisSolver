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
            description = "Methods for finding roots of functions.",
            tags = listOf("Bisection", "Newton-Raphson", "Secant"),
            imageRes = R.drawable.img_chapter1, 
            icon = Icons.Outlined.GridOn
        ),
        ChapterData(
            title = "Chapter 2: Linear Systems",
            description = "Solving systems of equations.",
            tags = listOf("Gaussian", "LU", "Cramer"),
            imageRes = R.drawable.img_chapter2,
            icon = Icons.Outlined.GridOn
        ),
        ChapterData(
            title = "Bonus Features",
            description = "Advanced mathematical tools.",
            tags = listOf("Plotter", "Matrix", "Error"),
            imageRes = null,
            icon = Icons.Outlined.Dataset,
            isBonus = true
        )
    )
}
