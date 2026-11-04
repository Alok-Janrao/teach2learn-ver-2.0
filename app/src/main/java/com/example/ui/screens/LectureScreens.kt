package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.AvatarInitials
import com.example.ui.components.GlassmorphicCard
import com.example.ui.theme.*

data class ChatMessage(val sender: String, val text: String, val time: String, val isTeacher: Boolean = false)

@Composable
fun LiveLectureScreen(
    lectureId: String,
    repository: Teach2LearnRepository,
    onBack: () -> Unit,
    onNavigateToQuiz: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val lectures by repository.lectures.collectAsState()
    val lecture = lectures.firstOrNull { it.id == lectureId } ?: lectures.first()
    val currentUser by repository.currentUser.collectAsState()

    var isHandRaised by remember { mutableStateOf(false) }
    var showDoubtDialog by remember { mutableStateOf(false) }
    var doubtText by remember { mutableStateOf("") }
    var showParticipants by remember { mutableStateOf(false) }
    var showNotesPanel by remember { mutableStateOf(false) }
    var isMuted by remember { mutableStateOf(true) }
    var isVideoOn by remember { mutableStateOf(false) }
    var isScreenShare by remember { mutableStateOf(true) }

    var chatInput by remember { mutableStateOf("") }
    var chatMessages by remember {
        mutableStateOf(
            listOf(
                ChatMessage("Rahul Sharma", "Welcome everyone! Today we are dissecting Pointer Arithmetic & Memory Allocation.", "7:01 PM", isTeacher = true),
                ChatMessage("Priya Patel", "Hello Rahul! Excited for this session.", "7:02 PM"),
                ChatMessage("Aman Verma", "Can you explain why sizeof(int*) is 8 bytes on 64-bit architecture?", "7:04 PM")
            )
        )
    }

    var lectureFinishedCelebration by remember { mutableStateOf(false) }

    if (showDoubtDialog) {
        AlertDialog(
            onDismissRequest = { showDoubtDialog = false },
            title = { Text("Ask Live Doubt to ${lecture.teacherName}") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Your question will be highlighted in the teacher's priority queue.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(
                        value = doubtText,
                        onValueChange = { doubtText = it },
                        placeholder = { Text("Type your live doubt here...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        minLines = 3
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (doubtText.isNotBlank()) {
                        chatMessages = chatMessages + ChatMessage(currentUser.name, "❓ DOUBT: $doubtText", "Now")
                        doubtText = ""
                        showDoubtDialog = false
                    }
                }) {
                    Text("Send Doubt")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDoubtDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showParticipants) {
        AlertDialog(
            onDismissRequest = { showParticipants = false },
            title = { Text("Active Participants (${lecture.studentCount + 1})") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val participants = listOf(
                        "${lecture.teacherName} (Student Mentor 🌟)",
                        "${currentUser.name} (You)",
                        "Priya Patel",
                        "Sneha Rao",
                        "Aman Verma",
                        "Rohit Deshmukh",
                        "Ananya Sen"
                    )
                    participants.forEach { p ->
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(BrandEmerald))
                            Text(text = p, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showParticipants = false }) { Text("Close") }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Live Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF0F172A))
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Exit", tint = Color.White)
                }
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(BrandRose))
                        Text(text = "LIVE", fontSize = 12.sp, fontWeight = FontWeight.Black, color = BrandRose)
                        Text(text = "• ${lecture.studentCount} Students", fontSize = 11.sp, color = Color.White.copy(alpha = 0.7f))
                    }
                    Text(
                        text = lecture.title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1
                    )
                }
            }

            Button(
                onClick = {
                    repository.completeLecture(lecture.id)
                    lectureFinishedCelebration = true
                },
                colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text("Finish (+10 XP)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        // Main Simulated Video Stream / Code Workspace Screen
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(210.dp)
                .background(Color(0xFF0B0F19))
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFF1E293B))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(text = "🖥️ Screen Share: ${lecture.teacherName}", fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }
                    Surface(shape = RoundedCornerShape(4.dp), color = Color(0xFF0F172A)) {
                        Text(text = "C99 Compiler", fontSize = 10.sp, color = BrandTertiary, modifier = Modifier.padding(4.dp))
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Simulated Interactive Code Screen
                Text(
                    text = "#include <stdio.h>\n#include <stdlib.h>\n\nint main() {\n    int *ptr = malloc(sizeof(int) * 5);\n    for(int i=0; i<5; i++) *(ptr + i) = (i+1) * 10;\n    printf(\"ptr[2] = %d\\n\", *(ptr + 2));\n    free(ptr);\n    return 0;\n}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    color = Color(0xFF38BDF8),
                    lineHeight = 15.sp
                )
            }

            // Pip Teacher Camera Floating Bubble
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .size(width = 85.dp, height = 65.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .border(1.dp, BrandPrimary, RoundedCornerShape(10.dp)),
                color = Color(0xFF0F172A)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "👨‍🏫", fontSize = 22.sp)
                    Text(text = lecture.teacherName.split(" ").firstOrNull() ?: "", fontSize = 9.sp, color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Live Controls Action Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { isMuted = !isMuted }) {
                Icon(if (isMuted) Icons.Default.MicOff else Icons.Default.Mic, contentDescription = "Mic", tint = if (isMuted) Color.Gray else BrandEmerald)
            }
            IconButton(onClick = { isHandRaised = !isHandRaised }) {
                Icon(Icons.Default.PanTool, contentDescription = "Raise Hand", tint = if (isHandRaised) BrandGold else Color.Gray)
            }
            IconButton(onClick = { showDoubtDialog = true }) {
                Icon(Icons.Default.LiveHelp, contentDescription = "Ask Doubt", tint = BrandPrimary)
            }
            IconButton(onClick = { showNotesPanel = !showNotesPanel }) {
                Icon(Icons.Default.NoteAlt, contentDescription = "Notes", tint = if (showNotesPanel) BrandPrimary else Color.Gray)
            }
            IconButton(onClick = { showParticipants = true }) {
                Icon(Icons.Default.Group, contentDescription = "Participants", tint = MaterialTheme.colorScheme.onSurface)
            }
        }

        if (lectureFinishedCelebration) {
            Surface(
                color = BrandEmerald.copy(alpha = 0.15f),
                border = androidx.compose.foundation.BorderStroke(1.dp, BrandEmerald),
                modifier = Modifier.fillMaxWidth().padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("🎉 Lecture completed! +10 XP awarded.", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandEmerald)
                    TextButton(onClick = { onNavigateToQuiz("quiz_c_1") }) {
                        Text("Take Quiz (+20 XP) →", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Live Chat Messages
        Text(
            text = "Peer Live Discussion",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(chatMessages) { msg ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (msg.isTeacher) BrandPrimary.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = if (msg.isTeacher) "🌟 ${msg.sender} (Mentor)" else msg.sender,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (msg.isTeacher) BrandPrimary else MaterialTheme.colorScheme.onBackground
                            )
                            Text(text = msg.time, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = msg.text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        }

        // Chat Input Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = chatInput,
                onValueChange = { chatInput = it },
                placeholder = { Text("Share thoughts or ask a peer...", fontSize = 12.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(20.dp),
                singleLine = true
            )
            IconButton(
                onClick = {
                    if (chatInput.isNotBlank()) {
                        chatMessages = chatMessages + ChatMessage(currentUser.name, chatInput, "Now")
                        chatInput = ""
                    }
                }
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun RecordedLectureScreen(
    lectureId: String,
    repository: Teach2LearnRepository,
    onBack: () -> Unit,
    onTakeQuiz: (String) -> Unit,
    onAskDoubt: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val lectures by repository.lectures.collectAsState()
    val lecture = lectures.firstOrNull { it.id == lectureId } ?: lectures.first()

    var isPlaying by remember { mutableStateOf(false) }
    var currentProgress by remember { mutableStateOf(lecture.progressPercent) }
    var playbackSpeed by remember { mutableStateOf("1.0x") }
    var completedToast by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
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
                    Text(text = "Recorded Lecture", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(text = lecture.title, fontWeight = FontWeight.Bold, fontSize = 14.sp, maxLines = 1)
                }
            }
        }

        // Simulated Video Player
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0F172A))
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(14.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "HD 1080p", fontSize = 10.sp, color = Color.White.copy(alpha = 0.7f))
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color.White.copy(alpha = 0.2f),
                            modifier = Modifier.clickable {
                                playbackSpeed = when (playbackSpeed) {
                                    "1.0x" -> "1.25x"
                                    "1.25x" -> "1.5x"
                                    "1.5x" -> "2.0x"
                                    else -> "1.0x"
                                }
                            }
                        ) {
                            Text(text = playbackSpeed, fontSize = 10.sp, color = Color.White, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                        }
                    }

                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary.copy(alpha = 0.8f))
                            .clickable {
                                isPlaying = !isPlaying
                                if (isPlaying) currentProgress = (currentProgress + 15).coerceAtMost(100)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Scrubber Bar
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "${(lecture.durationMinutes * currentProgress / 100)}:00", fontSize = 10.sp, color = Color.White)
                            Text(text = "${lecture.durationMinutes}:00", fontSize = 10.sp, color = Color.White)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { currentProgress / 100f },
                            modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                            color = BrandPrimary,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }

        // Details & Actions
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(text = lecture.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "Taught by ${lecture.teacherName} • ${lecture.durationMinutes} Minutes", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = lecture.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            repository.completeLecture(lecture.id)
                            completedToast = true
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald)
                    ) {
                        Text("Mark Complete (+10 XP)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { onTakeQuiz("quiz_c_1") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text("Take Quiz (+20 XP)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedButton(
                    onClick = { onAskDoubt("C Programming") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Ask a Doubt About This Video 💡", fontSize = 12.sp)
                }
            }
        }

        // Key Takeaways & Cheat Sheet
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(text = "Key Takeaways & Summary", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                lecture.keyTakeaways.forEach { takeaway ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.padding(vertical = 3.dp)
                    ) {
                        Text(text = "•", color = BrandPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(text = takeaway, fontSize = 12.sp, color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
