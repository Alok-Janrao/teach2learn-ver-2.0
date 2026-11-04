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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RankTier
import com.example.data.model.RequestStatus
import com.example.data.model.StudentTeacher
import com.example.data.model.TeachingRequest
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.*
import com.example.ui.theme.*

enum class TeachScreenTab(val title: String) {
    MY_TEACHER_PROFILE("My Teaching Hub"),
    SESSION_REQUESTS("Requests"),
    BROWSE_MENTORS("All Peer Teachers"),
    LIVE_SESSION_ROOM("Active 1-on-1 Room")
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TeachScreen(
    repository: Teach2LearnRepository,
    onNavigateToSubject: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by repository.currentUser.collectAsState()
    val studentTeachers by repository.studentTeachers.collectAsState()
    val teachingRequests by repository.teachingRequests.collectAsState()
    val teachingSessions by repository.teachingSessions.collectAsState()

    var selectedTab by remember { mutableStateOf(TeachScreenTab.MY_TEACHER_PROFILE) }

    // Teacher setup form state
    var isEditingSetup by remember { mutableStateOf(false) }
    var availableDays by remember { mutableStateOf(currentUser.teacherAvailableDays) }
    var availableTime by remember { mutableStateOf(currentUser.teacherAvailableTime) }
    var teachingBio by remember { mutableStateOf(currentUser.bio) }
    var selectedTeachSubjects by remember { mutableStateOf(currentUser.teachSubjects.toSet()) }

    // Session completion dialog state
    var showRateDialog by remember { mutableStateOf(false) }
    var ratingVal by remember { mutableStateOf(5.0f) }
    var feedbackVal by remember { mutableStateOf("Excellent peer explanation! Solved memory bugs instantly.") }
    var sessionSuccessToast by remember { mutableStateOf<String?>(null) }

    // Request session dialog state
    var showRequestModalForTeacher by remember { mutableStateOf<StudentTeacher?>(null) }
    var requestSubject by remember { mutableStateOf("C Programming") }
    var requestTopic by remember { mutableStateOf("Pointer Arithmetic & Arrays") }
    var requestPreferredTime by remember { mutableStateOf("Tomorrow, 7:00 PM") }
    var requestNote by remember { mutableStateOf("Need help understanding 2D pointer dereferencing for lab exam.") }

    if (showRequestModalForTeacher != null) {
        val teacher = showRequestModalForTeacher!!
        AlertDialog(
            onDismissRequest = { showRequestModalForTeacher = null },
            title = { Text("Request Session with ${teacher.name}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Select Subject:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = requestSubject,
                        onValueChange = { requestSubject = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Text("Specific Topic / Doubt:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = requestTopic,
                        onValueChange = { requestTopic = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Text("Preferred Date & Time:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = requestPreferredTime,
                        onValueChange = { requestPreferredTime = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Text("Message for Mentor:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = requestNote,
                        onValueChange = { requestNote = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    repository.requestTeachingSession(
                        teacherId = teacher.id,
                        teacherName = teacher.name,
                        subject = requestSubject,
                        topic = requestTopic,
                        preferredTime = requestPreferredTime,
                        note = requestNote
                    )
                    showRequestModalForTeacher = null
                    sessionSuccessToast = "Teaching request sent to ${teacher.name}!"
                }) {
                    Text("Submit Request")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRequestModalForTeacher = null }) { Text("Cancel") }
            }
        )
    }

    if (showRateDialog) {
        AlertDialog(
            onDismissRequest = { showRateDialog = false },
            title = { Text("Complete Session & Rate Mentor") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("How was your peer learning experience with Rahul Sharma?", fontSize = 13.sp)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        listOf(1, 2, 3, 4, 5).forEach { star ->
                            IconButton(onClick = { ratingVal = star.toFloat() }) {
                                Text(text = if (star <= ratingVal) "⭐" else "☆", fontSize = 24.sp)
                            }
                        }
                    }

                    OutlinedTextField(
                        value = feedbackVal,
                        onValueChange = { feedbackVal = it },
                        label = { Text("Student Feedback") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    repository.completeTeachingSession("sess_current", ratingVal, feedbackVal)
                    showRateDialog = false
                    sessionSuccessToast = "Peer session completed! +25 XP awarded to mentor & student."
                }) {
                    Text("Finish & Award XP (+25 XP)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showRateDialog = false }) { Text("Cancel") }
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
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "Teach & Mentor",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "\"Don't just learn. Teach what you know.\" Help peers, earn XP, and rank up to Grand Mentor.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Navigation Tabs
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp)
                ) {
                    TeachScreenTab.entries.forEach { tab ->
                        val isSelected = selectedTab == tab
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable { selectedTab = tab },
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent
                        ) {
                            Text(
                                text = tab.title,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        sessionSuccessToast?.let { msg ->
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
                        Text(text = "🎉 $msg", fontSize = 12.sp, color = BrandEmerald, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { sessionSuccessToast = null }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        when (selectedTab) {
            TeachScreenTab.MY_TEACHER_PROFILE -> {
                item {
                    // Mentor Card Summary
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            AvatarInitials(name = currentUser.name, size = 52.dp, backgroundColor = BrandSecondary)
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = currentUser.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                Text(text = "Subjects: ${currentUser.teachSubjects.joinToString(", ")}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(text = "⭐ ${currentUser.rating}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandGold)
                                    Text(text = "• ${currentUser.studentsHelped} Students Helped", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Availability", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = currentUser.teacherAvailableDays, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Column {
                                Text(text = "Time Slot", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = currentUser.teacherAvailableTime, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            }
                            Column {
                                Text(text = "Mode", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = currentUser.teachingMode, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = BrandEmerald)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { isEditingSetup = !isEditingSetup },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Text(if (isEditingSetup) "Close Settings" else "Edit Teaching Slots ⚙️", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { selectedTab = TeachScreenTab.LIVE_SESSION_ROOM },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald)
                            ) {
                                Text("Open Workspace 💻", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                if (isEditingSetup) {
                    item {
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Text(text = "What can you teach? (Configure profile)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.height(10.dp))

                            val options = listOf("C Programming", "Python", "Data Structures", "Java", "Mathematics", "Engineering Mechanics")
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                options.forEach { opt ->
                                    val isSelected = selectedTeachSubjects.contains(opt)
                                    FilterChip(
                                        selected = isSelected,
                                        onClick = {
                                            selectedTeachSubjects = if (isSelected) selectedTeachSubjects - opt else selectedTeachSubjects + opt
                                        },
                                        label = { Text(opt, fontSize = 12.sp) }
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = availableDays,
                                onValueChange = { availableDays = it },
                                label = { Text("Available Days") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = availableTime,
                                onValueChange = { availableTime = it },
                                label = { Text("Available Time Slots") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = teachingBio,
                                onValueChange = { teachingBio = it },
                                label = { Text("Teaching Bio & Specialization") },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                minLines = 2
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    repository.updateTeacherProfile(
                                        subjects = selectedTeachSubjects.toList(),
                                        availableDays = availableDays,
                                        availableTime = availableTime,
                                        teachingMode = "Online Live",
                                        bio = teachingBio
                                    )
                                    isEditingSetup = false
                                    sessionSuccessToast = "Teaching profile updated successfully!"
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Save & Publish Teacher Slots", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Peer Reviews on You
                item {
                    SectionHeader(title = "Student Peer Reviews (${currentUser.reviews.size})")
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        currentUser.reviews.forEach { rev ->
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        AvatarInitials(name = rev.studentName, size = 32.dp)
                                        Column {
                                            Text(text = rev.studentName, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text(text = "${rev.subject} • ${rev.date}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                    }
                                    Text(text = "⭐ ${rev.rating}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandGold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(text = "\"${rev.comment}\"", fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                            }
                        }
                    }
                }
            }

            TeachScreenTab.SESSION_REQUESTS -> {
                if (teachingRequests.isEmpty()) {
                    item {
                        EmptyStateView(
                            icon = "📬",
                            title = "No Pending Requests",
                            description = "When students need help in your subjects, requests will show up here."
                        )
                    }
                } else {
                    items(teachingRequests) { req ->
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Text(text = "From: ${req.studentName}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "${req.subject} • ${req.topic}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                    Text(text = "Time: ${req.requestedTime}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (req.status == RequestStatus.ACCEPTED) BrandEmerald.copy(alpha = 0.15f) else BrandGold.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = req.status.name,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (req.status == RequestStatus.ACCEPTED) BrandEmerald else BrandGold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(text = "\"${req.message}\"", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            if (req.status == RequestStatus.PENDING) {
                                Spacer(modifier = Modifier.height(10.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = { repository.acceptTeachingRequest(req.id) },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald)
                                    ) {
                                        Text("Accept Session", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                    OutlinedButton(
                                        onClick = { /* Suggest new time */ },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Suggest Time", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            TeachScreenTab.BROWSE_MENTORS -> {
                item {
                    Text(text = "Connect with Certified Student Mentors", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                items(studentTeachers) { teacher ->
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { showRequestModalForTeacher = teacher }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AvatarInitials(name = teacher.name, size = 48.dp)
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(text = teacher.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    RankBadge(rank = teacher.rank)
                                }
                                Text(text = "${teacher.college} • ${teacher.department}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "Teaches: ${teacher.subjects.joinToString(", ")}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                                Text(text = "⭐ ${teacher.rating} • ${teacher.studentsHelped} helped • ${teacher.sessionsCompleted} sessions", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = teacher.bio, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { showRequestModalForTeacher = teacher },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Request Teaching Session 🎓", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            TeachScreenTab.LIVE_SESSION_ROOM -> {
                // Interactive 1-on-1 Workspace
                item {
                    val activeSession = teachingSessions.firstOrNull()
                    activeSession?.let { sess ->
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(BrandEmerald))
                                        Text(text = "ACTIVE 1-ON-1 SESSION", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandEmerald)
                                    }
                                    Text(text = "${sess.subject} • ${sess.topic}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    Text(text = "Teacher: ${sess.teacherName} ↔ Student: ${sess.studentName}", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }

                                Button(
                                    onClick = { showRateDialog = true },
                                    colors = ButtonDefaults.buttonColors(containerColor = BrandRose),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Complete Session (+25 XP)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Interactive Collaborative Code Whiteboard
                            Text(text = "Shared Code Whiteboard 🖥️", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(Color(0xFF0F172A))
                                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = sess.whiteboardContent,
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    color = Color(0xFF34D399),
                                    lineHeight = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Shared Peer Notes
                            Text(text = "Shared Notes & Key Insights 📝", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = sess.sharedNotes,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier.padding(10.dp)
                                )
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
