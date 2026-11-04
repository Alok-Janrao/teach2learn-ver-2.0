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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Quiz
import com.example.data.model.QuizQuestion
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun PlayAndLearnScreen(
    repository: Teach2LearnRepository,
    onStartQuiz: (String) -> Unit,
    onNavigateToChallenges: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentUser by repository.currentUser.collectAsState()
    val quizzes by repository.quizzes.collectAsState()

    var selectedDifficulty by remember { mutableStateOf("All") }
    var showBattleDialog by remember { mutableStateOf(false) }
    var battleOpponentFound by remember { mutableStateOf(false) }

    val difficulties = listOf("All", "🔥 Extra Hard", "Medium", "Easy")

    val filteredQuizzes = quizzes.filter { q ->
        if (selectedDifficulty == "All") true
        else if (selectedDifficulty == "🔥 Extra Hard") q.difficulty.contains("Hard", ignoreCase = true)
        else q.difficulty.equals(selectedDifficulty, ignoreCase = true)
    }

    if (showBattleDialog) {
        AlertDialog(
            onDismissRequest = { showBattleDialog = false },
            title = { Text("⚔️ 1v1 Peer Subject Battle") },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!battleOpponentFound) {
                        Text("Matching you with an active peer learner in C Programming...", fontSize = 13.sp, textAlign = TextAlign.Center)
                        CircularProgressIndicator(modifier = Modifier.size(36.dp), color = BrandRose)
                        LaunchedEffect(Unit) {
                            kotlinx.coroutines.delay(1200)
                            battleOpponentFound = true
                        }
                    } else {
                        Text("Opponent Found! 🔥", fontWeight = FontWeight.Bold, color = BrandEmerald)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AvatarInitials(name = currentUser.name, size = 44.dp)
                                Text(text = currentUser.name.split(" ").firstOrNull() ?: "", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                            Text(text = "VS", fontSize = 20.sp, fontWeight = FontWeight.Black, color = BrandRose)
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                AvatarInitials(name = "Vikram Aditya", size = 44.dp, backgroundColor = BrandTertiary)
                                Text(text = "Vikram", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text("5 Rapid C Questions. First to score wins +50 XP!", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            confirmButton = {
                if (battleOpponentFound) {
                    Button(
                        onClick = {
                            showBattleDialog = false
                            onStartQuiz("quiz_c_1")
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandRose)
                    ) {
                        Text("Start Battle 🔥")
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showBattleDialog = false }) { Text("Cancel") }
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
                text = "Play & Learn 🎮",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Gamified quizzes, extra hard memory trap questions & 1v1 peer battles",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Streak Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFFE11D48), Color(0xFFF97316), Color(0xFFFBBF24))
                        )
                    )
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "🔥", fontSize = 32.sp)
                        Column {
                            Text(
                                text = "${currentUser.streakDays} Day Learning Streak!",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Solve today's quiz to keep your streak alive & earn +30 XP",
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }

        // 3D Concept Flip Arena
        item {
            SectionHeader(title = "⚡ 3D Concept Flashcard Arena", subtitle = "Interactive 3D card physics • Tap to flip")
            ThreeDeeFlipCard(
                modifier = Modifier.fillMaxWidth(),
                frontContent = {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(Color(0xFF311042), Color(0xFF4A154B), Color(0xFF6B21A8))
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
                                    color = Color(0xFFF472B6).copy(alpha = 0.25f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF472B6).copy(alpha = 0.6f))
                                ) {
                                    Text(
                                        text = "🧩 STRUCT MEMORY PADDING",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFFCE7F3),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Text(
                                    text = "🔄 Tap to Flip in 3D",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFF472B6)
                                )
                            }
                            Text(
                                text = "What is sizeof(struct { char a; int b; char c; }) in 64-bit C?",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "Is it 1 + 4 + 1 = 6 bytes? Or does the compiler add padding bytes to satisfy alignment?",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Text(
                                    text = "Tap to Reveal 3D Breakdown ➔",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFFDE68A)
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
                                    listOf(Color(0xFF0F172A), Color(0xFF1E293B), Color(0xFF334155))
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
                                    color = BrandEmerald.copy(alpha = 0.2f),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, BrandEmerald.copy(alpha = 0.6f))
                                ) {
                                    Text(
                                        text = "✅ 12 BYTES (WITH PADDING)",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandEmerald,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }
                                Text(
                                    text = "Structure Reordered = 8 Bytes!",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandGoldLight
                                )
                            }
                            Text(
                                text = "Explanation: Memory Word Alignment",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "`char a` (1 byte) + 3 pad bytes + `int b` (4 bytes) + `char c` (1 byte) + 3 trailing pad bytes = 12 bytes total. Reordering fields to `int b; char a; char c;` shrinks it to 8 bytes!",
                                fontSize = 11.sp,
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

        // Game Modes Grid
        item {
            SectionHeader(title = "Featured Challenges")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GameModeCard(
                    title = "🔥 Extra Hard Traps",
                    subtitle = "Pointers & Memory Traps",
                    icon = "🧠",
                    badge = "+60 XP",
                    color = BrandRose,
                    modifier = Modifier.weight(1f),
                    onClick = { onStartQuiz("quiz_c_extrahard") }
                )
                GameModeCard(
                    title = "1v1 Peer Battle",
                    subtitle = "Real-time match",
                    icon = "⚔️",
                    badge = "+50 XP",
                    color = BrandPrimary,
                    modifier = Modifier.weight(1f),
                    onClick = {
                        battleOpponentFound = false
                        showBattleDialog = true
                    }
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                GameModeCard(
                    title = "Campus Cup",
                    subtitle = "College leaderboards",
                    icon = "🏆",
                    badge = "Prize Pool",
                    color = BrandEmerald,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToChallenges
                )
                GameModeCard(
                    title = "Code Puzzles",
                    subtitle = "Bug bounty challenges",
                    icon = "🧩",
                    badge = "+25 XP",
                    color = BrandGold,
                    modifier = Modifier.weight(1f),
                    onClick = { onStartQuiz("quiz_py_1") }
                )
            }
        }

        // Difficulty Filter Row
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(difficulties) { diff ->
                    val isSelected = selectedDifficulty == diff
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) {
                            if (diff == "🔥 Extra Hard") BrandRose.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer
                        } else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) {
                                if (diff == "🔥 Extra Hard") BrandRose else MaterialTheme.colorScheme.primary
                            } else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.clickable { selectedDifficulty = diff }
                    ) {
                        Text(
                            text = diff,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) {
                                if (diff == "🔥 Extra Hard") BrandRose else MaterialTheme.colorScheme.primary
                            } else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Subject Quizzes List
        item {
            SectionHeader(
                title = "Subject Quizzes (${filteredQuizzes.size})",
                subtitle = "Includes extra hard trap questions with hints & full proof steps"
            )
        }

        items(filteredQuizzes) { quiz ->
            val isExtraHard = quiz.difficulty.contains("Hard", ignoreCase = true)

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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (isExtraHard) BrandRose.copy(alpha = 0.2f) else MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = quiz.difficulty.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isExtraHard) BrandRose else MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Text(
                                text = "⏱️ ${quiz.timeLimitSeconds}s",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = quiz.title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "${quiz.questions.size} Questions • ${quiz.topic}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { onStartQuiz(quiz.id) },
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isExtraHard) BrandRose else BrandEmerald
                        )
                    ) {
                        Text("Play (+${quiz.xpReward} XP)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun GameModeCard(
    title: String,
    subtitle: String,
    icon: String,
    badge: String,
    color: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Interactive3DCard(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        highlightColor = color,
        backgroundColor = MaterialTheme.colorScheme.surface,
        borderColor = color.copy(alpha = 0.35f),
        elevation = 5.dp,
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = icon, fontSize = 28.sp)
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = color.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = color,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = subtitle, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun QuizScreen(
    quizId: String,
    repository: Teach2LearnRepository,
    onFinishQuiz: () -> Unit,
    modifier: Modifier = Modifier
) {
    val quizzes by repository.quizzes.collectAsState()
    val quiz = quizzes.firstOrNull { it.id == quizId } ?: quizzes.first()

    var currentQuestionIndex by remember { mutableStateOf(0) }
    var selectedOptionIndex by remember { mutableStateOf<Int?>(null) }
    var isSubmitted by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var isCompleted by remember { mutableStateOf(false) }
    var showHint by remember { mutableStateOf(false) }

    val question = quiz.questions.getOrNull(currentQuestionIndex) ?: quiz.questions.first()

    if (isCompleted) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = if (score == quiz.questions.size) "🏆" else "🎉", fontSize = 54.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = if (score == quiz.questions.size) "Grandmaster Performance!" else "Quiz Completed!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "You scored $score out of ${quiz.questions.size}!",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = BrandPrimary
            )
            Spacer(modifier = Modifier.height(14.dp))
            XpPill(xp = quiz.xpReward, showFire = true, fontSize = 16)
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    repository.completeQuiz(quiz.id, if (quiz.questions.isNotEmpty()) (score * 100) / quiz.questions.size else 100)
                    onFinishQuiz()
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Collect +${quiz.xpReward} XP & Return", fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    } else {
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = onFinishQuiz) {
                    Icon(Icons.Default.Close, contentDescription = "Exit")
                }
                Text(
                    text = "Question ${currentQuestionIndex + 1} / ${quiz.questions.size}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = BrandGold.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "+${quiz.xpReward} XP",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandGold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { (currentQuestionIndex + 1f) / quiz.questions.size },
                modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                color = if (question.isTrapQuestion) BrandRose else BrandPrimary
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Question Card
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (question.isTrapQuestion) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BrandRose.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "⚠️ EXTRA HARD TRAP QUESTION",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BrandRose,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        } else {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = quiz.difficulty.uppercase(),
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        // Hint Toggle Button
                        if (question.hint.isNotBlank() && !isSubmitted) {
                            TextButton(
                                onClick = { showHint = !showHint },
                                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Lightbulb, contentDescription = null, tint = BrandGold, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (showHint) "Hide Hint" else "💡 Hint", fontSize = 11.sp, color = BrandGold, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    // Hint Box
                    if (showHint && question.hint.isNotBlank() && !isSubmitted) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = BrandGold.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(1.dp, BrandGold.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "💡 ${question.hint}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }

                    Text(
                        text = question.question,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    if (question.codeBlock.isNotBlank()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFF0F172A))
                                .padding(12.dp)
                        ) {
                            Text(
                                text = question.codeBlock,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = Color(0xFF38BDF8)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Options List
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                question.options.forEachIndexed { index, option ->
                    val isSelected = selectedOptionIndex == index
                    val isCorrect = index == question.correctOptionIndex

                    val cardColor = when {
                        isSubmitted && isCorrect -> BrandEmerald.copy(alpha = 0.2f)
                        isSubmitted && isSelected && !isCorrect -> MaterialTheme.colorScheme.errorContainer
                        isSelected -> MaterialTheme.colorScheme.primaryContainer
                        else -> MaterialTheme.colorScheme.surface
                    }

                    val borderColor = when {
                        isSubmitted && isCorrect -> BrandEmerald
                        isSubmitted && isSelected && !isCorrect -> MaterialTheme.colorScheme.error
                        isSelected -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    }

                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .border(1.dp, borderColor, RoundedCornerShape(14.dp))
                            .clickable(enabled = !isSubmitted) {
                                selectedOptionIndex = index
                            },
                        color = cardColor
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(borderColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${('A' + index)}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                            Text(
                                text = option.text,
                                fontSize = 13.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }

            // Explanation Reveal
            if (isSubmitted) {
                Spacer(modifier = Modifier.height(12.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(text = "💡 Comprehensive Explanation:", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandPrimary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = question.explanation, fontSize = 12.sp, lineHeight = 17.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action Button
            if (!isSubmitted) {
                Button(
                    onClick = {
                        if (selectedOptionIndex != null) {
                            isSubmitted = true
                            if (selectedOptionIndex == question.correctOptionIndex) {
                                score++
                            }
                        }
                    },
                    enabled = selectedOptionIndex != null,
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (question.isTrapQuestion) BrandRose else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Check Answer", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            } else {
                Button(
                    onClick = {
                        if (currentQuestionIndex + 1 < quiz.questions.size) {
                            currentQuestionIndex++
                            selectedOptionIndex = null
                            isSubmitted = false
                            showHint = false
                        } else {
                            isCompleted = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(50.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                ) {
                    Text(
                        text = if (currentQuestionIndex + 1 < quiz.questions.size) "Next Question →" else "See Final Results 🎉",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
