package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FirestoreLiveClass
import com.example.data.model.FirestoreShortVideo
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.AvatarInitials
import com.example.ui.theme.*

@Composable
fun ControlCenterShortVideosTab(
    repository: Teach2LearnRepository,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val videos by repository.firestoreShortVideos.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("All") }
    var videoToRemove by remember { mutableStateOf<FirestoreShortVideo?>(null) }

    val subjects = listOf("All", "C Programming", "Python", "Data Structures", "Engineering Mathematics", "AI & Data Science")

    val filtered = videos.filter { video ->
        val matchesSubject = selectedSubject == "All" || video.subject.equals(selectedSubject, ignoreCase = true)
        val matchesSearch = searchQuery.isBlank() ||
                video.title.contains(searchQuery, ignoreCase = true) ||
                video.teacherName.contains(searchQuery, ignoreCase = true) ||
                video.description.contains(searchQuery, ignoreCase = true)
        matchesSubject && matchesSearch
    }

    if (videoToRemove != null) {
        AlertDialog(
            onDismissRequest = { videoToRemove = null },
            title = { Text("Remove Inappropriate Video?", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove '${videoToRemove!!.title}'? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        val title = videoToRemove!!.title
                        repository.removeShortVideo(videoToRemove!!.videoId)
                        videoToRemove = null
                        onShowToast("Removed video: $title")
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandRose)
                ) {
                    Text("Remove Video")
                }
            },
            dismissButton = {
                TextButton(onClick = { videoToRemove = null }) { Text("Cancel") }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Short Videos Moderation (${videos.size} Uploaded)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search by title, teacher, or keyword...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(subjects) { subj ->
                            FilterChip(
                                selected = selectedSubject == subj,
                                onClick = { selectedSubject = subj },
                                label = { Text(subj, fontSize = 11.sp) }
                            )
                        }
                    }
                }
            }
        }

        if (filtered.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No short videos matched your search/filter.",
                        modifier = Modifier.padding(24.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(filtered, key = { it.videoId }) { video ->
                AdminShortVideoCard(
                    video = video,
                    onFlag = {
                        repository.flagShortVideo(video.videoId)
                        onShowToast("Flagged video for review")
                    },
                    onRemove = { videoToRemove = video }
                )
            }
        }
    }
}

@Composable
fun AdminShortVideoCard(
    video: FirestoreShortVideo,
    onFlag: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = BrandCyan.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "${video.thumbnailUrl} ${video.subject}",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandCyan,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Surface(
                    color = if (video.status == "active") BrandEmerald.copy(alpha = 0.2f) else BrandRose.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = video.status.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (video.status == "active") BrandEmerald else BrandRose,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Text(
                text = video.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            if (video.description.isNotBlank()) {
                Text(
                    text = video.description,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    AvatarInitials(name = video.teacherName, size = 20.dp)
                    Text(video.teacherName, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    Text("•", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${video.views} views", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("•", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${video.likes} likes", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    IconButton(onClick = onFlag, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Flag, contentDescription = "Flag", tint = BrandGold, modifier = Modifier.size(16.dp))
                    }
                    IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = BrandRose, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ControlCenterLiveClassesTab(
    repository: Teach2LearnRepository,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val liveClasses by repository.firestoreLiveClasses.collectAsState()

    val active = liveClasses.filter { it.status == "live" }
    val scheduled = liveClasses.filter { it.status == "scheduled" }
    val ended = liveClasses.filter { it.status == "ended" }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "ACTIVE LIVE CLASSES (${active.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = BrandRose
            )
        }

        if (active.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No live sessions actively running.",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            items(active, key = { it.classId }) { cls ->
                AdminLiveClassCard(
                    liveClass = cls,
                    onEndClass = {
                        repository.endLiveClass(cls.classId)
                        onShowToast("Ended live class '${cls.title}'")
                    },
                    onRemove = {
                        repository.removeLiveClass(cls.classId)
                        onShowToast("Removed live class record")
                    }
                )
            }
        }

        item {
            Text(
                text = "SCHEDULED CLASSES (${scheduled.size})",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        if (scheduled.isEmpty()) {
            item {
                Text("No scheduled classes at this moment.", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            items(scheduled, key = { it.classId }) { cls ->
                AdminLiveClassCard(
                    liveClass = cls,
                    onEndClass = null,
                    onRemove = {
                        repository.removeLiveClass(cls.classId)
                        onShowToast("Removed scheduled class")
                    }
                )
            }
        }
    }
}

@Composable
fun AdminLiveClassCard(
    liveClass: FirestoreLiveClass,
    onEndClass: (() -> Unit)?,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (liveClass.status == "live") BrandRose.copy(alpha = 0.2f) else BrandGold.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = if (liveClass.status == "live") "🔴 LIVE" else "🗓️ ${liveClass.scheduledAt}",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (liveClass.status == "live") BrandRose else BrandGold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }

                Text(
                    text = "Room: ${liveClass.roomId}",
                    fontSize = 11.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    color = BrandCyan
                )
            }

            Text(
                text = liveClass.title,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${liveClass.subject} • Host: ${liveClass.hostName} • ${liveClass.participantCount} users",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (onEndClass != null) {
                        OutlinedButton(
                            onClick = onEndClass,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = BrandRose),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("End", fontSize = 11.sp)
                        }
                    }
                    IconButton(onClick = onRemove, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = BrandRose, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}
