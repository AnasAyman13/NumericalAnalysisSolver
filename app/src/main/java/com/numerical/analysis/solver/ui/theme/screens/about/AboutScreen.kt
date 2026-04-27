package com.numerical.analysis.solver.ui.screens.about

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.numerical.analysis.solver.ui.theme.*
import com.numerical.analysis.solver.ui.theme.components.MathBackground


@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    Scaffold(
        containerColor = BackgroundLight,
        topBar = { TopNavigationBar(onNavigateBack = onNavigateBack) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            MathBackground()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .systemBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
            Spacer(modifier = Modifier.height(20.dp))
            AppHeader()
            Spacer(modifier = Modifier.height(20.dp))
            CourseInformationCard()
            Spacer(modifier = Modifier.height(20.dp))
            FacultySection()
            Spacer(modifier = Modifier.height(20.dp))
            DevelopmentTeamSection()
            Spacer(modifier = Modifier.height(28.dp))
            Footer()
            Spacer(modifier = Modifier.height(28.dp))
        }
        }
    }
}

@Composable
private fun TopNavigationBar(onNavigateBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 40.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = PrimaryColor,
                modifier = Modifier.size(24.dp)
            )
        }

        Text(
            text = "About",
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = Slate900,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.size(40.dp))
    }
}

@Composable
private fun AppHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(96.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(PrimaryColor),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Σ",
                color = MaterialTheme.colorScheme.onPrimary,
                fontSize = 48.sp,
                fontFamily = FontFamily.Serif,
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Numerical Solver",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Slate900
        )

        Text(
            text = "Version 1.0.0",
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = Slate500,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

@Composable
private fun CourseInformationCard() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "COURSE INFORMATION",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = PrimaryColor,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PrimaryColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Course",
                        tint = PrimaryColor
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "CS-252",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Slate900
                    )
                    Text(
                        text = "Numerical Analysis",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Slate600
                    )
                    Text(
                        text = "Spring Semester 2026",
                        fontSize = 12.sp,
                        color = Slate400,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun FacultySection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Under the Supervision of",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Slate900,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                FacultyItem(
                    name = "Dr. Rania Ahmed",
                    role = "Project Supervisor",
                    showDivider = false
                )
            }
        }
    }
}

@Composable
private fun FacultyItem(name: String, role: String, showDivider: Boolean) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Slate100),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Slate400
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {
                Text(
                    text = name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = Slate900
                )
                Text(
                    text = role,
                    fontSize = 12.sp,
                    color = Slate500
                )
            }
        }

        if (showDivider) {
            HorizontalDivider(color = Slate100, thickness = 1.dp)
        }
    }
}

@Composable
private fun DevelopmentTeamSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Development Team",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = Slate900,
            modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
        )

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            TeamMemberItem(
                initials = "AA",
                name = "Anas Ayman El-Gebaili",
                role = "Android Developer",
                studentId = "111863",
                avatarColor = PrimaryColor.copy(alpha = 0.1f),
                textColor = PrimaryColor
            )

            TeamMemberItem(
                initials = "AK",
                name = "Adham Sayed Kamel",
                role = "Android Developer",
                studentId = "110835",
                avatarColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                textColor = MaterialTheme.colorScheme.secondary
            )

            TeamMemberItem(
                initials = "AA",
                name = "Ahmed Mohamed Alktatny",
                role = "Android Developer",
                studentId = "110970",
                avatarColor = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                textColor = MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
private fun TeamMemberItem(
    initials: String,
    name: String,
    role: String,
    studentId: String,
    avatarColor: Color,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(avatarColor),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = initials,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Slate900
                    )
                    Text(
                        text = role,
                        fontSize = 12.sp,
                        color = Slate500
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Slate50)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "ID: $studentId",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = FontFamily.Monospace,
                    color = Slate500
                )
            }
        }
    }
}

@Composable
private fun Footer() {
    Text(
        text = "© 2026 Numerical Solver Team.\nAll rights reserved.",
        fontSize = 12.sp,
        color = Slate400,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth()
    )
}

@Preview(showSystemUi = true, name = "About Screen Preview")
@Composable
fun AboutScreenPreview() {
    MaterialTheme {
        AboutScreen(onNavigateBack = {})
    }
}