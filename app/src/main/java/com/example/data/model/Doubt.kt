package com.example.data.model

data class DoubtAnswer(
    val id: String,
    val doubtId: String,
    val authorName: String,
    val authorRank: RankTier,
    val authorAvatar: String = "",
    val text: String,
    val codeSnippet: String = "",
    val upvotes: Int = 4,
    val isBestAnswer: Boolean = false,
    val answeredAt: String = "2 hours ago"
)

data class Doubt(
    val id: String,
    val studentName: String,
    val studentAvatar: String = "",
    val subject: String,
    val topic: String,
    val question: String,
    val codeSnippet: String = "",
    val hasImageAttachment: Boolean = false,
    val answers: List<DoubtAnswer> = emptyList(),
    val likes: Int = 8,
    val isLiked: Boolean = false,
    val isSolved: Boolean = false,
    val createdAt: String = "Today, 4:15 PM"
)
