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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Lecture
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.AvatarInitials
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.SectionHeader
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoLecturesScreen(
    repository: Teach2LearnRepository,
    onJoinLiveLecture: (String) -> Unit,
    onWatchRecordedLecture: (String) -> Unit,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val lectures by repository.lectures.collectAsState()
    val subjects by repository.subjects.collectAsState()

    var selectedFilter by remember { mutableStateOf("All") }
    var selectedSubjectId by remember { mutableStateOf("all") }
    var searchQuery by remember { mutableStateOf("") }
    var reminderLectureTarget by remember { mutableStateOf<Lecture?>(null) }
    var selectedLeadMinutes by remember { mutableStateOf(15) }
    var toastMessage by remember { mutableStateOf<String?>(null) }

    val filterTabs = listOf("All", "🔴 Live Sessions", "🎬 Recorded Masterclasses", "✅ Completed")

    val filteredLectures = lectures.filter { lec ->
        val matchesSubject = selectedSubjectId == "all" || lec.subjectId == selectedSubjectId
        val matchesFilter = when (selectedFilter) {
            "🔴 Live Sessions" -> lec.isLive
            "🎬 Recorded Masterclasses" -> !lec.isLive
            "✅ Completed" -> lec.isCompleted
            else -> true
        }
        val matchesQuery = searchQuery.isBlank() ||
                lec.title.contains(searchQuery, ignoreCase = true) ||
                lec.teacherName.contains(searchQuery, ignoreCase = true) ||
                lec.description.contains(searchQuery, ignoreCase = true)
        matchesSubject && matchesFilter && matchesQuery
    }

    // Early Notification Reminder Dialog for Live Lectures
    if (reminderLectureTarget != null) {
        val target = reminderLectureTarget!!
        AlertDialog(
            onDismissRequest = { reminderLectureTarget = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🔔 Early Lecture Notification", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Set an early alert for \"${target.title}\" with ${target.teacherName} (${target.scheduledTime}).",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Choose how early you want to be notified:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    val reminderOptions = listOf(5 to "5 mins before", 15 to "15 mins before (Recommended)", 30 to "30 mins before", 60 to "1 hour before")
                    reminderOptions.forEach { (mins, label) ->
                        val isSelected = selectedLeadMinutes == mins
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedLeadMinutes = mins }
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                                if (isSelected) {
                                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.setLiveLectureReminder(target.id, selectedLeadMinutes)
                        toastMessage = "🔔 Early reminder set! We'll notify you $selectedLeadMinutes mins before."
                        reminderLectureTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Set Early Reminder 🔔")
                }
            },
            dismissButton = {
                if (target.isReminderSet) {
                    TextButton(onClick = {
                        repository.cancelLiveLectureReminder(target.id)
                        toastMessage = "Reminder removed."
                        reminderLectureTarget = null
                    }) {
                        Text("Remove Alert", color = MaterialTheme.colorScheme.error)
                    }
                } else {
                    TextButton(onClick = { reminderLectureTarget = null }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Top Header
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
                Column {
                    Text(
                        text = "Video Lectures & Masterclasses 🎥",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Interactive peer lectures with chapter timestamps, code runners & early alerts",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Toast feedback
        toastMessage?.let { msg ->
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BrandEmerald.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandEmerald)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "✨ $msg", fontSize = 12.sp, color = BrandEmerald, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { toastMessage = null }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search lectures by topic, mentor, or concept...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = BrandPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("video_lectures_search"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        // Subject Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedSubjectId == "all",
                        onClick = { selectedSubjectId = "all" },
                        label = { Text("All Subjects") }
                    )
                }
                items(subjects) { subj ->
                    FilterChip(
                        selected = selectedSubjectId == subj.id,
                        onClick = { selectedSubjectId = subj.id },
                        label = { Text("${subj.icon} ${subj.name}") }
                    )
                }
            }
        }

        // Type Tabs
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterTabs) { tab ->
                    val isSelected = selectedFilter == tab
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.clickable { selectedFilter = tab }
                    ) {
                        Text(
                            text = tab,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Section Title
        item {
            SectionHeader(
                title = "Available Video Lectures (${filteredLectures.size})",
                subtitle = "Taught by top department rankers and verified peer educators"
            )
        }

        // Lecture Items
        items(filteredLectures) { lecture ->
            val subj = subjects.firstOrNull { it.id == lecture.subjectId }

            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (lecture.isLive) {
                        onJoinLiveLecture(lecture.id)
                    } else {
                        onWatchRecordedLecture(lecture.id)
                    }
                }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    // Top Info Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (lecture.isLive) BrandRose.copy(alpha = 0.15f) else MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = if (lecture.isLive) "🔴 LIVE NOW" else "🎬 RECORDED",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (lecture.isLive) BrandRose else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            if (subj != null) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                                ) {
                                    Text(
                                        text = subj.name,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }

                        Text(
                            text = "⏱️ ${lecture.durationMinutes} mins",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Title & Description
                    Text(
                        text = lecture.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = lecture.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )

                    // Chapter Topics Preview Pill
                    if (lecture.chapters.isNotEmpty()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Icon(Icons.Default.FormatListNumbered, contentDescription = null, tint = BrandPrimary, modifier = Modifier.size(14.dp))
                            Text(
                                text = "${lecture.chapters.size} Chapters • ${lecture.chapters.firstOrNull()?.title ?: ""}",
                                fontSize = 11.sp,
                                color = BrandPrimary
                            )
                        }
                    }

                    // Progress bar for recorded lectures
                    if (!lecture.isLive && lecture.progressPercent > 0) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Progress", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${lecture.progressPercent}%", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                            }
                            LinearProgressIndicator(
                                progress = { lecture.progressPercent / 100f },
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                color = BrandPrimary
                            )
                        }
                    }

                    // Bottom Row: Teacher + Action Button + Early Alert for Live
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            AvatarInitials(name = lecture.teacherName, size = 28.dp)
                            Column {
                                Text(
                                    text = lecture.teacherName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = if (lecture.isLive) "📅 ${lecture.scheduledTime}" else "👥 ${lecture.studentCount} watched",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Live Lecture Early Notification Button
                            if (lecture.isLive) {
                                OutlinedButton(
                                    onClick = {
                                        selectedLeadMinutes = lecture.reminderLeadMinutes
                                        reminderLectureTarget = lecture
                                    },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(
                                        containerColor = if (lecture.isReminderSet) BrandGold.copy(alpha = 0.15f) else Color.Transparent
                                    ),
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (lecture.isReminderSet) BrandGold else MaterialTheme.colorScheme.outline
                                    ),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = if (lecture.isReminderSet) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                                        contentDescription = "Early Notification",
                                        tint = if (lecture.isReminderSet) BrandGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (lecture.isReminderSet) "${lecture.reminderLeadMinutes}m Early 🔔" else "Remind Me",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (lecture.isReminderSet) BrandGold else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Button(
                                    onClick = { onJoinLiveLecture(lecture.id) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandRose),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text("Join Live", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            } else {
                                Button(
                                    onClick = { onWatchRecordedLecture(lecture.id) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (lecture.progressPercent > 0) "Resume" else "Watch",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
