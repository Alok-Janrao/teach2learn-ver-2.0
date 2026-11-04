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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.XpPill
import com.example.ui.theme.*

enum class ProgressSectionTab(val title: String, val icon: String) {
    ALL("All", "📊"),
    SCORES("Scores", "🏆"),
    SUBJECTS("Subjects", "📚"),
    WEEKLY("Weekly", "📈"),
    GOALS("Goals", "🎯"),
    TIMELINE("History", "⏱️")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyProgressScreen(
    repository: Teach2LearnRepository,
    onBack: () -> Unit,
    onNavigateToSubject: (String) -> Unit = {},
    onNavigateToQuiz: (String) -> Unit = {},
    onNavigateToNotes: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val currentUser by repository.currentUser.collectAsState()
    val subjectProgressList by repository.subjectProgress.collectAsState()
    val strengths by repository.learningStrengths.collectAsState()
    val improvementAreas by repository.improvementAreas.collectAsState()
    val historyItems by repository.learningHistory.collectAsState()
    val weeklyProgress by repository.weeklyProgress.collectAsState()
    val goals by repository.learningGoals.collectAsState()

    val scoreBreakdown = remember(subjectProgressList, currentUser) {
        repository.getTeach2LearnScoreBreakdown()
    }

    var selectedSection by remember { mutableStateOf(ProgressSectionTab.ALL) }
    var selectedTimelineFilter by remember { mutableStateOf<TimelineActivityType?>(null) }
    var showCreateGoalDialog by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }

    // Goal creation inputs
    var newGoalTitle by remember { mutableStateOf("") }
    var newGoalCategory by remember { mutableStateOf("Exam Prep") }
    var newGoalTarget by remember { mutableStateOf("5") }
    var newGoalUnit by remember { mutableStateOf("Topics") }
    var newGoalDeadline by remember { mutableStateOf("In 7 days") }

    val goalCategories = listOf("Exam Prep", "Skill Mastery", "Quiz Target", "Peer Teaching")
    val goalUnits = listOf("Topics", "Hours", "Quizzes", "Sessions")

