package com.numerical.analysis.solver.ui.theme.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numerical.analysis.solver.data.ChapterData
import com.numerical.analysis.solver.data.getChaptersList

val PrimaryBlue = Color(0xFF1586EF)
val GradientEnd = Color(0xFF4AC29A)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    onChapterClick: (String) -> Unit
) {
    val chapters = getChaptersList()

    Scaffold(
        containerColor = Color(0xFFF0F4F8), // Soft premium background
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                    .shadow(16.dp),
                containerColor = Color.White.copy(alpha = 0.75f), // Transparent glass effect
                tonalElevation = 0.dp
            ) {
                NavigationBarItem(
                    selected = currentRoute == "home",
                    onClick = { onNavigate("home") },
                    icon = { Icon(Icons.Outlined.Home, contentDescription = "Home") },
                    label = { Text("Home", fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue,
                        indicatorColor = PrimaryBlue.copy(alpha = 0.1f),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == "history",
                    onClick = { onNavigate("history") },
                    icon = { Icon(Icons.Outlined.History, contentDescription = "History") },
                    label = { Text("History", fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue,
                        indicatorColor = PrimaryBlue.copy(alpha = 0.1f),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    )
                )
                NavigationBarItem(
                    selected = currentRoute == "about",
                    onClick = { onNavigate("about") },
                    icon = { Icon(Icons.Outlined.Info, contentDescription = "About") },
                    label = { Text("About", fontWeight = FontWeight.Medium) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PrimaryBlue,
                        selectedTextColor = PrimaryBlue,
                        indicatorColor = PrimaryBlue.copy(alpha = 0.1f),
                        unselectedIconColor = Color(0xFF94A3B8),
                        unselectedTextColor = Color(0xFF94A3B8)
                    )
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            contentPadding = PaddingValues(top = 32.dp, bottom = 40.dp)
        ) {
            item {
                Text(
                    text = "DASHBOARD",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryBlue,
                    letterSpacing = 1.5.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "Numerical Solver",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0F172A),
                    modifier = Modifier.padding(bottom = 24.dp)
                )
            }

            items(chapters) { chapter ->
                PremiumChapterCard(
                    chapter = chapter,
                    onClick = { onChapterClick(chapter.title) }
                )
                Spacer(Modifier.height(20.dp))
            }

            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "MTI University - CS-252",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp,
                )
            }
        }
    }
}

@Composable
fun PremiumChapterCard(chapter: ChapterData, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 16.dp, 
                shape = RoundedCornerShape(24.dp),
                spotColor = Color(0xFF1586EF).copy(alpha = 0.15f)
            )
            .clip(RoundedCornerShape(24.dp))
            .clickable { onClick() }
            .background(Color.White),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column {
            if (!chapter.isBonus && chapter.imageRes != null) {
                Box(
                    modifier = Modifier
                        .height(140.dp)
                        .fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = chapter.imageRes),
                        contentDescription = chapter.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Beautiful gradient overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                                )
                            )
                    )
                    Text(
                        text = chapter.title,
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(20.dp),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    )
                }
            } else {
                // Bonus aesthetic
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(PrimaryBlue, GradientEnd)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(chapter.icon, null, tint = Color.White, modifier = Modifier.size(36.dp))
                }
                Text(
                    text = chapter.title,
                    modifier = Modifier.padding(start = 24.dp, top = 20.dp),
                    color = Color(0xFF0F172A),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }

            Column(Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                Text(
                    text = chapter.description, 
                    color = Color(0xFF64748B), 
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )
                
                Spacer(Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    chapter.tags.forEach { tag ->
                        Box(
                            modifier = Modifier
                                .background(Color(0xFFF1F5F9), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(tag, fontSize = 12.sp, color = PrimaryBlue, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}