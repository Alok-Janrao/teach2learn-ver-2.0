package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FirestoreLiveClass
import com.example.data.model.FirestoreLiveParticipant
import com.example.data.model.LiveClassChatMessage
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.AvatarInitials
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveClassroomScreen(
    classId: String,
    repository: Teach2LearnRepository,
    onLeaveClassroom: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val liveClasses by repository.firestoreLiveClasses.collectAsState()
    val allParticipants by repository.liveParticipants.collectAsState()
    val allMessages by repository.liveChatMessages.collectAsState()
    val currentUser by repository.currentUser.collectAsState()

    val liveClass = liveClasses.firstOrNull { it.classId == classId } ?: FirestoreLiveClass(
        classId = classId,
        title = "Live Peer Learning Session",
        subject = "Engineering",
        hostName = "Host Mentor",
        roomId = "ROOM-LIVE-101"
    )

    val participants = allParticipants[classId] ?: listOf(
        FirestoreLiveParticipant("p1", classId, liveClass.hostId, "${liveClass.hostName} (Host)", "host", isMuted = false, isVideoOn = true),
        FirestoreLiveParticipant("p2", classId, currentUser.id, currentUser.name, if (currentUser.id == liveClass.hostId) "host" else "student", isMuted = true, isVideoOn = false)
    )

    val messages = allMessages[classId] ?: emptyList()
    val isHost = currentUser.id == liveClass.hostId

    // Local Device Hardware & Classroom Controls State
    var isMicMuted by remember { mutableStateOf(false) }
    var isCameraOn by remember { mutableStateOf(true) }
    var isHandRaised by remember { mutableStateOf(false) }
    var isScreenSharing by remember { mutableStateOf(false) }

    // Panels
    var showChatDrawer by remember { mutableStateOf(false) }
    var showParticipantsDrawer by remember { mutableStateOf(false) }
    var showClassInfoDialog by remember { mutableStateOf(false) }
    var showEndClassConfirmation by remember { mutableStateOf(false) }
    var chatInputText by remember { mutableStateOf("") }

    // Camera & Audio Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val cameraGranted = perms[android.Manifest.permission.CAMERA] ?: false
        val audioGranted = perms[android.Manifest.permission.RECORD_AUDIO] ?: false
        if (!cameraGranted) isCameraOn = false
        if (!audioGranted) isMicMuted = true
    }

    LaunchedEffect(Unit) {
        permissionLauncher.launch(
            arrayOf(
                android.Manifest.permission.CAMERA,
                android.Manifest.permission.RECORD_AUDIO
            )
        )
    }

    // End Class Confirmation Dialog
    if (showEndClassConfirmation) {
        AlertDialog(
            onDismissRequest = { showEndClassConfirmation = false },
            title = { Text("End Live Classroom?", fontWeight = FontWeight.Bold) },
            text = { Text("Ending this session will conclude the class for all connected students and finalize attendee attendance.") },
            confirmButton = {
                Button(
                    onClick = {
                        repository.endLiveClass(classId)
                        showEndClassConfirmation = false
                        onLeaveClassroom()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandRose)
                ) {
                    Text("End Class for All")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEndClassConfirmation = false }) { Text("Cancel") }
            }
        )
    }

    // Class Information Dialog
    if (showClassInfoDialog) {
        AlertDialog(
            onDismissRequest = { showClassInfoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("ℹ️", fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(liveClass.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Subject: ${liveClass.subject}", fontWeight = FontWeight.SemiBold, color = BrandCyan, fontSize = 13.sp)
                    Text("Host: ${liveClass.hostName}", fontSize = 12.sp)
                    Text("Room Code: ${liveClass.roomId}", fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    if (liveClass.description.isNotBlank()) {
                        Text(liveClass.description, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            confirmButton = {
                Button(onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("TEACH2LEARN Room Code", liveClass.roomId))
                    Toast.makeText(context, "Room Code Copied to Clipboard!", Toast.LENGTH_SHORT).show()
                    showClassInfoDialog = false
                }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy Code")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClassInfoDialog = false }) { Text("Close") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(shape = CircleShape, color = BrandRose, modifier = Modifier.size(8.dp)) {}
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = liveClass.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            text = "${liveClass.subject} • Room ${liveClass.roomId}",
                            fontSize = 11.sp,
                            color = BrandCyan
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showClassInfoDialog = true }, modifier = Modifier.testTag("classroom_info_button")) {
                        Icon(Icons.Default.Info, contentDescription = "Class Info")
                    }

                    // Participant Count Badge Toggle
                    IconButton(onClick = { showParticipantsDrawer = !showParticipantsDrawer }, modifier = Modifier.testTag("classroom_participants_button")) {
                        BadgedBox(
                            badge = {
                                Badge(containerColor = BrandPrimary) {
                                    Text("${participants.size}", fontSize = 10.sp)
                                }
                            }
                        ) {
                            Icon(Icons.Default.People, contentDescription = "Participants")
                        }
                    }

                    // Chat Toggle
                    IconButton(onClick = { showChatDrawer = !showChatDrawer }, modifier = Modifier.testTag("classroom_chat_button")) {
                        BadgedBox(
                            badge = {
                                if (messages.isNotEmpty()) {
                                    Badge(containerColor = BrandCyan, contentColor = Color.Black) {
                                        Text("${messages.size}", fontSize = 10.sp)
                                    }
                                }
                            }
                        ) {
                            Icon(Icons.Default.Chat, contentDescription = "Chat")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        bottomBar = {
            // Classroom Bottom Control Bar
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface,
                shadowElevation = 16.dp,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Microphone Control
                    IconButton(
                        onClick = {
                            isMicMuted = !isMicMuted
                            repository.toggleParticipantMic(classId, currentUser.id)
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (isMicMuted) BrandRose.copy(alpha = 0.2f) else BrandCyan.copy(alpha = 0.2f),
                                CircleShape
                            )
                            .testTag("mic_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = if (isMicMuted) "Unmute" else "Mute",
                            tint = if (isMicMuted) BrandRose else BrandCyan
                        )
                    }

                    // Camera Control
                    IconButton(
                        onClick = {
                            isCameraOn = !isCameraOn
                            repository.toggleParticipantCamera(classId, currentUser.id)
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (!isCameraOn) MaterialTheme.colorScheme.surfaceVariant else BrandCyan.copy(alpha = 0.2f),
                                CircleShape
                            )
                            .testTag("camera_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                            contentDescription = if (isCameraOn) "Turn Off Video" else "Turn On Video",
                            tint = if (isCameraOn) BrandCyan else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Hand Raise
                    IconButton(
                        onClick = {
                            isHandRaised = !isHandRaised
                            repository.toggleHandRaise(classId, currentUser.id)
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(
                                if (isHandRaised) BrandGold.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                                CircleShape
                            )
                            .testTag("hand_raise_button")
                    ) {
                        Text(
                            text = if (isHandRaised) "✋" else "👋",
                            fontSize = 20.sp
                        )
                    }

                    // Leave or End Class Button
                    if (isHost) {
                        Button(
                            onClick = { showEndClassConfirmation = true },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandRose),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("host_end_class_button")
                        ) {
                            Icon(Icons.Default.CallEnd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("End", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = {
                                repository.leaveLiveClass(classId)
                                onLeaveClassroom()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandRose),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("student_leave_class_button")
                        ) {
                            Icon(Icons.Default.CallEnd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Leave", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFF030712))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                // 1. PRIMARY TEACHER STAGE / SCREEN SHARE
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1.3f)
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.5.dp, BrandPrimary.copy(alpha = 0.4f), RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0B132B))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Live Stream Stage Canvas / Visualizer
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = BrandCyan.copy(alpha = 0.15f),
                                border = androidx.compose.foundation.BorderStroke(2.dp, BrandCyan),
                                modifier = Modifier.size(72.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("🎓", fontSize = 36.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "${liveClass.hostName} (Teacher Stream)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = Color.White
                            )

                            Text(
                                text = "Live WebRTC Audio/Video Feed Active",
                                fontSize = 11.sp,
                                color = BrandCyan
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Surface(
                                color = Color(0xFF1E293B),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(Icons.Default.Mic, contentDescription = null, tint = BrandCyan, modifier = Modifier.size(14.dp))
                                    Text("Speaking", fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }

                        // Top Overlay Badges
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Surface(
                                color = BrandRose,
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "● LIVE STAGE",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }

                            Surface(
                                color = Color.Black.copy(alpha = 0.6f),
                                shape = RoundedCornerShape(6.dp)
                            ) {
                                Text(
                                    text = "1080p 60fps",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }

                // 2. STUDENT VIDEO GRID (Interactive Video Tiles)
                Text(
                    text = "CLASSROOM ATTENDEES (${participants.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(participants, key = { it.participantId }) { participant ->
                        StudentVideoTile(
                            participant = participant,
                            isSelf = participant.userId == currentUser.id,
                            isMicActive = if (participant.userId == currentUser.id) !isMicMuted else !participant.isMuted,
                            isVideoActive = if (participant.userId == currentUser.id) isCameraOn else participant.isVideoOn,
                            hasHandRaised = if (participant.userId == currentUser.id) isHandRaised else participant.isHandRaised
                        )
                    }
                }

            }

            // In-Classroom Chat Bottom Sheet / Drawer
            if (showChatDrawer) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.65f)
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 24.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Classroom Live Chat", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            IconButton(onClick = { showChatDrawer = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        LazyColumn(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(messages) { msg ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (msg.isTeacher) BrandPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                text = if (msg.isTeacher) "🎓 ${msg.senderName} (Teacher)" else msg.senderName,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp,
                                                color = if (msg.isTeacher) BrandCyan else MaterialTheme.colorScheme.primary
                                            )
                                            Text(msg.timestamp, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(msg.message, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = chatInputText,
                                onValueChange = { chatInputText = it },
                                placeholder = { Text("Ask a question in class...") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true
                            )

                            IconButton(
                                onClick = {
                                    if (chatInputText.isNotBlank()) {
                                        repository.sendLiveChatMessage(classId, chatInputText)
                                        chatInputText = ""
                                    }
                                },
                                modifier = Modifier
                                    .background(BrandPrimary, CircleShape)
                                    .size(44.dp)
                            ) {
                                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            // In-Classroom Participants Drawer
            if (showParticipantsDrawer) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.65f)
                        .align(Alignment.BottomCenter)
                        .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 24.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Participants (${participants.size})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            IconButton(onClick = { showParticipantsDrawer = false }) {
                                Icon(Icons.Default.Close, contentDescription = "Close")
                            }
                        }

                        Divider(modifier = Modifier.padding(vertical = 8.dp))

                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(participants) { p ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        AvatarInitials(name = p.userName, size = 32.dp)
                                        Column {
                                            Text(
                                                text = p.userName,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp
                                            )
                                            Text(
                                                text = if (p.role == "host") "Educator / Host" else "Student",
                                                fontSize = 10.sp,
                                                color = if (p.role == "host") BrandCyan else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        if (p.isHandRaised) {
                                            Text("✋", fontSize = 14.sp)
                                        }
                                        Icon(
                                            imageVector = if (p.isMuted) Icons.Default.MicOff else Icons.Default.Mic,
                                            contentDescription = null,
                                            tint = if (p.isMuted) BrandRose else BrandEmerald,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Icon(
                                            imageVector = if (p.isVideoOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                                            contentDescription = null,
                                            tint = if (p.isVideoOn) BrandCyan else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }
}

@Composable
fun StudentVideoTile(
    participant: FirestoreLiveParticipant,
    isSelf: Boolean,
    isMicActive: Boolean,
    isVideoActive: Boolean,
    hasHandRaised: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(115.dp)
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (isMicActive) BrandEmerald.copy(alpha = 0.6f) else Color(0xFF1E293B),
                RoundedCornerShape(14.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (isVideoActive) {
                // Active Simulated Camera View (Gradient Camera Canvas)
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF1E293B), Color(0xFF0F172A))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    AvatarInitials(name = participant.userName, size = 42.dp)
                }
            } else {
                // Video Off State
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0B132B)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        AvatarInitials(name = participant.userName, size = 36.dp)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Camera Off", fontSize = 9.sp, color = Color.Gray)
                    }
                }
            }

            // Top Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (hasHandRaised) {
                    Text("✋", fontSize = 12.sp)
                } else {
                    Spacer(modifier = Modifier.width(4.dp))
                }

                Surface(
                    color = if (isMicActive) BrandEmerald.copy(alpha = 0.3f) else BrandRose.copy(alpha = 0.3f),
                    shape = CircleShape,
                    modifier = Modifier.size(20.dp)
                ) {
                    Icon(
                        imageVector = if (isMicActive) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = null,
                        tint = if (isMicActive) BrandEmerald else BrandRose,
                        modifier = Modifier.padding(3.dp)
                    )
                }
            }

            // Bottom Name Tag
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(6.dp),
                color = Color.Black.copy(alpha = 0.7f),
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = if (isSelf) "${participant.userName} (You)" else participant.userName,
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    maxLines = 1
                )
            }
        }
    }
}
