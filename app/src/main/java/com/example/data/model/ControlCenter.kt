package com.example.data.model

enum class UserRole(val displayName: String, val badgeIcon: String) {
    STUDENT("Student", "🎒"),
    TEACHER("Student Teacher", "👨‍🏫"),
    ADMIN("Platform Owner & Admin", "👑")
}

enum class ProSubscriptionStatus(val label: String, val badgeText: String, val colorHex: Long) {
    ACTIVE("Active", "🟢 Active", 0xFF10B981),
    EXPIRING_SOON("Expiring Soon", "🟡 Expiring Soon", 0xFFF59E0B),
    EXPIRED("Expired", "🔴 Expired", 0xFFEF4444)
}

enum class ProPlanType(val title: String, val priceDisplay: String, val billingCycle: String) {
    CAMPUS_PRO_ANNUAL("Campus Pro Annual", "₹999/yr", "Annual"),
    CAMPUS_PRO_SEMESTER("Campus Pro Semester", "₹499/sem", "Semester"),
    CAMPUS_BETA_PRO("Founder Grant Beta Pro", "Free Grant", "Beta Lifetime")
}

data class ProUserRecord(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String = "",
    val role: UserRole,
    val accountStatus: String = "Active", // Active, Suspended, Under Review
    val proPlan: ProPlanType,
    val proStatus: ProSubscriptionStatus,
    val subscriptionStartDate: String,
    val subscriptionExpiryDate: String,
    val startTimestamp: Long,
    val expiryTimestamp: Long,
    val lastActiveTime: String,
    val lastActiveTimestamp: Long,
    val college: String,
    val department: String,
    val educationLevel: String = "B.Tech",
    // Detailed Administrative Insights
    val learningActivitySummary: String,
    val teachingActivitySummary: String,
    val totalLecturesAttended: Int,
    val totalQuizzesTaken: Int,
    val totalSessionsHosted: Int,
    val totalStudentsHelped: Int,
    val overallProgressPercent: Int,
    val teach2LearnScore: Int,
    val proFeaturesUsed: List<String> = emptyList(),
    val featureUsageStats: Map<String, Int> = emptyMap()
)

data class ControlCenterOverviewMetrics(
    val totalUsers: Int,
    val activeUsers: Int,
    val newUsers: Int,
    val activeLearners: Int,
    val activeTeachers: Int,
    val proUsersCount: Int
)

data class ProHealthMetrics(
    val totalPro: Int,
    val activePro: Int,
    val expiringSoonPro: Int,
    val expiredPro: Int,
    val activePercentage: Float
)

data class ProAnalyticsData(
    val totalProUsers: Int,
    val activeProUsers: Int,
    val expiredProUsers: Int,
    val newProUsersThisMonth: Int,
    val proConversionRatePercent: Float,
    val proTrendPoints: List<Pair<String, Int>> // e.g. ("Week 1", 72), ("Week 2", 84), etc.
)

data class FeatureUsageMetric(
    val featureName: String,
    val usageCount: Int,
    val percentage: Int, // 0..100
    val category: String, // "AI Assistance", "Analytics", "Notes", etc.
    val description: String
)

data class PlatformInsightsData(
    val mostUsedFeature: String,
    val mostUsedFeatureStats: String,
    val mostActiveDay: String,
    val mostActiveDayStats: String,
    val mostActiveUsers: List<Pair<String, String>>,
    val fastestGrowingFeature: String,
    val fastestGrowingStats: String,
    val averageUserActivity: String,
    val hasSufficientData: Boolean = true
)

enum class ControlCenterNavTab(val title: String, val icon: String) {
    OVERVIEW("Overview", "📊"),
    USERS("Users", "👥"),
    SHORT_VIDEOS("Short Videos", "🎬"),
    LIVE_CLASSES("Live Classes", "📡"),
    PRO_USERS("Pro Users", "💎"),
    ANALYTICS("Analytics", "📈"),
    FEATURE_USAGE("Feature Usage", "🔥"),
    PLATFORM_INSIGHTS("Platform Insights", "💡"),
    SETTINGS("Settings", "⚙️")
}
