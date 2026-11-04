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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LeaderboardEntry
import com.example.data.model.LeaderboardScope
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.AvatarInitials
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.RankBadge
import com.example.ui.components.XpPill
import com.example.ui.theme.*

@Composable
fun LeaderboardScreen(
    repository: Teach2LearnRepository,
    modifier: Modifier = Modifier
) {
    val leaderboard by repository.leaderboard.collectAsState()
    var selectedScope by remember { mutableStateOf(LeaderboardScope.GLOBAL) }

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
                text = "Leaderboard & Rankings 🏅",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Climb ranks by learning, solving doubts, and teaching peer sessions",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Filter Scope Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(LeaderboardScope.entries) { scope ->
                    FilterChip(
                        selected = selectedScope == scope,
                        onClick = { selectedScope = scope },
                        label = { Text(scope.name.replace("_", " ")) }
                    )
                }
            }
        }

        // Top 3 Podium
        item {
            val top3 = leaderboard.take(3)
            if (top3.size >= 3) {
                PodiumSection(top1 = top3[0], top2 = top3[1], top3 = top3[2])
            }
        }

        // Ranking List Header
        item {
            Text(
                text = "All Rankings",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        items(leaderboard) { entry ->
            val isMe = entry.isCurrentUser
            val borderModifier = if (isMe) {
                Modifier.border(2.dp, BrandGold, RoundedCornerShape(14.dp))
            } else {
                Modifier.border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .then(borderModifier),
                color = if (isMe) BrandGold.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Rank Badge
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(
                                when (entry.rank) {
                                    1 -> BrandGold
                                    2 -> Color(0xFF94A3B8)
                                    3 -> Color(0xFFB45309)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "#${entry.rank}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (entry.rank <= 3) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    AvatarInitials(name = entry.name, size = 38.dp)

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isMe) "${entry.name} (You)" else entry.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            RankBadge(rank = entry.rankTier)
                        }
                        Text(
                            text = "${entry.college} • ${entry.department}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    XpPill(xp = entry.xp)
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun PodiumSection(
    top1: LeaderboardEntry,
    top2: LeaderboardEntry,
    top3: LeaderboardEntry
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1E1B4B), Color(0xFF0F172A))
                )
            )
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.Bottom
        ) {
            // #2 Silver
            PodiumColumn(
                entry = top2,
                podiumRank = 2,
                podiumColor = Color(0xFF94A3B8),
                podiumHeight = 80.dp
            )

            // #1 Gold
            PodiumColumn(
                entry = top1,
                podiumRank = 1,
                podiumColor = BrandGold,
                podiumHeight = 110.dp
            )

            // #3 Bronze
            PodiumColumn(
                entry = top3,
                podiumRank = 3,
                podiumColor = Color(0xFFB45309),
                podiumHeight = 65.dp
            )
        }
    }
}

@Composable
fun PodiumColumn(
    entry: LeaderboardEntry,
    podiumRank: Int,
    podiumColor: Color,
    podiumHeight: androidx.compose.ui.unit.Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = when (podiumRank) {
                1 -> "👑"
                2 -> "🥈"
                else -> "🥉"
            },
            fontSize = 22.sp
        )

        AvatarInitials(name = entry.name, size = 42.dp)

        Text(
            text = entry.name.split(" ").firstOrNull() ?: "",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Text(
            text = "${entry.xp} XP",
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = BrandGold
        )

        Box(
            modifier = Modifier
                .width(80.dp)
                .height(podiumHeight)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(podiumColor.copy(alpha = 0.3f))
                .border(1.dp, podiumColor, RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$podiumRank",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = podiumColor
            )
        }
    }
}
