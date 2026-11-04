package com.example.data.model

data class VideoComment(
    val id: String,
    val author: String,
    val text: String,
    val timestamp: String,
    val likes: Int = 2
)

data class ShortVideo(
    val id: String,
    val title: String,
    val subject: String = "C Programming",
    val topic: String = "Pointer Traps",
    val category: String = "Programming",
    val creatorName: String,
    val creatorAvatar: String = "",
    val creatorRank: RankTier,
    val videoUrl: String = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
    val thumbnail: String = "⚡",
    val summary: String,
    val keyConcept: String,
    val durationSeconds: Int = 60,
    val likesCount: Int = 84,
    val commentsCount: Int = 12,
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val isFollowing: Boolean = false,
    val isWatched: Boolean = false,
    val comments: List<VideoComment> = emptyList(),
    val quickQuizQuestion: String = "",
    val quickQuizOptions: List<String> = emptyList(),
    val quickQuizCorrectIndex: Int = 0,
    val quickQuizExplanation: String = ""
)

data class NotificationItem(
    val id: String,
    val title: String,
    val message: String,
    val timestamp: String,
    val type: String, // XP, RANK, TEACHING_REQ, LECTURE, DOUBT, REWARD
    val icon: String,
    val isRead: Boolean = false
)

