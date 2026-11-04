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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Lecture
import com.example.data.model.RankTier
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun HomeScreen(
    repository: Teach2LearnRepository,
    onNavigateToLearn: () -> Unit,
    onNavigateToSubject: (String) -> Unit,
    onNavigateToTeach: () -> Unit,
    onNavigateToPlay: () -> Unit,
    onNavigateToDoubts: () -> Unit,
    onNavigateToChallenges: () -> Unit,
    onNavigateToLiveLecture: (String) -> Unit,
    onNavigateToLeaderboard: () -> Unit,
    onNavigateToTeacherProfile: (String) -> Unit,
    onNavigateToNotes: () -> Unit = {},
    onNavigateToVideoLectures: () -> Unit = {},
    onNavigateToShortVideos: () -> Unit = {},
    onNavigateToLiveLearning: () -> Unit = {},
    onNavigateToQuiz: (String) -> Unit = {},
    onNavigateToProgress: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentUser by repository.currentUser.collectAsState()
    val subjects by repository.subjects.collectAsState()
    val lectures by repository.lectures.collectAsState()
    val doubts by repository.doubts.collectAsState()
    val studentTeachers by repository.studentTeachers.collectAsState()
    val challenges by repository.challenges.collectAsState()
    val leaderboard by repository.leaderboard.collectAsState()
    val weeklyProgress by repository.weeklyProgress.collectAsState()
    val subjectProgressList by repository.subjectProgress.collectAsState()

    val scoreBreakdown = remember(subjectProgressList, currentUser) {
        repository.getTeach2LearnScoreBreakdown()
    }

    val currentRank = remember(currentUser.xp) { RankTier.fromXp(currentUser.xp) }
    val userLeaderboardEntry = leaderboard.firstOrNull { it.isCurrentUser }

    var alertToast by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header & Profile Summary
        item {
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Welcome back, ${currentUser.name.split(" ").firstOrNull() ?: "Learner"} 👋",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${currentUser.college} • ${currentUser.department}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    XpPill(xp = currentUser.xp)
                }
            }
        }

        // Notification / Alert Banner if set
        alertToast?.let { msg ->
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BrandEmerald.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandEmerald)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "🔔", fontSize = 16.sp)
                            Text(text = msg, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandEmerald)
                        }
                        IconButton(onClick = { alertToast = null }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // 3D Animated Learning Holographic Core
        item {
            ThreeDeeLearningOrb(
                primaryColor = BrandPrimary,
                secondaryColor = BrandSecondary,
                accentColor = BrandTertiary,
                streakCount = currentUser.streakDays,
                xpAmount = currentUser.xp,
                userLevel = maxOf(1, currentUser.xp / 400 + 1),
                rankTitle = currentRank.title,
                onOrbClick = onNavigateToLeaderboard
            )
        }

        // 3D Interactive Flashcard of the Day (Tap to Flip in 3D space!)
        item {
            ThreeDeeFlipCard(
                modifier = Modifier.fillMaxWidth(),
                frontContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF4338CA))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = BrandGold.copy(alpha = 0.25f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandGold.copy(alpha = 0.6f))
                                ) {
                                    Text(
                                        text = "⚡ 3D DAILY CONCEPT FLIP",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFDE68A),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Text(
                                    text = "🔄 Tap to Flip in 3D",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandEmerald
                                )
                            }
                            Text(
                                text = "Pointer Arithmetic: What is (ptr + 2) in C?",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Given `int *ptr = 0x1000;`, what is the address of `*(ptr + 2)` on a standard 32/64-bit architecture?",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "Tap to Reveal Solution ➔",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = BrandGold
                                )
                            }
                        }
                    }
                },
                backContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF064E3B), Color(0xFF065F46), Color(0xFF047857))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color.White.copy(alpha = 0.2f)
                                ) {
                                    Text(
                                        text = "✅ 3D SOLUTION BREAKDOWN",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Text(
                                    text = "+25 XP Earned!",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandGoldLight
                                )
                            }
                            Text(
                                text = "Answer: 0x1008 (Scaled by sizeof(int) = 4 bytes)",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "In C, pointer addition automatically multiplies the offset by `sizeof(T)`. 0x1000 + (2 × 4 bytes) = 0x1008.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "🔄 Tap to Flip Back",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            )
        }

        // TEACH2LEARN Core Video Learning Modes (1. Short Learning, 2. Live Learning)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(
                    1.2.dp,
                    Brush.linearGradient(listOf(BrandCyan.copy(alpha = 0.5f), BrandPrimary.copy(alpha = 0.2f)))
                )
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "TEACH2LEARN MODES",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = BrandCyan
                        )
                        Surface(
                            color = BrandCyan.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "CLOUD POWERED",
                                color = BrandCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. SHORT LEARNING
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onNavigateToShortVideos() }
                                .border(1.dp, BrandCyan.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                            color = Color(0xFF0A1226)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(shape = CircleShape, color = BrandCyan.copy(alpha = 0.2f), modifier = Modifier.size(32.dp)) {
                                    Box(contentAlignment = Alignment.Center) { Text("⚡", fontSize = 16.sp) }
                                }
                                Text("SHORT LEARNING", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color.White)
                                Text("Vertical educational feed & 60s peer videos", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Watch & Upload", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandCyan)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(12.dp), tint = BrandCyan)
                                }
                            }
                        }

                        // 2. LIVE LEARNING
                        Surface(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onNavigateToLiveLearning() }
                                .border(1.dp, BrandRose.copy(alpha = 0.35f), RoundedCornerShape(14.dp)),
                            color = Color(0xFF140D24)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(shape = CircleShape, color = BrandRose.copy(alpha = 0.2f), modifier = Modifier.size(32.dp)) {
                                    Box(contentAlignment = Alignment.Center) { Text("📡", fontSize = 16.sp) }
                                }
                                Text("LIVE LEARNING", fontWeight = FontWeight.Black, fontSize = 12.sp, color = Color.White)
                                Text("Real-time classrooms, video grid & audio mic", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 2)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("Join & Host", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandRose)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(12.dp), tint = BrandRose)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Hero Stats Card Row (XP, Rank, Streak, Helped)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickStatCard(
                    label = "Current Rank",
                    value = currentRank.title,
                    icon = currentRank.badgeIcon,
                    color = Color(currentRank.colorHex),
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToLeaderboard
                )
                QuickStatCard(
                    label = "Streak",
                    value = "${currentUser.streakDays} Days",
                    icon = "🔥",
                    color = BrandRose,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToPlay
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickStatCard(
                    label = "XP Points",
                    value = "${currentUser.xp}",
                    icon = "⚡",
                    color = BrandGold,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToLeaderboard
                )
                QuickStatCard(
                    label = "Students Helped",
                    value = "${currentUser.studentsHelped}",
                    icon = "🤝",
                    color = BrandEmerald,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToTeach
                )
            }
        }

        // My Learning Progress Feature Spotlight Card
        item {
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = BrandPrimary.copy(alpha = 0.5f),
                onClick = onNavigateToProgress
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = BrandPrimary.copy(alpha = 0.15f),
                                modifier = Modifier.size(36.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("📊", fontSize = 18.sp)
                                }
                            }
                            Column {
                                Text("My Learning Progress", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text("Weekly Journey & TEACH2LEARN Index", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BrandGold.copy(alpha = 0.18f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BrandGold.copy(alpha = 0.6f))
                        ) {
                            Text(
                                text = "Score: ${scoreBreakdown.overallScore}/100",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandGoldLight,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Progress Dual Bar preview
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("📘 LEARN", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                                Text("${scoreBreakdown.learnScore}%", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            LinearProgressIndicator(
                                progress = { scoreBreakdown.learnScore / 100f },
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                color = BrandPrimary,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }

                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(3.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("🤝 TEACH", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandEmerald)
                                Text("${scoreBreakdown.teachScore}%", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            LinearProgressIndicator(
                                progress = { scoreBreakdown.teachScore / 100f },
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(CircleShape),
                                color = BrandEmerald,
                                trackColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "📈 ${weeklyProgress.currentWeekHours}h studied this week (+${weeklyProgress.hoursGrowthPercent}%) • ${weeklyProgress.currentWeekTopics} topics done",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Text(
                            text = "View Report →",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandPrimary
                        )
                    }
                }
            }
        }

        // Feature Action Row (Notes, Video Lectures, Short Videos, Extra Hard Quiz)
        item {
            SectionHeader(title = "Featured Learning Modes")
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    ActionCard(
                        title = "My Progress",
                        subtitle = "Analytics & Goals",
                        icon = "📊",
                        color = BrandPrimary,
                        onClick = onNavigateToProgress
                    )
                }
                item {
                    ActionCard(
                        title = "Notes & Cheats",
                        subtitle = "PDFs & Formulas",
                        icon = "📝",
                        color = BrandPrimary,
                        onClick = onNavigateToNotes
                    )
                }
                item {
                    ActionCard(
                        title = "Video Lectures",
                        subtitle = "Live & Recorded",
                        icon = "🎥",
                        color = BrandSecondary,
                        onClick = onNavigateToVideoLectures
                    )
                }
                item {
                    ActionCard(
                        title = "Short Videos",
                        subtitle = "60s Reels + Quiz",
                        icon = "⚡",
                        color = BrandRose,
                        onClick = onNavigateToShortVideos
                    )
                }
                item {
                    ActionCard(
                        title = "🔥 Extra Hard Quiz",
                        subtitle = "Memory & Pointers",
                        icon = "🧠",
                        color = Color(0xFFDC2626),
                        onClick = { onNavigateToQuiz("quiz_c_extrahard") }
                    )
                }
                item {
                    ActionCard(
                        title = "Peer Doubts",
                        subtitle = "Solve & earn XP",
                        icon = "💡",
                        color = BrandGold,
                        onClick = onNavigateToDoubts
                    )
                }
            }
        }

        // Daily Challenge Hero 3D Card
        item {
            val dailyChallenge = challenges.firstOrNull { it.type == com.example.data.model.ChallengeType.DAILY }
            dailyChallenge?.let { ch ->
                Interactive3DCard(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    highlightColor = BrandGold,
                    backgroundColor = Color(0xFF1E1B4B),
                    borderColor = BrandPrimary.copy(alpha = 0.5f),
                    elevation = 8.dp,
                    onClick = onNavigateToChallenges
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF4338CA))
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = BrandGold.copy(alpha = 0.25f)
                                    ) {
                                        Text(
                                            text = "⚡ DAILY 3D CHALLENGE",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFFFDE68A),
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                    Text(
                                        text = "⏳ ${ch.remainingTime}",
                                        fontSize = 11.sp,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = ch.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = ch.description,
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.85f),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Button(
                                onClick = onNavigateToChallenges,
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandGold),
                                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = "Solve (+30 XP)",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Upcoming Live Lectures with Early Reminder Trigger
        item {
            SectionHeader(
                title = "Live & Scheduled Lectures",
                subtitle = "Set early alert so you never miss a mentor session",
                actionText = "All Lectures",
                onActionClick = onNavigateToVideoLectures
            )

            val liveLectures = lectures.take(2)
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                liveLectures.forEach { lec ->
                    GlassmorphicCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { onNavigateToLiveLecture(lec.id) }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (lec.isLive) BrandRose.copy(alpha = 0.15f) else BrandPrimary.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = if (lec.isLive) "🔴" else "🎬", fontSize = 18.sp)
                                }

                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text(
                                            text = if (lec.isLive) "LIVE NOW" else lec.scheduledTime,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (lec.isLive) BrandRose else BrandPrimary
                                        )
                                        Text(
                                            text = "• ${lec.studentCount} students",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    Text(
                                        text = lec.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    Text(
                                        text = "Mentor: ${lec.teacherName}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = {
                                        if (lec.isReminderSet) {
                                            repository.cancelLiveLectureReminder(lec.id)
                                            alertToast = "Reminder removed for ${lec.title}"
                                        } else {
                                            repository.setLiveLectureReminder(lec.id, 15)
                                            alertToast = "🔔 15-min early alert set for ${lec.title}!"
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = if (lec.isReminderSet) Icons.Default.NotificationsActive else Icons.Default.NotificationsNone,
                                        contentDescription = "Set Reminder",
                                        tint = if (lec.isReminderSet) BrandGold else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Button(
                                    onClick = { onNavigateToLiveLecture(lec.id) },
                                    shape = RoundedCornerShape(10.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = if (lec.isLive) BrandRose else BrandPrimary),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = if (lec.isLive) "JOIN" else "WATCH",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Recommended Student Teachers
        item {
            SectionHeader(
                title = "Recommended Student Teachers",
                subtitle = "Peers ready to help you 1-on-1",
                actionText = "All Mentors",
                onActionClick = onNavigateToTeach
            )

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(studentTeachers) { teacher ->
                    TeacherCard(
                        teacher = teacher,
                        onConnect = { onNavigateToTeacherProfile(teacher.id) }
                    )
                }
            }
        }

        // Recent Doubts
        item {
            SectionHeader(
                title = "Recent Peer Doubts",
                subtitle = "Help a student & earn +15 to +30 XP",
                actionText = "Solve Doubts",
                onActionClick = onNavigateToDoubts
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                doubts.take(2).forEach { doubt ->
                    DoubtPreviewCard(
                        doubt = doubt,
                        onClick = onNavigateToDoubts
                    )
                }
            }
        }

        // Leaderboard Spotlight
        item {
            userLeaderboardEntry?.let { entry ->
                GlassmorphicCard(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = onNavigateToLeaderboard
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Text(
                                text = "#${entry.rank}",
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = BrandGold
                            )
                            Column {
                                Text(
                                    text = "Your Leaderboard Position",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                                Text(
                                    text = "Ranked #${entry.rank} in ${entry.college}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            XpPill(xp = entry.xp)
                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
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

@Composable
fun ActionCard(
    title: String,
    subtitle: String,
    icon: String,
    color: Color,
    onClick: () -> Unit
) {
    Interactive3DCard(
        modifier = Modifier.width(145.dp),
        shape = RoundedCornerShape(18.dp),
        backgroundColor = color.copy(alpha = 0.08f),
        borderColor = color.copy(alpha = 0.35f),
        highlightColor = color,
        elevation = 4.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color.copy(alpha = 0.22f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = icon, fontSize = 20.sp)
            }
            Text(
                text = title,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun TeacherCard(
    teacher: com.example.data.model.StudentTeacher,
    onConnect: () -> Unit
) {
    Surface(
        modifier = Modifier
            .width(200.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
            .clickable { onConnect() },
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                AvatarInitials(name = teacher.name, size = 38.dp, backgroundColor = BrandPrimary)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BrandGold.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "⭐ ${teacher.rating}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = teacher.name,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = teacher.subjects.take(2).joinToString(", "),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${teacher.studentsHelped} helped",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${teacher.xp} XP",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandGold
                )
            }

            Button(
                onClick = onConnect,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(36.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                Text(text = "Connect / Request", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun DoubtPreviewCard(
    doubt: com.example.data.model.Doubt,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
            .clickable { onClick() },
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "${doubt.subject} • ${doubt.topic}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                if (doubt.isSolved) {
                    Text(text = "✅ Solved", fontSize = 11.sp, color = BrandEmerald, fontWeight = FontWeight.Bold)
                } else {
                    Text(text = "⚡ +15 XP to solve", fontSize = 11.sp, color = BrandGold, fontWeight = FontWeight.Bold)
                }
            }

            Text(
                text = doubt.question,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Asked by ${doubt.studentName}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "💬 ${doubt.answers.size} answers",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
