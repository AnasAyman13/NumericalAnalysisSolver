import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Dataset
import androidx.compose.material.icons.outlined.GridOn
import androidx.compose.ui.graphics.vector.ImageVector

data class ChapterData(
    val title: String,
    val description: String,
    val tags: List<String>,
    val imageRes: Int?, // Use Int for R.drawable.name
    val icon: ImageVector,
    val isBonus: Boolean = false
)

private val chapters = listOf(
    ChapterData(
        title = "Chapter 1: Root Finding",
        description = "Methods for finding roots of functions.",
        tags = listOf("Bisection", "Newton-Raphson", "Secant"),
        imageRes = com.numerical.analysis.solver.R.drawable.img_root_finding,
        icon = Icons.Outlined.GridOn
    ),
    ChapterData(
        title = "Chapter 2: Linear Systems",
        description = "Solving systems of equations.",
        tags = listOf("Gaussian", "LU", "Cramer"),
        imageRes = com.numerical.analysis.solver.R.drawable.img_linear_systems,
        icon = Icons.Outlined.GridOn
    ),
    ChapterData(
        title = "Bonus Features",
        description = "Advanced math tools.",
        tags = listOf("Plotter", "Matrix", "Error"),
        imageRes = null,
        icon = Icons.Outlined.Dataset,
        isBonus = true
    )
)