package com.example.ui.screens

import android.net.Uri
import android.widget.MediaController
import android.widget.VideoView
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.repository.Teach2LearnRepository
import com.example.data.service.FirebaseLearningVideoService
import com.example.ui.theme.*
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateShortScreen(
    repository: Teach2LearnRepository,
    onBack: () -> Unit,
    onSuccessNavigateToFeed: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val currentUser by repository.currentUser.collectAsState()
    val videoService = remember { FirebaseLearningVideoService(context) }

    var selectedVideoUri by remember { mutableStateOf<Uri?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedSubject by remember { mutableStateOf("C Programming") }
    var selectedThumbnailEmoji by remember { mutableStateOf("💻") }
    var subjectMenuExpanded by remember { mutableStateOf(false) }

    var isUploading by remember { mutableStateOf(false) }
    var uploadProgress by remember { mutableStateOf(0) }
    var uploadError by remember { mutableStateOf<String?>(null) }
    var isPublishedSuccess by remember { mutableStateOf(false) }

    val subjects = listOf(
        "C Programming",
        "Python",
        "Data Structures",
        "Engineering Mathematics",
        "AI & Data Science",
        "Operating Systems",
        "Computer Networks"
    )

    val thumbnailEmojis = listOf("💻", "🐍", "⚡", "📐", "🤖", "🔬", "🔧", "💡")

    // Android Zero-Permission Photo/Video Picker
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            // Validate video format/type via ContentResolver
            val mimeType = context.contentResolver.getType(uri)
            if (mimeType != null && !mimeType.startsWith("video/")) {
                uploadError = "Selected file is not a supported video ($mimeType). Please select MP4, WebM, or MOV."
            } else {
                selectedVideoUri = uri
                uploadError = null
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Create Short",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack, modifier = Modifier.testTag("create_short_back_button")) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

            // Success Confirmation Card
            if (isPublishedSuccess) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BrandEmerald.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = BrandEmerald,
                            modifier = Modifier.size(54.dp)
                        ) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                        Text(
                            text = "Your short has been published.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Your peer learning video is now available in the Short Learning feed! (+50 XP earned)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Button(
                            onClick = onSuccessNavigateToFeed,
                            colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("View in Short Learning Feed")
                        }
                    }
                }
            }

            // Video Picker / Preview Area
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .border(1.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f), RoundedCornerShape(18.dp))
                    .testTag("video_picker_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
            ) {
                if (selectedVideoUri == null) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clickable {
                                videoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = BrandCyan.copy(alpha = 0.2f),
                                modifier = Modifier.size(56.dp)
                            ) {
                                Icon(
                                    Icons.Default.CloudUpload,
                                    contentDescription = "Select Video",
                                    tint = BrandCyan,
                                    modifier = Modifier.padding(14.dp)
                                )
                            }
                            Text(
                                text = "Select Video from Device",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Supported: MP4, WebM, MOV (Max 60s)",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    // Video Preview with VideoView
                    Box(modifier = Modifier.fillMaxSize()) {
                        AndroidView(
                            modifier = Modifier.fillMaxSize(),
                            factory = { ctx ->
                                VideoView(ctx).apply {
                                    setVideoURI(selectedVideoUri)
                                    val mc = MediaController(ctx)
                                    mc.setAnchorView(this)
                                    setMediaController(mc)
                                    setOnPreparedListener { mp ->
                                        mp.isLooping = true
                                        mp.start()
                                    }
                                }
                            }
                        )

                        // Change video button overlay
                        FilledTonalButton(
                            onClick = {
                                videoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                                )
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(8.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Change", fontSize = 11.sp)
                        }
                    }
                }
            }

            // Error display
            if (uploadError != null) {
                Surface(
                    color = BrandRose.copy(alpha = 0.15f),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = BrandRose, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(uploadError!!, color = BrandRose, fontSize = 12.sp)
                    }
                }
            }

            // Title Field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title *") },
                placeholder = { Text("e.g. Memory Traps in C Pointers") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_short_title_input"),
                shape = RoundedCornerShape(12.dp),
                singleLine = true
            )

            // Subject Selector Dropdown
            Box(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = selectedSubject,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Subject *") },
                    trailingIcon = {
                        IconButton(onClick = { subjectMenuExpanded = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { subjectMenuExpanded = true }
                        .testTag("create_short_subject_dropdown"),
                    shape = RoundedCornerShape(12.dp)
                )

                DropdownMenu(
                    expanded = subjectMenuExpanded,
                    onDismissRequest = { subjectMenuExpanded = false }
                ) {
                    subjects.forEach { subj ->
                        DropdownMenuItem(
                            text = { Text(subj) },
                            onClick = {
                                selectedSubject = subj
                                subjectMenuExpanded = false
                            }
                        )
                    }
                }
            }

            // Short Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Short Description") },
                placeholder = { Text("Summarize the key concept explained in 60s...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("create_short_desc_input"),
                shape = RoundedCornerShape(12.dp),
                minLines = 3
            )

            // Thumbnail Icon / Emoji Selector
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "Thumbnail Badge",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    thumbnailEmojis.forEach { emoji ->
                        val isSelected = selectedThumbnailEmoji == emoji
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isSelected) BrandCyan.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant,
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, BrandCyan) else null,
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { selectedThumbnailEmoji = emoji }
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(emoji, fontSize = 18.sp)
                            }
                        }
                    }
                }
            }

            // Upload Progress Bar
            if (isUploading) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Uploading to Firebase Cloud Storage...", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text("$uploadProgress%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = BrandCyan)
                    }
                    LinearProgressIndicator(
                        progress = { uploadProgress / 100f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = BrandCyan
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Publish Short Button
            Button(
                onClick = {
                    if (selectedVideoUri == null) {
                        uploadError = "Please select a video file first."
                        return@Button
                    }
                    if (title.isBlank()) {
                        uploadError = "Please enter a title for your Short."
                        return@Button
                    }

                    uploadError = null
                    isUploading = true
                    uploadProgress = 10

                    coroutineScope.launch {
                        try {
                            val videoId = "short_${UUID.randomUUID().toString().take(8)}"
                            val downloadUrl = videoService.uploadVideoToStorage(
                                videoUri = selectedVideoUri!!,
                                videoId = videoId,
                                userId = currentUser.id,
                                onProgress = { progress -> uploadProgress = progress }
                            )

                            // Publish in Repository and Firestore
                            val publishedVideo = repository.publishShortVideo(
                                title = title,
                                description = description,
                                subject = selectedSubject,
                                videoUrl = downloadUrl,
                                thumbnailUrl = selectedThumbnailEmoji
                            )

                            videoService.saveShortVideoMetadata(publishedVideo)

                            isUploading = false
                            isPublishedSuccess = true
                        } catch (e: Exception) {
                            isUploading = false
                            uploadError = "Upload failed: ${e.localizedMessage ?: "Unknown error"}"
                        }
                    }
                },
                enabled = !isUploading && !isPublishedSuccess,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("publish_short_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary)
            ) {
                if (isUploading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Uploading...", fontWeight = FontWeight.Bold)
                } else {
                    Icon(Icons.Default.Publish, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Publish Short", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

        }
    }
}
