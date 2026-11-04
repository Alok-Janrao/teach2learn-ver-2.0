package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NotificationItem
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.AvatarInitials
import com.example.ui.components.EmptyStateView
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

// ==========================================
// 1. SEARCH SCREEN
// ==========================================
@Composable
fun SearchScreen(
    repository: Teach2LearnRepository,
    onNavigateToSubject: (String) -> Unit,
    onNavigateToTeacher: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("ALL") }

    val subjects by repository.subjects.collectAsState()
    val teachers by repository.studentTeachers.collectAsState()
    val doubts by repository.doubts.collectAsState()

    val filteredSubjects = remember(query, selectedCategory, subjects) {
        if (selectedCategory != "ALL" && selectedCategory != "SUBJECTS") emptyList()
        else subjects.filter { it.name.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true) }
    }

    val filteredTeachers = remember(query, selectedCategory, teachers) {
        if (selectedCategory != "ALL" && selectedCategory != "TEACHERS") emptyList()
        else teachers.filter { it.name.contains(query, ignoreCase = true) || it.subjects.any { s -> s.contains(query, ignoreCase = true) } }
    }

    val filteredDoubts = remember(query, selectedCategory, doubts) {
        if (selectedCategory != "ALL" && selectedCategory != "DOUBTS") emptyList()
        else doubts.filter { it.question.contains(query, ignoreCase = true) || it.subject.contains(query, ignoreCase = true) }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Search Everything 🔍",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search subjects, student mentors, doubts, lectures...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = if (query.isNotEmpty()) {
                    { IconButton(onClick = { query = "" }) { Icon(Icons.Default.Clear, contentDescription = null) } }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        // Category Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                val categories = listOf("ALL" to "All", "SUBJECTS" to "📚 Subjects", "TEACHERS" to "👨‍🏫 Student Mentors", "DOUBTS" to "💡 Doubts")
                items(categories) { (key, label) ->
                    FilterChip(
                        selected = selectedCategory == key,
                        onClick = { selectedCategory = key },
                        label = { Text(label, fontSize = 12.sp) }
                    )
                }
            }
        }

        if (filteredSubjects.isNotEmpty()) {
            item { SectionHeader(title = "Subjects (${filteredSubjects.size})") }
            items(filteredSubjects) { subj ->
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onNavigateToSubject(subj.id) }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(text = subj.icon, fontSize = 24.sp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = subj.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "${subj.department} • ${subj.totalLectures} Lectures", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        }

        if (filteredTeachers.isNotEmpty()) {
            item { SectionHeader(title = "Student Mentors (${filteredTeachers.size})") }
            items(filteredTeachers) { teacher ->
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onNavigateToTeacher(teacher.id) }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        AvatarInitials(name = teacher.name, size = 36.dp)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = teacher.name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "Teaches: ${teacher.subjects.joinToString(", ")}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        Text(text = "⭐ ${teacher.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandGold)
                    }
                }
            }
        }

        if (filteredDoubts.isNotEmpty()) {
            item { SectionHeader(title = "Community Doubts (${filteredDoubts.size})") }
            items(filteredDoubts) { doubt ->
                GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                    Text(text = "${doubt.subject} • ${doubt.topic}", fontSize = 10.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(text = doubt.question, fontSize = 12.sp, fontWeight = FontWeight.Medium, maxLines = 2)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

// ==========================================
// 2. NOTIFICATIONS SCREEN
// ==========================================
@Composable
fun NotificationsScreen(
    repository: Teach2LearnRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val notifications by repository.notifications.collectAsState()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                    Text(text = "Notifications 🔔", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }

                TextButton(onClick = { repository.markAllNotificationsRead() }) {
                    Text("Mark All Read", fontSize = 12.sp)
                }
            }
        }

        if (notifications.isEmpty()) {
            item {
                EmptyStateView(
                    icon = "🔕",
                    title = "No Notifications",
                    description = "You're all caught up! Updates about teaching requests and XP will appear here."
                )
            }
        } else {
            items(notifications) { notif ->
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(
                            1.dp,
                            if (!notif.isRead) BrandPrimary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            RoundedCornerShape(14.dp)
                        ),
                    color = if (!notif.isRead) BrandPrimary.copy(alpha = 0.06f) else MaterialTheme.colorScheme.surface
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = notif.icon, fontSize = 24.sp)
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(text = notif.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = notif.timestamp, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = notif.message, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

// ==========================================
// 3. PREMIUM SCREEN
// ==========================================
@Composable
fun PremiumScreen(
    repository: Teach2LearnRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var upgraded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
                Text(text = "Teach2Learn Pro 👑", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black, color = BrandGold)
            }
        }

        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Supercharge Your Peer Teaching & Learning", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(8.dp))
                val perks = listOf(
                    "⭐ Verified Peer Mentor Badge on Profile",
                    "🚀 2x XP Multiplier on all teaching sessions and doubt answers",
                    "📝 Unlimited offline note downloads & university answer keys",
                    "🎥 1080p recording archive of all live lecture sessions",
                    "📜 Free Verified Physical & Digital Peer Certificate"
                )
                perks.forEach { p ->
                    Text(text = p, fontSize = 12.sp, modifier = Modifier.padding(vertical = 3.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        repository.activatePremium()
                        upgraded = true
                    },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandGold)
                ) {
                    Text(if (upgraded) "Pro Plan Active! 🎉" else "Unlock Pro Plan (Free Campus Beta)", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }

        item { Spacer(modifier = Modifier.height(30.dp)) }
    }
}

// ==========================================
// 4. ADMIN CONSOLE SCREEN
// ==========================================
@Composable
fun AdminScreen(
    repository: Teach2LearnRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    ControlCenterScreen(
        repository = repository,
        onBack = onBack,
        modifier = modifier
    )
}
