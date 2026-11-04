package com.example.ui.screens

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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Lecture
import com.example.data.model.Subject
import com.example.data.model.SubjectNote
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.*
import com.example.ui.theme.*

enum class SubjectTab(val label: String, val icon: String) {
    OVERVIEW("Overview", "ℹ️"),
    LIVE_LECTURES("Live", "🔴"),
    RECORDED_LECTURES("Lectures", "🎬"),
    NOTES("Notes", "📝"),
    DOUBTS("Doubts", "💡"),
    QUIZZES("Quizzes", "⚡"),
    CHALLENGES("Challenges", "🏆"),
    STUDENT_TEACHERS("Teachers", "👨‍🏫")
}

@Composable
fun SubjectDetailScreen(
    subjectId: String,
    repository: Teach2LearnRepository,
    onBack: () -> Unit,
    onJoinLiveLecture: (String) -> Unit,
    onWatchRecordedLecture: (String) -> Unit,
    onStartQuiz: (String) -> Unit,
    onRaiseDoubt: (String) -> Unit,
    onOpenTeacherProfile: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val subjects by repository.subjects.collectAsState()
    val subject = subjects.firstOrNull { it.id == subjectId } ?: subjects.first()

    val lectures by repository.lectures.collectAsState()
    val subjectLectures = remember(lectures, subject.id) {
        lectures.filter { it.subjectId == subject.id || it.subjectId == "subj_c" }
    }
    val liveLectures = remember(subjectLectures) { subjectLectures.filter { it.isLive } }
    val recordedLectures = remember(subjectLectures) { subjectLectures.filter { !it.isLive } }

    val notes by repository.notes.collectAsState()
    val subjectNotes = remember(notes, subject.id) {
        notes.filter { it.subjectId == subject.id || it.subjectId == "subj_c" }
    }

    val doubts by repository.doubts.collectAsState()
    val subjectDoubts = remember(doubts, subject.name) {
        doubts.filter { it.subject.contains(subject.name, ignoreCase = true) || subject.name.contains(it.subject, ignoreCase = true) }
    }

    val quizzes by repository.quizzes.collectAsState()
    val subjectQuizzes = remember(quizzes, subject.id) {
        quizzes.filter { it.subjectId == subject.id || it.subjectId == "subj_c" }
    }

    val teachers by repository.studentTeachers.collectAsState()
    val subjectTeachers = remember(teachers, subject.name) {
        teachers.filter { teacher -> teacher.subjects.any { it.contains(subject.name, ignoreCase = true) || subject.name.contains(it, ignoreCase = true) } }
    }

    var selectedTab by remember { mutableStateOf(SubjectTab.OVERVIEW) }
    var toastFeedback by remember { mutableStateOf<String?>(null) }
    var activeReadingNote by remember { mutableStateOf<SubjectNote?>(null) }
    var reminderLectureTarget by remember { mutableStateOf<Lecture?>(null) }

    val clipboardManager = LocalClipboardManager.current

    // Note Reading Dialog
    if (activeReadingNote != null) {
        val note = activeReadingNote!!
        AlertDialog(
            onDismissRequest = { activeReadingNote = null },
            modifier = Modifier.fillMaxWidth(0.95f),
            title = {
                Column {
                    Surface(shape = RoundedCornerShape(6.dp), color = BrandPrimary.copy(alpha = 0.15f)) {
                        Text(text = note.category.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = note.title, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                    Text(text = "By ${note.author} • ${note.pagesCount} Pages", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            },
            text = {
                LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Surface(shape = RoundedCornerShape(10.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("📌 Overview & Highlights", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandPrimary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(note.contentPreview, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    if (note.keyFormulas.isNotEmpty()) {
                        item {
                            Text("📐 Key High-Yield Formulas (Tap to Copy):", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        items(note.keyFormulas) { f ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F172A),
                                modifier = Modifier.fillMaxWidth().clickable {
                                    clipboardManager.setText(AnnotatedString(f))
                                    toastFeedback = "Formula copied to clipboard!"
                                }
                            ) {
                                Text(text = f, fontFamily = FontFamily.Monospace, fontSize = 11.sp, color = Color(0xFF38BDF8), modifier = Modifier.padding(8.dp))
                            }
                        }
                    }

                    if (note.fullContent.isNotBlank()) {
                        item {
                            Text("📖 Full Content:", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surface, border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)), modifier = Modifier.fillMaxWidth()) {
                                Text(note.fullContent, fontSize = 12.sp, lineHeight = 17.sp, modifier = Modifier.padding(10.dp))
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.downloadNote(note.id)
                        toastFeedback = "Downloaded '${note.title}' for offline reading (+5 XP)"
                        activeReadingNote = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Download PDF")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeReadingNote = null }) { Text("Close") }
            }
        )
    }

    // Early Alert Dialog for Live Lecture
    if (reminderLectureTarget != null) {
        val target = reminderLectureTarget!!
        AlertDialog(
            onDismissRequest = { reminderLectureTarget = null },
            title = { Text("🔔 Early Live Lecture Alert") },
            text = {
                Text("Set an early notification for '${target.title}' with ${target.teacherName} scheduled at ${target.scheduledTime}.")
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.setLiveLectureReminder(target.id, 15)
                        toastFeedback = "Early reminder set for 15 mins before ${target.scheduledTime}!"
                        reminderLectureTarget = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text("Set 15m Early Alert 🔔")
                }
            },
            dismissButton = {
                TextButton(onClick = { reminderLectureTarget = null }) { Text("Cancel") }
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
        // Navigation Header
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${subject.educationLevel} → ${subject.department} → ${subject.code}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = subject.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
                Surface(
                    shape = CircleShape,
                    color = BrandPrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${subject.progressPercent}% Done",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        // Hero Subject Card
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(BrandPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = subject.icon, fontSize = 26.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = subject.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = subject.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                ProgressBarWithLabel(
                    title = "Course Completion",
                    progressPercent = subject.progressPercent,
                    color = BrandPrimary
                )

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = {
                            if (recordedLectures.isNotEmpty()) {
                                onWatchRecordedLecture(recordedLectures.first().id)
                            } else {
                                selectedTab = SubjectTab.RECORDED_LECTURES
                            }
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Continue Learning ▶", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    OutlinedButton(
                        onClick = { onRaiseDoubt(subject.name) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Ask a Doubt 💡", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }

        // Download toast feedback
        toastFeedback?.let { msg ->
            item {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = BrandEmerald.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandEmerald)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "✨ $msg", fontSize = 12.sp, color = BrandEmerald, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { toastFeedback = null }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Section Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                SubjectTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = "${tab.icon} ${tab.label}",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
        }

        // TAB CONTENTS
        when (selectedTab) {
            SubjectTab.OVERVIEW -> {
                item {
                    Text(text = "Syllabus Topics", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        subject.topics.forEachIndexed { index, topic ->
                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp)),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(BrandPrimary.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = "${index + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                                    }
                                    Text(text = topic, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                                    Text(text = "Completed", fontSize = 11.sp, color = BrandEmerald, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }

            SubjectTab.LIVE_LECTURES -> {
                if (liveLectures.isEmpty()) {
                    item {
                        EmptyStateView(
                            icon = "🔴",
                            title = "No Live Lectures Now",
                            description = "Next scheduled live session begins at 7:00 PM today.",
                            actionText = "Set Next Lecture Reminder",
                            onAction = { toastFeedback = "Reminder scheduled for 7:00 PM live session!" }
                        )
                    }
                } else {
                    items(liveLectures) { lec ->
                        GlassmorphicCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { onJoinLiveLecture(lec.id) }
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(text = "🔴 LIVE NOW", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandRose)
                                        Text(text = "• ${lec.studentCount} students", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = lec.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "Teacher: ${lec.teacherName} • ${lec.scheduledTime}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                }

                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(onClick = { reminderLectureTarget = lec }) {
                                        Icon(
                                            imageVector = if (lec.isReminderSet) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                                            contentDescription = "Alert",
                                            tint = if (lec.isReminderSet) BrandGold else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }

                                    Button(
                                        onClick = { onJoinLiveLecture(lec.id) },
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandRose),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("JOIN", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            SubjectTab.RECORDED_LECTURES -> {
                items(recordedLectures) { lec ->
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onWatchRecordedLecture(lec.id) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(BrandPrimary.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(text = "▶️", fontSize = 20.sp)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = lec.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = "Teacher: ${lec.teacherName} • ${lec.durationMinutes} mins • ${lec.chapters.size} Chapters", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(modifier = Modifier.height(4.dp))
                                ProgressBarWithLabel(title = "Progress", progressPercent = lec.progressPercent, color = BrandPrimary)
                            }
                        }
                    }
                }
            }

            SubjectTab.NOTES -> {
                items(subjectNotes) { note ->
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { activeReadingNote = note }
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(shape = RoundedCornerShape(6.dp), color = BrandPrimary.copy(alpha = 0.12f)) {
                                    Text(text = note.category, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandPrimary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                                IconButton(
                                    onClick = { repository.toggleBookmarkNote(note.id) },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = if (note.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Bookmark",
                                        tint = if (note.isBookmarked) BrandGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Text(text = note.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "By ${note.author} • ${note.pagesCount} pages • ${note.downloads} downloads", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = note.contentPreview, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { activeReadingNote = note }) {
                                    Text("Read Online", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Button(
                                    onClick = {
                                        repository.downloadNote(note.id)
                                        toastFeedback = "Downloaded '${note.title}' to storage (+5 XP)"
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            SubjectTab.DOUBTS -> {
                item {
                    Button(
                        onClick = { onRaiseDoubt(subject.name) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Post a Doubt in ${subject.name} 💡")
                    }
                }
                items(subjectDoubts) { doubt ->
                    DoubtPreviewCard(doubt = doubt, onClick = { onRaiseDoubt(subject.name) })
                }
            }

            SubjectTab.QUIZZES -> {
                items(subjectQuizzes) { quiz ->
                    val isHard = quiz.difficulty.contains("Hard", ignoreCase = true)
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onStartQuiz(quiz.id) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Surface(shape = RoundedCornerShape(6.dp), color = if (isHard) BrandRose.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer) {
                                        Text(text = quiz.difficulty.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isHard) BrandRose else MaterialTheme.colorScheme.primary, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                    }
                                    Text(text = "⏱️ ${quiz.timeLimitSeconds}s", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = quiz.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "${quiz.questions.size} Questions • ${quiz.topic}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Button(
                                onClick = { onStartQuiz(quiz.id) },
                                colors = ButtonDefaults.buttonColors(containerColor = if (isHard) BrandRose else BrandEmerald),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Text("Start (+${quiz.xpReward} XP)", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }

            SubjectTab.CHALLENGES -> {
                item {
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Subject Sprint: ${subject.name}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Solve curated memory trap and code debugging puzzles to earn bonus XP.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { onStartQuiz("quiz_c_extrahard") },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandRose)
                        ) {
                            Text("Launch Extra Hard Sprint 🔥 (+60 XP)", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            SubjectTab.STUDENT_TEACHERS -> {
                items(subjectTeachers) { teacher ->
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onOpenTeacherProfile(teacher.id) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AvatarInitials(name = teacher.name, size = 44.dp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = teacher.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(text = "${teacher.college} • ⭐ ${teacher.rating}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "Available: ${teacher.availableDays} (${teacher.availableTime})", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                            }
                            Button(
                                onClick = { onOpenTeacherProfile(teacher.id) },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Request 1-on-1", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
