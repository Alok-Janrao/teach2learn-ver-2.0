package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.data.repository.Teach2LearnRepository
import com.example.ui.components.AvatarInitials
import com.example.ui.components.XpPill
import com.example.ui.screens.*
import com.example.ui.theme.BrandEmerald
import com.example.ui.theme.BrandGold
import com.example.ui.theme.BrandPrimary
import com.example.ui.theme.BrandRose
import com.example.ui.theme.MyApplicationTheme

enum class MainBottomNav(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("home", "Home", Icons.Filled.Home, Icons.Outlined.Home),
    LEARN("learn", "Learn", Icons.Filled.School, Icons.Outlined.School),
    TEACH("teach", "Teach", Icons.Filled.CoPresent, Icons.Outlined.CoPresent),
    DOUBTS("doubts", "Doubts", Icons.Filled.Lightbulb, Icons.Outlined.Lightbulb),
    PLAY("play", "Play", Icons.Filled.SportsEsports, Icons.Outlined.SportsEsports),
    PROFILE("profile", "Profile", Icons.Filled.Person, Icons.Outlined.Person)
}

class MainActivity : ComponentActivity() {
    private val repository = Teach2LearnRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        com.example.util.configureAppCheck(this, intent)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Teach2LearnApp(repository = repository)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Teach2LearnApp(repository: Teach2LearnRepository) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val currentUser by repository.currentUser.collectAsState()
    val notifications by repository.notifications.collectAsState()
    val unreadNotificationsCount = notifications.count { !it.isRead }

    val bottomBarRoutes = remember {
        MainBottomNav.entries.map { it.route }
    }

