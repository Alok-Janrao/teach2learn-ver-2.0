package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.FirestoreShortVideo
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.AvatarInitials
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShortLearningScreen(
    repository: Teach2LearnRepository,
    onNavigateToCreateShort: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val videos by repository.firestoreShortVideos.collectAsState()
    val context = LocalContext.current
    val listState = rememberLazyListState()

    var selectedSubject by remember { mutableStateOf("All") }
    var shareSnackbar by remember { mutableStateOf<String?>(null) }
    var isMutedAll by remember { mutableStateOf(false) }

    val subjects = listOf("All", "C Programming", "Python", "Data Structures", "Engineering Mathematics", "AI & Data Science")

    val filteredVideos = remember(videos, selectedSubject) {
        if (selectedSubject == "All") videos.filter { it.status != "removed" }
        else videos.filter { it.status != "removed" && it.subject.equals(selectedSubject, ignoreCase = true) }
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
                            color = BrandCyan.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "⚡",
                                fontSize = 16.sp,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "SHORT LEARNING",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Bite-Sized Engineering Concepts",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("short_learning_back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = { isMutedAll = !isMutedAll },
                        modifier = Modifier.testTag("mute_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (isMutedAll) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                            contentDescription = if (isMutedAll) "Unmute" else "Mute",
                            tint = if (isMutedAll) BrandRose else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    FilledTonalButton(
                        onClick = onNavigateToCreateShort,
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = BrandPrimary.copy(alpha = 0.2f),
                            contentColor = BrandPrimary
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("create_short_top_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Create", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onNavigateToCreateShort,
                containerColor = BrandCyan,
                contentColor = Color.Black,
                icon = { Icon(Icons.Default.VideoCall, contentDescription = "Create Short") },
                text = { Text("Create Short", fontWeight = FontWeight.Bold) },
                modifier = Modifier.testTag("create_short_fab")
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Subject Filter Bar
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(subjects) { subject ->
                    val isSelected = selectedSubject == subject
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedSubject = subject },
                        label = { Text(subject, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = BrandPrimary,
                            selectedLabelColor = Color.White
                        ),
                        modifier = Modifier.testTag("filter_chip_$subject")
                    )
                }
            }

            if (filteredVideos.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(text = "🎬", fontSize = 48.sp)
                        Text(
                            text = "No Shorts in $selectedSubject yet",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Be the first student-teacher to upload a 60-second explanation!",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = onNavigateToCreateShort,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Create Short Video")
                        }
                    }
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredVideos, key = { it.videoId }) { video ->
                        ShortVideoFeedCard(
                            video = video,
                            isGlobalMuted = isMutedAll,
                            onLike = { repository.toggleShortVideoLike(video.videoId) },
                            onSave = { repository.toggleShortVideoSave(video.videoId) },
                            onShare = {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, "Watch '${video.title}' by ${video.teacherName} on TEACH2LEARN:\n${video.videoUrl}")
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Short Learning Video")
                                context.startActivity(shareIntent)
                            },
                            onView = { repository.incrementShortVideoViews(video.videoId) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShortVideoFeedCard(
    video: FirestoreShortVideo,
    isGlobalMuted: Boolean,
    onLike: () -> Unit,
    onSave: () -> Unit,
    onShare: () -> Unit,
    onView: () -> Unit
) {
    var isPlaying by remember { mutableStateOf(false) }
    var isExpandedDescription by remember { mutableStateOf(false) }
    val context = LocalContext.current

    LaunchedEffect(video.videoId) {
        onView()
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .border(
                1.dp,
                Brush.linearGradient(listOf(BrandPrimary.copy(alpha = 0.4f), Color.Transparent)),
                RoundedCornerShape(20.dp)
            )
            .testTag("short_video_card_${video.videoId}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column {
            // Video Player Container (Vertical 16:9 or 9:16 optimized)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(Color.Black)
            ) {
                // Interactive Video Player using AndroidView & VideoView
                AndroidView(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { isPlaying = !isPlaying },
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            setVideoURI(Uri.parse(video.videoUrl))
                            val mc = MediaController(ctx)
                            mc.setAnchorView(this)
                            setMediaController(null) // Custom modern Compose overlay controls
                            setOnPreparedListener { mp ->
                                mp.isLooping = true
                                if (isGlobalMuted) {
                                    mp.setVolume(0f, 0f)
                                } else {
                                    mp.setVolume(1f, 1f)
                                }
                                mp.start()
                                isPlaying = true
                            }
                            setOnErrorListener { _, _, _ ->
                                true // Handled gracefully
                            }
                        }
                    },
                    update = { videoView ->
                        if (isPlaying) {
                            if (!videoView.isPlaying) videoView.start()
                        } else {
                            if (videoView.isPlaying) videoView.pause()
                        }
                    }
                )

                // Top Badge: Subject & Duration
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(video.thumbnailUrl, fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = video.subject,
                                color = BrandCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Surface(
                        color = Color.Black.copy(alpha = 0.7f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "${video.durationSeconds}s",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                // Play / Pause Indicator
                if (!isPlaying) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.35f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BrandPrimary.copy(alpha = 0.85f),
                            modifier = Modifier.size(56.dp)
                        ) {
                            Icon(
                                Icons.Default.PlayArrow,
                                contentDescription = "Play",
                                tint = Color.White,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .fillMaxSize()
                            )
                        }
                    }
                }

                // Right Floating Social Action Column
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 12.dp, bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Like Button
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        IconButton(
                            onClick = onLike,
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                                .size(40.dp)
                                .testTag("like_button_${video.videoId}")
                        ) {
                            Icon(
                                imageVector = if (video.isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (video.isLiked) BrandRose else Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "${video.likes}",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }

                    // Save / Bookmark Button
                    IconButton(
                        onClick = onSave,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .size(40.dp)
                            .testTag("save_button_${video.videoId}")
                    ) {
                        Icon(
                            imageVector = if (video.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Save",
                            tint = if (video.isSaved) BrandGold else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Share Button
                    IconButton(
                        onClick = onShare,
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape)
                            .size(40.dp)
                            .testTag("share_button_${video.videoId}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Video Meta & Description
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Text(
                    text = video.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Teacher Info & View Count Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        AvatarInitials(name = video.teacherName, size = 24.dp)
                        Text(
                            text = video.teacherName,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(
                            text = "${video.views} views",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (video.description.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = video.description,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = if (isExpandedDescription) 10 else 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable { isExpandedDescription = !isExpandedDescription }
                    )
                }
            }
        }
    }
}
