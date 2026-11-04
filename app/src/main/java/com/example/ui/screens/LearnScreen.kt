package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Subject
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun LearnScreen(
    repository: Teach2LearnRepository,
    onSelectSubject: (String) -> Unit,
    onSelectTeacher: (String) -> Unit,
    onNavigateToNotes: () -> Unit = {},
    onNavigateToVideoLectures: () -> Unit = {},
    onNavigateToShortVideos: () -> Unit = {},
    onNavigateToProgress: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val educationLevels by repository.educationLevels.collectAsState()
    val departments by repository.departments.collectAsState()
    val subjects by repository.subjects.collectAsState()

    var selectedLevelId by remember { mutableStateOf("btech") }
    var selectedDepartmentCode by remember { mutableStateOf("CSE") }
    var searchQuery by remember { mutableStateOf("") }

    val filteredSubjects = remember(selectedDepartmentCode, searchQuery, subjects) {
        subjects.filter { subj ->
            (selectedDepartmentCode == "ALL" || subj.department.contains(selectedDepartmentCode, ignoreCase = true) || selectedDepartmentCode == "Other") &&
                    (searchQuery.isBlank() || subj.name.contains(searchQuery, ignoreCase = true) || subj.description.contains(searchQuery, ignoreCase = true))
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Learn & Master 📚",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Select your syllabus, explore notes, video lectures & short videos",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Quick Learning Hub Banners (Notes, Video Lectures, Reels)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = BrandPrimary.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1f).clickable { onNavigateToNotes() }
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📝", fontSize = 22.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Notes & PDFs", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = BrandSecondary.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandSecondary.copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1f).clickable { onNavigateToVideoLectures() }
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🎥", fontSize = 22.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Video Lectures", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandSecondary)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = BrandRose.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandRose.copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1f).clickable { onNavigateToShortVideos() }
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("⚡", fontSize = 22.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("60s Reels", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandRose)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = BrandGold.copy(alpha = 0.1f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandGold.copy(alpha = 0.3f)),
                    modifier = Modifier.weight(1f).clickable { onNavigateToProgress() }
                ) {
                    Column(modifier = Modifier.padding(10.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📊", fontSize = 22.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("My Progress", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandGold)
                    }
                }
            }
        }

        // Search Field
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search subjects (e.g. C, Python, DSA)...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    { IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Clear, contentDescription = null) } }
                } else null,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("learn_search_input"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        // 1. Education Level Selection
        item {
            SectionHeader(title = "1. Education Level", subtitle = "Choose your academic track")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(educationLevels) { level ->
                    val isSelected = selectedLevelId == level.id
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(14.dp))
                            .border(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(14.dp)
                            )
                            .clickable { selectedLevelId = level.id },
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = level.icon, fontSize = 16.sp)
                            Text(
                                text = level.title,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 13.sp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                }
            }
        }

        // 2. Department Selection (When B.Tech / M.Tech is selected)
        if (selectedLevelId in listOf("btech", "mtech", "diploma")) {
            item {
                SectionHeader(title = "2. Department / Branch", subtitle = "Filter by engineering discipline")
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedDepartmentCode == "ALL",
                            onClick = { selectedDepartmentCode = "ALL" },
                            label = { Text("All Branches") }
                        )
                    }
                    items(departments) { dept ->
                        FilterChip(
                            selected = selectedDepartmentCode == dept.code,
                            onClick = { selectedDepartmentCode = dept.code },
                            label = { Text("${dept.icon} ${dept.code}") }
                        )
                    }
                }
            }
        }

        // 3. Subjects List
        item {
            SectionHeader(
                title = "3. Available Subjects (${filteredSubjects.size})",
                subtitle = "Tap any subject to view lectures, notes, quizzes & teachers"
            )
        }

        if (filteredSubjects.isEmpty()) {
            item {
                EmptyStateView(
                    icon = "🔍",
                    title = "No subjects found",
                    description = "Try searching for a different keyword or choose another department filter."
                )
            }
        } else {
            items(filteredSubjects) { subj ->
                SubjectCardItem(
                    subject = subj,
                    onClick = { onSelectSubject(subj.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun SubjectCardItem(
    subject: Subject,
    onClick: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("subject_card_${subject.id}"),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(BrandPrimary.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = subject.icon, fontSize = 26.sp)
            }

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = subject.code,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subject.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Mini progress and stats
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "📚 ${subject.totalLectures} Lectures",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "💡 ${subject.totalDoubts} Doubts",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "👨‍🏫 ${subject.totalTeachers} Teachers",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
                ProgressBarWithLabel(
                    title = "Your Learning Progress",
                    progressPercent = subject.progressPercent,
                    color = BrandPrimary
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open Subject",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
