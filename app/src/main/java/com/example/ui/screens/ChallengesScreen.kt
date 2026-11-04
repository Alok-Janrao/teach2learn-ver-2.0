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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Challenge
import com.example.data.model.ChallengeType
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.XpPill
import com.example.ui.theme.*

@Composable
fun ChallengesScreen(
    repository: Teach2LearnRepository,
    onStartQuiz: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val challenges by repository.challenges.collectAsState()
    var selectedType by remember { mutableStateOf<ChallengeType?>(null) }
    var challengeToast by remember { mutableStateOf<String?>(null) }

    val filteredChallenges = remember(challenges, selectedType) {
        if (selectedType == null) challenges else challenges.filter { it.type == selectedType }
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
                text = "Competitions & Challenges 🏆",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Compete across Daily, Department, and Inter-College leagues for XP and trophy badges",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    FilterChip(
                        selected = selectedType == null,
                        onClick = { selectedType = null },
                        label = { Text("All Leagues") }
                    )
                }
                items(ChallengeType.entries) { type ->
                    FilterChip(
                        selected = selectedType == type,
                        onClick = { selectedType = type },
                        label = { Text(type.name.replace("_", " ")) }
                    )
                }
            }
        }

        challengeToast?.let { msg ->
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
                        Text(text = "🏅 $msg", fontSize = 12.sp, color = BrandEmerald, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { challengeToast = null }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        items(filteredChallenges) { challenge ->
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BrandRose.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = challenge.type.name.replace("_", " "),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = BrandRose,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = "⏳ ${challenge.remainingTime}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Text(
                    text = challenge.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "👥 ${challenge.participantsCount} Students Competing",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    XpPill(xp = challenge.xpReward)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        onStartQuiz("quiz_c_1")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("Enter Challenge (+${challenge.xpReward} XP)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
