package com.example.data.model

enum class RequestStatus {
    PENDING,
    ACCEPTED,
    REJECTED,
    COMPLETED
}

data class StudentTeacher(
    val id: String,
    val userId: String,
    val name: String,
    val avatarUrl: String = "",
    val college: String,
    val department: String,
    val subjects: List<String>,
    val skillLevel: String = "Mentor",
    val rating: Float = 4.9f,
    val xp: Int = 1850,
    val rank: RankTier = RankTier.MASTER,
    val studentsHelped: Int = 86,
    val sessionsCompleted: Int = 42,
    val availableDays: String = "Mon, Wed, Fri, Sat",
    val availableTime: String = "7:00 PM - 9:30 PM",
    val teachingMode: String = "Online Live / Video",
    val bio: String = "Specialized in Pointers, Data Structures, and Python algorithms. Happy to simplify tough concepts step by step!",
    val isOnlineNow: Boolean = true
)

data class TeachingRequest(
    val id: String,
    val studentId: String,
    val studentName: String,
    val teacherId: String,
    val teacherName: String,
    val subject: String,
    val topic: String,
    val requestedTime: String,
    val message: String,
    val status: RequestStatus = RequestStatus.PENDING,
    val suggestedNewTime: String? = null,
    val createdAt: String = "Just now"
)

data class TeachingSession(
    val id: String,
    val teacherName: String,
    val studentName: String,
    val subject: String,
    val topic: String,
    val scheduledTime: String,
    val isLive: Boolean = true,
    val isCompleted: Boolean = false,
    val whiteboardContent: String = "// Live Peer Code Workspace\n#include <stdio.h>\n\nint main() {\n    int num = 42;\n    int *ptr = &num;\n    printf(\"Value: %d\\nAddress: %p\\n\", *ptr, ptr);\n    return 0;\n}",
    val sharedNotes: String = "- Pointers store memory addresses of variables.\n- Dereferencing with * gets the actual value.\n- Array name acts as pointer to first element.",
    val studentRating: Float = 5.0f,
    val studentFeedback: String = "Explained pointers with super clear memory diagrams! Everything clicked."
)
