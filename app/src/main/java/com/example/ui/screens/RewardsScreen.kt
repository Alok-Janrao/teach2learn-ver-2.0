package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.data.model.RankTier
import com.example.data.model.Reward
import com.example.data.model.UserBadge
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.GlassmorphicCard
import com.example.ui.components.RankBadge
import com.example.ui.components.SectionHeader
import com.example.ui.components.XpPill
import com.example.ui.theme.*

@Composable
fun RewardsScreen(
    repository: Teach2LearnRepository,
    modifier: Modifier = Modifier
) {
    val currentUser by repository.currentUser.collectAsState()
    val rewards by repository.rewards.collectAsState()

    var rewardToast by remember { mutableStateOf<String?>(null) }
    val currentRank = remember(currentUser.xp) { RankTier.fromXp(currentUser.xp) }

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
                text = "Rewards & Achievements 🎁",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Redeem your hard-earned XP for verified credentials, goodies, and mentor badges",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // User XP & Rank Status Banner
        item {
            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(text = "Your Available Balance", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "${currentUser.xp} XP", fontSize = 24.sp, fontWeight = FontWeight.Black, color = BrandGold)
                            RankBadge(rank = currentRank)
                        }
                    }
                    Text(text = "⚡ Tier Level: ${currentRank.title}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
        }

        rewardToast?.let { msg ->
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
                        Text(text = "🎉 $msg", fontSize = 12.sp, color = BrandEmerald, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { rewardToast = null }) {
                            Icon(Icons.Default.Close, contentDescription = null, tint = BrandEmerald, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        // Rewards Catalog
        item {
            SectionHeader(title = "Rewards Catalog")
        }

        items(rewards) { item ->
            val canClaim = currentUser.xp >= item.requiredXp && !item.isClaimed

            GlassmorphicCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(BrandPrimary.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = item.icon, fontSize = 24.sp)
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(text = item.title, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = item.category,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Text(text = item.description, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Required: ${item.requiredXp} XP (${item.requiredRank.title})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = BrandGold)
                    }

                    Button(
                        onClick = {
                            repository.claimReward(item.id)
                            rewardToast = "Claimed '${item.title}'! Check your profile certificates."
                        },
                        enabled = canClaim,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (item.isClaimed) MaterialTheme.colorScheme.surfaceVariant else BrandEmerald
                        )
                    ) {
                        Text(
                            text = if (item.isClaimed) "Claimed ✓" else "Redeem",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Badges Showcase
        item {
            SectionHeader(
                title = "Badges Showcase (${currentUser.badges.count { it.isUnlocked }} / ${currentUser.badges.size} Unlocked)",
                subtitle = "Earn badges by mentoring, teaching, and answering questions"
            )
        }

        items(currentUser.badges.chunked(2)) { pair ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                pair.forEach { badge ->
                    BadgeCard(badge = badge, modifier = Modifier.weight(1f))
                }
                if (pair.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun BadgeCard(badge: UserBadge, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .border(
                1.dp,
                if (badge.isUnlocked) BrandGold.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                RoundedCornerShape(14.dp)
            ),
        color = if (badge.isUnlocked) BrandGold.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (badge.isUnlocked) BrandGold.copy(alpha = 0.2f) else Color.Gray.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text(text = if (badge.isUnlocked) badge.icon else "🔒", fontSize = 20.sp)
            }

            Text(
                text = badge.name,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Text(
                text = badge.description,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                maxLines = 2
            )

            if (badge.isUnlocked) {
                Text(text = "Unlocked ✨", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = BrandEmerald)
            } else {
                Text(text = "Locked", fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}
