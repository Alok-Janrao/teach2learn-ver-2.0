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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FirestoreLiveClass
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.AvatarInitials
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveLearningScreen(
    repository: Teach2LearnRepository,
    onNavigateToClassroom: (String) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val liveClasses by repository.firestoreLiveClasses.collectAsState()
    val currentUser by repository.currentUser.collectAsState()

    var showStartClassDialog by remember { mutableStateOf(false) }
    var showJoinRoomDialog by remember { mutableStateOf(false) }
    var joinRoomIdInput by remember { mutableStateOf("") }
    var joinRoomError by remember { mutableStateOf<String?>(null) }

    // Start Live Class Form State
    var classTitle by remember { mutableStateOf("") }
    var classSubject by remember { mutableStateOf("C Programming") }
    var classDescription by remember { mutableStateOf("") }
    var scheduledDateTime by remember { mutableStateOf("LIVE NOW") }
    var isStartImmediately by remember { mutableStateOf(true) }

    val activeLiveClasses = liveClasses.filter { it.status == "live" }
    val scheduledClasses = liveClasses.filter { it.status == "scheduled" }

    // Start Live Class Dialog
    if (showStartClassDialog) {
        AlertDialog(
            onDismissRequest = { showStartClassDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("📡", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Start Live Class", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = classTitle,
                        onValueChange = { classTitle = it },
                        label = { Text("Class Title *") },
                        placeholder = { Text("e.g. Dynamic Memory Allocation Lab") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = classSubject,
                        onValueChange = { classSubject = it },
                        label = { Text("Subject *") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    OutlinedTextField(
                        value = classDescription,
                        onValueChange = { classDescription = it },
                        label = { Text("Description") },
                        placeholder = { Text("What will be covered in this live session...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 2
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Start Live Immediately", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Switch(
                            checked = isStartImmediately,
                            onCheckedChange = { isStartImmediately = it }
                        )
                    }

                    if (!isStartImmediately) {
                        OutlinedTextField(
                            value = scheduledDateTime,
                            onValueChange = { scheduledDateTime = it },
                            label = { Text("Scheduled Date & Time") },
                            placeholder = { Text("e.g. Today, 8:30 PM") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (classTitle.isNotBlank()) {
                            val newClass = repository.scheduleOrStartLiveClass(
                                title = classTitle,
                                subject = classSubject,
                                description = classDescription,
                                scheduledAt = scheduledDateTime,
                                isStartNow = isStartImmediately
                            )
                            showStartClassDialog = false
                            classTitle = ""
                            classDescription = ""
                            if (isStartImmediately) {
                                onNavigateToClassroom(newClass.classId)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text(if (isStartImmediately) "Launch Classroom" else "Schedule Class")
                }
            },
            dismissButton = {
                TextButton(onClick = { showStartClassDialog = false }) { Text("Cancel") }
            }
        )
    }

    // Join via Room ID Dialog
    if (showJoinRoomDialog) {
        AlertDialog(
            onDismissRequest = { showJoinRoomDialog = false },
            title = { Text("Join Live Class via Room ID", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Enter the 6-character room code shared by your teacher or peer mentor:", fontSize = 12.sp)
                    OutlinedTextField(
                        value = joinRoomIdInput,
                        onValueChange = { joinRoomIdInput = it.uppercase() },
                        placeholder = { Text("e.g. ROOM-C-LIVE-101") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    if (joinRoomError != null) {
                        Text(joinRoomError!!, color = BrandRose, fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val matched = liveClasses.firstOrNull {
                            it.roomId.equals(joinRoomIdInput.trim(), ignoreCase = true) ||
                            it.classId.equals(joinRoomIdInput.trim(), ignoreCase = true)
                        }
                        if (matched != null) {
                            showJoinRoomDialog = false
                            joinRoomError = null
                            repository.joinLiveClass(matched.classId)
                            onNavigateToClassroom(matched.classId)
                        } else {
                            joinRoomError = "No active room found with this code. Check the code or select from active classes below."
                        }
                    }
                ) {
                    Text("Join")
                }
            },
            dismissButton = {
                TextButton(onClick = { showJoinRoomDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BrandRose.copy(alpha = 0.2f)
                        ) {
                            Text("📡", fontSize = 16.sp, modifier = Modifier.padding(6.dp))
                        }
                        Column {
                            Text(
                                text = "LIVE LEARNING",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Real-Time Audio / Video Classrooms",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("live_learning_back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Action Banner: Start or Join Class
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        Brush.linearGradient(listOf(BrandCyan.copy(alpha = 0.4f), BrandPrimary.copy(alpha = 0.2f)))
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = "Interactive Peer Classrooms",
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Join real-time video sessions with audio mic controls, multi-student video grids, live code breakdowns, and interactive chat.",
                            fontSize = 12.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 18.sp
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Button(
                                onClick = { showStartClassDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("start_live_class_button"),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                            ) {
                                Icon(Icons.Default.VideoCall, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Start Live Class", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = { showJoinRoomDialog = true },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("join_live_class_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Join via Code", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }

            // Section 1: ACTIVE LIVE CLASSES
            item {
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
                            shape = CircleShape,
                            color = BrandRose,
                            modifier = Modifier.size(10.dp)
                        ) {}
                        Text(
                            text = "ACTIVE LIVE CLASSES (${activeLiveClasses.size})",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            letterSpacing = 0.5.sp,
                            color = BrandRose
                        )
                    }
                }
            }

            if (activeLiveClasses.isEmpty()) {
                item {
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("📡", fontSize = 32.sp)
                            Text("No active live classes right now", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("Tap 'Start Live Class' above to host a session!", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            } else {
                items(activeLiveClasses, key = { it.classId }) { liveClass ->
                    LiveClassCard(
                        liveClass = liveClass,
                        isHost = currentUser.id == liveClass.hostId,
                        onJoin = {
                            repository.joinLiveClass(liveClass.classId)
                            onNavigateToClassroom(liveClass.classId)
                        },
                        onEnd = { repository.endLiveClass(liveClass.classId) }
                    )
                }
            }

            // Section 2: SCHEDULED LIVE CLASSES
            item {
                Text(
                    text = "UPCOMING SCHEDULED CLASSES (${scheduledClasses.size})",
                    fontWeight = FontWeight.Black,
                    fontSize = 13.sp,
                    letterSpacing = 0.5.sp,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 10.dp)
                )
            }

            if (scheduledClasses.isEmpty()) {
                item {
                    Text(
                        text = "No scheduled sessions currently planned.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                items(scheduledClasses, key = { it.classId }) { liveClass ->
                    ScheduledClassCard(
                        liveClass = liveClass,
                        isHost = currentUser.id == liveClass.hostId,
                        onStartNow = {
                            repository.scheduleOrStartLiveClass(
                                title = liveClass.title,
                                subject = liveClass.subject,
                                description = liveClass.description,
                                scheduledAt = "LIVE NOW",
                                isStartNow = true
                            )
                            onNavigateToClassroom(liveClass.classId)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LiveClassCard(
    liveClass: FirestoreLiveClass,
    isHost: Boolean,
    onJoin: () -> Unit,
    onEnd: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .border(
                1.2.dp,
                Brush.linearGradient(listOf(BrandRose.copy(alpha = 0.5f), Color.Transparent)),
                RoundedCornerShape(18.dp)
            )
            .testTag("live_class_card_${liveClass.classId}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Live Status Header & Room Code
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = BrandRose,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Surface(shape = CircleShape, color = Color.White, modifier = Modifier.size(6.dp)) {}
                        Text("LIVE NOW", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Black)
                    }
                }

                Surface(
                    color = BrandCyan.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = liveClass.roomId,
                        color = BrandCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = liveClass.title,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = liveClass.subject,
                color = BrandPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (liveClass.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = liveClass.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Footer with Teacher, Participants, and Join CTA
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AvatarInitials(name = liveClass.hostName, size = 26.dp)
                    Column {
                        Text(liveClass.hostName, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.People, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("${liveClass.participantCount} in class", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (isHost) {
                        OutlinedButton(
                            onClick = onEnd,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandRose),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("end_class_button_${liveClass.classId}")
                        ) {
                            Text("End", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Button(
                        onClick = onJoin,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("join_class_button_${liveClass.classId}")
                    ) {
                        Icon(Icons.Default.VideoCall, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Join Class", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduledClassCard(
    liveClass: FirestoreLiveClass,
    isHost: Boolean,
    onStartNow: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = BrandGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "🗓️ ${liveClass.scheduledAt}",
                        color = BrandGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Text(
                    text = "${liveClass.participantCount} registered",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = liveClass.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Text(
                text = "${liveClass.subject} • Host: ${liveClass.hostName}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (isHost) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onStartNow,
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCyan, contentColor = Color.Black),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Start Live Now", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}
