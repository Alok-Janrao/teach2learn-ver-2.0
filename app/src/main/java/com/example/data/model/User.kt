package com.example.data.model

enum class RankTier(
    val title: String,
    val minXp: Int,
    val maxXp: Int,
    val badgeIcon: String,
    val colorHex: Long
) {
    BEGINNER("Beginner", 0, 100, "🌱", 0xFF94A3B8),
    LEARNER("Learner", 101, 300, "📘", 0xFF38BDF8),
    EXPLORER("Explorer", 301, 600, "🚀", 0xFF818CF8),
    SKILLED("Skilled", 601, 1000, "⚡", 0xFF34D399),
    EXPERT("Expert", 1001, 1500, "🔥", 0xFFF59E0B),
    MASTER("Master", 1501, 2500, "👑", 0xFFEC4899),
    MENTOR("Mentor", 2501, 4000, "🌟", 0xFF8B5CF6),
    GRAND_MENTOR("Grand Mentor", 4001, 100000, "🪐", 0xFFE11D48);

    companion object {
        fun fromXp(xp: Int): RankTier {
            return entries.lastOrNull { xp >= it.minXp } ?: BEGINNER
        }
    }
}

data class UserBadge(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val isUnlocked: Boolean,
    val unlockedDate: String? = null
)

data class UserCertificate(
    val id: String,
    val title: String,
    val certificateId: String,
    val issueDate: String,
    val issuer: String = "Teach2Learn Academic Council"
)

data class UserReview(
    val id: String,
    val studentName: String,
    val studentAvatar: String,
    val rating: Float,
    val comment: String,
    val subject: String,
    val date: String
)

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String = "",
    val educationLevel: String = "B.Tech",
    val college: String = "Government College of Engineering",
    val department: String = "CSE",
    val yearSemester: String = "1st Year / 1st Sem",
    val bio: String = "Eager peer learner ready to collaborate and study together!",
    val xp: Int = 0,
    val streakDays: Int = 0,
    val studentsHelped: Int = 0,
    val sessionsCompleted: Int = 0,
    val completedLectures: Int = 0,
    val completedQuizzes: Int = 0,
    val profileCompletion: Int = 0,
    val learningProgress: Int = 0,
    val teachingProgress: Int = 0,
    val rating: Float = 5.0f,
    val learnSubjects: List<String> = emptyList(),
    val teachSubjects: List<String> = emptyList(),
    val isTeacherProfileActive: Boolean = false,
    val teacherAvailableDays: String = "Mon, Wed, Fri",
    val teacherAvailableTime: String = "6:00 PM - 9:00 PM",
    val teachingMode: String = "Online Live",
    val isPremium: Boolean = false,
    val role: UserRole = UserRole.STUDENT,
    val badges: List<UserBadge> = emptyList(),
    val certificates: List<UserCertificate> = emptyList(),
    val reviews: List<UserReview> = emptyList()
)
