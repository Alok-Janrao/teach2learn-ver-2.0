package com.example.data.model

data class QuizOption(
    val id: Int,
    val text: String
)

data class QuizQuestion(
    val id: String,
    val question: String,
    val options: List<QuizOption>,
    val correctOptionIndex: Int,
    val explanation: String,
    val codeBlock: String = "",
    val hint: String = "",
    val isTrapQuestion: Boolean = false,
    val isExtraHard: Boolean = false,
    val difficulty: String = "Medium",
    val topic: String = "General"
)

data class Quiz(
    val id: String,
    val subjectId: String,
    val title: String,
    val topic: String,
    val questions: List<QuizQuestion>,
    val xpReward: Int = 20,
    val timeLimitSeconds: Int = 60,
    val difficulty: String = "Medium"
)

data class ChallengeTask(
    val id: String,
    val title: String,
    val isCompleted: Boolean = false
)

enum class ChallengeType {
    DAILY,
    WEEKLY,
    SUBJECT,
    DEPARTMENT,
    COLLEGE
}

data class Challenge(
    val id: String,
    val type: ChallengeType,
    val title: String,
    val description: String,
    val subject: String,
    val xpReward: Int = 30,
    val totalQuestions: Int = 5,
    val remainingTime: String = "14h 32m",
    val participantsCount: Int = 142,
    val isCompleted: Boolean = false,
    val questions: List<QuizQuestion> = emptyList()
)

enum class LeaderboardScope {
    GLOBAL,
    COLLEGE,
    DEPARTMENT,
    SUBJECT,
    WEEKLY,
    MONTHLY
}

data class LeaderboardEntry(
    val rank: Int,
    val userId: String,
    val name: String,
    val avatarUrl: String = "",
    val college: String,
    val department: String,
    val xp: Int,
    val rankTier: RankTier,
    val badge: String,
    val studentsHelped: Int,
    val isCurrentUser: Boolean = false
)

data class Reward(
    val id: String,
    val title: String,
    val description: String,
    val category: String,
    val icon: String,
    val requiredXp: Int,
    val requiredRank: RankTier,
    val isUnlocked: Boolean = false,
    val isClaimed: Boolean = false,
    val perkCode: String = ""
)