    if (showCreateGoalDialog) {
        AlertDialog(
            onDismissRequest = { showCreateGoalDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("🎯", fontSize = 20.sp)
                    Text("Create Learning Goal", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = newGoalTitle,
                        onValueChange = { newGoalTitle = it },
                        label = { Text("Goal Title") },
                        placeholder = { Text("e.g. Master C Pointer Arithmetic") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    Text("Category", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(goalCategories) { cat ->
                            FilterChip(
                                selected = newGoalCategory == cat,
                                onClick = { newGoalCategory = cat },
                                label = { Text(cat, fontSize = 11.sp) }
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        OutlinedTextField(
                            value = newGoalTarget,
                            onValueChange = { if (it.all { c -> c.isDigit() }) newGoalTarget = it },
                            label = { Text("Target") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true
                        )

                        Column(modifier = Modifier.weight(1.2f)) {
                            Text("Unit", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                items(goalUnits) { u ->
                                    FilterChip(
                                        selected = newGoalUnit == u,
                                        onClick = { newGoalUnit = u },
                                        label = { Text(u, fontSize = 10.sp) }
                                    )
                                }
                            }
                        }
                    }

                    OutlinedTextField(
                        value = newGoalDeadline,
                        onValueChange = { newGoalDeadline = it },
                        label = { Text("Target Deadline") },
                        placeholder = { Text("e.g. In 5 days or End of Week") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newGoalTitle.isNotBlank()) {
                            val target = newGoalTarget.toIntOrNull() ?: 5
                            repository.addLearningGoal(
                                title = newGoalTitle.trim(),
                                category = newGoalCategory,
                                target = target,
                                unit = newGoalUnit,
                                deadline = newGoalDeadline.ifBlank { "Next week" }
                            )
                            newGoalTitle = ""
                            showCreateGoalDialog = false
                            snackbarMessage = "Goal created successfully! 🎯"
                        }
                    },
                    enabled = newGoalTitle.isNotBlank()
                ) {
                    Text("Save Goal")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCreateGoalDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "My Learning Progress",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "Comprehensive Journey & Performance Analytics",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("progress_back_button")) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    XpPill(xp = currentUser.xp)
                    Spacer(modifier = Modifier.width(8.dp))
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (selectedSection == ProgressSectionTab.ALL || selectedSection == ProgressSectionTab.GOALS) {
                ExtendedFloatingActionButton(
                    onClick = { showCreateGoalDialog = true },
                    icon = { Icon(Icons.Default.Add, contentDescription = "Add Goal") },
                    text = { Text("New Goal") },
                    containerColor = BrandPrimary,
                    contentColor = Color.White,
                    modifier = Modifier.testTag("add_goal_fab")
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(4.dp))

                // Snack banner
                snackbarMessage?.let { msg ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = BrandEmerald.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandEmerald),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(msg, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandEmerald)
                            IconButton(onClick = { snackbarMessage = null }, modifier = Modifier.size(20.dp)) {
                                Icon(Icons.Default.Close, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                // Section Filter Tabs
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(ProgressSectionTab.entries) { tab ->
                        FilterChip(
                            selected = selectedSection == tab,
                            onClick = { selectedSection = tab },
                            label = { Text("${tab.icon} ${tab.title}", fontSize = 12.sp, fontWeight = if (selectedSection == tab) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = BrandPrimary,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
            }

            // 1 & 7: TEACH2LEARN & OVERALL LEARNING SCORE CARD
            if (selectedSection == ProgressSectionTab.ALL || selectedSection == ProgressSectionTab.SCORES) {
                item {
                    Teach2LearnScoreHeroCard(scoreBreakdown = scoreBreakdown)
                }
            }

            // 5: WEEKLY PROGRESS COMPARISON
            if (selectedSection == ProgressSectionTab.ALL || selectedSection == ProgressSectionTab.WEEKLY) {
                item {
                    WeeklyProgressSection(weeklyProgress = weeklyProgress)
                }
            }

            // 1: SUBJECT & TOPIC PROGRESS
            if (selectedSection == ProgressSectionTab.ALL || selectedSection == ProgressSectionTab.SUBJECTS) {
                item {
                    SubjectProgressSection(
                        subjects = subjectProgressList,
                        onSelectSubject = onNavigateToSubject
                    )
                }
            }

            // 2: STRENGTHS
            if (selectedSection == ProgressSectionTab.ALL || selectedSection == ProgressSectionTab.SCORES) {
                item {
                    StrengthsSection(strengths = strengths)
                }
            }

            // 3: AREAS TO IMPROVE
            if (selectedSection == ProgressSectionTab.ALL || selectedSection == ProgressSectionTab.SCORES) {
                item {
                    ImprovementAreasSection(
                        areas = improvementAreas,
                        onAction = { area ->
                            when (area.actionType) {
                                "quiz" -> onNavigateToQuiz("quiz_c_1")
                                "notes" -> onNavigateToNotes()
                                else -> onNavigateToSubject("subj_math")
                            }
                        }
                    )
                }
            }

            // 6: LEARNING GOALS
            if (selectedSection == ProgressSectionTab.ALL || selectedSection == ProgressSectionTab.GOALS) {
                item {
                    LearningGoalsSection(
                        goals = goals,
                        onIncrement = { repository.incrementGoalProgress(it) },
                        onToggle = { repository.toggleGoalCompletion(it) },
                        onDelete = { repository.deleteGoal(it) },
                        onAddNew = { showCreateGoalDialog = true }
                    )
                }
            }

            // 4: LEARNING HISTORY TIMELINE
            if (selectedSection == ProgressSectionTab.ALL || selectedSection == ProgressSectionTab.TIMELINE) {
                item {
                    LearningHistoryTimelineSection(
                        historyItems = historyItems,
                        selectedFilter = selectedTimelineFilter,
                        onFilterChanged = { selectedTimelineFilter = it }
                    )
                }
            }

            item {
                Spacer(modifier = Modifier.height(72.dp))
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 1 & 7: TEACH2LEARN HERO SCORE CARD
// -------------------------------------------------------------
@Composable
fun Teach2LearnScoreHeroCard(scoreBreakdown: Teach2LearnScoreBreakdown) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.linearGradient(
                    listOf(Color(0xFF1E1B4B), Color(0xFF312E81), Color(0xFF4338CA))
                )
            )
            .padding(20.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            // Header Row: Title & Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("🎓", fontSize = 16.sp)
                        Text(
                            text = "TEACH2LEARN SCORE",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp,
                            color = Color(0xFFA5B4FC)
                        )
                    }
                    Text(
                        text = "Composite Peer Learning Index",
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = BrandGold.copy(alpha = 0.25f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandGold)
                ) {
                    Text(
                        text = "🌟 ${scoreBreakdown.overallScore} / 100",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black,
                        color = BrandGoldLight,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                    )
                }
            }

            // Dual Score Breakdown: LEARN vs TEACH
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // LEARN Score Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF6366F1).copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("📘 LEARN Score", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF93C5FD))
                            Text("${scoreBreakdown.learnScore}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }

                        LinearProgressIndicator(
                            progress = { scoreBreakdown.learnScore / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = Color(0xFF38BDF8),
                            trackColor = Color.White.copy(alpha = 0.15f),
                        )

                        Text(
                            text = "Activity, completed topics, quizzes & practice",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.75f),
                            lineHeight = 13.sp
                        )
                    }
                }

                // TEACH Score Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color.White.copy(alpha = 0.08f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandEmerald.copy(alpha = 0.4f)),
                    modifier = Modifier.weight(1f)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("🤝 TEACH Score", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF6EE7B7))
                            Text("${scoreBreakdown.teachScore}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color.White)
                        }

                        LinearProgressIndicator(
                            progress = { scoreBreakdown.teachScore / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(CircleShape),
                            color = Color(0xFF34D399),
                            trackColor = Color.White.copy(alpha = 0.15f),
                        )

                        Text(
                            text = "Teaching sessions, students helped & peer doubts",
                            fontSize = 10.sp,
                            color = Color.White.copy(alpha = 0.75f),
                            lineHeight = 13.sp
                        )
                    }
                }
            }

            // Summary Evaluation banner
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = Color.Black.copy(alpha = 0.25f),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("💡", fontSize = 14.sp)
                    Text(
                        text = scoreBreakdown.evaluationSummary,
                        fontSize = 11.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 5: WEEKLY PROGRESS COMPARISON
// -------------------------------------------------------------
@Composable
fun WeeklyProgressSection(weeklyProgress: WeeklyProgressComparison) {
    GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("📈", fontSize = 18.sp)
                    Column {
                        Text("Weekly Progress", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text("Current Week vs Previous Week", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BrandEmerald.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "+${weeklyProgress.hoursGrowthPercent}% Hours",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandEmerald,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            // Quick Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                WeeklyStatMiniCard(
                    label = "Study Hours",
                    current = "${weeklyProgress.currentWeekHours}h",
                    previous = "${weeklyProgress.previousWeekHours}h",
                    isPositive = weeklyProgress.currentWeekHours >= weeklyProgress.previousWeekHours,
                    modifier = Modifier.weight(1f)
                )
                WeeklyStatMiniCard(
                    label = "Topics Done",
                    current = "${weeklyProgress.currentWeekTopics}",
                    previous = "${weeklyProgress.previousWeekTopics}",
                    isPositive = weeklyProgress.currentWeekTopics >= weeklyProgress.previousWeekTopics,
                    modifier = Modifier.weight(1f)
                )
                WeeklyStatMiniCard(
                    label = "Quizzes Taken",
                    current = "${weeklyProgress.currentWeekQuizzes}",
                    previous = "${weeklyProgress.previousWeekQuizzes}",
                    isPositive = weeklyProgress.currentWeekQuizzes >= weeklyProgress.previousWeekQuizzes,
                    modifier = Modifier.weight(1f)
                )
            }

            // Daily Activity Bar Chart
            Text(
                text = "Daily Study Activity (Hours)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            val maxHours = (weeklyProgress.dailyBreakdown.map { maxOf(it.currentWeekHours, it.previousWeekHours) }.maxOrNull() ?: 4f).coerceAtLeast(3f)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                weeklyProgress.dailyBreakdown.forEach { day ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        // Hour value indicator
                        Text(
                            text = "${day.currentWeekHours}h",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (day.isToday) BrandPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        // Dual Bar (Current Week vs Previous Week)
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(3.dp)
                        ) {
                            // Previous week bar (muted)
                            Box(
                                modifier = Modifier
                                    .width(6.dp)
                                    .height((80 * (day.previousWeekHours / maxHours)).dp.coerceAtLeast(6.dp))
                                    .clip(RoundedCornerShape(topStart = 3.dp, topEnd = 3.dp))
                                    .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                            )

                            // Current week bar (highlighted)
                            Box(
                                modifier = Modifier
                                    .width(10.dp)
                                    .height((80 * (day.currentWeekHours / maxHours)).dp.coerceAtLeast(6.dp))
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(
                                        if (day.isToday) BrandRose else BrandPrimary
                                    )
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = day.shortDay,
                            fontSize = 10.sp,
                            fontWeight = if (day.isToday) FontWeight.Black else FontWeight.Normal,
                            color = if (day.isToday) BrandRose else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            // Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(8.dp).background(BrandPrimary, CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Text("This Week", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.width(16.dp))

                Box(modifier = Modifier.size(8.dp).background(MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Last Week", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(modifier = Modifier.width(16.dp))

                Box(modifier = Modifier.size(8.dp).background(BrandRose, CircleShape))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Today", fontSize = 10.sp, color = BrandRose)
            }
        }
    }
}

@Composable
fun WeeklyStatMiniCard(
    label: String,
    current: String,
    previous: String,
    isPositive: Boolean,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(label, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(current, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(
                text = "vs $previous last wk",
                fontSize = 9.sp,
                color = if (isPositive) BrandEmerald else BrandRose,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 1: SUBJECT & TOPIC PROGRESS SECTION
// -------------------------------------------------------------
@Composable
fun SubjectProgressSection(
    subjects: List<SubjectProgress>,
    onSelectSubject: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("📚", fontSize = 18.sp)
                Text("Subject & Topic Mastery", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Text("${subjects.size} Enrolled", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        subjects.forEach { subj ->
            SubjectProgressCard(subject = subj, onClick = { onSelectSubject(subj.subjectId) })
        }
    }
}

@Composable
fun SubjectProgressCard(
    subject: SubjectProgress,
    onClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = { isExpanded = !isExpanded }
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = BrandPrimary.copy(alpha = 0.12f),
                        modifier = Modifier.size(36.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(subject.icon, fontSize = 18.sp)
                        }
                    }

                    Column {
                        Text(
                            text = subject.subjectName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "${subject.code} • ${subject.topicsCompleted}/${subject.totalTopics} Topics Completed",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "${subject.progressPercent}%",
                        fontWeight = FontWeight.Black,
                        fontSize = 14.sp,
                        color = if (subject.progressPercent >= 70) BrandEmerald else if (subject.progressPercent >= 50) BrandGold else BrandRose
                    )
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (subject.masteryLevel) {
                            "Proficient" -> BrandEmerald.copy(alpha = 0.15f)
                            "Intermediate" -> BrandGold.copy(alpha = 0.15f)
                            else -> BrandRose.copy(alpha = 0.15f)
                        }
                    ) {
                        Text(
                            text = subject.masteryLevel,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (subject.masteryLevel) {
                                "Proficient" -> BrandEmerald
                                "Intermediate" -> BrandGold
                                else -> BrandRose
                            },
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Progress Bar
            LinearProgressIndicator(
                progress = { subject.progressPercent / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(CircleShape),
                color = if (subject.progressPercent >= 70) BrandEmerald else if (subject.progressPercent >= 50) BrandGold else BrandRose,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            // Accuracy & Expansion indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Quiz Average: ${subject.quizAveragePercent}%",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isExpanded) "Hide Topics" else "View Topics (${subject.topicBreakdown.size})",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = BrandPrimary
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = BrandPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Expanded Topic-by-Topic Breakdown
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    subject.topicBreakdown.forEach { topic ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = if (topic.isCompleted) "✅" else "⏳",
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = topic.topicName,
                                    fontSize = 11.sp,
                                    color = if (topic.isCompleted) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            if (topic.accuracyPercent > 0) {
                                Text(
                                    text = "${topic.accuracyPercent}%",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (topic.accuracyPercent >= 80) BrandEmerald else BrandGold
                                )
                            } else {
                                Text(
                                    text = "Not Started",
                                    fontSize = 9.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 2: STRENGTHS SECTION
// -------------------------------------------------------------
@Composable
fun StrengthsSection(strengths: List<LearningStrength>) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("💪", fontSize = 18.sp)
            Text("Identified Strengths", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        strengths.forEach { strength ->
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = BrandEmerald.copy(alpha = 0.3f)
            ) {
                Row(
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Surface(
                        shape = CircleShape,
                        color = BrandEmerald.copy(alpha = 0.15f),
                        modifier = Modifier.size(40.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(strength.icon, fontSize = 20.sp)
                        }
                    }

                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(strength.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BrandEmerald.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = strength.metric,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandEmerald,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(
                            text = strength.description,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 15.sp
                        )

                        Text(
                            text = "Category: ${strength.category}",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = BrandPrimary
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 3: AREAS TO IMPROVE SECTION
// -------------------------------------------------------------
@Composable
fun ImprovementAreasSection(
    areas: List<ImprovementArea>,
    onAction: (ImprovementArea) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("🎯", fontSize = 18.sp)
            Text("Areas to Improve & Recommendations", fontWeight = FontWeight.Bold, fontSize = 15.sp)
        }

        areas.forEach { area ->
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = BrandRose.copy(alpha = 0.35f)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("⚠️", fontSize = 16.sp)
                            Column {
                                Text(area.topic, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(area.subjectName, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BrandRose.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${area.currentAccuracyPercent}% Score",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandRose,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = area.issueDescription,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Recommendation box
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BrandPrimary.copy(alpha = 0.08f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.25f)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("💡", fontSize = 14.sp)
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Recommendation", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = BrandPrimary)
                                Text(area.recommendation, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Button(
                        onClick = { onAction(area) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(38.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                    ) {
                        Text(area.actionLabel, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 6: GOALS SECTION
// -------------------------------------------------------------
@Composable
fun LearningGoalsSection(
    goals: List<LearningGoal>,
    onIncrement: (String) -> Unit,
    onToggle: (String) -> Unit,
    onDelete: (String) -> Unit,
    onAddNew: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("🎯", fontSize = 18.sp)
                Text("Learning Goals", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            TextButton(onClick = onAddNew) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Goal", fontSize = 11.sp)
            }
        }

        if (goals.isEmpty()) {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "No active goals yet. Tap '+ Add Goal' to create your first target!",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(16.dp)
                )
            }
        } else {
            goals.forEach { goal ->
                LearningGoalItemCard(
                    goal = goal,
                    onIncrement = { onIncrement(goal.id) },
                    onToggle = { onToggle(goal.id) },
                    onDelete = { onDelete(goal.id) }
                )
            }
        }
    }
}

@Composable
fun LearningGoalItemCard(
    goal: LearningGoal,
    onIncrement: () -> Unit,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    GlassmorphicCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (goal.isCompleted) BrandEmerald.copy(alpha = 0.4f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Checkbox(
                        checked = goal.isCompleted,
                        onCheckedChange = { onToggle() },
                        colors = CheckboxDefaults.colors(checkedColor = BrandEmerald)
                    )

                    Column {
                        Text(
                            text = goal.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (goal.isCompleted) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${goal.category} • Target: ${goal.targetProgress} ${goal.unit} • ${goal.deadline}",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "Delete Goal",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Progress Bar & Controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                LinearProgressIndicator(
                    progress = { goal.progressPercent / 100f },
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(CircleShape),
                    color = if (goal.isCompleted) BrandEmerald else BrandPrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Text(
                    text = "${goal.currentProgress}/${goal.targetProgress} ${goal.unit}",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (goal.isCompleted) BrandEmerald else MaterialTheme.colorScheme.onSurface
                )

                if (!goal.isCompleted) {
                    FilledTonalButton(
                        onClick = onIncrement,
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("+1 ${goal.unit.take(4)}", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// COMPONENT 4: LEARNING HISTORY TIMELINE SECTION
// -------------------------------------------------------------
@Composable
fun LearningHistoryTimelineSection(
    historyItems: List<LearningHistoryItem>,
    selectedFilter: TimelineActivityType?,
    onFilterChanged: (TimelineActivityType?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text("⏱️", fontSize = 18.sp)
                Text("Learning History Timeline", fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }
            Text("${historyItems.size} Total", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        // Filter chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            item {
                FilterChip(
                    selected = selectedFilter == null,
                    onClick = { onFilterChanged(null) },
                    label = { Text("All", fontSize = 11.sp) }
                )
            }
            items(TimelineActivityType.entries) { type ->
                FilterChip(
                    selected = selectedFilter == type,
                    onClick = { onFilterChanged(if (selectedFilter == type) null else type) },
                    label = { Text("${type.icon} ${type.label.split(" ").first()}", fontSize = 11.sp) }
                )
            }
        }

        val filteredItems = if (selectedFilter != null) {
            historyItems.filter { it.type == selectedFilter }
        } else historyItems

        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            filteredItems.forEachIndexed { index, item ->
                TimelineRowItem(
                    item = item,
                    isLast = index == filteredItems.lastIndex
                )
            }
        }
    }
}

@Composable
fun TimelineRowItem(
    item: LearningHistoryItem,
    isLast: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Vertical Timeline Spine
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(28.dp)
        ) {
            Surface(
                shape = CircleShape,
                color = when (item.type) {
                    TimelineActivityType.QUIZ_RESULT -> BrandGold.copy(alpha = 0.2f)
                    TimelineActivityType.TEACHING_SESSION -> BrandEmerald.copy(alpha = 0.2f)
                    TimelineActivityType.PRACTICE_SESSION -> BrandPrimary.copy(alpha = 0.2f)
                    TimelineActivityType.TOPIC_COMPLETED -> Color(0xFF10B981).copy(alpha = 0.2f)
                    TimelineActivityType.STUDY_ACTIVITY -> Color(0xFF8B5CF6).copy(alpha = 0.2f)
                },
                modifier = Modifier.size(28.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(item.icon, fontSize = 13.sp)
                }
            }

            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                )
            }
        }

        // Content Card
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(bottom = if (isLast) 0.dp else 14.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surface,
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = item.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            item.scorePercent?.let { score ->
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = BrandEmerald.copy(alpha = 0.15f)
                                ) {
                                    Text(
                                        text = "$score%",
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandEmerald,
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                    )
                                }
                            }
                            Text(
                                text = "+${item.xpEarned} XP",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandGold
                            )
                        }
                    }

                    Text(
                        text = item.subtitle,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (item.details.isNotBlank()) {
                        Text(
                            text = item.details,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                            lineHeight = 13.sp
                        )
                    }

                    Text(
                        text = "${item.timestamp} (${item.relativeTime})",
                        fontSize = 9.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    )
                }
            }
        }
    }
}
