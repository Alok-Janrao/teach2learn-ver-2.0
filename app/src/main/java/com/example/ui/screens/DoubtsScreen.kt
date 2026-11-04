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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Doubt
import com.example.data.model.DoubtAnswer
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun DoubtsScreen(
    repository: Teach2LearnRepository,
    modifier: Modifier = Modifier
) {
    val doubts by repository.doubts.collectAsState()
    val subjects by repository.subjects.collectAsState()
    val currentUser by repository.currentUser.collectAsState()

    var selectedSubject by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var filterOnlyUnsolved by remember { mutableStateOf(false) }
    var filterOnlyMyDoubts by remember { mutableStateOf(false) }

    var showAskDoubtDialog by remember { mutableStateOf(false) }
    var showDoubtDetail by remember { mutableStateOf<Doubt?>(null) }
    var answerInputText by remember { mutableStateOf("") }
    var answerCodeText by remember { mutableStateOf("") }
    var showAnswerSheet by remember { mutableStateOf(false) }
    var doubtToastMessage by remember { mutableStateOf<String?>(null) }

    // Filter Logic
    val filteredDoubts = remember(doubts, selectedSubject, searchQuery, filterOnlyUnsolved, filterOnlyMyDoubts) {
        doubts.filter { doubt ->
            val matchSubject = selectedSubject == null || doubt.subject.equals(selectedSubject, ignoreCase = true)
            val matchQuery = searchQuery.isBlank() || doubt.question.contains(searchQuery, ignoreCase = true) || doubt.topic.contains(searchQuery, ignoreCase = true)
            val matchUnsolved = !filterOnlyUnsolved || !doubt.isSolved
            val matchMyDoubts = !filterOnlyMyDoubts || doubt.studentName == currentUser.name
            matchSubject && matchQuery && matchUnsolved && matchMyDoubts
        }
    }

    // Modal: Ask a Doubt
    if (showAskDoubtDialog) {
        var subjectChoice by remember { mutableStateOf(subjects.firstOrNull()?.name ?: "C Programming") }
        var topicInput by remember { mutableStateOf("") }
        var questionInput by remember { mutableStateOf("") }
        var codeSnippetInput by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAskDoubtDialog = false },
            title = {
                Text(
                    text = "Ask a Question 💡",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "Your doubt will be shared with top peer mentors in this subject. Earn XP when it is resolved!",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Subject Selector
                    Text(text = "Subject:", fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(subjects) { subj ->
                            val isSel = subjectChoice == subj.name
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (isSel) BrandPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                modifier = Modifier.clickable { subjectChoice = subj.name }
                            ) {
                                Text(
                                    text = "${subj.icon} ${subj.name}",
                                    fontSize = 11.sp,
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = topicInput,
                        onValueChange = { topicInput = it },
                        label = { Text("Topic / Chapter (e.g., Pointer Arithmetic)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = questionInput,
                        onValueChange = { questionInput = it },
                        label = { Text("Describe your doubt...") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 3
                    )

                    OutlinedTextField(
                        value = codeSnippetInput,
                        onValueChange = { codeSnippetInput = it },
                        label = { Text("Code Snippet (Optional)") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 2,
                        placeholder = { Text("Paste code with error here...", fontSize = 12.sp) }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (questionInput.isNotBlank() && topicInput.isNotBlank()) {
                            repository.postDoubt(
                                subject = subjectChoice,
                                topic = topicInput,
                                question = questionInput,
                                code = codeSnippetInput
                            )
                            showAskDoubtDialog = false
                            doubtToastMessage = "Doubt posted! Peer mentors have been notified."
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Post Doubt (+5 XP)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAskDoubtDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal: Detailed Doubt View & Answer flow
    if (showDoubtDetail != null) {
        val currentDoubt = doubts.firstOrNull { it.id == showDoubtDetail?.id } ?: showDoubtDetail!!

        AlertDialog(
            onDismissRequest = {
                showDoubtDetail = null
                showAnswerSheet = false
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "${currentDoubt.subject} • ${currentDoubt.topic}",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Asked by ${currentDoubt.studentName}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    if (currentDoubt.isSolved) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BrandEmerald.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "SOLVED ✓",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandEmerald,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Text(
                            text = currentDoubt.question,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        if (currentDoubt.codeSnippet.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F172A),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = currentDoubt.codeSnippet,
                                    color = Color(0xFF38BDF8),
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    modifier = Modifier.padding(10.dp)
                                )
                            }
                        }
                    }

                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Answers (${currentDoubt.answers.size})",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )

                            TextButton(onClick = { showAnswerSheet = !showAnswerSheet }) {
                                Text(if (showAnswerSheet) "Cancel Answer" else "+ Add Answer (+15 XP)")
                            }
                        }
                    }

                    if (showAnswerSheet) {
                        item {
                            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                                Text(text = "Your Explanation & Code", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                OutlinedTextField(
                                    value = answerInputText,
                                    onValueChange = { answerInputText = it },
                                    placeholder = { Text("Write clear explanation for peer...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    minLines = 2
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = answerCodeText,
                                    onValueChange = { answerCodeText = it },
                                    placeholder = { Text("Working code fix (Optional)...") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    minLines = 2
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = {
                                        if (answerInputText.isNotBlank()) {
                                            repository.answerDoubt(
                                                doubtId = currentDoubt.id,
                                                answerText = answerInputText,
                                                codeSnippet = answerCodeText
                                            )
                                            answerInputText = ""
                                            answerCodeText = ""
                                            showAnswerSheet = false
                                            doubtToastMessage = "Answer posted! +15 XP awarded."
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text("Submit Answer")
                                }
                            }
                        }
                    }

                    if (currentDoubt.answers.isEmpty()) {
                        item {
                            Text(
                                text = "No answers yet. Be the first peer to answer and earn +15 XP!",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    } else {
                        items(currentDoubt.answers) { ans ->
                            AnswerCard(
                                answer = ans,
                                isDoubtAuthor = currentDoubt.studentName == currentUser.name,
                                onMarkBest = {
                                    repository.markBestAnswer(currentDoubt.id, ans.id)
                                    doubtToastMessage = "Marked as Best Answer! Author received +30 XP."
                                }
                            )
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    showDoubtDetail = null
                    showAnswerSheet = false
                }) {
                    Text("Close")
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
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Peer Doubts Forum 💡",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Ask questions, solve doubts, earn +15 XP per answer",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAskDoubtDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    modifier = Modifier.testTag("ask_doubt_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Ask", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Notification Banner if any
        doubtToastMessage?.let { msg ->
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
                        IconButton(onClick = { doubtToastMessage = null }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(16.dp))
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
                placeholder = { Text("Search doubts by topic, error, or concept...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = if (searchQuery.isNotEmpty()) {
                    { IconButton(onClick = { searchQuery = "" }) { Icon(Icons.Default.Clear, contentDescription = null) } }
                } else null,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        // Filter Subject Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedSubject == null,
                        onClick = { selectedSubject = null },
                        label = { Text("All Subjects") }
                    )
                }
                items(subjects) { subj ->
                    FilterChip(
                        selected = selectedSubject == subj.name,
                        onClick = { selectedSubject = if (selectedSubject == subj.name) null else subj.name },
                        label = { Text("${subj.icon} ${subj.name}") }
                    )
                }
            }
        }

        // Quick Toggles (Unsolved / My Doubts)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = filterOnlyUnsolved,
                    onClick = { filterOnlyUnsolved = !filterOnlyUnsolved },
                    label = { Text("❓ Unsolved Only") }
                )
                FilterChip(
                    selected = filterOnlyMyDoubts,
                    onClick = { filterOnlyMyDoubts = !filterOnlyMyDoubts },
                    label = { Text("🙋 My Doubts") }
                )
            }
        }

        // Doubt List
        if (filteredDoubts.isEmpty()) {
            item {
                EmptyStateView(
                    icon = "💡",
                    title = "No Doubts Found",
                    description = "No doubts match your current filter. Be the first to ask a doubt or explore another subject!"
                )
            }
        } else {
            items(filteredDoubts) { doubt ->
                DoubtFeedCard(
                    doubt = doubt,
                    onOpenDetail = { showDoubtDetail = doubt },
                    onLikeToggle = { repository.toggleLikeDoubt(doubt.id) }
                )
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun DoubtFeedCard(
    doubt: Doubt,
    onOpenDetail: () -> Unit,
    onLikeToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    GlassmorphicCard(
        modifier = modifier.fillMaxWidth(),
        onClick = onOpenDetail
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AvatarInitials(name = doubt.studentName, size = 30.dp)
                Column {
                    Text(
                        text = doubt.studentName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "${doubt.subject} • ${doubt.topic}",
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (doubt.isSolved) BrandEmerald.copy(alpha = 0.15f) else BrandRose.copy(alpha = 0.15f)
            ) {
                Text(
                    text = if (doubt.isSolved) "Solved ✓" else "Open",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (doubt.isSolved) BrandEmerald else BrandRose,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = doubt.question,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onBackground,
            maxLines = 2
        )

        if (doubt.codeSnippet.isNotBlank()) {
            Spacer(modifier = Modifier.height(6.dp))
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = Color(0xFF0F172A),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = doubt.codeSnippet,
                    color = Color(0xFF38BDF8),
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    maxLines = 2,
                    modifier = Modifier.padding(6.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLikeToggle() }
                ) {
                    Icon(
                        imageVector = if (doubt.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Likes",
                        tint = if (doubt.isLiked) BrandRose else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${doubt.likes}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Comment,
                        contentDescription = "Answers",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${doubt.answers.size} Answers",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Text(
                text = doubt.createdAt,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun AnswerCard(
    answer: DoubtAnswer,
    isDoubtAuthor: Boolean,
    onMarkBest: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .border(
                1.dp,
                if (answer.isBestAnswer) BrandEmerald else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(12.dp)
            ),
        color = if (answer.isBestAnswer) BrandEmerald.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    AvatarInitials(name = answer.authorName, size = 26.dp)
                    Column {
                        Text(
                            text = answer.authorName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        RankBadge(rank = answer.authorRank)
                    }
                }

                if (answer.isBestAnswer) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = BrandEmerald
                    ) {
                        Text(
                            text = "👑 BEST ANSWER",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                } else if (isDoubtAuthor) {
                    TextButton(onClick = onMarkBest) {
                        Text("Mark Best 👑", fontSize = 10.sp, color = BrandGold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = answer.text,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground
            )

            if (answer.codeSnippet.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = Color(0xFF0F172A),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = answer.codeSnippet,
                        color = Color(0xFF34D399),
                        fontFamily = FontFamily.Monospace,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "👍 ${answer.upvotes} Upvotes",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = answer.answeredAt,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
