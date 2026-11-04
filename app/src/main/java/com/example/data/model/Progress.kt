package com.example.data.model

enum class TimelineActivityType(val label: String, val icon: String) {
    TOPIC_COMPLETED("Topic Completed", "✅"),
    QUIZ_RESULT("Quiz Result", "⚡"),
    PRACTICE_SESSION("Practice Session", "💻"),
    STUDY_ACTIVITY("Study Activity", "📖"),
    TEACHING_SESSION("Teaching Session", "🎓")
}

data class SubjectTopicProgress(
    val topicName: String,
    val isCompleted: Boolean,
    val accuracyPercent: Int
)

data class SubjectProgress(
    val subjectId: String,
    val subjectName: String,
    val code: String,
    val icon: String,
    val progressPercent: Int,
    val topicsCompleted: Int,
    val totalTopics: Int,
    val quizAveragePercent: Int,
    val masteryLevel: String, // e.g. "Proficient", "Intermediate", "Needs Review"
    val topicBreakdown: List<SubjectTopicProgress> = emptyList()
)

data class LearningStrength(
    val id: String,
    val title: String,
    val description: String,
    val metric: String,
    val icon: String,
    val category: String // "Quiz Performance", "Completed Topics", "Consistency", "Activity"
)

data class ImprovementArea(
    val id: String,
    val subjectName: String,
    val topic: String,
    val currentAccuracyPercent: Int,
    val issueDescription: String,
    val recommendation: String,
    val actionLabel: String,
    val actionType: String = "quiz" // "quiz", "notes", "lecture"
)

data class LearningHistoryItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val timestamp: String,
    val relativeTime: String,
    val type: TimelineActivityType,
    val scorePercent: Int? = null,
    val xpEarned: Int,
    val icon: String,
    val details: String = ""
)

data class DailyActivity(
    val dayName: String,
    val shortDay: String,
    val currentWeekHours: Float,
    val previousWeekHours: Float,
    val isToday: Boolean = false
)

data class WeeklyProgressComparison(
    val currentWeekHours: Float,
    val previousWeekHours: Float,
    val currentWeekTopics: Int,
    val previousWeekTopics: Int,
    val currentWeekQuizzes: Int,
    val previousWeekQuizzes: Int,
    val currentWeekTeachingMinutes: Int,
    val previousWeekTeachingMinutes: Int,
    val dailyBreakdown: List<DailyActivity>
) {
    val hoursGrowthPercent: Int
        get() = if (previousWeekHours > 0) (((currentWeekHours - previousWeekHours) / previousWeekHours) * 100).toInt() else 100

    val topicsGrowthPercent: Int
        get() = if (previousWeekTopics > 0) (((currentWeekTopics - previousWeekTopics).toFloat() / previousWeekTopics) * 100).toInt() else 100
}

data class LearningGoal(
    val id: String,
    val title: String,
    val category: String, // "Exam Prep", "Quiz Target", "Skill Mastery", "Peer Teaching"
    val currentProgress: Int,
    val targetProgress: Int,
    val unit: String, // "Topics", "Hours", "Quizzes", "Sessions"
    val deadline: String,
    val isCompleted: Boolean = false
) {
    val progressPercent: Int
        get() = if (targetProgress > 0) ((currentProgress.toFloat() / targetProgress) * 100).toInt().coerceIn(0, 100) else 0
}

data class Teach2LearnScoreBreakdown(
    val learnScore: Int, // 0-100: based on learning activity, completed topics, quizzes, practice
    val teachScore: Int, // 0-100: based on teaching sessions, students helped, peer doubts answered
    val overallScore: Int, // composite score
    val learnDetails: String,
    val teachDetails: String,
    val evaluationSummary: String
)
