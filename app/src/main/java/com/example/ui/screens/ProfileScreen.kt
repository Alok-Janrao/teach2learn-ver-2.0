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
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.example.data.model.RankTier
import com.example.data.model.UserRole
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.*
import com.example.ui.theme.*

enum class ProfileTab(val title: String) {
    OVERVIEW("Overview"),
    LEARNING("Learning"),
    TEACHING("Teaching"),
    CERTIFICATES("Certificates"),
    SETTINGS("Settings")
}

@Composable
fun ProfileScreen(
    repository: Teach2LearnRepository,
    onSignOut: () -> Unit,
    onNavigateToPremium: () -> Unit,
    onNavigateToAdmin: () -> Unit,
    onNavigateToProgress: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentUser by repository.currentUser.collectAsState()
    val allSubjects by repository.subjects.collectAsState()
    val currentRank = remember(currentUser.xp) { RankTier.fromXp(currentUser.xp) }
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current

    var selectedTab by remember { mutableStateOf(ProfileTab.OVERVIEW) }
    var editBioDialog by remember { mutableStateOf(false) }
    var bioInput by remember { mutableStateOf(currentUser.bio) }
    var showSwitchUserModal by remember { mutableStateOf(false) }
    var profileToast by remember { mutableStateOf<String?>(null) }

    if (editBioDialog) {
        AlertDialog(
            onDismissRequest = { editBioDialog = false },
            title = { Text("Edit Student Bio") },
            text = {
                OutlinedTextField(
                    value = bioInput,
                    onValueChange = { bioInput = it },
                    label = { Text("Bio / Specialization") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    minLines = 3
                )
            },
            confirmButton = {
                Button(onClick = {
                    repository.updateTeacherProfile(
                        subjects = currentUser.teachSubjects,
                        availableDays = currentUser.teacherAvailableDays,
                        availableTime = currentUser.teacherAvailableTime,
                        teachingMode = currentUser.teachingMode,
                        bio = bioInput
                    )
                    editBioDialog = false
                    profileToast = "Bio updated successfully!"
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { editBioDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showSwitchUserModal) {
        AlertDialog(
            onDismissRequest = { showSwitchUserModal = false },
            title = { Text("Switch Demo User Profile 🔄") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    val users = listOf(
                        Triple("user_alok", "Alok Janrao", "Learner / Expert in C & Python"),
                        Triple("user_rahul", "Rahul Sharma", "Master Mentor (C, DSA, 86 helped)"),
                        Triple("user_sneha", "Sneha Rao", "Grand Mentor (Python, AI/DS, 112 helped)")
                    )

                    users.forEach { (id, name, desc) ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .clickable {
                                    repository.switchUser(id)
                                    showSwitchUserModal = false
                                    profileToast = "Switched active profile to $name"
                                },
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(text = name, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(text = desc, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showSwitchUserModal = false }) { Text("Close") }
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
            // Profile Card Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF4338CA))
                        )
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        AvatarInitials(name = currentUser.name, size = 60.dp, backgroundColor = BrandPrimary)

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = currentUser.name,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = Color.White
                            )
                            Text(
                                text = "${currentUser.college} • ${currentUser.department}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                            Text(
                                text = "${currentUser.educationLevel} • ${currentUser.yearSemester}",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f)
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            RankBadge(rank = currentRank)
                        }

                        IconButton(onClick = { showSwitchUserModal = true }) {
                            Icon(Icons.Default.SwapHoriz, contentDescription = "Switch User", tint = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color.White.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Stats Grid inside Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "⚡ ${currentUser.xp}", fontWeight = FontWeight.Bold, color = BrandGold, fontSize = 15.sp)
                            Text(text = "XP Earned", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🔥 ${currentUser.streakDays} Days", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 15.sp)
                            Text(text = "Streak", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "🤝 ${currentUser.studentsHelped}", fontWeight = FontWeight.Bold, color = BrandEmerald, fontSize = 15.sp)
                            Text(text = "Helped", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "⭐ ${currentUser.rating}", fontWeight = FontWeight.Bold, color = BrandGold, fontSize = 15.sp)
                            Text(text = "Rating", fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                }
            }
        }

        profileToast?.let { msg ->
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
                        IconButton(onClick = { profileToast = null }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Rank Progress to Next Tier & Profile Completion
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                val nextRank = when (currentRank) {
                    RankTier.BEGINNER -> RankTier.LEARNER
                    RankTier.LEARNER -> RankTier.EXPLORER
                    RankTier.EXPLORER -> RankTier.SKILLED
                    RankTier.SKILLED -> RankTier.EXPERT
                    RankTier.EXPERT -> RankTier.MASTER
                    RankTier.MASTER -> RankTier.MENTOR
                    RankTier.MENTOR -> RankTier.GRAND_MENTOR
                    RankTier.GRAND_MENTOR -> RankTier.GRAND_MENTOR
                }

                val currentTierMin = currentRank.minXp
                val nextTierMin = nextRank.minXp
                val progressToNext = if (nextTierMin > currentTierMin) {
                    ((currentUser.xp - currentTierMin).toFloat() / (nextTierMin - currentTierMin).toFloat() * 100).toInt().coerceIn(0, 100)
                } else 100

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Profile & Rank Progression", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "${currentUser.profileCompletion}% Profile Setup", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                }
                Spacer(modifier = Modifier.height(6.dp))
                ProgressBarWithLabel(
                    title = "Profile Setup Progress",
                    progressPercent = currentUser.profileCompletion,
                    color = BrandPrimary
                )
                Spacer(modifier = Modifier.height(8.dp))
                ProgressBarWithLabel(
                    title = "${currentRank.title} → ${nextRank.title}",
                    progressPercent = progressToNext,
                    color = BrandGold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (currentRank == RankTier.GRAND_MENTOR) "You have reached the highest tier: Grand Mentor 👑" else "${nextTierMin - currentUser.xp} XP needed to reach ${nextRank.title}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Profile Navigation Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab.ordinal,
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                divider = {}
            ) {
                ProfileTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Tab(
                        selected = isSelected,
                        onClick = { selectedTab = tab },
                        text = {
                            Text(
                                text = tab.title,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
            }
        }

        when (selectedTab) {
            ProfileTab.OVERVIEW -> {
                item {
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "About Me", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            TextButton(onClick = {
                                bioInput = currentUser.bio
                                editBioDialog = true
                            }) {
                                Text("Edit Bio ✏️", fontSize = 11.sp)
                            }
                        }
                        Text(text = currentUser.bio, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                        Spacer(modifier = Modifier.height(12.dp))
                        Text(text = "Learning Goals:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(
                            text = if (currentUser.learnSubjects.isEmpty()) "No subjects enrolled yet" else currentUser.learnSubjects.joinToString(", "),
                            fontSize = 12.sp,
                            color = BrandPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Teaching Specialization:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(
                            text = if (currentUser.teachSubjects.isEmpty()) "No teaching subjects added yet" else currentUser.teachSubjects.joinToString(", "),
                            fontSize = 12.sp,
                            color = BrandEmerald
                        )
                    }
                }

                // Activity Metrics Card
                item {
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Learning Activity Summary", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "${currentUser.completedLectures}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandPrimary)
                                Text(text = "Lectures Watched", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column {
                                Text(text = "${currentUser.completedQuizzes}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandTertiary)
                                Text(text = "Quizzes Completed", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Column {
                                Text(text = "${currentUser.learningProgress}%", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = BrandEmerald)
                                Text(text = "Learning Track", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = onNavigateToProgress,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary.copy(alpha = 0.12f), contentColor = BrandPrimary)
                        ) {
                            Text("📊 Open My Learning Progress Report →", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                // Badges Preview
                item {
                    val unlockedBadges = currentUser.badges.filter { it.isUnlocked }
                    SectionHeader(title = "Earned Badges (${unlockedBadges.size})")
                    if (unlockedBadges.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "No badges unlocked yet. Complete lessons and quizzes to earn badges!",
                                modifier = Modifier.padding(14.dp),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            items(unlockedBadges) { badge ->
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = BrandGold.copy(alpha = 0.12f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandGold.copy(alpha = 0.4f))
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Text(text = badge.icon, fontSize = 14.sp)
                                        Text(text = badge.name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandGold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            ProfileTab.LEARNING -> {
                item {
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Enrolled Learning Subjects", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(10.dp))
                        val enrolledSubjects = allSubjects.filter { it.name in currentUser.learnSubjects }
                        val displaySubjects = if (enrolledSubjects.isNotEmpty()) enrolledSubjects else allSubjects.take(3)
                        displaySubjects.forEachIndexed { index, subj ->
                            if (index > 0) Spacer(modifier = Modifier.height(10.dp))
                            val color = when (index % 3) {
                                0 -> BrandPrimary
                                1 -> BrandTertiary
                                else -> BrandSecondary
                            }
                            ProgressBarWithLabel(title = subj.name, progressPercent = subj.progressPercent, color = color)
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onNavigateToProgress,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                        ) {
                            Text("📊 View Full Learning Progress & Goals", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            ProfileTab.TEACHING -> {
                item {
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Mentorship Impact", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "• ${currentUser.studentsHelped} students mentored across subjects", fontSize = 12.sp)
                        Text(text = "• ${currentUser.sessionsCompleted} one-on-one live sessions completed", fontSize = 12.sp)
                        Text(text = "• Teaching track progress: ${currentUser.teachingProgress}%", fontSize = 12.sp)
                        Text(text = "• Overall rating: ⭐ ${currentUser.rating} / 5.0", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandGold)
                    }
                }
            }

            ProfileTab.CERTIFICATES -> {
                if (currentUser.certificates.isEmpty()) {
                    item {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Text(
                                text = "Complete course tracks and peer teaching sessions to earn verified certificates.",
                                modifier = Modifier.padding(16.dp),
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    items(currentUser.certificates) { cert ->
                        GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(46.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(BrandEmerald.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(text = "📜", fontSize = 22.sp)
                                }

                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = cert.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                    Text(text = "ID: ${cert.certificateId} • Issued ${cert.issueDate}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text(text = "Issuer: ${cert.issuer} (Verified)", fontSize = 11.sp, color = BrandEmerald, fontWeight = FontWeight.SemiBold)
                                }

                                IconButton(onClick = { profileToast = "Downloaded ${cert.title}" }) {
                                    Icon(Icons.Default.Download, contentDescription = "Download")
                                }
                            }
                        }
                    }
                }
            }

            ProfileTab.SETTINGS -> {
                item {
                    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                        Text(text = "Quick Platform Management", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = onNavigateToPremium,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = BrandGold)
                        ) {
                            Text("Upgrade to Teach2Learn Pro 👑", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        if (currentUser.role == UserRole.ADMIN) {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BrandPrimary.copy(alpha = 0.08f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("👑", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "TEACH2LEARN CONTROL CENTER",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Black,
                                            color = BrandPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Platform Management & Insights • Authorized Admin Only",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = onNavigateToAdmin,
                                        modifier = Modifier.fillMaxWidth().testTag("btn_control_center"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                                    ) {
                                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Launch Control Center 🚀", fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        } else {
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = BrandPrimary.copy(alpha = 0.05f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("🛡️", fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            "ADMINISTRATOR ACCESS",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandPrimary
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Sign in as Platform Administrator (Alok Janrao) to view real-time active users, live telemetry & platform controls.",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    OutlinedButton(
                                        onClick = {
                                            repository.switchUser("user_alok")
                                            profileToast = "Switched to Administrator (Alok Janrao) 👑"
                                        },
                                        modifier = Modifier.fillMaxWidth().testTag("btn_switch_to_admin"),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Text("Switch to Administrator Profile 👑", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Official Website & Distribution Hub
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth().testTag("card_official_website")
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text("🌐", fontSize = 18.sp)
                                        Column {
                                            Text(
                                                text = "Official Website & APK Hub",
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "teach2learn-app.netlify.app",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = BrandEmerald.copy(alpha = 0.15f),
                                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandEmerald.copy(alpha = 0.3f))
                                    ) {
                                        Text(
                                            text = "v1.2.0 LIVE",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = BrandEmerald,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = "Access high-yield engineering notes, 60s micro reels, and peer classrooms on the web or download the latest APK.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Teach2LearnRepository.OFFICIAL_WEBSITE_URL))
                                            context.startActivity(intent)
                                        },
                                        modifier = Modifier.weight(1f).testTag("btn_open_website"),
                                        shape = RoundedCornerShape(10.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                                    ) {
                                        Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Open Website", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    OutlinedButton(
                                        onClick = {
                                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                                type = "text/plain"
                                                putExtra(
                                                    Intent.EXTRA_TEXT,
                                                    "🎓 Join TEACH2LEARN - Peer-to-Peer Engineering Learning Platform!\nDownload the official Android app and access high-yield notes, 60s micro reels, and live classrooms:\n${Teach2LearnRepository.OFFICIAL_WEBSITE_URL}"
                                                )
                                            }
                                            context.startActivity(Intent.createChooser(shareIntent, "Share TEACH2LEARN"))
                                        },
                                        modifier = Modifier.weight(1f).testTag("btn_share_app"),
                                        shape = RoundedCornerShape(10.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Share App", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                TextButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(Teach2LearnRepository.OFFICIAL_WEBSITE_URL))
                                        profileToast = "Copied link: ${Teach2LearnRepository.OFFICIAL_WEBSITE_URL}"
                                    },
                                    modifier = Modifier.align(Alignment.CenterHorizontally).testTag("btn_copy_link")
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Copy Web Link to Clipboard", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = onSignOut,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Sign Out 👋", color = MaterialTheme.colorScheme.error)
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
