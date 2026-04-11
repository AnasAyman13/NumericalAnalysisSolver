package com.numerical.analysis.solver.ui.screens.dashboard

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

// --- Design Tokens ---
private val Primary = Color(0xFF1586EF)
private val BackgroundLight = Color(0xFFF2F2F7)
private val SlateDark900 = Color(0xFF0F172A)

// --- Model ---
data class ChapterData(
    val title: String,
    val description: String,
    val tags: List<String>,ٍ
    val imageUrl: String,
    val icon: ImageVector,
    val isBonus: Boolean = false
)

private val chapters = listOf(
    ChapterData("Chapter 1: Root Finding", "Methods for finding roots of functions.", listOf("Bisection", "Newton-Raphson", "Secant"), "https://images.unsplash.com/photo-1509228468518-180dd48a57a1", Icons.Outlined.GridOn),
    ChapterData("Chapter 2: Linear Systems", "Solving systems of equations.", listOf("Gaussian", "LU", "Cramer"), "https://images.unsplash.com/photo-1635070041078-e363dbe005cb", Icons.Outlined.GridOn),
    ChapterData("Bonus Features", "Advanced math tools.", listOf("Plotter", "Matrix", "Error"), "", Icons.Outlined.Dataset, true)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(onChapterClick: (String) -> Unit = {}) {
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        containerColor = BackgroundLight,
        topBar = {
            TopAppBar(
                title = { Text("Numerical Analysis", fontWeight = FontWeight.Bold) },
                actions = { IconButton(onClick = {}) { Icon(Icons.Outlined.Info, null, tint = Primary) } }
            )
        },
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(selected = selectedTab == 0, onClick = { selectedTab = 0 }, icon = { Icon(Icons.Outlined.Home, null) }, label = { Text("Home") })
                NavigationBarItem(selected = selectedTab == 1, onClick = { selectedTab = 1 }, icon = { Icon(Icons.Outlined.History, null) }, label = { Text("History") })
                NavigationBarItem(selected = selectedTab == 2, onClick = { selectedTab = 2 }, icon = { Icon(Icons.Outlined.Person, null) }, label = { Text("About") })
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            item {
                Spacer(Modifier.height(16.dp))
                Text("DASHBOARD", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Color.Gray)
                Text("Select a Chapter", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(16.dp))
            }
            items(chapters.size) { index ->
                val chapter = chapters[index]
                DashboardCard(chapter, onClick = { onChapterClick(chapter.title) })
                Spacer(Modifier.height(16.dp))
            }
            item {
                Text("MTI University - CS-252", modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp), textAlign = androidx.compose.ui.text.style.TextAlign.Center, color = Primary.copy(0.6f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun DashboardCard(chapter: ChapterData, onClick: () -> Unit) {
    val cardBg = if (chapter.isBonus) SlateDark900 else Color.White
    val contentColor = if (chapter.isBonus) Color.White else Color.Black

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBg),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        if (!chapter.isBonus) {
            Box(Modifier.height(120.dp).fillMaxWidth()) {
                AsyncImage(model = chapter.imageUrl, contentDescription = null, contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize())
                Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(0.7f)))))
                Text(chapter.title, modifier = Modifier.align(Alignment.BottomStart).padding(16.dp), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
            }
        }
        Column(Modifier.padding(16.dp)) {
            if (chapter.isBonus) Text(chapter.title, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = Color.White)
            Text(chapter.description, color = if (chapter.isBonus) Color.LightGray else Color.Gray, fontSize = 14.sp)
            Spacer(Modifier.height(12.dp))
            Button(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(if (chapter.isBonus) Color.White else Primary, if (chapter.isBonus) SlateDark900 else Color.White)) {
                Text("OPEN MODULE", fontWeight = FontWeight.Bold)
                Icon(Icons.AutoMirrored.Filled.ArrowForward, null, Modifier.padding(start = 8.dp).size(18.dp))
            }
        }
    }
}