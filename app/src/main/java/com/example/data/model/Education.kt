package com.example.data.model

data class LectureChapter(
    val title: String,
    val timestamp: String,
    val progressPercent: Int
)

enum class LiveLectureStatus {
    UPCOMING,
    LIVE_NOW,
    ENDED
}

data class Lecture(
    val id: String,
    val educationLevel: String = "B.Tech",
    val department: String = "CSE",
    val subjectId: String,
    val subjectName: String = "C Programming",
    val topic: String = "Pointer Arithmetic",
    val title: String,
    val description: String,
    val teacherName: String,
    val teacherAvatar: String = "",
    val durationMinutes: Int = 20,
    val isLive: Boolean = false,
    val liveStatus: LiveLectureStatus = LiveLectureStatus.UPCOMING,
    val date: String = "Today",
    val startTime: String = "7:00 PM",
    val endTime: String = "8:00 PM",
    val meetingUrl: String = "https://teach2learn.peer/live/room-c99",
    val scheduledTime: String = "7:00 PM Today",
    val studentCount: Int = 24,
    val progressPercent: Int = 0,
    val lastPositionSeconds: Int = 0,
    val isCompleted: Boolean = false,
    val videoUrl: String = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
    val thumbnail: String = "🎬",
    val notesSummary: String = "Covers pointer arithmetic, memory allocation with malloc/calloc, and pointer-to-pointer dereferencing.",
    val keyTakeaways: List<String> = listOf("Memory addresses & pointers", "Pointer arithmetic rules", "Dynamic allocation safety"),
    val isReminderSet: Boolean = false,
    val reminderLeadMinutes: Int = 15,
    val chapters: List<LectureChapter> = emptyList(),
    val codeSnippet: String = "",
    val transcript: String = ""
)

data class SubjectNote(
    val id: String,
    val educationLevel: String = "B.Tech",
    val department: String = "CSE",
    val subjectId: String,
    val subjectName: String = "C Programming",
    val topic: String = "Pointers & Memory",
    val title: String,
    val author: String,
    val createdAt: String = "Aug 2026",
    val pagesCount: Int = 8,
    val downloads: Int = 142,
    val rating: Float = 4.9f,
    val isPremiumOnly: Boolean = false,
    val contentPreview: String = "Key Formulae, Cheatsheet, and Solved university questions.",
    val category: String = "Formula Sheet", // Formula Sheet, Solved Papers, Cheatsheet, Handwritten Notes, Lab Code
    val isBookmarked: Boolean = false,
    val isRead: Boolean = false,
    val importantPoints: List<String> = emptyList(),
    val keyFormulas: List<String> = emptyList(),
    val chapters: List<String> = emptyList(),
    val fullContent: String = ""
)

data class Subject(
    val id: String,
    val name: String,
    val code: String,
    val department: String,
    val educationLevel: String,
    val icon: String,
    val description: String,
    val progressPercent: Int = 60,
    val teachingProgressPercent: Int = 40,
    val topics: List<String> = emptyList(),
    val totalLectures: Int = 14,
    val totalDoubts: Int = 28,
    val totalTeachers: Int = 9
)

data class Department(
    val id: String,
    val name: String,
    val code: String,
    val icon: String,
    val description: String,
    val educationLevel: String
)

data class EducationLevel(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: String
)

