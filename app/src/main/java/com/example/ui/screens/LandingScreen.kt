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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.*
import com.example.ui.theme.*

data class FeatureHighlight(
    val title: String,
    val description: String,
    val icon: String,
    val gradient: Brush
)

@Composable
fun LandingScreen(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit,
    onExploreAsGuest: () -> Unit,
    modifier: Modifier = Modifier
) {
    val highlights = remember {
        listOf(
            FeatureHighlight(
                "Peer Teaching",
                "Excel at C, Python, or Math? Teach peers and earn mentorship status.",
                "🤝",
                Brush.linearGradient(listOf(Color(0xFF4F46E5), Color(0xFF7C3AED)))
            ),
            FeatureHighlight(
                "Learn & Solve Doubts",
                "Never get stuck. Ask questions and get fast answers from student mentors.",
                "💡",
                Brush.linearGradient(listOf(Color(0xFF06B6D4), Color(0xFF3B82F6)))
            ),
            FeatureHighlight(
                "XP & Rank Progression",
                "Earn XP for every lecture, quiz, doubt solved, and peer session taught.",
                "⚡",
                Brush.linearGradient(listOf(Color(0xFFF59E0B), Color(0xFFD97706)))
            ),
            FeatureHighlight(
                "Play & Compete",
                "Interactive quizzes, daily campus code battles, and department cups.",
                "🎮",
                Brush.linearGradient(listOf(Color(0xFF10B981), Color(0xFF059669)))
            ),
            FeatureHighlight(
                "Verified Certificates",
                "Unlock official peer educator credentials to supercharge your resume.",
                "📜",
                Brush.linearGradient(listOf(Color(0xFFEC4899), Color(0xFF8B5CF6)))
            )
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Spacer(modifier = Modifier.height(28.dp))

            // App Brand Pill
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(text = "🚀", fontSize = 14.sp)
                    Text(
                        text = "Official APK Release • ${Teach2LearnRepository.APP_VERSION} Stable",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // App Title & Tagline
            Text(
                text = "TEACH2LEARN",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "\"${Teach2LearnRepository.APP_TAGLINE}\"",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.secondary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "The collaborative Android learning hub for engineering students. Connect with peer mentors, access hierarchical study notes, watch 60s micro reels, and master tricky programming concepts.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 3D Animated Holographic Learning Core
            ThreeDeeLearningOrb(
                primaryColor = BrandPrimary,
                secondaryColor = BrandSecondary,
                accentColor = BrandTertiary,
                streakCount = 7,
                xpAmount = 2450,
                userLevel = 5,
                rankTitle = "Grandmaster",
                onOrbClick = onGetStarted
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Primary Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = onGetStarted,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("landing_get_started_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text(
                        text = "Get Started",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                OutlinedButton(
                    onClick = onSignIn,
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("landing_sign_in_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "Sign In",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onExploreAsGuest,
                modifier = Modifier.testTag("landing_explore_guest_button")
            ) {
                Text(
                    text = "Quick Demo / Explore Platform →",
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Web Portal & Live Website Link
            val context = LocalContext.current
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(Teach2LearnRepository.OFFICIAL_WEBSITE_URL))
                        context.startActivity(intent)
                    }
                    .testTag("landing_official_website_card")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🌐", fontSize = 18.sp)
                        }
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(
                                    text = "Official Web Portal",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "LIVE",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Black,
                                    color = BrandEmerald,
                                    modifier = Modifier
                                        .background(BrandEmerald.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                            Text(
                                text = "teach2learn-app.netlify.app",
                                style = MaterialTheme.typography.bodySmall,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        Icons.Default.OpenInNew,
                        contentDescription = "Open Web Portal",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Core Loop Visual Card
            GlassmorphicCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            ) {
                Text(
                    text = "The Teach2Learn Mastery Loop",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(12.dp))

                val loopSteps = listOf(
                    "1. Learn & Practice" to "Master concepts from syllabus lectures",
                    "2. Become Skilled" to "Test knowledge through interactive quizzes",
                    "3. Teach Others" to "Conduct 1-on-1 sessions & solve doubts",
                    "4. Earn XP & Rank Up" to "Climb from Beginner to Grand Mentor",
                    "5. Unlock Rewards" to "Get certificates, premium perks & badges"
                )

                loopSteps.forEachIndexed { index, step ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "${index + 1}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Column {
                            Text(
                                text = step.first,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = step.second,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            SectionHeader(title = "Platform Highlights")
        }

        items(highlights) { item ->
            Interactive3DCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = 6.dp,
                onClick = onGetStarted
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(item.gradient)
                        .padding(18.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = item.icon, fontSize = 24.sp)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = item.description,
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(36.dp))
        }
    }
}