    val showBottomBar = currentRoute in bottomBarRoutes
    val showTopBar = currentRoute in bottomBarRoutes

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            if (showTopBar) {
                TopAppBar(
                    title = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = BrandPrimary.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "🎓",
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(6.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = "TEACH2LEARN",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp,
                                    letterSpacing = 0.5.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Peer-to-Peer Hub",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    },
                    actions = {
                        // XP Counter
                        XpPill(xp = currentUser.xp, modifier = Modifier.clickable { navController.navigate("rewards") })

                        Spacer(modifier = Modifier.width(4.dp))

                        // Progress Analytics Shortcut
                        IconButton(
                            onClick = { navController.navigate("my_progress") },
                            modifier = Modifier.testTag("topbar_progress_button")
                        ) {
                            Icon(Icons.Default.TrendingUp, contentDescription = "My Progress", tint = BrandPrimary)
                        }

                        // Notes Shortcut
                        IconButton(
                            onClick = { navController.navigate("notes") },
                            modifier = Modifier.testTag("topbar_notes_button")
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = "Notes", tint = BrandPrimary)
                        }

                        // Search Button
                        IconButton(
                            onClick = { navController.navigate("search") },
                            modifier = Modifier.testTag("topbar_search_button")
                        ) {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurface)
                        }

                        // Notifications Button with unread badge
                        IconButton(
                            onClick = { navController.navigate("notifications") },
                            modifier = Modifier.testTag("topbar_notifications_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (unreadNotificationsCount > 0) {
                                        Badge(containerColor = BrandRose) {
                                            Text(text = "$unreadNotificationsCount", fontSize = 10.sp)
                                        }
                                    }
                                }
                            ) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = MaterialTheme.colorScheme.onSurface)
                            }
                        }

                        // Short Reels Icon Shortcut
                        IconButton(
                            onClick = { navController.navigate("reels") },
                            modifier = Modifier.testTag("topbar_reels_button")
                        ) {
                            Icon(Icons.Default.PlayCircle, contentDescription = "Short Videos", tint = BrandEmerald)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .navigationBarsPadding(),
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.2.dp,
                        Brush.linearGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                                MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            )
                        )
                    ),
                    shadowElevation = 12.dp
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        tonalElevation = 0.dp,
                        modifier = Modifier.height(68.dp)
                    ) {
                        MainBottomNav.entries.forEach { item ->
                            val isSelected = currentRoute == item.route
                            val animatedScale by animateFloatAsState(
                                targetValue = if (isSelected) 1.18f else 1.0f,
                                animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow),
                                label = "navScale_${item.route}"
                            )
                            val animatedOffsetY by animateFloatAsState(
                                targetValue = if (isSelected) -4f else 0f,
                                animationSpec = spring(dampingRatio = 0.55f, stiffness = Spring.StiffnessMediumLow),
                                label = "navOffset_${item.route}"
                            )

                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    if (currentRoute != item.route) {
                                        navController.navigate(item.route) {
                                            popUpTo(navController.graph.findStartDestination().id) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                icon = {
                                    Box(
                                        modifier = Modifier.graphicsLayer {
                                            scaleX = animatedScale
                                            scaleY = animatedScale
                                            translationY = animatedOffsetY * density
                                            cameraDistance = 12f * density
                                        },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                                            contentDescription = item.label
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = item.label,
                                        fontSize = 10.5.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MaterialTheme.colorScheme.primary,
                                    selectedTextColor = MaterialTheme.colorScheme.primary,
                                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.16f)
                                )
                            )
                        }
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "landing",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Landing Screen
            composable("landing") {
                LandingScreen(
                    onGetStarted = { navController.navigate("auth_signup") },
                    onSignIn = { navController.navigate("auth_signin") },
                    onExploreAsGuest = { navController.navigate(MainBottomNav.HOME.route) }
                )
            }

            // Auth Screen (Sign In)
            composable("auth_signin") {
                AuthScreen(
                    repository = repository,
                    initialIsSignUp = false,
                    onAuthSuccess = {
                        navController.navigate(MainBottomNav.HOME.route) {
                            popUpTo("landing") { inclusive = true }
                        }
                    },
                    onBackToLanding = { navController.popBackStack() }
                )
            }

            // Auth Screen (Sign Up)
            composable("auth_signup") {
                AuthScreen(
                    repository = repository,
                    initialIsSignUp = true,
                    onAuthSuccess = {
                        navController.navigate(MainBottomNav.HOME.route) {
                            popUpTo("landing") { inclusive = true }
                        }
                    },
                    onBackToLanding = { navController.popBackStack() }
                )
            }

            // 1. Home Dashboard
            composable(MainBottomNav.HOME.route) {
                HomeScreen(
                    repository = repository,
                    onNavigateToLearn = { navController.navigate(MainBottomNav.LEARN.route) },
                    onNavigateToSubject = { subjectId -> navController.navigate("subject/$subjectId") },
                    onNavigateToTeach = { navController.navigate(MainBottomNav.TEACH.route) },
                    onNavigateToPlay = { navController.navigate(MainBottomNav.PLAY.route) },
                    onNavigateToDoubts = { navController.navigate(MainBottomNav.DOUBTS.route) },
                    onNavigateToChallenges = { navController.navigate("challenges") },
                    onNavigateToLiveLecture = { lectureId -> navController.navigate("live_lecture/$lectureId") },
                    onNavigateToLeaderboard = { navController.navigate("leaderboard") },
                    onNavigateToTeacherProfile = { navController.navigate(MainBottomNav.TEACH.route) },
                    onNavigateToNotes = { navController.navigate("notes") },
                    onNavigateToVideoLectures = { navController.navigate("video_lectures") },
                    onNavigateToShortVideos = { navController.navigate("short_learning") },
                    onNavigateToLiveLearning = { navController.navigate("live_learning") },
                    onNavigateToQuiz = { quizId -> navController.navigate("quiz/$quizId") },
                    onNavigateToProgress = { navController.navigate("my_progress") }
                )
            }

            // 2. Learn Screen
            composable(MainBottomNav.LEARN.route) {
                LearnScreen(
                    repository = repository,
                    onSelectSubject = { subjectId -> navController.navigate("subject/$subjectId") },
                    onSelectTeacher = { navController.navigate(MainBottomNav.TEACH.route) },
                    onNavigateToNotes = { navController.navigate("notes") },
                    onNavigateToVideoLectures = { navController.navigate("video_lectures") },
                    onNavigateToShortVideos = { navController.navigate("reels") },
                    onNavigateToProgress = { navController.navigate("my_progress") }
                )
            }

            // 3. Notes Screen
            composable("notes") {
                NotesScreen(
                    repository = repository,
                    onBack = { navController.popBackStack() }
                )
            }

            // 4. Video Lectures Screen (Live + Recorded)
            composable("video_lectures") {
                VideoLecturesScreen(
                    repository = repository,
                    onJoinLiveLecture = { lectureId -> navController.navigate("live_lecture/$lectureId") },
                    onWatchRecordedLecture = { lectureId -> navController.navigate("recorded_lecture/$lectureId") },
                    onBack = { navController.popBackStack() }
                )
            }

            // Subject Detail Screen
            composable(
                route = "subject/{subjectId}",
                arguments = listOf(navArgument("subjectId") { type = NavType.StringType })
            ) { backStackEntry ->
                val subjectId = backStackEntry.arguments?.getString("subjectId") ?: "subj_c"
                SubjectDetailScreen(
                    subjectId = subjectId,
                    repository = repository,
                    onBack = { navController.popBackStack() },
                    onJoinLiveLecture = { lectureId -> navController.navigate("live_lecture/$lectureId") },
                    onWatchRecordedLecture = { lectureId -> navController.navigate("recorded_lecture/$lectureId") },
                    onStartQuiz = { quizId -> navController.navigate("quiz/$quizId") },
                    onRaiseDoubt = { subjectName -> navController.navigate(MainBottomNav.DOUBTS.route) },
                    onOpenTeacherProfile = { navController.navigate(MainBottomNav.TEACH.route) }
                )
            }

            // Live Lecture Room
            composable(
                route = "live_lecture/{lectureId}",
                arguments = listOf(navArgument("lectureId") { type = NavType.StringType })
            ) { backStackEntry ->
                val lectureId = backStackEntry.arguments?.getString("lectureId") ?: "lec_c_live"
                LiveLectureScreen(
                    lectureId = lectureId,
                    repository = repository,
                    onBack = { navController.popBackStack() },
                    onNavigateToQuiz = { quizId -> navController.navigate("quiz/$quizId") }
                )
            }

            // Recorded Lecture Player
            composable(
                route = "recorded_lecture/{lectureId}",
                arguments = listOf(navArgument("lectureId") { type = NavType.StringType })
            ) { backStackEntry ->
                val lectureId = backStackEntry.arguments?.getString("lectureId") ?: "lec_c_rec_1"
                RecordedLectureScreen(
                    lectureId = lectureId,
                    repository = repository,
                    onBack = { navController.popBackStack() },
                    onTakeQuiz = { quizId -> navController.navigate("quiz/$quizId") },
                    onAskDoubt = { navController.navigate(MainBottomNav.DOUBTS.route) }
                )
            }

            // 5. Teach Screen
            composable(MainBottomNav.TEACH.route) {
                TeachScreen(
                    repository = repository,
                    onNavigateToSubject = { subjectId -> navController.navigate("subject/$subjectId") }
                )
            }

            // 6. Doubts Forum Screen
            composable(MainBottomNav.DOUBTS.route) {
                DoubtsScreen(
                    repository = repository
                )
            }

            // 7. Play & Learn Screen
            composable(MainBottomNav.PLAY.route) {
                PlayAndLearnScreen(
                    repository = repository,
                    onStartQuiz = { quizId -> navController.navigate("quiz/$quizId") },
                    onNavigateToChallenges = { navController.navigate("challenges") }
                )
            }

            // Interactive Quiz Runner
            composable(
                route = "quiz/{quizId}",
                arguments = listOf(navArgument("quizId") { type = NavType.StringType })
            ) { backStackEntry ->
                val quizId = backStackEntry.arguments?.getString("quizId") ?: "quiz_c_1"
                QuizScreen(
                    quizId = quizId,
                    repository = repository,
                    onFinishQuiz = { navController.popBackStack() }
                )
            }

            // Challenges Screen
            composable("challenges") {
                ChallengesScreen(
                    repository = repository,
                    onStartQuiz = { quizId -> navController.navigate("quiz/$quizId") }
                )
            }

            // Short Learning (Vertical Educational Video Feed & Upload)
            composable("short_learning") {
                ShortLearningScreen(
                    repository = repository,
                    onNavigateToCreateShort = { navController.navigate("create_short") },
                    onBack = { navController.popBackStack() }
                )
            }
            composable("create_short") {
                CreateShortScreen(
                    repository = repository,
                    onBack = { navController.popBackStack() },
                    onSuccessNavigateToFeed = { navController.popBackStack() }
                )
            }

            // Live Learning (Real-time Audio/Video Classrooms)
            composable("live_learning") {
                LiveLearningScreen(
                    repository = repository,
                    onNavigateToClassroom = { classId -> navController.navigate("live_classroom/$classId") },
                    onBack = { navController.popBackStack() }
                )
            }
            composable(
                route = "live_classroom/{classId}",
                arguments = listOf(navArgument("classId") { type = NavType.StringType })
            ) { backStackEntry ->
                val classId = backStackEntry.arguments?.getString("classId") ?: "live_c_prog_101"
                LiveClassroomScreen(
                    classId = classId,
                    repository = repository,
                    onLeaveClassroom = { navController.popBackStack() }
                )
            }

            // Short Video Reels (both "reels" and "short_videos")
            composable("reels") {
                ShortLearningScreen(
                    repository = repository,
                    onNavigateToCreateShort = { navController.navigate("create_short") },
                    onBack = { navController.popBackStack() }
                )
            }
            composable("short_videos") {
                ShortLearningScreen(
                    repository = repository,
                    onNavigateToCreateShort = { navController.navigate("create_short") },
                    onBack = { navController.popBackStack() }
                )
            }

            // Leaderboard
            composable("leaderboard") {
                LeaderboardScreen(repository = repository)
            }

            // Rewards
            composable("rewards") {
                RewardsScreen(repository = repository)
            }

            // Search
            composable("search") {
                SearchScreen(
                    repository = repository,
                    onNavigateToSubject = { subjectId -> navController.navigate("subject/$subjectId") },
                    onNavigateToTeacher = { navController.navigate(MainBottomNav.TEACH.route) }
                )
            }

            // Notifications
            composable("notifications") {
                NotificationsScreen(
                    repository = repository,
                    onBack = { navController.popBackStack() }
                )
            }

            // Premium
            composable("premium") {
                PremiumScreen(
                    repository = repository,
                    onBack = { navController.popBackStack() }
                )
            }

            // Control Center / Admin (Authorized Platform Owner Only)
            composable("admin") {
                ControlCenterScreen(
                    repository = repository,
                    onBack = { navController.popBackStack() }
                )
            }
            composable("control_center") {
                ControlCenterScreen(
                    repository = repository,
                    onBack = { navController.popBackStack() }
                )
            }

            // 8. Profile Screen
            composable(MainBottomNav.PROFILE.route) {
                ProfileScreen(
                    repository = repository,
                    onSignOut = {
                        navController.navigate("landing") {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onNavigateToPremium = { navController.navigate("premium") },
                    onNavigateToAdmin = { navController.navigate("admin") },
                    onNavigateToProgress = { navController.navigate("my_progress") }
                )
            }

            // My Learning Progress Screen
            composable("my_progress") {
                MyProgressScreen(
                    repository = repository,
                    onBack = { navController.popBackStack() },
                    onNavigateToSubject = { subjectId -> navController.navigate("subject/$subjectId") },
                    onNavigateToQuiz = { quizId -> navController.navigate("quiz/$quizId") },
                    onNavigateToNotes = { navController.navigate("notes") }
                )
            }
        }
    }
}
