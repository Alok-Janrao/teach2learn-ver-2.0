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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SubjectNote
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.SectionHeader
import com.example.ui.components.XpPill
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotesScreen(
    repository: Teach2LearnRepository,
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val notes by repository.notes.collectAsState()
    val subjects by repository.subjects.collectAsState()

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var selectedSubjectId by remember { mutableStateOf<String?>("all") }
    var activeReadingNote by remember { mutableStateOf<SubjectNote?>(null) }
    var showUploadDialog by remember { mutableStateOf(false) }
    var actionToast by remember { mutableStateOf<String?>(null) }

    val categories = listOf("All", "Formula Sheet", "Cheatsheet", "Solved Papers", "Handwritten Notes")

    val filteredNotes = notes.filter { note ->
        val matchesSubject = selectedSubjectId == "all" || note.subjectId == selectedSubjectId
        val matchesCat = selectedCategory == "All" || note.category.equals(selectedCategory, ignoreCase = true)
        val matchesQuery = searchQuery.isBlank() ||
                note.title.contains(searchQuery, ignoreCase = true) ||
                note.author.contains(searchQuery, ignoreCase = true) ||
                note.contentPreview.contains(searchQuery, ignoreCase = true)
        matchesSubject && matchesCat && matchesQuery
    }

    val clipboardManager = LocalClipboardManager.current

    // Note Reader Dialog / Full PDF Simulator
    if (activeReadingNote != null) {
        val note = activeReadingNote!!
        AlertDialog(
            onDismissRequest = { activeReadingNote = null },
            modifier = Modifier.fillMaxWidth(0.95f),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BrandPrimary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = note.category.uppercase(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = BrandPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = note.title,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "By ${note.author} • ${note.pagesCount} Pages • ⭐ ${note.rating}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = {
                        repository.toggleBookmarkNote(note.id)
                        activeReadingNote = note.copy(isBookmarked = !note.isBookmarked)
                    }) {
                        Icon(
                            imageVector = if (note.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = BrandGold
                        )
                    }
                }
            },
            text = {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 450.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Quick Takeaway / Summary
                    item {
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "📌 Summary & Highlights",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = BrandPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = note.contentPreview,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Key Formula Cards
                    if (note.keyFormulas.isNotEmpty()) {
                        item {
                            Text(
                                text = "📐 High-Yield Formulae & Rules (Tap to Copy)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
                        items(note.keyFormulas) { formula ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF0F172A),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        clipboardManager.setText(AnnotatedString(formula))
                                        actionToast = "Formula copied to clipboard!"
                                    }
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = formula,
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 11.sp,
                                        color = Color(0xFF38BDF8),
                                        modifier = Modifier.weight(1f)
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = Color(0xFF94A3B8),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Full Study Content Reader
                    if (note.fullContent.isNotBlank()) {
                        item {
                            Text(
                                text = "📖 Full Study Notes",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                ),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = note.fullContent,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        repository.downloadNote(note.id)
                        actionToast = "Notes downloaded for offline reading! (+5 XP)"
                        activeReadingNote = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Download PDF")
                }
            },
            dismissButton = {
                TextButton(onClick = { activeReadingNote = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Upload Peer Notes Dialog
    if (showUploadDialog) {
        var uploadTitle by remember { mutableStateOf("") }
        var uploadSubjectId by remember { mutableStateOf(subjects.firstOrNull()?.id ?: "subj_c") }
        var uploadCategory by remember { mutableStateOf("Formula Sheet") }
        var uploadPages by remember { mutableStateOf("5") }
        var uploadSummary by remember { mutableStateOf("") }
        var uploadFormula by remember { mutableStateOf("") }
        var uploadContent by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showUploadDialog = false },
            modifier = Modifier.fillMaxWidth(0.95f),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("✍️ Share Notes with Peers", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    XpPill(xp = 35, fontSize = 11)
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
                            label = { Text("Note Title") },
                            placeholder = { Text("e.g. Unit 3 Quick Pointers & Memory Formulas") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    item {
                        Text("Category", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(listOf("Formula Sheet", "Cheatsheet", "Solved Papers", "Handwritten Notes", "Lab Viva")) { cat ->
                                val isSelected = uploadCategory == cat
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { uploadCategory = cat },
                                    label = { Text(cat, fontSize = 11.sp) }
                                )
                            }
                        }
                    }

                    item {
                        OutlinedTextField(
                            value = uploadPages,
                            onValueChange = { uploadPages = it },
                            label = { Text("Page Count") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uploadSummary,
                            onValueChange = { uploadSummary = it },
                            label = { Text("Quick Summary & Key Topics") },
                            placeholder = { Text("Covers memory models, malloc/calloc differences, and viva questions...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            minLines = 2
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uploadFormula,
                            onValueChange = { uploadFormula = it },
                            label = { Text("Key Formula or Core Rule (Optional)") },
                            placeholder = { Text("*(arr + i) == arr[i]") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    item {
                        OutlinedTextField(
                            value = uploadContent,
                            onValueChange = { uploadContent = it },
                            label = { Text("Study Notes Content / Formula Breakdown") },
                            placeholder = { Text("Type or paste detailed definitions, exam tips, and step-by-step solutions...") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            minLines = 4
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (uploadTitle.isNotBlank() && uploadSummary.isNotBlank()) {
                            repository.uploadNote(
                                subjectId = uploadSubjectId,
                                title = uploadTitle,
                                author = "Alok Janrao (You)",
                                category = uploadCategory,
                                pagesCount = uploadPages.toIntOrNull() ?: 5,
                                contentPreview = uploadSummary,
                                keyFormulas = if (uploadFormula.isNotBlank()) listOf(uploadFormula) else emptyList(),
                                fullContent = if (uploadContent.isNotBlank()) uploadContent else uploadSummary
                            )
                            showUploadDialog = false
                            actionToast = "Notes uploaded successfully! Earned +35 XP 🎉"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Publish (+35 XP)")
                }
            },
            dismissButton = {
                TextButton(onClick = { showUploadDialog = false }) { Text("Cancel") }
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
        // Header
        item {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                        }
                    }
                    Column {
                        Text(
                            text = "Peer Notes & Cheatsheets 📚",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "High-yield formula sheets, solved university papers & lab viva guides",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Button(
                    onClick = { showUploadDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = BrandPrimary),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Upload", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Action Toast Banner
        actionToast?.let { msg ->
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
                        IconButton(onClick = { actionToast = null }, modifier = Modifier.size(20.dp)) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(14.dp))
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
                placeholder = { Text("Search by topic, formula, subject or author...", fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = BrandPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notes_search_field"),
                shape = RoundedCornerShape(14.dp),
                singleLine = true
            )
        }

        // Subject Filter Row
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = selectedSubjectId == "all",
                        onClick = { selectedSubjectId = "all" },
                        label = { Text("All Subjects") }
                    )
                }
                items(subjects) { subj ->
                    FilterChip(
                        selected = selectedSubjectId == subj.id,
                        onClick = { selectedSubjectId = subj.id },
                        label = { Text("${subj.icon} ${subj.name}") }
                    )
                }
            }
        }

        // Category Filter Row
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

        // Notes List
        item {
            SectionHeader(
                title = "Study Materials (${filteredNotes.size})",
                subtitle = "Handcrafted and reviewed by verified peer toppers"
            )
        }

        if (filteredNotes.isEmpty()) {
            item {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surface,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("📂", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No study notes found", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Try selecting another category or upload your own notes to earn +35 XP!", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        items(filteredNotes) { note ->
            val subj = subjects.firstOrNull { it.id == note.subjectId }

            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { activeReadingNote = note }
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                                shape = RoundedCornerShape(6.dp),
                                color = BrandPrimary.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = subj?.name ?: "Engineering",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = BrandEmerald.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = note.category,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandEmerald,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { repository.toggleBookmarkNote(note.id) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (note.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (note.isBookmarked) BrandGold else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Text(
                        text = note.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Text(
                        text = note.contentPreview,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(text = "✍️ ${note.author}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "•", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "${note.pagesCount} Pages", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "•", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = "⭐ ${note.rating}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandGold)
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { activeReadingNote = note },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text("Read", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                            Button(
                                onClick = {
                                    repository.downloadNote(note.id)
                                    actionToast = "Downloaded '${note.title}' (+5 XP)"
                                },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BrandEmerald),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
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
