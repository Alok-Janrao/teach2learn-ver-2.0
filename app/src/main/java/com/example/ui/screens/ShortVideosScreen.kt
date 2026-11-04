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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ShortVideo
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.AvatarInitials
import com.example.ui.components.XpPill
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortVideosScreen(
    repository: Teach2LearnRepository,
    modifier: Modifier = Modifier
) {
    val videos by repository.shortVideos.collectAsState()
    var shareToast by remember { mutableStateOf<String?>(null) }
    var showCommentSheetForVideo by remember { mutableStateOf<ShortVideo?>(null) }
    var commentText by remember { mutableStateOf("") }
    var showUploadModal by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "🔥 Trending", "Programming", "Mathematics", "DSA", "Career")

    val filteredVideos = videos.filter { v ->
        if (selectedCategory == "All" || selectedCategory == "🔥 Trending") true
        else v.category.equals(selectedCategory, ignoreCase = true)
    }

    // Comment Sheet Dialog
    if (showCommentSheetForVideo != null) {
        val v = showCommentSheetForVideo!!
        AlertDialog(
            onDismissRequest = { showCommentSheetForVideo = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Comments (${v.commentsCount})", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    XpPill(xp = 5, fontSize = 11)
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (v.comments.isEmpty()) {
                        Text(
                            text = "No comments yet. Be the first peer to join the discussion!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    v.comments.forEach { c ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = c.author, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandPrimary)
                                    Text(text = c.timestamp, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(text = c.text, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = { Text("Write a comment (+5 XP)...", fontSize = 12.sp) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (commentText.isNotBlank()) {
                            repository.addVideoComment(v.id, commentText)
                            commentText = ""
                            showCommentSheetForVideo = null
                            shareToast = "Comment posted! Earned +5 XP"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Post")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCommentSheetForVideo = null }) { Text("Close") }
            }
        )
    }

    // Upload 60s Micro-Reel Modal
    if (showUploadModal) {
        var uploadTitle by remember { mutableStateOf("") }
        var uploadCat by remember { mutableStateOf("Programming") }
        var uploadSummary by remember { mutableStateOf("") }
        var uploadConcept by remember { mutableStateOf("") }
        var uploadQuizQ by remember { mutableStateOf("") }
        var uploadQuizAns by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showUploadModal = false },
            modifier = Modifier.fillMaxWidth(0.95f),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("⚡ Upload 60s Micro-Reel", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    XpPill(xp = 50, fontSize = 11)
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        OutlinedTextField(
                            value = uploadTitle,
                            onValueChange = { uploadTitle = it },
                            label = { Text("Reel Title") },
                            placeholder = { Text("e.g. 60s Trick to Understand Pointer Dereferencing") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    item {
                        Text("Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(listOf("Programming", "Mathematics", "DSA", "Career")) { cat ->
                                FilterChip(
                                    selected = uploadCat == cat,
                                    onClick = { uploadCat = cat },
                                    label = { Text(cat, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = uploadConcept,
                            onValueChange = { uploadConcept = it },
                            label = { Text("Key Formula or Code Snippet") },
                            placeholder = { Text("*(p + i) == p[i]") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uploadSummary,
                            onValueChange = { uploadSummary = it },
                            label = { Text("Micro Explanation Summary") },
                            placeholder = { Text("Explain the concept in 2-3 snappy sentences...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            minLines = 2
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uploadQuizQ,
                            onValueChange = { uploadQuizQ = it },
                            label = { Text("10s Micro-Quiz Question (Optional)") },
                            placeholder = { Text("What is the time complexity of binary search?") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uploadQuizAns,
                            onValueChange = { uploadQuizAns = it },
                            label = { Text("Correct Answer / Solution") },
                            placeholder = { Text("O(log N)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (uploadTitle.isNotBlank() && uploadSummary.isNotBlank()) {
                            repository.uploadShortVideo(
                                title = uploadTitle,
                                category = uploadCat,
                                summary = uploadSummary,
                                keyConcept = uploadConcept.ifBlank { "Core Concept" },
                                quizQuestion = uploadQuizQ,
                                quizOptions = if (uploadQuizAns.isNotBlank()) listOf(uploadQuizAns, "O(N)", "O(1)", "O(N^2)") else emptyList(),
                                quizCorrectIndex = 0,
                                quizExplanation = "Verified explanation by peer educator."
                            )
                            showUploadModal = false
                            shareToast = "Micro-learning reel published! +50 XP awarded 🎉"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Publish Reel (+50 XP)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadModal = false }) { Text("Cancel") }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Micro-Learning Reels ⚡",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "Bite-sized 60-second peer explanations on tricky engineering topics",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showUploadModal = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.AddCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Post 60s", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Category Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(categories) { cat ->
                    val isSelected = selectedCategory == cat
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                        ),
                        modifier = Modifier.clickable { selectedCategory = cat }
                    ) {
                        Text(
                            text = cat,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }
                }
            }
        }

        // Toast Feedback
        shareToast?.let { msg ->
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
                        IconButton(onClick = { shareToast = null }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }
        }

        // Video Reel Items
        items(filteredVideos) { video ->
            var isPlaying by remember { mutableStateOf(false) }
            var showMicroQuiz by remember { mutableStateOf(false) }
            var selectedQuizOption by remember { mutableStateOf<Int?>(null) }
            var quizAnswered by remember { mutableStateOf(false) }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF0F172A), Color(0xFF1E1B4B), Color(0xFF020617))
                        )
                    )
                    .border(1.dp, Color(0xFF334155), RoundedCornerShape(20.dp))
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Top Bar
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
                                shape = RoundedCornerShape(8.dp),
                                color = BrandPrimary.copy(alpha = 0.4f)
                            ) {
                                Text(
                                    text = video.category,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF93C5FD),
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color.White.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "⚡ 60s Reel",
                                    fontSize = 10.sp,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }

                        // Follow Creator Button
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = if (video.isFollowing) Color.White.copy(alpha = 0.2f) else BrandPrimary,
                            modifier = Modifier.clickable {
                                repository.toggleFollowCreator(video.id)
                                shareToast = if (!video.isFollowing) "Followed ${video.creatorName}!" else "Unfollowed ${video.creatorName}"
                            }
                        ) {
                            Text(
                                text = if (video.isFollowing) "✓ Following" else "+ Follow",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Video Play Center & Concept Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color(0xFF030712).copy(alpha = 0.6f))
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(14.dp))
                            .clickable { isPlaying = !isPlaying },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                            modifier = Modifier.padding(12.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(if (isPlaying) BrandEmerald else Color.White.copy(alpha = 0.25f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            if (video.keyConcept.isNotBlank()) {
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = Color(0xFF0F172A),
                                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155))
                                ) {
                                    Text(
                                        text = video.keyConcept,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF38BDF8),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Interactive Micro-Quiz Flashcard Toggle
                    if (video.quickQuizQuestion.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = Color(0xFF1E1B4B),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4338CA)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("🧠 10s Micro-Quiz Check", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFFA5B4FC))
                                    TextButton(
                                        onClick = { showMicroQuiz = !showMicroQuiz },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text(if (showMicroQuiz) "Hide ▲" else "Test Concept (+10 XP) ▼", fontSize = 10.sp, color = BrandGold)
                                    }
                                }

                                if (showMicroQuiz) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(text = video.quickQuizQuestion, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Medium)
                                    Spacer(modifier = Modifier.height(6.dp))

                                    video.quickQuizOptions.take(2).forEachIndexed { idx, opt ->
                                        val isSelected = selectedQuizOption == idx
                                        val isCorrect = idx == video.quickQuizCorrectIndex
                                        val optColor = when {
                                            quizAnswered && isCorrect -> BrandEmerald.copy(alpha = 0.3f)
                                            quizAnswered && isSelected && !isCorrect -> BrandRose.copy(alpha = 0.3f)
                                            isSelected -> BrandPrimary.copy(alpha = 0.4f)
                                            else -> Color(0xFF0F172A)
                                        }

                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = optColor,
                                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF334155)),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 2.dp)
                                                .clickable(enabled = !quizAnswered) {
                                                    selectedQuizOption = idx
                                                    quizAnswered = true
                                                    if (isCorrect) {
                                                        repository.addXp(10, "Solved micro-quiz on reel (+10 XP)")
                                                        shareToast = "Correct! +10 XP awarded 🎉"
                                                    }
                                                }
                                        ) {
                                            Text(
                                                text = opt,
                                                fontSize = 10.sp,
                                                color = Color.White,
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Reel Info and Interaction Controls
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                AvatarInitials(name = video.creatorName, size = 24.dp)
                                Text(
                                    text = video.creatorName,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                                Text(
                                    text = "• ${video.creatorRank.title}",
                                    fontSize = 10.sp,
                                    color = Color(0xFF94A3B8)
                                )
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = video.title,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = Color.White
                            )

                            Text(
                                text = video.summary,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.8f),
                                maxLines = 2
                            )
                        }

                        // Right Interaction Column
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            IconButton(
                                onClick = { repository.toggleLikeVideo(video.id) },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (video.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                    contentDescription = "Like",
                                    tint = if (video.isLiked) BrandRose else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = { showCommentSheetForVideo = video },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    Icons.Default.Comment,
                                    contentDescription = "Comments",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            IconButton(
                                onClick = {
                                    repository.toggleSaveVideo(video.id)
                                    shareToast = if (!video.isSaved) "Saved reel to collection!" else "Removed from saved"
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = if (video.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                    contentDescription = "Save",
                                    tint = if (video.isSaved) BrandGold else Color.White,
                                    modifier = Modifier.size(20.dp)
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
