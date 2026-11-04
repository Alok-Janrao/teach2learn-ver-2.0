package com.example.data.model

data class FirestoreShortVideo(
    val videoId: String = "",
    val title: String = "",
    val description: String = "",
    val subject: String = "C Programming",
    val teacherId: String = "",
    val teacherName: String = "",
    val videoUrl: String = "",
    val thumbnailUrl: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val views: Int = 0,
    val likes: Int = 0,
    val status: String = "active", // "active", "flagged", "removed"
    val isLiked: Boolean = false,
    val isSaved: Boolean = false,
    val durationSeconds: Int = 45
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "videoId" to videoId,
            "title" to title,
            "description" to description,
            "subject" to subject,
            "teacherId" to teacherId,
            "teacherName" to teacherName,
            "videoUrl" to videoUrl,
            "thumbnailUrl" to thumbnailUrl,
            "createdAt" to createdAt,
            "views" to views,
            "likes" to likes,
            "status" to status
        )
    }
}

data class FirestoreLiveClass(
    val classId: String = "",
    val hostId: String = "",
    val hostName: String = "",
    val title: String = "",
    val subject: String = "Engineering",
    val description: String = "",
    val scheduledAt: String = "Today, 7:00 PM",
    val status: String = "live", // "scheduled", "live", "ended"
    val roomId: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val participantCount: Int = 1,
    val teacherAvatar: String = ""
) {
    fun toMap(): Map<String, Any> {
        return mapOf(
            "classId" to classId,
            "hostId" to hostId,
            "hostName" to hostName,
            "title" to title,
            "subject" to subject,
            "description" to description,
            "scheduledAt" to scheduledAt,
            "status" to status,
            "roomId" to roomId,
            "createdAt" to createdAt,
            "participantCount" to participantCount
        )
    }
}

data class FirestoreLiveParticipant(
    val participantId: String = "",
    val classId: String = "",
    val userId: String = "",
    val userName: String = "",
    val role: String = "student", // "host", "student"
    val isMuted: Boolean = true,
    val isVideoOn: Boolean = false,
    val isHandRaised: Boolean = false,
    val joinedAt: Long = System.currentTimeMillis()
)

data class LiveClassChatMessage(
    val id: String = "",
    val classId: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val message: String = "",
    val timestamp: String = "",
    val isTeacher: Boolean = false
)
