package com.example.data.repository

import com.example.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID

class Teach2LearnRepository {

    companion object {
        const val APP_VERSION = "v1.2.0"
        const val APP_BUILD = 2
        const val OFFICIAL_WEBSITE_URL = "https://teach2learn-app.netlify.app/"
        const val OFFICIAL_APK_DOWNLOAD_URL = "https://teach2learn-app.netlify.app/teach2learn.apk"
        const val APP_TAGLINE = "Learn by Teaching. Master by Sharing."
        const val APP_SUBTITLE = "Peer-to-Peer Engineering Mentorship"
    }

    private data class UserAccount(
        val profile: UserProfile,
        val subjectProgressMap: Map<String, Pair<Int, Int>> = emptyMap(),
        val lectureProgressMap: Map<String, Pair<Int, Boolean>> = emptyMap()
    )

    private val _userAccounts = mutableMapOf<String, UserAccount>()

    private val _currentUser = MutableStateFlow(
        UserProfile(
            id = "user_alok",
            name = "Alok Janrao",
            email = "alokjanrao5@gmail.com",
            educationLevel = "B.Tech",
            college = "Government College of Engineering",
            department = "CSE",
            yearSemester = "3rd Year / 5th Sem",
            bio = "Love C programming and building Android apps! Currently exploring Data Structures and helping peers debug memory leaks.",
            xp = 1240,
            streakDays = 7,
            studentsHelped = 34,
            sessionsCompleted = 18,
            completedLectures = 8,
            completedQuizzes = 6,
            profileCompletion = 85,
            learningProgress = 65,
            teachingProgress = 50,
            rating = 4.8f,
            learnSubjects = listOf("Data Structures", "Python", "Mathematics", "AI & Data Science"),
            teachSubjects = listOf("C Programming", "Python"),
            isTeacherProfileActive = true,
            teacherAvailableDays = "Mon, Wed, Fri, Sat",
            teacherAvailableTime = "6:30 PM - 9:00 PM",
            teachingMode = "Online Live",
            isPremium = true,
            role = UserRole.ADMIN,
            badges = listOf(
                UserBadge("b1", "First Teacher", "Conducted your very first peer teaching session", "🎓", true, "Aug 10"),
                UserBadge("b2", "First Student Helped", "Successfully solved a peer's doubt", "🤝", true, "Aug 12"),
                UserBadge("b3", "Doubt Solver", "Answered 10+ doubts with top community votes", "💡", true, "Aug 18"),
                UserBadge("b4", "Quiz Master", "Scored 100% in 5 consecutive subject quizzes", "⚡", true, "Aug 22"),
                UserBadge("b5", "7-Day Streak", "Learned and taught for 7 continuous days", "🔥", true, "Aug 28"),
                UserBadge("b6", "30-Day Streak", "30 days of consistent peer learning", "🗓️", false),
                UserBadge("b7", "Top Teacher", "Reached top 5 teachers in your college", "🌟", false),
                UserBadge("b8", "Top Learner", "Completed all semester subject tracks", "🏆", false),
                UserBadge("b9", "Mentor", "Earned 2,500+ XP and mentored 50+ students", "👑", false),
                UserBadge("b10", "XP Champion", "Reached 4,000+ XP in the Global Leaderboard", "🪐", false)
            ),
            certificates = listOf(
                UserCertificate("c1", "Peer Mentor in C Programming", "T2L-CERT-C-1029", "August 2026"),
                UserCertificate("c2", "Foundational Python Educator", "T2L-CERT-PY-8831", "July 2026")
            ),
            reviews = listOf(
                UserReview("r1", "Rahul Sharma", "", 5.0f, "Alok explained C pointer arithmetic so clearly! The memory layout visualization made it easy.", "C Programming", "Yesterday"),
                UserReview("r2", "Priya Patel", "", 4.8f, "Super patient and walked me through dynamic memory allocation bugs in my lab code.", "C Programming", "3 days ago"),
                UserReview("r3", "Sneha Rao", "", 5.0f, "Helped me understand Python dictionary comprehension and file I/O within 30 minutes.", "Python", "Last week")
            )
        )
    )
    val currentUser: StateFlow<UserProfile> = _currentUser.asStateFlow()

    private val _educationLevels = MutableStateFlow(
        listOf(
            EducationLevel("btech", "B.Tech", "Bachelor of Technology / Engineering", "⚙️"),
            EducationLevel("mtech", "M.Tech", "Master of Technology", "🔬"),
            EducationLevel("diploma", "Diploma", "Polytechnic & Vocational Engineering", "📐"),
            EducationLevel("class12", "Class 12", "Higher Secondary / Science / Maths", "📚"),
            EducationLevel("class11", "Class 11", "Senior Secondary Foundation", "🧪"),
            EducationLevel("class10", "Class 10", "Secondary Board Exams", "🎒"),
            EducationLevel("other", "Other", "Competitive Exams & Upskilling", "💡")
        )
    )
    val educationLevels: StateFlow<List<EducationLevel>> = _educationLevels.asStateFlow()

    private val _departments = MutableStateFlow(
        listOf(
            Department("cse", "Computer Science & Engineering (CSE)", "CSE", "💻", "Programming, Algorithms, Systems, DBMS", "btech"),
            Department("it", "Information Technology (IT)", "IT", "🌐", "Web tech, Cloud computing, Networks", "btech"),
            Department("aids", "AI & Data Science", "AI & DS", "🤖", "Machine Learning, Deep Learning, Python", "btech"),
            Department("etc", "Electronics & Telecommunication (E&TC)", "E&TC", "📡", "Signals, Microcontrollers, Wireless", "btech"),
            Department("electrical", "Electrical Engineering", "EE", "⚡", "Circuits, Power Systems, Machines", "btech"),
            Department("mechanical", "Mechanical Engineering", "ME", "🔧", "Thermodynamics, CAD, Solid Mechanics", "btech"),
            Department("civil", "Civil Engineering", "CE", "🏗️", "Structures, Surveying, Fluid Mechanics", "btech"),
            Department("electronics", "Electronics Engineering", "ECE", "📟", "VLSI, Embedded Systems, Digital Electronics", "btech"),
            Department("other_dept", "Other Disciplines", "Other", "🎯", "General Engineering & Applied Sciences", "btech")
        )
    )
    val departments: StateFlow<List<Department>> = _departments.asStateFlow()

    private val _subjects = MutableStateFlow(
        listOf(
            Subject(
                id = "subj_c",
                name = "C Programming",
                code = "CS101",
                department = "CSE",
                educationLevel = "B.Tech",
                icon = "💻",
                description = "Master fundamental programming, pointers, memory management, and file operations in C.",
                progressPercent = 80,
                teachingProgressPercent = 70,
                topics = listOf("Introduction & Syntax", "Control Flow & Loops", "Functions & Recursion", "Arrays & Strings", "Pointers & Memory", "Structures & Unions", "File I/O"),
                totalLectures = 12,
                totalDoubts = 34,
                totalTeachers = 8
            ),
            Subject(
                id = "subj_python",
                name = "Python Programming",
                code = "CS102",
                department = "CSE",
                educationLevel = "B.Tech",
                icon = "🐍",
                description = "Explore object-oriented Python, data handling, scripting, and algorithmic problem solving.",
                progressPercent = 60,
                teachingProgressPercent = 50,
                topics = listOf("Variables & Types", "Data Structures (List, Dict, Set)", "Functions & Lambdas", "OOP & Classes", "Modules & Packages", "Exception Handling"),
                totalLectures = 15,
                totalDoubts = 29,
                totalTeachers = 11
            ),
            Subject(
                id = "subj_dsa",
                name = "Data Structures & Algorithms",
                code = "CS201",
                department = "CSE",
                educationLevel = "B.Tech",
                icon = "🌲",
                description = "Master Linked Lists, Stacks, Queues, Binary Trees, Graph Traversal, and Dynamic Programming.",
                progressPercent = 45,
                teachingProgressPercent = 30,
                topics = listOf("Time & Space Complexity", "Linked Lists", "Stacks & Queues", "Trees & BST", "Heaps & Hash Tables", "Graph Algorithms"),
                totalLectures = 18,
                totalDoubts = 52,
                totalTeachers = 14
            ),
            Subject(
                id = "subj_java",
                name = "Java Programming",
                code = "CS103",
                department = "CSE",
                educationLevel = "B.Tech",
                icon = "☕",
                description = "Core Java, Multithreading, JVM internals, Collections framework, and Design Patterns.",
                progressPercent = 50,
                teachingProgressPercent = 35,
                topics = listOf("Java Basics & JVM", "OOP Principles", "Collections Framework", "Generics", "Exception Handling", "Threads & Concurrency"),
                totalLectures = 14,
                totalDoubts = 23,
                totalTeachers = 7
            ),
            Subject(
                id = "subj_math",
                name = "Engineering Mathematics",
                code = "MA101",
                department = "CSE",
                educationLevel = "B.Tech",
                icon = "📐",
                description = "Linear Algebra, Differential Calculus, Matrices, Laplace Transforms, and Numerical Methods.",
                progressPercent = 75,
                teachingProgressPercent = 40,
                topics = listOf("Matrices & Determinants", "Eigenvalues & Vectors", "Calculus & Partial Derivatives", "Differential Equations", "Laplace Transforms"),
                totalLectures = 16,
                totalDoubts = 41,
                totalTeachers = 6
            ),
            Subject(
                id = "subj_mech",
                name = "Engineering Mechanics",
                code = "ME101",
                department = "Mechanical",
                educationLevel = "B.Tech",
                icon = "⚙️",
                description = "Statics, Dynamics, Centroids, Moment of Inertia, Friction, and Truss Analysis.",
                progressPercent = 35,
                teachingProgressPercent = 20,
                topics = listOf("Concurrent Force Systems", "Equilibrium & Free Body Diagrams", "Friction", "Centroid & Moment of Inertia", "Kinematics"),
                totalLectures = 10,
                totalDoubts = 19,
                totalTeachers = 5
            ),
            Subject(
                id = "subj_ee",
                name = "Basic Electrical Engineering",
                code = "EE101",
                department = "Electrical",
                educationLevel = "B.Tech",
                icon = "⚡",
                description = "Kirchhoff's Laws, AC circuits, Transformers, DC Motors, and Semiconductor devices.",
                progressPercent = 40,
                teachingProgressPercent = 25,
                topics = listOf("DC Circuits & Kirchhoff Laws", "Network Theorems", "AC Circuit Analysis", "Single Phase Transformers", "Electrical Machines"),
                totalLectures = 11,
                totalDoubts = 26,
                totalTeachers = 5
            )
        )
    )
    val subjects: StateFlow<List<Subject>> = _subjects.asStateFlow()

    private val _lectures = MutableStateFlow(
        listOf(
            Lecture(
                id = "lec_live_1",
                subjectId = "subj_c",
                title = "Deep Dive into Pointers & Pointer Arithmetic",
                description = "Live interactive breakdown of pointer arithmetic, pointer-to-pointer dereferencing, and memory addresses with live peer coding.",
                teacherName = "Rahul Sharma",
                durationMinutes = 45,
                isLive = true,
                scheduledTime = "7:00 PM Today",
                studentCount = 28,
                progressPercent = 0,
                isCompleted = false,
                isReminderSet = false,
                reminderLeadMinutes = 15,
                chapters = listOf(
                    LectureChapter("Introduction & Virtual Memory Layout", "00:00", 0),
                    LectureChapter("Address-Of (&) and Dereference (*) Operators", "08:15", 20),
                    LectureChapter("Pointer Arithmetic & Array Offsets", "18:30", 45),
                    LectureChapter("Double Pointers (**ptr) Demystified", "28:40", 70),
                    LectureChapter("Live Student Code Debugging & Q&A", "38:00", 90)
                ),
                codeSnippet = "#include <stdio.h>\n#include <stdlib.h>\n\nint main() {\n    int arr[5] = {10, 20, 30, 40, 50};\n    int *p = arr;\n    printf(\"*(p + 2) = %d\\n\", *(p + 2)); // 30\n    int **pp = &p;\n    printf(\"**pp = %d\\n\", **pp);       // 10\n    return 0;\n}",
                transcript = "Welcome everyone! Today we are dissecting Pointer Arithmetic. Remember that adding 1 to a pointer advances it by sizeof(type) bytes, never just 1 physical byte in memory."
            ),
            Lecture(
                id = "lec_live_2",
                subjectId = "subj_python",
                title = "Python Object Oriented Programming & Dunder Methods",
                description = "Live masterclass explaining classes, constructors, __repr__, inheritance, and custom iterators.",
                teacherName = "Sneha Rao",
                durationMinutes = 40,
                isLive = true,
                scheduledTime = "8:30 PM Today",
                studentCount = 35,
                progressPercent = 0,
                isCompleted = false,
                isReminderSet = false,
                reminderLeadMinutes = 15,
                chapters = listOf(
                    LectureChapter("Classes vs Instances in Python", "00:00", 0),
                    LectureChapter("__init__ vs __new__ Under the Hood", "07:20", 25),
                    LectureChapter("Operator Overloading (__add__, __str__)", "16:45", 50),
                    LectureChapter("Creating Custom Iterators with __iter__", "27:10", 75),
                    LectureChapter("Live Interactive Coding Exercise", "35:00", 95)
                ),
                codeSnippet = "class Vector:\n    def __init__(self, x, y):\n        self.x, self.y = x, y\n    def __add__(self, other):\n        return Vector(self.x + other.x, self.y + other.y)\n    def __repr__(self):\n        return f'Vector({self.x}, {self.y})'\n\nv1 = Vector(2, 3)\nv2 = Vector(4, 5)\nprint(v1 + v2) # Vector(6, 8)",
                transcript = "Hello learners! Dunder (double-underscore) methods allow your custom classes to integrate seamlessly with Python's built-in syntax like plus operator and print statements."
            ),
            Lecture(
                id = "lec_live_3",
                subjectId = "subj_dsa",
                title = "Graph Traversal Masterclass: BFS, DFS & Dijkstra",
                description = "Live interactive visual session on shortest paths, priority queues, and graph cycle detection.",
                teacherName = "Priya Patel",
                durationMinutes = 50,
                isLive = true,
                scheduledTime = "Tomorrow, 6:00 PM",
                studentCount = 42,
                progressPercent = 0,
                isCompleted = false,
                isReminderSet = false,
                reminderLeadMinutes = 30,
                chapters = listOf(
                    LectureChapter("Adjacency List vs Matrix Representation", "00:00", 0),
                    LectureChapter("Breadth First Search (Queue Pattern)", "10:15", 25),
                    LectureChapter("Depth First Search & Topological Sort", "22:00", 50),
                    LectureChapter("Dijkstra's Algorithm with Min-Heap", "34:30", 75),
                    LectureChapter("Live Problem Solving & Doubt Clearing", "44:00", 90)
                ),
                codeSnippet = "from collections import deque\n\ndef bfs(graph, start):\n    visited = {start}\n    queue = deque([start])\n    while queue:\n        node = queue.popleft()\n        print(node, end=' ')\n        for neighbor in graph[node]:\n            if neighbor not in visited:\n                visited.add(neighbor)\n                queue.append(neighbor)",
                transcript = "Graphs are everywhere from social networks to GPS routing. Today we learn how BFS explores level by level while DFS dives deep recursively."
            ),
            Lecture(
                id = "lec_rec_1",
                subjectId = "subj_c",
                title = "Dynamic Memory Allocation (malloc, calloc, realloc, free)",
                description = "Understand heap vs stack allocation, memory leak prevention, and debugging segmentation faults.",
                teacherName = "Rahul Sharma",
                durationMinutes = 22,
                isLive = false,
                scheduledTime = "Recorded",
                studentCount = 142,
                progressPercent = 70,
                isCompleted = false,
                chapters = listOf(
                    LectureChapter("Stack Memory vs Heap Memory", "00:00", 0),
                    LectureChapter("malloc() vs calloc() initialization", "05:10", 25),
                    LectureChapter("Expanding Buffers with realloc()", "11:40", 55),
                    LectureChapter("Avoiding Memory Leaks with free()", "17:20", 80)
                ),
                codeSnippet = "int *arr = (int*) malloc(5 * sizeof(int));\nif (arr == NULL) {\n    printf(\"Memory allocation failed!\\n\");\n    return 1;\n}\n// Initialize & use arr\nfree(arr);\narr = NULL; // Prevent dangling pointer!",
                transcript = "Stack memory is automatically managed by function frames, but heap memory allocated via malloc must always be explicitly released using free()."
            ),
            Lecture(
                id = "lec_rec_2",
                subjectId = "subj_c",
                title = "Structs, Unions, and Bitfields in C",
                description = "How complex data types are stored in memory with padding, alignment, and bitwise flags.",
                teacherName = "Alok Janrao",
                durationMinutes = 18,
                isLive = false,
                scheduledTime = "Recorded",
                studentCount = 98,
                progressPercent = 100,
                isCompleted = true,
                chapters = listOf(
                    LectureChapter("Struct Definition & Member Access", "00:00", 0),
                    LectureChapter("Struct Memory Padding & Alignment Rules", "04:30", 30),
                    LectureChapter("Unions: Shared Memory Concept", "09:50", 60),
                    LectureChapter("Bitfields for Embedded Efficiency", "14:10", 85)
                ),
                codeSnippet = "struct Packet {\n    unsigned int id : 4;       // 4 bits\n    unsigned int flag : 1;     // 1 bit\n    unsigned int priority : 3; // 3 bits\n}; // Total: fits in exactly 1 byte!",
                transcript = "Compiler alignment pads struct members to CPU word boundaries. Ordering members from largest to smallest saves precious RAM in embedded firmware."
            ),
            Lecture(
                id = "lec_rec_3",
                subjectId = "subj_python",
                title = "Decorators and Generator Functions in Python",
                description = "Master closures, wrapper functions, yield keyword, and memory-efficient streaming pipelines.",
                teacherName = "Sneha Rao",
                durationMinutes = 25,
                isLive = false,
                scheduledTime = "Recorded",
                studentCount = 180,
                progressPercent = 40,
                isCompleted = false,
                chapters = listOf(
                    LectureChapter("Function Closures in Python", "00:00", 0),
                    LectureChapter("Writing Custom Timing & Logging Decorators", "06:15", 30),
                    LectureChapter("Passing Arguments to Decorators", "13:00", 60),
                    LectureChapter("Generators & Memory Streams with yield", "19:20", 85)
                ),
                codeSnippet = "import time\ndef timer(func):\n    def wrapper(*args, **kwargs):\n        start = time.time()\n        result = func(*args, **kwargs)\n        print(f'{func.__name__} took {time.time() - start:.4f}s')\n        return result\n    return wrapper",
                transcript = "Decorators let you wrap existing functions with reusable cross-cutting concerns like authentication, caching, or performance profiling."
            ),
            Lecture(
                id = "lec_rec_4",
                subjectId = "subj_dsa",
                title = "Binary Tree Traversals (Inorder, Preorder, Postorder & Level Order)",
                description = "Recursive vs iterative stack implementations with complete dry-run trace.",
                teacherName = "Priya Patel",
                durationMinutes = 30,
                isLive = false,
                scheduledTime = "Recorded",
                studentCount = 210,
                progressPercent = 85,
                isCompleted = false,
                chapters = listOf(
                    LectureChapter("Tree Terminology & Node Structure", "00:00", 0),
                    LectureChapter("Inorder Traversal (Left, Root, Right)", "05:40", 20),
                    LectureChapter("Preorder & Postorder Logic", "12:15", 45),
                    LectureChapter("Iterative Stack-based Traversals", "19:00", 70),
                    LectureChapter("Level-Order Traversal with Queue", "24:50", 90)
                ),
                codeSnippet = "class TreeNode:\n    def __init__(self, val=0, left=None, right=None):\n        self.val = val\n        self.left = left\n        self.right = right\n\ndef inorder(root):\n    return inorder(root.left) + [root.val] + inorder(root.right) if root else []",
                transcript = "Inorder traversal on a Binary Search Tree always outputs elements in strictly sorted ascending order!"
            )
        )
    )
    val lectures: StateFlow<List<Lecture>> = _lectures.asStateFlow()

    private val _notes = MutableStateFlow(
        listOf(
            SubjectNote(
                id = "n1",
                subjectId = "subj_c",
                title = "C Programming Complete Formula & Pointer Cheatsheet",
                author = "Rahul Sharma",
                pagesCount = 14,
                downloads = 380,
                rating = 4.9f,
                isPremiumOnly = false,
                category = "Cheatsheet",
                isBookmarked = true,
                contentPreview = "Includes pointer syntax rules, memory leak diagnostics, and top 20 viva questions.",
                keyFormulas = listOf(
                    "*(arr + i) == arr[i] (Pointer Arithmetic Equivalence)",
                    "sizeof(arr) / sizeof(arr[0]) (Element Count Macro)",
                    "malloc(num * sizeof(type)) vs calloc(num, sizeof(type)) (Zero Initialized)",
                    "ptr = &var; *ptr = val; (Dereferencing)"
                ),
                chapters = listOf("Pointers & Arithmetic", "Dynamic Memory Allocation", "Struct Alignment & Unions", "Bitwise Operators & Bitfields", "File Handling in C"),
                fullContent = "1. POINTER ARITHMETIC RULES:\n• Adding integer k to pointer of type T advances address by k * sizeof(T).\n• Subtracting two pointers gives number of elements between them, not bytes.\n\n2. DYNAMIC MEMORY BEST PRACTICES:\n• Always check if pointer returned by malloc is NULL.\n• Set pointer to NULL after calling free() to avoid dangling pointer bugs.\n• Avoid memory leaks: every malloc must have a matching free."
            ),
            SubjectNote(
                id = "n2",
                subjectId = "subj_c",
                title = "University Solved C Exam Papers (2022-2025)",
                author = "Alok Janrao",
                pagesCount = 28,
                downloads = 520,
                rating = 4.8f,
                isPremiumOnly = false,
                category = "Solved Papers",
                isBookmarked = false,
                contentPreview = "Full step-by-step solved university questions with commented code blocks and viva answers.",
                keyFormulas = listOf(
                    "fopen(filename, mode) Modes: 'r', 'w', 'a', 'rb', 'wb'",
                    "typedef struct Node { int data; struct Node* next; } Node;",
                    "argc = argument count, argv[] = argument vector strings"
                ),
                chapters = listOf("Section A: 2-Mark Conceptual Viva", "Section B: 5-Mark Coding Algorithms", "Section C: 10-Mark System Implementations", "Model Question Papers 1-4"),
                fullContent = "UNIVERSITY QUESTION 1:\nExplain the difference between call by value and call by reference in C.\nSolution:\nIn Call by Value, a copy of the actual parameter is passed to the function. Changes made inside the function do not affect the original variable.\nIn Call by Reference, the address of the variable is passed using pointers (*). Modifications inside the function directly alter the original variable in memory."
            ),
            SubjectNote(
                id = "n3",
                subjectId = "subj_python",
                title = "Python OOP, Decorators & DSA Quick Revision Notes",
                author = "Sneha Rao",
                pagesCount = 18,
                downloads = 410,
                rating = 4.9f,
                isPremiumOnly = false,
                category = "Handwritten Notes",
                isBookmarked = true,
                contentPreview = "Clean summary of classes, magic methods, list comprehensions, time complexity and generators.",
                keyFormulas = listOf(
                    "[expr for item in iterable if condition] (List Comprehension)",
                    "{k: v for k, v in zip(keys, vals)} (Dict Comprehension)",
                    "time.perf_counter() for microsecond benchmarking",
                    "collections.defaultdict(list) & Counter()"
                ),
                chapters = listOf("Core Data Structures (Lists, Dicts, Sets, Tuples)", "OOP & Dunder Methods", "Decorators & Closures", "Generators & Memory Streams", "Exception Handling"),
                fullContent = "PYTHON OOP PRINCIPLES:\n1. Encapsulation: Use leading underscore `_var` for protected and `__var` for private name-mangling.\n2. Inheritance: `class Derived(Base): super().__init__()`.\n3. Polymorphism: Duck typing - 'If it quacks like a duck, it is a duck'.\n4. Dunder Methods: `__repr__` for unambiguous developer representation, `__str__` for user readable text."
            ),
            SubjectNote(
                id = "n4",
                subjectId = "subj_dsa",
                title = "DSA Ultimate Handwritten Revision Guide & Visual Proofs",
                author = "Priya Patel",
                pagesCount = 32,
                downloads = 890,
                rating = 5.0f,
                isPremiumOnly = true,
                category = "Handwritten Notes",
                isBookmarked = false,
                contentPreview = "Visual diagram breakdowns of Trees, Graphs, Dynamic Programming and Recurrence Relations.",
                keyFormulas = listOf(
                    "Master Theorem: T(n) = aT(n/b) + O(n^d)",
                    "Binary Heap Parent: (i - 1)//2, Left: 2i + 1, Right: 2i + 2",
                    "Dijkstra Time Complexity: O((V + E) log V) with Min-Heap",
                    "Floyd-Warshall All-Pairs Shortest Path: O(V^3)"
                ),
                chapters = listOf("Asymptotic Analysis (Big-O, Omega, Theta)", "Linked Lists & Fast-Slow Pointers", "Binary Trees & AVL Rotations", "Graph Algorithms (BFS, DFS, Dijkstra, MST)", "Dynamic Programming Patterns"),
                fullContent = "TREE BALANCING INVARIANTS:\nAn AVL tree maintains a balance factor of {-1, 0, +1} for every node.\n• Left-Left heavy -> Right Rotation on pivot.\n• Right-Right heavy -> Left Rotation on pivot.\n• Left-Right heavy -> Left Rotation on child, then Right Rotation on parent.\n• Right-Left heavy -> Right Rotation on child, then Left Rotation on parent."
            ),
            SubjectNote(
                id = "n5",
                subjectId = "subj_math",
                title = "Engineering Mathematics Quick Formula Pocketbook",
                author = "Aman Verma",
                pagesCount = 20,
                downloads = 340,
                rating = 4.85f,
                isPremiumOnly = false,
                category = "Formula Sheet",
                isBookmarked = false,
                contentPreview = "Matrices, Eigenvalues, Laplace Transforms, Fourier Series, and Differential Equations.",
                keyFormulas = listOf(
                    "|A - λI| = 0 (Characteristic Equation for Eigenvalues)",
                    "L{e^(at)} = 1 / (s - a) for s > a (Laplace Transform)",
                    "L{sin(at)} = a / (s^2 + a^2), L{cos(at)} = s / (s^2 + a^2)",
                    "Cayley-Hamilton Theorem: Every square matrix satisfies its own characteristic equation."
                ),
                chapters = listOf("Linear Algebra & Matrices", "Differential Calculus & Jacobians", "Laplace Transforms & Inverses", "Fourier Series & Harmonics", "Vector Calculus (Grad, Div, Curl)"),
                fullContent = "EIGENVALUES AND EIGENVECTORS:\nFor matrix A, solve det(A - λI) = 0 for eigenvalues λ.\nProperties:\n1. Sum of eigenvalues = Trace of matrix (sum of main diagonal elements).\n2. Product of eigenvalues = Determinant of matrix det(A).\n3. Eigenvalues of symmetric matrix are always real."
            ),
            SubjectNote(
                id = "n6",
                subjectId = "subj_ee",
                title = "Basic Electrical Engineering Lab & Formula Guide",
                author = "Aman Verma",
                pagesCount = 16,
                downloads = 260,
                rating = 4.75f,
                isPremiumOnly = false,
                category = "Formula Sheet",
                isBookmarked = false,
                contentPreview = "Kirchhoff's Laws, Thevenin & Norton Theorems, Transformers, and AC circuit analysis.",
                keyFormulas = listOf(
                    "V = IR, P = VI = I^2 R = V^2 / R (Ohm's & Joule's Law)",
                    "Z = sqrt(R^2 + (X_L - X_C)^2) (AC Impedance)",
                    "Transformer Ratio: V1 / V2 = N1 / N2 = I2 / I1",
                    "Resonance Frequency: f_r = 1 / (2 * pi * sqrt(L * C))"
                ),
                chapters = listOf("DC Circuit Analysis & Network Theorems", "Single-Phase AC Circuits", "Three-Phase Power Systems", "Magnetic Circuits & Transformers", "DC Machines & Induction Motors"),
                fullContent = "THEVENIN'S THEOREM:\nAny linear electrical network with voltage/current sources and resistances can be replaced by an equivalent circuit containing a single voltage source (V_th) in series with a single resistance (R_th).\n• V_th is open circuit voltage across terminals.\n• R_th is resistance with all independent voltage sources short-circuited and current sources open-circuited."
            )
        )
    )
    val notes: StateFlow<List<SubjectNote>> = _notes.asStateFlow()

    private val _studentTeachers = MutableStateFlow(
        listOf(
            StudentTeacher(
                id = "t_rahul",
                userId = "user_rahul",
                name = "Rahul Sharma",
                college = "National Institute of Technology",
                department = "CSE",
                subjects = listOf("C Programming", "Data Structures", "System Design"),
                skillLevel = "Master",
                rating = 4.9f,
                xp = 2450,
                rank = RankTier.MASTER,
                studentsHelped = 86,
                sessionsCompleted = 42,
                availableDays = "Mon, Wed, Fri, Sat",
                availableTime = "7:00 PM - 9:30 PM",
                teachingMode = "Online Live / Video",
                bio = "C enthusiast! Ranked top 3 peer teachers. I love debugging tough segmentation faults with visual memory graphs."
            ),
            StudentTeacher(
                id = "t_sneha",
                userId = "user_sneha",
                name = "Sneha Rao",
                college = "Govt Engineering College",
                department = "AI & Data Science",
                subjects = listOf("Python Programming", "Machine Learning", "Mathematics"),
                skillLevel = "Mentor",
                rating = 4.95f,
                xp = 2890,
                rank = RankTier.MENTOR,
                studentsHelped = 112,
                sessionsCompleted = 58,
                availableDays = "Daily",
                availableTime = "5:00 PM - 8:00 PM",
                teachingMode = "Online Video & Code Workspace",
                bio = "Peer educator who believes anyone can write beautiful Python code. 100+ students helped pass campus lab exams!"
            ),
            StudentTeacher(
                id = "t_priya",
                userId = "user_priya",
                name = "Priya Patel",
                college = "Institute of Technology",
                department = "CSE",
                subjects = listOf("Data Structures", "Java Programming", "Algorithms"),
                skillLevel = "Master",
                rating = 4.85f,
                xp = 2180,
                rank = RankTier.MASTER,
                studentsHelped = 74,
                sessionsCompleted = 36,
                availableDays = "Tue, Thu, Sat, Sun",
                availableTime = "6:00 PM - 9:00 PM",
                teachingMode = "Online Interactive",
                bio = "Specializing in Binary Trees, Graphs, and recursion patterns. Let's conquer LeetCode & university exams together!"
            ),
            StudentTeacher(
                id = "t_aman",
                userId = "user_aman",
                name = "Aman Verma",
                college = "College of Engineering",
                department = "Electrical",
                subjects = listOf("Engineering Mathematics", "Electrical Engineering"),
                skillLevel = "Expert",
                rating = 4.75f,
                xp = 1420,
                rank = RankTier.EXPERT,
                studentsHelped = 48,
                sessionsCompleted = 22,
                availableDays = "Mon, Thu, Sun",
                availableTime = "4:00 PM - 7:00 PM",
                teachingMode = "Online Whiteboard",
                bio = "Mathematics made simple through intuitive graphical explanations and real-life electrical analogies."
            )
        )
    )
    val studentTeachers: StateFlow<List<StudentTeacher>> = _studentTeachers.asStateFlow()

    private val _teachingRequests = MutableStateFlow(
        listOf(
            TeachingRequest(
                id = "req_1",
                studentId = "user_priya",
                studentName = "Priya Patel",
                teacherId = "user_alok",
                teacherName = "Alok Janrao",
                subject = "C Programming",
                topic = "Pointer Arithmetic & Array Offsets",
                requestedTime = "Tomorrow, 7:30 PM",
                message = "Hey Alok, having trouble calculating pointer difference in 2D arrays for my assignment tomorrow. Could you help?",
                status = RequestStatus.PENDING,
                createdAt = "10 mins ago"
            ),
            TeachingRequest(
                id = "req_2",
                studentId = "user_rahul",
                studentName = "Rahul Sharma",
                teacherId = "user_alok",
                teacherName = "Alok Janrao",
                subject = "Python Programming",
                topic = "Multiprocessing vs Threading",
                requestedTime = "Friday, 8:00 PM",
                message = "Want to review GIL and multiprocessing pools for a project. Let's do a peer session!",
                status = RequestStatus.ACCEPTED,
                createdAt = "Yesterday"
            )
        )
    )
    val teachingRequests: StateFlow<List<TeachingRequest>> = _teachingRequests.asStateFlow()

    private val _teachingSessions = MutableStateFlow(
        listOf(
            TeachingSession(
                id = "sess_current",
                teacherName = "Rahul Sharma",
                studentName = "Alok Janrao",
                subject = "C Programming",
                topic = "Pointers & Memory Layout",
                scheduledTime = "7:30 PM",
                isLive = true,
                isCompleted = false
            )
        )
    )
    val teachingSessions: StateFlow<List<TeachingSession>> = _teachingSessions.asStateFlow()

    private val _doubts = MutableStateFlow(
        listOf(
            Doubt(
                id = "d1",
                studentName = "Alok Janrao",
                subject = "C Programming",
                topic = "Pointers & Segfaults",
                question = "Why does dereferencing an uninitialized pointer (wild pointer) cause a Segmentation Fault instead of printing 0?",
                codeSnippet = "int *ptr;\nprintf(\"%d\", *ptr); // Crashes with SIGSEGV",
                hasImageAttachment = false,
                likes = 12,
                isLiked = false,
                isSolved = true,
                createdAt = "2 hours ago",
                answers = listOf(
                    DoubtAnswer(
                        id = "ans1",
                        doubtId = "d1",
                        authorName = "Rahul Sharma",
                        authorRank = RankTier.MASTER,
                        text = "Because an uninitialized local pointer contains arbitrary garbage memory addresses. When you dereference it (*ptr), the CPU MMU attempts to access an address outside your process's allocated virtual address space, triggering the OS memory protection fault (SIGSEGV).",
                        codeSnippet = "// Always initialize or set to NULL\nint *ptr = NULL;\nif (ptr != NULL) { printf(\"%d\", *ptr); }",
                        upvotes = 9,
                        isBestAnswer = true,
                        answeredAt = "1 hour ago"
                    ),
                    DoubtAnswer(
                        id = "ans2",
                        doubtId = "d1",
                        authorName = "Sneha Rao",
                        authorRank = RankTier.MENTOR,
                        text = "Think of memory like house addresses. An uninitialized pointer holds a random random address (e.g. 0x7FFE0012). Your program doesn't have permission to open that door, so the OS stops it immediately!",
                        upvotes = 5,
                        isBestAnswer = false,
                        answeredAt = "45 mins ago"
                    )
                )
            ),
            Doubt(
                id = "d2",
                studentName = "Priya Patel",
                subject = "Python Programming",
                topic = "Mutable Default Arguments",
                question = "Why does a default empty list in a Python function parameter retain previous items across multiple calls?",
                codeSnippet = "def append_item(val, items=[]):\n    items.append(val)\n    return items\n\nprint(append_item(1)) # [1]\nprint(append_item(2)) # [1, 2] why?",
                hasImageAttachment = false,
                likes = 15,
                isLiked = true,
                isSolved = true,
                createdAt = "5 hours ago",
                answers = listOf(
                    DoubtAnswer(
                        id = "ans3",
                        doubtId = "d2",
                        authorName = "Sneha Rao",
                        authorRank = RankTier.MENTOR,
                        text = "Default parameter expressions in Python are evaluated once when the function is defined, NOT each time the function is called! So the list object is created only once in memory and shared.",
                        codeSnippet = "def append_item(val, items=None):\n    if items is None:\n        items = []\n    items.append(val)\n    return items",
                        upvotes = 14,
                        isBestAnswer = true,
                        answeredAt = "4 hours ago"
                    )
                )
            ),
            Doubt(
                id = "d3",
                studentName = "Aman Verma",
                subject = "Data Structures",
                topic = "Linked List Cycle Detection",
                question = "How does Floyd's Tortoise and Hare algorithm guarantee meeting if there is a cycle in a singly linked list?",
                codeSnippet = "ListNode *slow = head, *fast = head;\nwhile (fast && fast->next) {\n    slow = slow->next;\n    fast = fast->next->next;\n    if (slow == fast) return true;\n}",
                hasImageAttachment = false,
                likes = 7,
                isLiked = false,
                isSolved = false,
                createdAt = "Yesterday",
                answers = emptyList()
            )
        )
    )
    val doubts: StateFlow<List<Doubt>> = _doubts.asStateFlow()

    private val _quizzes = MutableStateFlow(
        listOf(
            Quiz(
                id = "quiz_c_extrahard",
                subjectId = "subj_c",
                title = "🔥 Grandmaster C: Pointers, Struct Padding & Memory Traps",
                topic = "Double Pointers, Bitfields, Undefined Behavior & Memory Layout",
                difficulty = "🔥 Extra Hard",
                xpReward = 60,
                timeLimitSeconds = 120,
                questions = listOf(
                    QuizQuestion(
                        id = "q_eh_1",
                        question = "What is the exact printed output of this multi-level pointer arithmetic code?",
                        codeBlock = "int arr[] = {10, 20, 30, 40, 50};\nint *p[] = {arr, arr + 1, arr + 2, arr + 3, arr + 4};\nint **pp = p;\npp++;\nprintf(\"%d %d\", pp - p, *(*pp + 2));",
                        options = listOf(
                            QuizOption(0, "1 40"),
                            QuizOption(1, "1 30"),
                            QuizOption(2, "4 40"),
                            QuizOption(3, "Undefined Behavior")
                        ),
                        correctOptionIndex = 0,
                        explanation = "pp starts at &p[0]. After pp++, pp points to p[1] which holds (arr + 1). 1) pp - p gives the index offset: 1. 2) *pp dereferences to (arr + 1). (*pp + 2) is (arr + 1 + 2) = (arr + 3). Dereferencing that (*(arr + 3)) accesses arr[3] which is 40. Output is '1 40'.",
                        hint = "Trace pp as a pointer to pointer array elements. pp++ shifts by one pointer element.",
                        isTrapQuestion = true
                    ),
                    QuizQuestion(
                        id = "q_eh_2",
                        question = "Assuming a standard 64-bit architecture with 4-byte int and 8-byte pointer alignment, what is sizeof(struct Node)?",
                        codeBlock = "struct Node {\n    char a;      // 1 byte\n    int *ptr;    // 8 bytes\n    short b;     // 2 bytes\n    int c;       // 4 bytes\n    char d;      // 1 byte\n};",
                        options = listOf(
                            QuizOption(0, "16 bytes"),
                            QuizOption(1, "24 bytes"),
                            QuizOption(2, "32 bytes"),
                            QuizOption(3, "16 bytes without padding")
                        ),
                        correctOptionIndex = 2,
                        explanation = "Layout trace: char a (offset 0), 7 bytes padding for 8-byte aligned int *ptr (offset 8-15), short b (offset 16-17), 2 bytes padding for 4-byte aligned int c (offset 20-23), char d (offset 24), 7 bytes tail padding to make total size a multiple of largest alignment (8). Total = 32 bytes!",
                        hint = "Check largest member alignment (8 bytes for pointer) and tail padding requirement.",
                        isTrapQuestion = true
                    ),
                    QuizQuestion(
                        id = "q_eh_3",
                        question = "What is the critical flaw in the following dynamic string manipulation routine?",
                        codeBlock = "char* create_greeting(const char* name) {\n    char buffer[128];\n    snprintf(buffer, sizeof(buffer), \"Hello %s!\", name);\n    return buffer;\n}",
                        options = listOf(
                            QuizOption(0, "snprintf buffer size is too small"),
                            QuizOption(1, "Returns address of a stack-allocated local variable (Dangling Pointer)"),
                            QuizOption(2, "Missing string null-terminator ('\\0')"),
                            QuizOption(3, "Const qualifier mismatch")
                        ),
                        correctOptionIndex = 1,
                        explanation = "'buffer' is allocated on the stack frame of create_greeting(). When create_greeting() returns, its stack frame is invalidated and deallocated. Returning a pointer to 'buffer' produces a dangling pointer whose memory will be overwritten by subsequent function calls.",
                        hint = "Where is buffer located in memory? Heap, Stack, or Data segment?",
                        isTrapQuestion = true
                    ),
                    QuizQuestion(
                        id = "q_eh_4",
                        question = "What does the expression `*++ptr` evaluate to versus `++*ptr`?",
                        codeBlock = "int vals[] = {100, 200, 300};\nint *ptr = vals;",
                        options = listOf(
                            QuizOption(0, "*++ptr increments the pointer first then dereferences (200); ++*ptr increments the dereferenced value (101)"),
                            QuizOption(1, "Both evaluate to 200"),
                            QuizOption(2, "Both evaluate to 101"),
                            QuizOption(3, "*++ptr causes a segmentation fault")
                        ),
                        correctOptionIndex = 0,
                        explanation = "In `*++ptr`, prefix ++ binds to the pointer `ptr`, incrementing its address to point to `vals[1]`, then * dereferences yielding 200. In `++*ptr`, * dereferences `ptr` to get `vals[0]` (100), and ++ increments that integer in-place to 101.",
                        hint = "Right-to-left associativity of unary operators: * and ++ have equal precedence.",
                        isTrapQuestion = false
                    )
                )
            ),
            Quiz(
                id = "quiz_dsa_extrahard",
                subjectId = "subj_dsa",
                title = "🔥 Grandmaster DSA: AVL Tree Rotations & Invariants",
                topic = "Tree Rotations, Amortized Complexity & Graph Invariants",
                difficulty = "🔥 Extra Hard",
                xpReward = 50,
                timeLimitSeconds = 90,
                questions = listOf(
                    QuizQuestion(
                        id = "qd_eh_1",
                        question = "When inserting key 25 into an AVL tree containing keys [10, 20, 30, 40, 50], an imbalance occurs at node 30 with Left-Right (LR) heavy state. What double rotation sequence restores balance?",
                        codeBlock = "",
                        options = listOf(
                            QuizOption(0, "Left Rotate on Left Child, then Right Rotate on Disbalanced Parent"),
                            QuizOption(1, "Right Rotate on Disbalanced Parent, then Left Rotate on Left Child"),
                            QuizOption(2, "Single Left Rotation on Disbalanced Parent"),
                            QuizOption(3, "Double Right Rotation on Root")
                        ),
                        correctOptionIndex = 0,
                        explanation = "For an LR imbalance: 1) Perform a Left Rotation on the left child of the unbalanced node to convert it to an LL configuration. 2) Perform a Right Rotation on the unbalanced node itself to restore the AVL balance factor invariant to {-1, 0, +1}.",
                        hint = "LR imbalance requires converting the elbow shape to a line first.",
                        isTrapQuestion = false
                    ),
                    QuizQuestion(
                        id = "qd_eh_2",
                        question = "What is the amortized time complexity per operation for Disjoint Set Union (DSU) with both Union by Rank and Path Compression optimizations?",
                        codeBlock = "",
                        options = listOf(
                            QuizOption(0, "O(1) strictly"),
                            QuizOption(1, "O(α(N)) where α is the Inverse Ackermann function (effectively constant < 5)"),
                            QuizOption(2, "O(log N)"),
                            QuizOption(3, "O(N log N)")
                        ),
                        correctOptionIndex = 1,
                        explanation = "With both Path Compression and Union by Rank heuristics, Tarjan proved the amortized cost per find/union operation is O(α(N)), where α(N) is the inverse Ackermann function, which is ≤ 4 for all practical universe sizes (N < 10^80).",
                        hint = "Think of Tarjan's classical bound on DSU.",
                        isTrapQuestion = false
                    )
                )
            ),
            Quiz(
                id = "quiz_py_extrahard",
                subjectId = "subj_python",
                title = "🔥 Grandmaster Python: Closures, Metaclasses & GIL",
                topic = "Late Binding Closures, Bytecode Execution & Thread Safety",
                difficulty = "🔥 Extra Hard",
                xpReward = 50,
                timeLimitSeconds = 90,
                questions = listOf(
                    QuizQuestion(
                        id = "qp_eh_1",
                        question = "What is the output of calling funcs[0]() and funcs[2]()?",
                        codeBlock = "funcs = [lambda: i * 2 for i in range(3)]\nprint(funcs[0](), funcs[2]())",
                        options = listOf(
                            QuizOption(0, "0 4"),
                            QuizOption(1, "4 4"),
                            QuizOption(2, "0 2"),
                            QuizOption(3, "NameError: i is not defined")
                        ),
                        correctOptionIndex = 1,
                        explanation = "Python closures exhibit 'Late Binding': variables used in closures are looked up at function execution time, NOT definition time. When the list comprehension finishes, the loop variable `i` remains in scope holding value 2. Thus all lambdas evaluate `2 * 2 = 4`!",
                        hint = "Python looks up 'i' from the enclosing scope when the function is called, not created.",
                        isTrapQuestion = true
                    ),
                    QuizQuestion(
                        id = "qp_eh_2",
                        question = "How do you fix the late-binding trap in the lambda list comprehension?",
                        codeBlock = "",
                        options = listOf(
                            QuizOption(0, "funcs = [lambda i=i: i * 2 for i in range(3)]"),
                            QuizOption(1, "Use global i inside lambda"),
                            QuizOption(2, "Convert list comprehension to set comprehension"),
                            QuizOption(3, "Wrap lambda with eval()")
                        ),
                        correctOptionIndex = 0,
                        explanation = "By using `lambda i=i: i * 2`, the default argument `i=i` is evaluated at function definition time, binding the current loop value to each lambda's local scope parameter.",
                        hint = "Default arguments are evaluated at function creation time.",
                        isTrapQuestion = false
                    )
                )
            ),
            Quiz(
                id = "quiz_c_1",
                subjectId = "subj_c",
                title = "C Pointers & Memory Mastery",
                topic = "Pointers, Addresses & Dereferencing",
                difficulty = "Medium",
                xpReward = 20,
                timeLimitSeconds = 90,
                questions = listOf(
                    QuizQuestion(
                        id = "q1",
                        question = "What is the output of the following C code?",
                        codeBlock = "int a = 10;\nint *p = &a;\n*p = 25;\nprintf(\"%d\", a);",
                        options = listOf(
                            QuizOption(0, "10"),
                            QuizOption(1, "25"),
                            QuizOption(2, "Address of a"),
                            QuizOption(3, "Compilation Error")
                        ),
                        correctOptionIndex = 1,
                        explanation = "*p dereferences the address of 'a' and modifies its stored value in memory to 25."
                    ),
                    QuizQuestion(
                        id = "q2",
                        question = "Which C library function frees dynamically allocated heap memory?",
                        codeBlock = "",
                        options = listOf(
                            QuizOption(0, "delete()"),
                            QuizOption(1, "dealloc()"),
                            QuizOption(2, "free()"),
                            QuizOption(3, "release()")
                        ),
                        correctOptionIndex = 2,
                        explanation = "In standard C, heap allocations created via malloc(), calloc(), or realloc() must be freed using free()."
                    ),
                    QuizQuestion(
                        id = "q3",
                        question = "If 'arr' is an array of integers, what does 'arr + 1' represent?",
                        codeBlock = "int arr[5] = {10, 20, 30, 40, 50};",
                        options = listOf(
                            QuizOption(0, "Value 11"),
                            QuizOption(1, "Pointer to the next element (arr[1])"),
                            QuizOption(2, "Pointer to the last element"),
                            QuizOption(3, "Memory address plus 1 byte")
                        ),
                        correctOptionIndex = 1,
                        explanation = "Pointer arithmetic scales with sizeof(type). arr + 1 advances the pointer by sizeof(int) bytes to point at arr[1]."
                    )
                )
            ),
            Quiz(
                id = "quiz_py_1",
                subjectId = "subj_python",
                title = "Python Data Structures Sprint",
                topic = "Lists, Dicts, Tuples & Comprehensions",
                difficulty = "Easy",
                xpReward = 20,
                timeLimitSeconds = 60,
                questions = listOf(
                    QuizQuestion(
                        id = "pq1",
                        question = "Which of the following data types is immutable in Python?",
                        codeBlock = "",
                        options = listOf(
                            QuizOption(0, "List"),
                            QuizOption(1, "Dictionary"),
                            QuizOption(2, "Tuple"),
                            QuizOption(3, "Set")
                        ),
                        correctOptionIndex = 2,
                        explanation = "Tuples, strings, and integers are immutable in Python; elements cannot be modified after instantiation."
                    ),
                    QuizQuestion(
                        id = "pq2",
                        question = "What is the output of: [x**2 for x in range(4) if x % 2 == 0]?",
                        codeBlock = "",
                        options = listOf(
                            QuizOption(0, "[0, 4]"),
                            QuizOption(1, "[0, 1, 4, 9]"),
                            QuizOption(2, "[4, 16]"),
                            QuizOption(3, "[0, 2]")
                        ),
                        correctOptionIndex = 0,
                        explanation = "range(4) produces 0, 1, 2, 3. Even numbers are 0 and 2. Their squares are 0 and 4."
                    )
                )
            )
        )
    )
    val quizzes: StateFlow<List<Quiz>> = _quizzes.asStateFlow()

    private val _challenges = MutableStateFlow(
        listOf(
            Challenge(
                id = "ch_daily",
                type = ChallengeType.DAILY,
                title = "Daily Peer Sprint: Bug Hunter",
                description = "Identify and fix 3 common pointer and memory leak flaws in peer C code samples.",
                subject = "C Programming",
                xpReward = 30,
                remainingTime = "11h 25m",
                participantsCount = 186,
                isCompleted = false,
                questions = listOf(
                    QuizQuestion(
                        id = "cq1",
                        question = "Find the critical bug in this string allocation code:",
                        codeBlock = "char *str = malloc(strlen(\"Hello\"));\nstrcpy(str, \"Hello\");",
                        options = listOf(
                            QuizOption(0, "No bug exists"),
                            QuizOption(1, "Missing 1 byte for null terminator ('\\0')"),
                            QuizOption(2, "strcpy cannot copy string literals"),
                            QuizOption(3, "malloc must take sizeof(char*)")
                        ),
                        correctOptionIndex = 1,
                        explanation = "strlen(\"Hello\") returns 5, but storing \"Hello\\0\" requires 6 bytes (strlen + 1)."
                    )
                )
            ),
            Challenge(
                id = "ch_weekly",
                type = ChallengeType.WEEKLY,
                title = "Weekly College Code Cup",
                description = "Compete against peer engineers across your college campus on Algorithmic Problem Solving.",
                subject = "Data Structures",
                xpReward = 100,
                remainingTime = "3 days 6 hours",
                participantsCount = 420,
                isCompleted = false
            ),
            Challenge(
                id = "ch_dept",
                type = ChallengeType.DEPARTMENT,
                title = "CSE Department Battle: Recursion & Trees",
                description = "Solve depth-first traversal and binary search tree balancing puzzles to score points for CSE!",
                subject = "Data Structures",
                xpReward = 50,
                remainingTime = "1 day 8 hours",
                participantsCount = 310,
                isCompleted = false
            )
        )
    )
    val challenges: StateFlow<List<Challenge>> = _challenges.asStateFlow()

    private val _leaderboard = MutableStateFlow(
        listOf(
            LeaderboardEntry(1, "user_sneha", "Sneha Rao", "", "Govt Engineering College", "AI & DS", 2890, RankTier.MENTOR, "🌟 Mentor", 112),
            LeaderboardEntry(2, "user_rahul", "Rahul Sharma", "", "National Institute of Tech", "CSE", 2450, RankTier.MASTER, "👑 Master", 86),
            LeaderboardEntry(3, "user_priya", "Priya Patel", "", "Institute of Technology", "CSE", 2180, RankTier.MASTER, "👑 Master", 74),
            LeaderboardEntry(4, "user_aman", "Aman Verma", "", "College of Engineering", "Electrical", 1420, RankTier.EXPERT, "🔥 Expert", 48),
            LeaderboardEntry(5, "user_alok", "Alok Janrao (You)", "", "Govt College of Engineering", "CSE", 1240, RankTier.EXPERT, "🔥 Expert", 34, isCurrentUser = true),
            LeaderboardEntry(6, "user_rohit", "Rohit Deshmukh", "", "Govt College of Engineering", "IT", 980, RankTier.SKILLED, "⚡ Skilled", 29),
            LeaderboardEntry(7, "user_ananya", "Ananya Sen", "", "National Institute of Tech", "CSE", 820, RankTier.SKILLED, "⚡ Skilled", 22),
            LeaderboardEntry(8, "user_vikram", "Vikram Singh", "", "Institute of Technology", "Mechanical", 540, RankTier.EXPLORER, "🚀 Explorer", 14)
        )
    )
    val leaderboard: StateFlow<List<LeaderboardEntry>> = _leaderboard.asStateFlow()

    private val _rewards = MutableStateFlow(
        listOf(
            Reward("rew_prem", "30 Days Premium Access", "Unlock downloadable formula sheets, priority doubt resolution, and offline notes.", "Perk", "⭐", 1500, RankTier.MASTER, isUnlocked = false, isClaimed = false),
            Reward("rew_badge_mentor", "Golden Mentor Profile Badge", "Display an exclusive glowing Mentor badge and golden verified border on all your peer answers.", "Profile", "👑", 2500, RankTier.MENTOR, isUnlocked = false, isClaimed = false),
            Reward("rew_cert", "Certified Peer Educator Certificate", "Official verifiable PDF certificate acknowledging your peer teaching and academic contributions.", "Certificate", "📜", 1000, RankTier.EXPERT, isUnlocked = true, isClaimed = false, perkCode = "T2L-CERT-2026-9812"),
            Reward("rew_frame", "Cyber Neon Profile Frame", "Futuristic animated glowing avatar frame for top subject contributors.", "Cosmetic", "✨", 600, RankTier.SKILLED, isUnlocked = true, isClaimed = true),
            Reward("rew_challenge", "VIP College Hackathon Entry", "Free direct pass to the inter-college tech tournament with sponsored prize pools.", "Event", "🏆", 2000, RankTier.MASTER, isUnlocked = false, isClaimed = false)
        )
    )
    val rewards: StateFlow<List<Reward>> = _rewards.asStateFlow()

    private val _shortVideos = MutableStateFlow(
        listOf(
            ShortVideo(
                id = "v1",
                title = "Pointer Arithmetic in 60 Seconds! ⚡",
                category = "Programming",
                creatorName = "Rahul Sharma",
                creatorRank = RankTier.MASTER,
                summary = "Never get confused about arr[i] vs *(arr + i). Pointers increment by the data size, not just 1 byte!",
                keyConcept = "sizeof(type) memory scaling",
                likesCount = 342,
                commentsCount = 28,
                isLiked = true,
                isSaved = true,
                isFollowing = true,
                comments = listOf(
                    VideoComment("vc1", "Alok Janrao", "Super concise explanation! Cleared my lab viva question.", "2 hours ago"),
                    VideoComment("vc2", "Priya Patel", "The visual memory animation made it so easy to picture.", "5 hours ago")
                )
            ),
            ShortVideo(
                id = "v2",
                title = "Python List Comprehensions vs map() & filter() 🐍",
                category = "Programming",
                creatorName = "Sneha Rao",
                creatorRank = RankTier.MENTOR,
                summary = "Why idiomatic Python developers prefer list comprehensions for readability and speed in 90% of data tasks.",
                keyConcept = "[expr for item in iterable if condition]",
                likesCount = 512,
                commentsCount = 45,
                isLiked = false,
                isSaved = false,
                isFollowing = false
            ),
            ShortVideo(
                id = "v3",
                title = "3 Exam Memory Tricks for Differential Equations 📐",
                category = "Mathematics",
                creatorName = "Aman Verma",
                creatorRank = RankTier.EXPERT,
                summary = "Solve auxiliary equations with complex roots in seconds using Euler's formula identity.",
                keyConcept = "e^(ix) = cos(x) + i*sin(x)",
                likesCount = 289,
                commentsCount = 19,
                isLiked = false,
                isSaved = true,
                isFollowing = false
            ),
            ShortVideo(
                id = "v4",
                title = "How to Ace Your Campus Tech Interview 🚀",
                category = "Career",
                creatorName = "Priya Patel",
                creatorRank = RankTier.MASTER,
                summary = "Structure your answers using the STAR method (Situation, Task, Action, Result) and explain complexity upfront.",
                keyConcept = "STAR Communication Framework",
                likesCount = 670,
                commentsCount = 54,
                isLiked = true,
                isSaved = true,
                isFollowing = true
            )
        )
    )
    val shortVideos: StateFlow<List<ShortVideo>> = _shortVideos.asStateFlow()

    private val _notifications = MutableStateFlow(
        listOf(
            NotificationItem("n1", "XP Earned! +30 XP", "Your doubt answer on C Pointers was marked as the Best Answer by Alok.", "10 mins ago", "XP", "🔥", false),
            NotificationItem("n2", "New Teaching Request", "Priya Patel requested a 1-on-1 session on C Pointer Arithmetic.", "30 mins ago", "TEACHING_REQ", "🎓", false),
            NotificationItem("n3", "Live Lecture Starting Soon", "Rahul Sharma's live lecture 'Deep Dive into Pointers' starts at 7:00 PM.", "1 hour ago", "LECTURE", "📡", false),
            NotificationItem("n4", "Reward Unlocked! 📜", "You unlocked the 'Certified Peer Educator Certificate'. Tap to view & download.", "Yesterday", "REWARD", "🎉", true),
            NotificationItem("n5", "Rank Upgrade! ⚡", "Congratulations! You reached Skilled Rank with over 1000 XP.", "2 days ago", "RANK", "🚀", true)
        )
    )
    val notifications: StateFlow<List<NotificationItem>> = _notifications.asStateFlow()

    // Interactive Core Actions
    fun addXp(amount: Int, reason: String) {
        _currentUser.update { user ->
            val newXp = user.xp + amount
            val oldTier = RankTier.fromXp(user.xp)
            val newTier = RankTier.fromXp(newXp)

            if (newTier != oldTier) {
                addNotification(
                    title = "Rank Up: ${newTier.title}! ${newTier.badgeIcon}",
                    message = "Amazing progress! You have advanced to ${newTier.title} tier.",
                    type = "RANK",
                    icon = newTier.badgeIcon
                )
            }

            user.copy(xp = newXp)
        }

        // Also update user's entry in leaderboard
        _leaderboard.update { list ->
            list.map { entry ->
                if (entry.isCurrentUser) {
                    val updatedXp = entry.xp + amount
                    entry.copy(
                        xp = updatedXp,
                        rankTier = RankTier.fromXp(updatedXp)
                    )
                } else entry
            }.sortedByDescending { it.xp }
                .mapIndexed { idx, item -> item.copy(rank = idx + 1) }
        }

        addNotification(
            title = "XP Earned: +$amount XP",
            message = reason,
            type = "XP",
            icon = "⚡"
        )
        syncUserProgressAndPersist()
    }

    private fun syncUserProgressAndPersist() {
        val current = _currentUser.value
        val subjectsList = _subjects.value
        val enrolled = subjectsList.filter { it.name in current.learnSubjects }
        val calculatedLearnProgress = if (enrolled.isNotEmpty()) {
            (enrolled.sumOf { it.progressPercent } / enrolled.size).coerceIn(0, 100)
        } else 0

        val teaching = subjectsList.filter { it.name in current.teachSubjects }
        val calculatedTeachProgress = if (teaching.isNotEmpty()) {
            (teaching.sumOf { it.teachingProgressPercent } / teaching.size).coerceIn(0, 100)
        } else 0

        val calculatedProfileCompletion = if (current.xp == 0 && current.completedLectures == 0 && current.completedQuizzes == 0 && current.studentsHelped == 0) {
            0
        } else {
            var score = 10
            if (current.bio.isNotBlank() && !current.bio.startsWith("Eager peer learner")) score += 15
            if (current.learnSubjects.isNotEmpty()) score += 15
            if (current.teachSubjects.isNotEmpty()) score += 15
            if (current.completedLectures > 0) score += 15
            if (current.completedQuizzes > 0) score += 15
            if (current.studentsHelped > 0) score += 15
            score.coerceIn(0, 100)
        }

        val updatedProfile = current.copy(
            learningProgress = calculatedLearnProgress,
            teachingProgress = calculatedTeachProgress,
            profileCompletion = calculatedProfileCompletion
        )
        if (updatedProfile != current) {
            _currentUser.value = updatedProfile
        }

        val subjMap = _subjects.value.associate { it.id to Pair(it.progressPercent, it.teachingProgressPercent) }
        val lecMap = _lectures.value.associate { it.id to Pair(it.progressPercent, it.isCompleted) }
        val account = UserAccount(updatedProfile, subjMap, lecMap)
        if (updatedProfile.email.isNotBlank()) {
            _userAccounts[updatedProfile.email.trim().lowercase()] = account
        }
        _userAccounts[updatedProfile.id] = account
    }

    private fun incrementSubjectProgress(subjectId: String, delta: Int) {
        _subjects.update { list ->
            list.map {
                if (it.id == subjectId) {
                    it.copy(progressPercent = (it.progressPercent + delta).coerceAtMost(100))
                } else it
            }
        }
        syncUserProgressAndPersist()
    }

    fun updateLectureProgress(lectureId: String, progressPercent: Int, positionSeconds: Int) {
        var completedNow = false
        var targetSubjectId = ""
        _lectures.update { list ->
            list.map {
                if (it.id == lectureId) {
                    targetSubjectId = it.subjectId
                    val newlyCompleted = progressPercent >= 90 && !it.isCompleted
                    if (newlyCompleted) completedNow = true
                    it.copy(
                        progressPercent = progressPercent.coerceIn(0, 100),
                        lastPositionSeconds = positionSeconds,
                        isCompleted = it.isCompleted || newlyCompleted
                    )
                } else it
            }
        }
        if (completedNow) {
            _currentUser.update { it.copy(completedLectures = it.completedLectures + 1) }
            addXp(10, "Completed video lecture session (+10 XP)")
            if (targetSubjectId.isNotBlank()) {
                incrementSubjectProgress(targetSubjectId, 15)
            }
        }
    }

    fun completeLecture(lectureId: String) {
        var found = false
        var targetSubjectId = ""
        _lectures.update { list ->
            list.map {
                if (it.id == lectureId && !it.isCompleted) {
                    found = true
                    targetSubjectId = it.subjectId
                    it.copy(isCompleted = true, progressPercent = 100)
                } else it
            }
        }
        if (found) {
            _currentUser.update { it.copy(completedLectures = it.completedLectures + 1) }
            addXp(10, "Completed lecture session (+10 XP)")
            if (targetSubjectId.isNotBlank()) {
                incrementSubjectProgress(targetSubjectId, 15)
            }
        }
    }

    fun completeQuiz(quizId: String, scorePercent: Int) {
        val targetQuiz = _quizzes.value.firstOrNull { it.id == quizId }
        val reward = targetQuiz?.xpReward ?: 20
        _currentUser.update { it.copy(completedQuizzes = it.completedQuizzes + 1) }
        addXp(reward, "Completed ${targetQuiz?.title ?: "quiz"} with $scorePercent% score (+$reward XP)")
        targetQuiz?.subjectId?.let { subjId ->
            incrementSubjectProgress(subjId, 20)
        }
    }

    fun markNoteAsRead(noteId: String) {
        var newlyRead = false
        var targetSubjectId = ""
        _notes.update { list ->
            list.map {
                if (it.id == noteId && !it.isRead) {
                    newlyRead = true
                    targetSubjectId = it.subjectId
                    it.copy(isRead = true)
                } else it
            }
        }
        if (newlyRead) {
            addXp(5, "Studied topic notes & formulas (+5 XP)")
            if (targetSubjectId.isNotBlank()) {
                incrementSubjectProgress(targetSubjectId, 10)
            }
        }
    }

    fun markShortVideoWatched(videoId: String) {
        var newlyWatched = false
        _shortVideos.update { list ->
            list.map {
                if (it.id == videoId && !it.isWatched) {
                    newlyWatched = true
                    it.copy(isWatched = true)
                } else it
            }
        }
        if (newlyWatched) {
            addXp(5, "Watched 60-second micro lesson (+5 XP)")
        }
    }

    fun completeChallenge(challengeId: String) {
        var reward = 30
        _challenges.update { list ->
            list.map {
                if (it.id == challengeId && !it.isCompleted) {
                    reward = it.xpReward
                    it.copy(isCompleted = true)
                } else it
            }
        }
        addXp(reward, "Completed challenge")
    }

    fun postDoubt(subject: String, topic: String, question: String, code: String) {
        val newDoubt = Doubt(
            id = "d_${UUID.randomUUID()}",
            studentName = _currentUser.value.name,
            studentAvatar = _currentUser.value.avatarUrl,
            subject = subject,
            topic = topic,
            question = question,
            codeSnippet = code,
            hasImageAttachment = false,
            createdAt = "Just now"
        )
        _doubts.update { listOf(newDoubt) + it }
        addNotification(
            title = "Doubt Posted",
            message = "Your question in $subject was shared with student teachers.",
            type = "DOUBT",
            icon = "❓"
        )
    }

    fun answerDoubt(doubtId: String, answerText: String, codeSnippet: String) {
        val newAnswer = DoubtAnswer(
            id = "ans_${UUID.randomUUID()}",
            doubtId = doubtId,
            authorName = _currentUser.value.name,
            authorRank = RankTier.fromXp(_currentUser.value.xp),
            text = answerText,
            codeSnippet = codeSnippet,
            upvotes = 0,
            isBestAnswer = false,
            answeredAt = "Just now"
        )

        _doubts.update { list ->
            list.map { doubt ->
                if (doubt.id == doubtId) {
                    doubt.copy(answers = doubt.answers + newAnswer)
                } else doubt
            }
        }

        addXp(15, "Answered a student's peer doubt")
    }

    fun markBestAnswer(doubtId: String, answerId: String) {
        _doubts.update { list ->
            list.map { doubt ->
                if (doubt.id == doubtId) {
                    doubt.copy(
                        isSolved = true,
                        answers = doubt.answers.map { ans ->
                            ans.copy(isBestAnswer = ans.id == answerId)
                        }
                    )
                } else doubt
            }
        }
        addXp(30, "Your peer marked the best answer (+30 XP award)")
    }

    fun toggleLikeDoubt(doubtId: String) {
        _doubts.update { list ->
            list.map {
                if (it.id == doubtId) {
                    val newLiked = !it.isLiked
                    it.copy(
                        isLiked = newLiked,
                        likes = if (newLiked) it.likes + 1 else it.likes - 1
                    )
                } else it
            }
        }
    }

    fun toggleLikeVideo(videoId: String) {
        _shortVideos.update { list ->
            list.map {
                if (it.id == videoId) {
                    val newLiked = !it.isLiked
                    it.copy(
                        isLiked = newLiked,
                        likesCount = if (newLiked) it.likesCount + 1 else it.likesCount - 1
                    )
                } else it
            }
        }
    }

    fun toggleSaveVideo(videoId: String) {
        _shortVideos.update { list ->
            list.map {
                if (it.id == videoId) {
                    it.copy(isSaved = !it.isSaved)
                } else it
            }
        }
    }

    fun addVideoComment(videoId: String, commentText: String) {
        val comment = VideoComment(
            id = "vc_${UUID.randomUUID()}",
            author = _currentUser.value.name,
            text = commentText,
            timestamp = "Just now"
        )
        _shortVideos.update { list ->
            list.map {
                if (it.id == videoId) {
                    it.copy(
                        comments = it.comments + comment,
                        commentsCount = it.commentsCount + 1
                    )
                } else it
            }
        }
    }

    fun requestTeachingSession(teacherId: String, teacherName: String, subject: String, topic: String, preferredTime: String, note: String) {
        val newReq = TeachingRequest(
            id = "req_${UUID.randomUUID()}",
            studentId = _currentUser.value.id,
            studentName = _currentUser.value.name,
            teacherId = teacherId,
            teacherName = teacherName,
            subject = subject,
            topic = topic,
            requestedTime = preferredTime,
            message = note,
            status = RequestStatus.PENDING,
            createdAt = "Just now"
        )
        _teachingRequests.update { listOf(newReq) + it }
        addNotification(
            title = "Teaching Request Sent",
            message = "Requested a session with $teacherName on $topic.",
            type = "TEACHING_REQ",
            icon = "🎓"
        )
    }

    fun acceptTeachingRequest(requestId: String) {
        _teachingRequests.update { list ->
            list.map {
                if (it.id == requestId) it.copy(status = RequestStatus.ACCEPTED) else it
            }
        }
        addNotification(
            title = "Teaching Request Accepted",
            message = "Session confirmed! Join the peer workspace at the scheduled time.",
            type = "TEACHING_REQ",
            icon = "🤝"
        )
    }

    fun completeTeachingSession(sessionId: String, rating: Float, feedback: String) {
        _teachingSessions.update { list ->
            list.map {
                if (it.id == sessionId) it.copy(isCompleted = true, studentRating = rating, studentFeedback = feedback) else it
            }
        }
        _currentUser.update {
            it.copy(
                studentsHelped = it.studentsHelped + 1,
                sessionsCompleted = it.sessionsCompleted + 1
            )
        }
        addXp(25, "Completed peer teaching session (+25 XP)")
    }

    fun updateTeacherProfile(
        subjects: List<String>,
        availableDays: String,
        availableTime: String,
        teachingMode: String,
        bio: String
    ) {
        _currentUser.update {
            it.copy(
                teachSubjects = subjects,
                teacherAvailableDays = availableDays,
                teacherAvailableTime = availableTime,
                teachingMode = teachingMode,
                bio = bio,
                isTeacherProfileActive = true
            )
        }
        syncUserProgressAndPersist()
        addNotification(
            title = "Teacher Profile Updated",
            message = "You are now active to receive peer teaching requests.",
            type = "TEACHING_REQ",
            icon = "🌟"
        )
    }

    fun updateUserProfile(
        name: String,
        college: String,
        department: String,
        yearSemester: String,
        bio: String,
        learnSubjects: List<String>,
        teachSubjects: List<String>
    ) {
        _currentUser.update {
            it.copy(
                name = name,
                college = college,
                department = department,
                yearSemester = yearSemester,
                bio = bio,
                learnSubjects = learnSubjects,
                teachSubjects = teachSubjects
            )
        }
        syncUserProgressAndPersist()
    }

    fun claimReward(rewardId: String) {
        _rewards.update { list ->
            list.map {
                if (it.id == rewardId) it.copy(isClaimed = true) else it
            }
        }
        addNotification(
            title = "Reward Claimed!",
            message = "Your reward perk has been activated in your account.",
            type = "REWARD",
            icon = "🎁"
        )
    }

    fun activatePremium() {
        _currentUser.update { it.copy(isPremium = true) }
        addNotification(
            title = "Teach2Learn Premium Activated! ⭐",
            message = "Unlimited notes downloads, mentor matching, and premium live lectures unlocked.",
            type = "REWARD",
            icon = "👑"
        )
    }

    fun loginUser(email: String, password: String): Boolean {
        val normalized = email.trim().lowercase()
        val existingAccount = _userAccounts[normalized] ?: _userAccounts.values.firstOrNull { it.profile.email.equals(normalized, ignoreCase = true) }
        if (existingAccount != null) {
            _currentUser.value = existingAccount.profile
            _subjects.update { list ->
                list.map { subj ->
                    val saved = existingAccount.subjectProgressMap[subj.id]
                    if (saved != null) subj.copy(progressPercent = saved.first, teachingProgressPercent = saved.second)
                    else subj
                }
            }
            _lectures.update { list ->
                list.map { lec ->
                    val saved = existingAccount.lectureProgressMap[lec.id]
                    if (saved != null) lec.copy(progressPercent = saved.first, isCompleted = saved.second)
                    else lec
                }
            }
        } else {
            val generatedName = if (email.contains("@")) {
                email.substringBefore("@").replace(".", " ").split(" ")
                    .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
            } else "Student User"

            val newUser = UserProfile(
                id = "user_${UUID.randomUUID().toString().take(6)}",
                name = generatedName,
                email = email.trim(),
                educationLevel = "B.Tech",
                college = "Government College of Engineering",
                department = "CSE",
                yearSemester = "1st Year / 1st Sem",
                bio = "Eager peer learner ready to collaborate and study together!",
                xp = 0,
                streakDays = 0,
                studentsHelped = 0,
                sessionsCompleted = 0,
                completedLectures = 0,
                completedQuizzes = 0,
                profileCompletion = 0,
                learningProgress = 0,
                teachingProgress = 0,
                rating = 5.0f,
                learnSubjects = listOf("C Programming", "Python"),
                teachSubjects = emptyList(),
                isTeacherProfileActive = false,
                badges = emptyList(),
                certificates = emptyList(),
                reviews = emptyList()
            )
            _currentUser.value = newUser
            _subjects.update { list ->
                list.map { it.copy(progressPercent = 0, teachingProgressPercent = 0) }
            }
            _lectures.update { list ->
                list.map { it.copy(progressPercent = 0, isCompleted = false, lastPositionSeconds = 0) }
            }
            syncUserProgressAndPersist()
        }
        return true
    }

    fun registerUser(
        fullName: String,
        email: String,
        educationLevel: String,
        college: String,
        department: String,
        yearSemester: String,
        learnList: List<String>,
        teachList: List<String>
    ) {
        val newUser = UserProfile(
            id = "user_${UUID.randomUUID().toString().take(6)}",
            name = fullName.trim(),
            email = email.trim(),
            educationLevel = educationLevel,
            college = college,
            department = department,
            yearSemester = yearSemester,
            bio = "Eager peer learner ready to collaborate and study together!",
            xp = 0,
            streakDays = 0,
            studentsHelped = 0,
            sessionsCompleted = 0,
            completedLectures = 0,
            completedQuizzes = 0,
            profileCompletion = 0,
            learningProgress = 0,
            teachingProgress = 0,
            rating = 5.0f,
            learnSubjects = if (learnList.isEmpty()) listOf("C Programming", "Python") else learnList,
            teachSubjects = teachList,
            isTeacherProfileActive = teachList.isNotEmpty(),
            badges = emptyList(),
            certificates = emptyList(),
            reviews = emptyList()
        )
        _currentUser.value = newUser
        // Reset subjects learning and teaching progress to 0% for the new user
        _subjects.update { list ->
            list.map { it.copy(progressPercent = 0, teachingProgressPercent = 0) }
        }
        // Reset lecture completions for the new user
        _lectures.update { list ->
            list.map { it.copy(progressPercent = 0, isCompleted = false, lastPositionSeconds = 0) }
        }
        syncUserProgressAndPersist()
        addNotification(
            title = "Welcome to TEACH2LEARN! 🎉",
            message = "Your learning profile is set up with 0% initial progress. Complete lessons, quizzes, and notes to level up!",
            type = "XP",
            icon = "🚀"
        )
    }

    fun switchUser(userId: String) {
        when (userId) {
            "user_rahul" -> {
                val profile = UserProfile(
                    id = "user_rahul",
                    name = "Rahul Sharma",
                    email = "rahul.c@example.edu",
                    educationLevel = "B.Tech",
                    college = "National Institute of Technology",
                    department = "CSE",
                    yearSemester = "4th Year / 7th Sem",
                    bio = "Top peer teacher in C & Data Structures. Love helping juniors conquer memory models.",
                    xp = 2450,
                    streakDays = 14,
                    studentsHelped = 86,
                    sessionsCompleted = 42,
                    completedLectures = 14,
                    completedQuizzes = 12,
                    profileCompletion = 95,
                    learningProgress = 85,
                    teachingProgress = 80,
                    rating = 4.9f,
                    learnSubjects = listOf("System Design", "Distributed Systems"),
                    teachSubjects = listOf("C Programming", "Data Structures"),
                    isTeacherProfileActive = true,
                    isPremium = true,
                    role = UserRole.TEACHER
                )
                _currentUser.value = profile
                _subjects.update { list ->
                    list.map {
                        when (it.id) {
                            "subj_c" -> it.copy(progressPercent = 95, teachingProgressPercent = 90)
                            "subj_dsa" -> it.copy(progressPercent = 90, teachingProgressPercent = 85)
                            else -> it.copy(progressPercent = 50, teachingProgressPercent = 30)
                        }
                    }
                }
                syncUserProgressAndPersist()
            }
            "user_sneha" -> {
                val profile = UserProfile(
                    id = "user_sneha",
                    name = "Sneha Rao",
                    email = "sneha.ai@example.edu",
                    educationLevel = "B.Tech",
                    college = "Govt Engineering College",
                    department = "AI & DS",
                    yearSemester = "4th Year / 8th Sem",
                    bio = "Grand Mentor and Python enthusiast. 100+ peer students assisted.",
                    xp = 2890,
                    streakDays = 21,
                    studentsHelped = 112,
                    sessionsCompleted = 58,
                    completedLectures = 18,
                    completedQuizzes = 15,
                    profileCompletion = 98,
                    learningProgress = 90,
                    teachingProgress = 85,
                    rating = 4.95f,
                    learnSubjects = listOf("Deep Learning", "Cloud Computing"),
                    teachSubjects = listOf("Python Programming", "Machine Learning", "Mathematics"),
                    isTeacherProfileActive = true,
                    isPremium = true,
                    role = UserRole.TEACHER
                )
                _currentUser.value = profile
                _subjects.update { list ->
                    list.map {
                        when (it.id) {
                            "subj_python" -> it.copy(progressPercent = 100, teachingProgressPercent = 95)
                            "subj_math" -> it.copy(progressPercent = 90, teachingProgressPercent = 80)
                            else -> it.copy(progressPercent = 60, teachingProgressPercent = 40)
                        }
                    }
                }
                syncUserProgressAndPersist()
            }
            "user_vikram" -> {
                val profile = UserProfile(
                    id = "user_vikram",
                    name = "Vikram Malhotra",
                    email = "vikram.m@example.edu",
                    educationLevel = "B.Tech",
                    college = "Indian Institute of Technology",
                    department = "CSE",
                    yearSemester = "2nd Year / 3rd Sem",
                    bio = "Computer science student focused on mastering core fundamentals and solving coding challenges.",
                    xp = 820,
                    streakDays = 5,
                    studentsHelped = 6,
                    sessionsCompleted = 4,
                    completedLectures = 14,
                    completedQuizzes = 16,
                    profileCompletion = 80,
                    learningProgress = 62,
                    teachingProgress = 20,
                    rating = 4.5f,
                    learnSubjects = listOf("C Programming", "Data Structures"),
                    teachSubjects = emptyList(),
                    isTeacherProfileActive = false,
                    isPremium = false,
                    role = UserRole.STUDENT
                )
                _currentUser.value = profile
                syncUserProgressAndPersist()
            }
            else -> {
                val profile = UserProfile(
                    id = "user_alok",
                    name = "Alok Janrao",
                    email = "alokjanrao5@gmail.com",
                    educationLevel = "B.Tech",
                    college = "Government College of Engineering",
                    department = "CSE",
                    yearSemester = "3rd Year / 5th Sem",
                    bio = "Love C programming and building Android apps! Currently exploring Data Structures and helping peers debug memory leaks.",
                    xp = 1240,
                    streakDays = 7,
                    studentsHelped = 34,
                    sessionsCompleted = 18,
                    completedLectures = 8,
                    completedQuizzes = 6,
                    profileCompletion = 85,
                    learningProgress = 65,
                    teachingProgress = 50,
                    rating = 4.8f,
                    learnSubjects = listOf("Data Structures", "Python", "Mathematics", "AI & Data Science"),
                    teachSubjects = listOf("C Programming", "Python"),
                    isTeacherProfileActive = true,
                    isPremium = true,
                    role = UserRole.ADMIN
                )
                _currentUser.value = profile
                _subjects.update { list ->
                    list.map {
                        when (it.id) {
                            "subj_c" -> it.copy(progressPercent = 80, teachingProgressPercent = 70)
                            "subj_python" -> it.copy(progressPercent = 60, teachingProgressPercent = 50)
                            "subj_dsa" -> it.copy(progressPercent = 45, teachingProgressPercent = 30)
                            "subj_java" -> it.copy(progressPercent = 50, teachingProgressPercent = 35)
                            "subj_math" -> it.copy(progressPercent = 75, teachingProgressPercent = 40)
                            "subj_mech" -> it.copy(progressPercent = 35, teachingProgressPercent = 20)
                            "subj_ee" -> it.copy(progressPercent = 40, teachingProgressPercent = 25)
                            else -> it
                        }
                    }
                }
                syncUserProgressAndPersist()
            }
        }
    }

    fun scheduleLiveLecture(
        title: String,
        subjectId: String,
        subjectName: String,
        topic: String,
        date: String,
        startTime: String,
        endTime: String,
        meetingUrl: String,
        description: String,
        durationMinutes: Int = 45
    ) {
        val newLecture = Lecture(
            id = "lec_live_${UUID.randomUUID().toString().take(6)}",
            subjectId = subjectId,
            subjectName = subjectName,
            topic = topic,
            title = title,
            description = description,
            teacherName = _currentUser.value.name,
            durationMinutes = durationMinutes,
            isLive = true,
            liveStatus = LiveLectureStatus.UPCOMING,
            date = date,
            startTime = startTime,
            endTime = endTime,
            meetingUrl = meetingUrl.ifBlank { "https://teach2learn.peer/live/room-${UUID.randomUUID().toString().take(4)}" },
            scheduledTime = "$startTime $date",
            studentCount = 1
        )
        _lectures.update { listOf(newLecture) + it }
        addXp(40, "Scheduled a live peer lecture (+40 XP)")
        addNotification(
            title = "Live Lecture Scheduled! 📡",
            message = "'$title' on $topic is scheduled for $startTime $date.",
            type = "LECTURE",
            icon = "🎓"
        )
    }

    private fun addNotification(title: String, message: String, type: String, icon: String) {
        val item = NotificationItem(
            id = "notif_${UUID.randomUUID()}",
            title = title,
            message = message,
            timestamp = "Just now",
            type = type,
            icon = icon,
            isRead = false
        )
        _notifications.update { listOf(item) + it }
    }

    fun setLiveLectureReminder(lectureId: String, leadMinutes: Int) {
        _lectures.update { list ->
            list.map {
                if (it.id == lectureId) {
                    it.copy(isReminderSet = true, reminderLeadMinutes = leadMinutes)
                } else it
            }
        }
        val lecture = _lectures.value.firstOrNull { it.id == lectureId }
        val lectureTitle = lecture?.title ?: "Live Lecture"
        val teacher = lecture?.teacherName ?: "Mentor"
        val time = lecture?.scheduledTime ?: "7:00 PM Today"
        addNotification(
            title = "🔔 Early Reminder Set ($leadMinutes min)",
            message = "Early alert scheduled! '$lectureTitle' with $teacher begins at $time. We'll remind you $leadMinutes minutes before.",
            type = "LECTURE",
            icon = "⏰"
        )
    }

    fun cancelLiveLectureReminder(lectureId: String) {
        _lectures.update { list ->
            list.map {
                if (it.id == lectureId) {
                    it.copy(isReminderSet = false)
                } else it
            }
        }
        addNotification(
            title = "Reminder Removed",
            message = "Early notification cancelled for this live session.",
            type = "LECTURE",
            icon = "🔕"
        )
    }

    fun toggleBookmarkNote(noteId: String) {
        _notes.update { list ->
            list.map {
                if (it.id == noteId) it.copy(isBookmarked = !it.isBookmarked) else it
            }
        }
    }

    fun downloadNote(noteId: String) {
        _notes.update { list ->
            list.map {
                if (it.id == noteId) it.copy(downloads = it.downloads + 1) else it
            }
        }
        addXp(5, "Downloaded study notes for offline revision (+5 XP)")
    }

    fun uploadNote(
        subjectId: String,
        title: String,
        author: String,
        category: String,
        pagesCount: Int,
        contentPreview: String,
        keyFormulas: List<String>,
        fullContent: String
    ) {
        val newNote = SubjectNote(
            id = "note_${UUID.randomUUID().toString().take(6)}",
            subjectId = subjectId,
            title = title,
            author = author,
            pagesCount = pagesCount,
            downloads = 1,
            rating = 5.0f,
            isPremiumOnly = false,
            category = category,
            isBookmarked = false,
            contentPreview = contentPreview,
            keyFormulas = keyFormulas,
            chapters = listOf("Section 1: Core Fundamentals", "Section 2: High-Yield Formulas", "Section 3: Solved Practice"),
            fullContent = fullContent
        )
        _notes.update { listOf(newNote) + it }
        addXp(35, "Uploaded peer study notes & cheatsheet (+35 XP)")
        addNotification(
            title = "Notes Published! 📚",
            message = "'$title' is now live for all peer students to study.",
            type = "REWARD",
            icon = "✨"
        )
    }

    fun toggleFollowCreator(videoId: String) {
        _shortVideos.update { list ->
            val target = list.firstOrNull { it.id == videoId }
            val creator = target?.creatorName
            list.map {
                if (it.creatorName == creator) {
                    it.copy(isFollowing = !it.isFollowing)
                } else it
            }
        }
    }

    fun uploadShortVideo(
        title: String,
        category: String,
        summary: String,
        keyConcept: String,
        quizQuestion: String = "",
        quizOptions: List<String> = emptyList(),
        quizCorrectIndex: Int = 0,
        quizExplanation: String = ""
    ) {
        val newVideo = ShortVideo(
            id = "v_${UUID.randomUUID().toString().take(6)}",
            title = title,
            category = category,
            creatorName = _currentUser.value.name,
            creatorRank = RankTier.fromXp(_currentUser.value.xp),
            summary = summary,
            keyConcept = keyConcept,
            likesCount = 1,
            commentsCount = 0,
            isLiked = false,
            isSaved = false,
            isFollowing = false,
            comments = emptyList(),
            quickQuizQuestion = quizQuestion,
            quickQuizOptions = quizOptions,
            quickQuizCorrectIndex = quizCorrectIndex,
            quickQuizExplanation = quizExplanation
        )
        _shortVideos.update { listOf(newVideo) + it }
        addXp(50, "Shared a 60-second micro learning reel with peers (+50 XP)")
        addNotification(
            title = "Micro Reel Published! ⚡",
            message = "Your peer reel '$title' is now live in the micro-learning feed.",
            type = "XP",
            icon = "🎥"
        )
    }

    fun addNotificationPublic(title: String, message: String, type: String, icon: String) {
        addNotification(title, message, type, icon)
    }

    fun markAllNotificationsRead() {
        _notifications.update { list -> list.map { it.copy(isRead = true) } }
    }

    // ==========================================
    // MY LEARNING PROGRESS STATE & METHODS
    // ==========================================

    private val _subjectProgress = MutableStateFlow(
        listOf(
            SubjectProgress(
                subjectId = "subj_c",
                subjectName = "C Programming",
                code = "CS101",
                icon = "💻",
                progressPercent = 85,
                topicsCompleted = 6,
                totalTopics = 7,
                quizAveragePercent = 92,
                masteryLevel = "Proficient",
                topicBreakdown = listOf(
                    SubjectTopicProgress("Syntax & Data Types", true, 95),
                    SubjectTopicProgress("Control Flow & Loops", true, 90),
                    SubjectTopicProgress("Functions & Scope", true, 92),
                    SubjectTopicProgress("Arrays & Strings", true, 88),
                    SubjectTopicProgress("Pointers & Memory Allocation", true, 94),
                    SubjectTopicProgress("Structures & Unions", true, 86),
                    SubjectTopicProgress("File I/O & Preprocessors", false, 65)
                )
            ),
            SubjectProgress(
                subjectId = "subj_python",
                subjectName = "Python Programming",
                code = "CS102",
                icon = "🐍",
                progressPercent = 68,
                topicsCompleted = 4,
                totalTopics = 6,
                quizAveragePercent = 78,
                masteryLevel = "Intermediate",
                topicBreakdown = listOf(
                    SubjectTopicProgress("Basic Syntax & Loops", true, 92),
                    SubjectTopicProgress("Lists, Dicts & Tuples", true, 84),
                    SubjectTopicProgress("Functions & Lambdas", true, 80),
                    SubjectTopicProgress("OOP & Classes", true, 75),
                    SubjectTopicProgress("File Handling & JSON", false, 58),
                    SubjectTopicProgress("Modules & Virtual Envs", false, 0)
                )
            ),
            SubjectProgress(
                subjectId = "subj_dsa",
                subjectName = "Data Structures & Algorithms",
                code = "CS201",
                icon = "🌳",
                progressPercent = 52,
                topicsCompleted = 3,
                totalTopics = 6,
                quizAveragePercent = 70,
                masteryLevel = "Intermediate",
                topicBreakdown = listOf(
                    SubjectTopicProgress("Asymptotic Complexity", true, 85),
                    SubjectTopicProgress("Arrays & Matrices", true, 78),
                    SubjectTopicProgress("Singly & Doubly Linked Lists", true, 72),
                    SubjectTopicProgress("Stacks & Queues", false, 62),
                    SubjectTopicProgress("Binary Trees & BST", false, 50),
                    SubjectTopicProgress("Graph Traversals (BFS/DFS)", false, 0)
                )
            ),
            SubjectProgress(
                subjectId = "subj_math",
                subjectName = "Engineering Mathematics",
                code = "MATH101",
                icon = "📐",
                progressPercent = 42,
                topicsCompleted = 2,
                totalTopics = 5,
                quizAveragePercent = 64,
                masteryLevel = "Needs Attention",
                topicBreakdown = listOf(
                    SubjectTopicProgress("Matrices & Eigenvalues", true, 75),
                    SubjectTopicProgress("Differential Calculus", true, 68),
                    SubjectTopicProgress("Integral Calculus", false, 55),
                    SubjectTopicProgress("Differential Equations", false, 48),
                    SubjectTopicProgress("Discrete Mathematics", false, 40)
                )
            ),
            SubjectProgress(
                subjectId = "subj_dbms",
                subjectName = "Database Management Systems",
                code = "CS202",
                icon = "🗄️",
                progressPercent = 35,
                topicsCompleted = 2,
                totalTopics = 6,
                quizAveragePercent = 56,
                masteryLevel = "Needs Attention",
                topicBreakdown = listOf(
                    SubjectTopicProgress("ER Modeling", true, 78),
                    SubjectTopicProgress("Relational Algebra", true, 62),
                    SubjectTopicProgress("SQL Queries & Subqueries", false, 54),
                    SubjectTopicProgress("Normalization (1NF-3NF)", false, 45),
                    SubjectTopicProgress("ACID Properties & Transactions", false, 42),
                    SubjectTopicProgress("Indexing & Hashing", false, 0)
                )
            )
        )
    )
    val subjectProgress: StateFlow<List<SubjectProgress>> = _subjectProgress.asStateFlow()

    private val _learningStrengths = MutableStateFlow(
        listOf(
            LearningStrength(
                id = "s1",
                title = "Pointer Arithmetic Master",
                description = "94% accuracy across 8 pointer quizzes and lab challenges. Top 5% in your class cohort.",
                metric = "94% Accuracy",
                icon = "🎯",
                category = "Quiz Performance"
            ),
            LearningStrength(
                id = "s2",
                title = "Consistent 7-Day Study Streak",
                description = "Studied at least 1.5 hours every day without interruption over the past week.",
                metric = "7 Days Active",
                icon = "🔥",
                category = "Consistency"
            ),
            LearningStrength(
                id = "s3",
                title = "High Peer Impact & Teaching",
                description = "Conducted 18 peer sessions and helped 34 classmates debug lab code with a 4.8★ rating.",
                metric = "34 Students Helped",
                icon = "🤝",
                category = "Activity"
            ),
            LearningStrength(
                id = "s4",
                title = "Fast Topic Completion in C",
                description = "Completed 6 out of 7 C Programming chapters including structures and pointers.",
                metric = "6/7 Completed",
                icon = "⚡",
                category = "Completed Topics"
            )
        )
    )
    val learningStrengths: StateFlow<List<LearningStrength>> = _learningStrengths.asStateFlow()

    private val _improvementAreas = MutableStateFlow(
        listOf(
            ImprovementArea(
                id = "i1",
                subjectName = "Database Management Systems",
                topic = "SQL Normalization (2NF & 3NF)",
                currentAccuracyPercent = 45,
                issueDescription = "Frequent errors in identifying transitive functional dependencies and lossless joins.",
                recommendation = "Review the 3NF decomposition formula cheatsheet and take the 5-question peer revision quiz.",
                actionLabel = "Practice 3NF Quiz",
                actionType = "quiz"
            ),
            ImprovementArea(
                id = "i2",
                subjectName = "C Programming",
                topic = "File I/O & Buffer Flush",
                currentAccuracyPercent = 58,
                issueDescription = "Incomplete chapter notes and skipped practice exercises on fopen/fread error checking.",
                recommendation = "Watch the 60s Micro-Reel on C File Handling and solve 2 practical file pointer exercises.",
                actionLabel = "Watch Reel & Notes",
                actionType = "notes"
            ),
            ImprovementArea(
                id = "i3",
                subjectName = "Engineering Mathematics",
                topic = "Discrete Mathematics & Graphs",
                currentAccuracyPercent = 40,
                issueDescription = "Zero activity recorded for 12 days. Low retention on Propositional Logic truth tables.",
                recommendation = "Dedicate 20 minutes to solve 3 basic truth table problems before the upcoming midterm exam.",
                actionLabel = "Study Discrete Math",
                actionType = "lecture"
            )
        )
    )
    val improvementAreas: StateFlow<List<ImprovementArea>> = _improvementAreas.asStateFlow()

    private val _learningHistory = MutableStateFlow(
        listOf(
            LearningHistoryItem(
                id = "h1",
                title = "C Memory Allocation & Pointers",
                subtitle = "Completed Quiz with 92% Accuracy",
                timestamp = "Today, 4:30 PM",
                relativeTime = "2h ago",
                type = TimelineActivityType.QUIZ_RESULT,
                scorePercent = 92,
                xpEarned = 50,
                icon = "⚡",
                details = "Scored 9/10 on dynamic memory allocation, malloc error boundaries, and pointer arithmetic."
            ),
            LearningHistoryItem(
                id = "h2",
                title = "Linked List Node Insertion in C",
                subtitle = "Practice Session & Code Runner",
                timestamp = "Today, 2:15 PM",
                relativeTime = "4h ago",
                type = TimelineActivityType.PRACTICE_SESSION,
                scorePercent = 100,
                xpEarned = 35,
                icon = "💻",
                details = "Successfully implemented singly linked list head, tail, and position-based insertion without leaks."
            ),
            LearningHistoryItem(
                id = "h3",
                title = "Peer Teaching: Pointer Arithmetic",
                subtitle = "Conducted 45-min live session for 3 students",
                timestamp = "Yesterday, 6:00 PM",
                relativeTime = "1d ago",
                type = TimelineActivityType.TEACHING_SESSION,
                scorePercent = null,
                xpEarned = 120,
                icon = "🎓",
                details = "Explained stack vs heap memory layouts and pointer dereferencing with interactive whiteboarding."
            ),
            LearningHistoryItem(
                id = "h4",
                title = "C Structures & Typedef Unions",
                subtitle = "Marked topic chapter as completed",
                timestamp = "Yesterday, 11:30 AM",
                relativeTime = "1d ago",
                type = TimelineActivityType.TOPIC_COMPLETED,
                scorePercent = null,
                xpEarned = 40,
                icon = "✅",
                details = "Completed lecture slides, reviewed typedef conventions, and submitted structure alignment lab notes."
            ),
            LearningHistoryItem(
                id = "h5",
                title = "Calculus & Linear Algebra Cheatsheet",
                subtitle = "Read Formula Notes & Solved Samples",
                timestamp = "2 days ago",
                relativeTime = "2d ago",
                type = TimelineActivityType.STUDY_ACTIVITY,
                scorePercent = null,
                xpEarned = 25,
                icon = "📖",
                details = "Reviewed 8-page formula sheet on eigenvalues, diagonal matrices, and partial derivatives."
            ),
            LearningHistoryItem(
                id = "h6",
                title = "Python Dictionary Comprehension Quiz",
                subtitle = "Timed subject assessment",
                timestamp = "3 days ago",
                relativeTime = "3d ago",
                type = TimelineActivityType.QUIZ_RESULT,
                scorePercent = 85,
                xpEarned = 45,
                icon = "⚡",
                details = "Demonstrated strong grasp of nested dictionary comprehensions and filter predicates."
            ),
            LearningHistoryItem(
                id = "h7",
                title = "Group Doubt Session: C Segfaults",
                subtitle = "Helped 4 peers in community forum",
                timestamp = "4 days ago",
                relativeTime = "4d ago",
                type = TimelineActivityType.TEACHING_SESSION,
                scorePercent = null,
                xpEarned = 80,
                icon = "🎓",
                details = "Diagnosed null pointer dereferences and array out-of-bounds errors in peer semester lab code."
            ),
            LearningHistoryItem(
                id = "h8",
                title = "Data Structures: Asymptotic Analysis",
                subtitle = "Completed Big-O & Recurrence module",
                timestamp = "5 days ago",
                relativeTime = "5d ago",
                type = TimelineActivityType.TOPIC_COMPLETED,
                scorePercent = null,
                xpEarned = 40,
                icon = "✅",
                details = "Solved master theorem recurrence relations and analyzed best/worst cases for divide & conquer."
            )
        )
    )
    val learningHistory: StateFlow<List<LearningHistoryItem>> = _learningHistory.asStateFlow()

    private val _weeklyProgress = MutableStateFlow(
        WeeklyProgressComparison(
            currentWeekHours = 15.5f,
            previousWeekHours = 10.8f,
            currentWeekTopics = 7,
            previousWeekTopics = 4,
            currentWeekQuizzes = 6,
            previousWeekQuizzes = 3,
            currentWeekTeachingMinutes = 180,
            previousWeekTeachingMinutes = 105,
            dailyBreakdown = listOf(
                DailyActivity("Monday", "Mon", 2.2f, 1.4f),
                DailyActivity("Tuesday", "Tue", 2.8f, 1.6f),
                DailyActivity("Wednesday", "Wed", 1.8f, 1.2f),
                DailyActivity("Thursday", "Thu", 3.2f, 2.0f),
                DailyActivity("Friday", "Fri", 2.1f, 1.5f),
                DailyActivity("Saturday", "Sat", 2.4f, 1.8f),
                DailyActivity("Sunday", "Sun", 1.0f, 1.3f, isToday = true)
            )
        )
    )
    val weeklyProgress: StateFlow<WeeklyProgressComparison> = _weeklyProgress.asStateFlow()

    private val _learningGoals = MutableStateFlow(
        listOf(
            LearningGoal(
                id = "g1",
                title = "Complete C Pointer & Dynamic Memory Labs",
                category = "Skill Mastery",
                currentProgress = 6,
                targetProgress = 7,
                unit = "Topics",
                deadline = "In 2 days",
                isCompleted = false
            ),
            LearningGoal(
                id = "g2",
                title = "Study 15 Hours Across CSE Subjects",
                category = "Exam Prep",
                currentProgress = 15,
                targetProgress = 15,
                unit = "Hours",
                deadline = "Completed!",
                isCompleted = true
            ),
            LearningGoal(
                id = "g3",
                title = "Host 4 Peer Teaching Sessions",
                category = "Peer Teaching",
                currentProgress = 3,
                targetProgress = 4,
                unit = "Sessions",
                deadline = "In 4 days",
                isCompleted = false
            ),
            LearningGoal(
                id = "g4",
                title = "Score >80% on 5 Subject Quizzes",
                category = "Quiz Target",
                currentProgress = 4,
                targetProgress = 5,
                unit = "Quizzes",
                deadline = "End of Week",
                isCompleted = false
            )
        )
    )
    val learningGoals: StateFlow<List<LearningGoal>> = _learningGoals.asStateFlow()

    fun addLearningGoal(title: String, category: String, target: Int, unit: String, deadline: String) {
        val newGoal = LearningGoal(
            id = "g_${UUID.randomUUID().toString().take(6)}",
            title = title,
            category = category,
            currentProgress = 0,
            targetProgress = target,
            unit = unit,
            deadline = deadline,
            isCompleted = false
        )
        _learningGoals.update { listOf(newGoal) + it }
        addNotification(
            title = "New Goal Created! 🎯",
            message = "Goal '$title' added to your personal learning progress.",
            type = "XP",
            icon = "🎯"
        )
    }

    fun incrementGoalProgress(goalId: String) {
        _learningGoals.update { list ->
            list.map { goal ->
                if (goal.id == goalId) {
                    val newProgress = (goal.currentProgress + 1).coerceAtMost(goal.targetProgress)
                    val isNowCompleted = newProgress >= goal.targetProgress
                    if (isNowCompleted && !goal.isCompleted) {
                        addXp(75, "Completed goal: ${goal.title} (+75 XP)")
                        addNotification(
                            title = "Goal Accomplished! 🏆",
                            message = "Congratulations on completing '${goal.title}'!",
                            type = "REWARD",
                            icon = "🎉"
                        )
                    }
                    goal.copy(currentProgress = newProgress, isCompleted = isNowCompleted)
                } else goal
            }
        }
    }

    fun toggleGoalCompletion(goalId: String) {
        _learningGoals.update { list ->
            list.map { goal ->
                if (goal.id == goalId) {
                    val toggled = !goal.isCompleted
                    val newProgress = if (toggled) goal.targetProgress else (goal.targetProgress / 2)
                    if (toggled) {
                        addXp(50, "Achieved learning goal: ${goal.title} (+50 XP)")
                    }
                    goal.copy(isCompleted = toggled, currentProgress = newProgress)
                } else goal
            }
        }
    }

    fun deleteGoal(goalId: String) {
        _learningGoals.update { list -> list.filterNot { it.id == goalId } }
    }

    fun recordLearningActivity(
        title: String,
        subtitle: String,
        type: TimelineActivityType,
        xp: Int,
        scorePercent: Int? = null,
        details: String = ""
    ) {
        val newItem = LearningHistoryItem(
            id = "h_${UUID.randomUUID().toString().take(6)}",
            title = title,
            subtitle = subtitle,
            timestamp = "Just now",
            relativeTime = "Just now",
            type = type,
            scorePercent = scorePercent,
            xpEarned = xp,
            icon = type.icon,
            details = details
        )
        _learningHistory.update { listOf(newItem) + it }
        addXp(xp, "$title (+$xp XP)")
    }

    fun getTeach2LearnScoreBreakdown(): Teach2LearnScoreBreakdown {
        val subjects = _subjectProgress.value
        val history = _learningHistory.value
        val user = _currentUser.value

        // LEARN Score: calculated from average subject progress (40%), quiz accuracy (40%), and recent study consistency (20%)
        val avgProgress = if (subjects.isNotEmpty()) subjects.map { it.progressPercent }.average().toInt() else 60
        val avgQuiz = if (subjects.isNotEmpty()) subjects.map { it.quizAveragePercent }.average().toInt() else 75
        val studyScore = ((avgProgress * 0.45) + (avgQuiz * 0.45) + (user.streakDays.coerceAtMost(10) * 1.0)).toInt().coerceIn(0, 100)

        // TEACH Score: calculated from students helped, teaching sessions completed, and rating
        val sessionsWeight = (user.sessionsCompleted * 2.5).toInt().coerceAtMost(35)
        val helpedWeight = (user.studentsHelped * 1.2).toInt().coerceAtMost(40)
        val ratingWeight = (user.rating * 5.0).toInt().coerceAtMost(25)
        val teachScore = (sessionsWeight + helpedWeight + ratingWeight).coerceIn(0, 100)

        // Overall TEACH2LEARN score: weighted composite (50% LEARN, 50% TEACH)
        val overall = ((studyScore * 0.5) + (teachScore * 0.5)).toInt()

        return Teach2LearnScoreBreakdown(
            learnScore = studyScore,
            teachScore = teachScore,
            overallScore = overall,
            learnDetails = "$avgProgress% subject syllabus coverage • $avgQuiz% avg quiz accuracy • ${user.streakDays}-day streak",
            teachDetails = "${user.sessionsCompleted} sessions completed • ${user.studentsHelped} peers helped • ${user.rating}★ rating",
            evaluationSummary = if (overall >= 80) "Exceptional Balanced Peer Scholar & Mentor" else if (overall >= 60) "Active Learner with Strong Mentorship Potential" else "Developing Foundation in Peer Learning"
        )
    }

    // ==========================================
    // TEACH2LEARN CONTROL CENTER (ADMIN ONLY)
    // ==========================================

    fun isCurrentAdmin(): Boolean {
        val user = _currentUser.value
        return user.role == UserRole.ADMIN && user.email.equals("alokjanrao5@gmail.com", ignoreCase = true)
    }

    fun hasAdminPermission(): Boolean = isCurrentAdmin()

    fun hasAdminPermission(userId: String): Boolean {
        val user = _userAccounts[userId]?.profile ?: if (_currentUser.value.id == userId) _currentUser.value else null
        return user?.role == UserRole.ADMIN && user.email.equals("alokjanrao5@gmail.com", ignoreCase = true)
    }

    private val _proUsers = MutableStateFlow(
        listOf(
            ProUserRecord(
                id = "user_alok",
                name = "Alok Janrao",
                email = "alokjanrao5@gmail.com",
                role = UserRole.ADMIN,
                accountStatus = "Active",
                proPlan = ProPlanType.CAMPUS_BETA_PRO,
                proStatus = ProSubscriptionStatus.ACTIVE,
                subscriptionStartDate = "01 Aug 2026",
                subscriptionExpiryDate = "01 Aug 2027",
                startTimestamp = 1785542400000L,
                expiryTimestamp = 1817078400000L,
                lastActiveTime = "2 mins ago",
                lastActiveTimestamp = System.currentTimeMillis() - (2 * 60 * 1000L),
                college = "Government College of Engineering",
                department = "CSE",
                educationLevel = "B.Tech",
                learningActivitySummary = "Completed 8 lectures in C and Data Structures. 6 subject quizzes passed with 92% average.",
                teachingActivitySummary = "Conducted 18 peer study sessions in C & Python. Helped 34 students with pointers and dynamic memory debugging.",
                totalLecturesAttended = 8,
                totalQuizzesTaken = 6,
                totalSessionsHosted = 18,
                totalStudentsHelped = 34,
                overallProgressPercent = 65,
                teach2LearnScore = 81,
                proFeaturesUsed = listOf("AI Learning Assistant", "Advanced Progress", "Smart Recommendations", "Unlimited Note Downloads", "1080p Lecture Archive", "Verified Digital Certificates"),
                featureUsageStats = mapOf("AI Learning Assistant" to 68, "Advanced Progress" to 24, "Unlimited Note Downloads" to 14, "1080p Lecture Archive" to 18)
            ),
            ProUserRecord(
                id = "user_rahul",
                name = "Rahul Sharma",
                email = "rahul.c@example.edu",
                role = UserRole.TEACHER,
                accountStatus = "Active",
                proPlan = ProPlanType.CAMPUS_PRO_ANNUAL,
                proStatus = ProSubscriptionStatus.ACTIVE,
                subscriptionStartDate = "15 Jul 2026",
                subscriptionExpiryDate = "15 Jul 2027",
                startTimestamp = 1784083200000L,
                expiryTimestamp = 1815619200000L,
                lastActiveTime = "12 mins ago",
                lastActiveTimestamp = System.currentTimeMillis() - (12 * 60 * 1000L),
                college = "National Institute of Technology",
                department = "CSE",
                educationLevel = "B.Tech",
                learningActivitySummary = "14 lectures attended in Advanced System Design. 12 quizzes completed with 95% average score.",
                teachingActivitySummary = "Master Peer Teacher. 42 live sessions hosted, 86 juniors helped across memory models and recursion.",
                totalLecturesAttended = 14,
                totalQuizzesTaken = 12,
                totalSessionsHosted = 42,
                totalStudentsHelped = 86,
                overallProgressPercent = 85,
                teach2LearnScore = 92,
                proFeaturesUsed = listOf("AI Learning Assistant", "Unlimited Note Downloads", "1080p Lecture Archive", "Verified Digital Certificates"),
                featureUsageStats = mapOf("1080p Lecture Archive" to 42, "AI Learning Assistant" to 54, "Verified Digital Certificates" to 3)
            ),
            ProUserRecord(
                id = "user_sneha",
                name = "Sneha Rao",
                email = "sneha.ai@example.edu",
                role = UserRole.TEACHER,
                accountStatus = "Active",
                proPlan = ProPlanType.CAMPUS_PRO_SEMESTER,
                proStatus = ProSubscriptionStatus.EXPIRING_SOON,
                subscriptionStartDate = "20 Mar 2026",
                subscriptionExpiryDate = "20 Sep 2026",
                startTimestamp = 1773964800000L,
                expiryTimestamp = 1789862400000L,
                lastActiveTime = "25 mins ago",
                lastActiveTimestamp = System.currentTimeMillis() - (25 * 60 * 1000L),
                college = "Govt Engineering College",
                department = "AI & Data Science",
                educationLevel = "B.Tech",
                learningActivitySummary = "18 lectures completed across Deep Learning and Cloud Computing. 15 quizzes taken (98% score).",
                teachingActivitySummary = "Grand Mentor with 58 live sessions and 112 students assisted in Python and Machine Learning lab prep.",
                totalLecturesAttended = 18,
                totalQuizzesTaken = 15,
                totalSessionsHosted = 58,
                totalStudentsHelped = 112,
                overallProgressPercent = 90,
                teach2LearnScore = 95,
                proFeaturesUsed = listOf("AI Learning Assistant", "Advanced Progress", "Priority Live Teaching Seats", "Smart Recommendations"),
                featureUsageStats = mapOf("AI Learning Assistant" to 89, "Priority Live Teaching Seats" to 34, "Advanced Progress" to 41)
            ),
            ProUserRecord(
                id = "user_vikram",
                name = "Vikram Malhotra",
                email = "vikram.m@example.edu",
                role = UserRole.STUDENT,
                accountStatus = "Active",
                proPlan = ProPlanType.CAMPUS_PRO_ANNUAL,
                proStatus = ProSubscriptionStatus.ACTIVE,
                subscriptionStartDate = "10 Aug 2026",
                subscriptionExpiryDate = "10 Aug 2027",
                startTimestamp = 1786320000000L,
                expiryTimestamp = 1817856000000L,
                lastActiveTime = "1 hour ago",
                lastActiveTimestamp = System.currentTimeMillis() - (60 * 60 * 1000L),
                college = "Indian Institute of Information Technology",
                department = "IT",
                educationLevel = "B.Tech",
                learningActivitySummary = "16 lectures completed in DBMS and Operating Systems. 14 quizzes taken with 88% accuracy.",
                teachingActivitySummary = "Learner track. Has answered 4 peer doubts on database SQL normalization.",
                totalLecturesAttended = 16,
                totalQuizzesTaken = 14,
                totalSessionsHosted = 2,
                totalStudentsHelped = 6,
                overallProgressPercent = 78,
                teach2LearnScore = 74,
                proFeaturesUsed = listOf("AI Learning Assistant", "Smart Recommendations", "Unlimited Note Downloads"),
                featureUsageStats = mapOf("AI Learning Assistant" to 52, "Unlimited Note Downloads" to 22, "Smart Recommendations" to 19)
            ),
            ProUserRecord(
                id = "user_ananya",
                name = "Ananya Gupta",
                email = "ananya.g@example.edu",
                role = UserRole.STUDENT,
                accountStatus = "Active",
                proPlan = ProPlanType.CAMPUS_PRO_SEMESTER,
                proStatus = ProSubscriptionStatus.EXPIRED,
                subscriptionStartDate = "01 Feb 2026",
                subscriptionExpiryDate = "01 Aug 2026",
                startTimestamp = 1769904000000L,
                expiryTimestamp = 1785542400000L,
                lastActiveTime = "2 days ago",
                lastActiveTimestamp = System.currentTimeMillis() - (48 * 60 * 60 * 1000L),
                college = "Sardar Patel Institute of Technology",
                department = "CSE",
                educationLevel = "B.Tech",
                learningActivitySummary = "10 lectures completed in Engineering Mathematics. 8 quizzes taken with 76% accuracy.",
                teachingActivitySummary = "Primarily learning track. Participated in 3 group study rooms.",
                totalLecturesAttended = 10,
                totalQuizzesTaken = 8,
                totalSessionsHosted = 0,
                totalStudentsHelped = 2,
                overallProgressPercent = 52,
                teach2LearnScore = 60,
                proFeaturesUsed = listOf("Unlimited Note Downloads", "Advanced Progress"),
                featureUsageStats = mapOf("Unlimited Note Downloads" to 18, "Advanced Progress" to 12)
            ),
            ProUserRecord(
                id = "user_arjun",
                name = "Arjun Nair",
                email = "arjun.n@example.edu",
                role = UserRole.TEACHER,
                accountStatus = "Active",
                proPlan = ProPlanType.CAMPUS_PRO_ANNUAL,
                proStatus = ProSubscriptionStatus.ACTIVE,
                subscriptionStartDate = "12 May 2026",
                subscriptionExpiryDate = "12 May 2027",
                startTimestamp = 1778544000000L,
                expiryTimestamp = 1810080000000L,
                lastActiveTime = "3 hours ago",
                lastActiveTimestamp = System.currentTimeMillis() - (3 * 60 * 60 * 1000L),
                college = "College of Engineering Pune",
                department = "Electrical Engineering",
                educationLevel = "B.Tech",
                learningActivitySummary = "11 lectures in Signal Processing & Control Systems. 9 quizzes taken with 85% average.",
                teachingActivitySummary = "Conducted 24 live lab problem solving sessions. Helped 48 peers solve circuit theorems.",
                totalLecturesAttended = 11,
                totalQuizzesTaken = 9,
                totalSessionsHosted = 24,
                totalStudentsHelped = 48,
                overallProgressPercent = 80,
                teach2LearnScore = 86,
                proFeaturesUsed = listOf("AI Learning Assistant", "Priority Live Teaching Seats", "Verified Digital Certificates"),
                featureUsageStats = mapOf("Priority Live Teaching Seats" to 28, "AI Learning Assistant" to 39, "Verified Digital Certificates" to 2)
            ),
            ProUserRecord(
                id = "user_deepa",
                name = "Deepa Joshi",
                email = "deepa.j@example.edu",
                role = UserRole.STUDENT,
                accountStatus = "Active",
                proPlan = ProPlanType.CAMPUS_PRO_SEMESTER,
                proStatus = ProSubscriptionStatus.EXPIRING_SOON,
                subscriptionStartDate = "25 Mar 2026",
                subscriptionExpiryDate = "25 Sep 2026",
                startTimestamp = 1774396800000L,
                expiryTimestamp = 1790294400000L,
                lastActiveTime = "4 hours ago",
                lastActiveTimestamp = System.currentTimeMillis() - (4 * 60 * 60 * 1000L),
                college = "Government Polytechnic",
                department = "Computer Technology",
                educationLevel = "Diploma",
                learningActivitySummary = "12 lectures in Java Object-Oriented Programming. 7 quizzes passed with 81% average.",
                teachingActivitySummary = "Active in doubts discussion. Resolved 9 peer queries.",
                totalLecturesAttended = 12,
                totalQuizzesTaken = 7,
                totalSessionsHosted = 1,
                totalStudentsHelped = 9,
                overallProgressPercent = 64,
                teach2LearnScore = 68,
                proFeaturesUsed = listOf("AI Learning Assistant", "Advanced Progress", "Unlimited Note Downloads"),
                featureUsageStats = mapOf("AI Learning Assistant" to 44, "Unlimited Note Downloads" to 16, "Advanced Progress" to 22)
            ),
            ProUserRecord(
                id = "user_kunal",
                name = "Kunal Sen",
                email = "kunal.s@example.edu",
                role = UserRole.STUDENT,
                accountStatus = "Active",
                proPlan = ProPlanType.CAMPUS_PRO_ANNUAL,
                proStatus = ProSubscriptionStatus.ACTIVE,
                subscriptionStartDate = "05 Aug 2026",
                subscriptionExpiryDate = "05 Aug 2027",
                startTimestamp = 1785888000000L,
                expiryTimestamp = 1817424000000L,
                lastActiveTime = "5 hours ago",
                lastActiveTimestamp = System.currentTimeMillis() - (5 * 60 * 60 * 1000L),
                college = "Delhi Technological University",
                department = "Software Engineering",
                educationLevel = "B.Tech",
                learningActivitySummary = "9 lectures in Computer Networks and Cryptography. 6 quizzes with 84% score.",
                teachingActivitySummary = "Hosted 3 study sessions on TCP/IP handshake and subnetting.",
                totalLecturesAttended = 9,
                totalQuizzesTaken = 6,
                totalSessionsHosted = 3,
                totalStudentsHelped = 11,
                overallProgressPercent = 58,
                teach2LearnScore = 62,
                proFeaturesUsed = listOf("AI Learning Assistant", "Unlimited Note Downloads"),
                featureUsageStats = mapOf("AI Learning Assistant" to 31, "Unlimited Note Downloads" to 12)
            ),
            ProUserRecord(
                id = "user_tanvi",
                name = "Tanvi Deshmukh",
                email = "tanvi.d@example.edu",
                role = UserRole.STUDENT,
                accountStatus = "Active",
                proPlan = ProPlanType.CAMPUS_PRO_SEMESTER,
                proStatus = ProSubscriptionStatus.ACTIVE,
                subscriptionStartDate = "01 Jul 2026",
                subscriptionExpiryDate = "01 Jan 2027",
                startTimestamp = 1782873600000L,
                expiryTimestamp = 1798761600000L,
                lastActiveTime = "Yesterday",
                lastActiveTimestamp = System.currentTimeMillis() - (24 * 60 * 60 * 1000L),
                college = "VJTI Mumbai",
                department = "Electronics & Telecomm",
                educationLevel = "B.Tech",
                learningActivitySummary = "15 lectures in Microcontrollers & Embedded C. 11 quizzes with 91% score.",
                teachingActivitySummary = "Assisted 12 classmates with Arduino and 8051 assembly lab assignments.",
                totalLecturesAttended = 15,
                totalQuizzesTaken = 11,
                totalSessionsHosted = 4,
                totalStudentsHelped = 12,
                overallProgressPercent = 72,
                teach2LearnScore = 77,
                proFeaturesUsed = listOf("AI Learning Assistant", "Advanced Progress", "Smart Recommendations"),
                featureUsageStats = mapOf("AI Learning Assistant" to 60, "Advanced Progress" to 29, "Smart Recommendations" to 21)
            ),
            ProUserRecord(
                id = "user_aditya",
                name = "Aditya Kulkarni",
                email = "aditya.k@example.edu",
                role = UserRole.STUDENT,
                accountStatus = "Active",
                proPlan = ProPlanType.CAMPUS_PRO_SEMESTER,
                proStatus = ProSubscriptionStatus.EXPIRED,
                subscriptionStartDate = "10 Jan 2026",
                subscriptionExpiryDate = "10 Jul 2026",
                startTimestamp = 1767993600000L,
                expiryTimestamp = 1783641600000L,
                lastActiveTime = "5 days ago",
                lastActiveTimestamp = System.currentTimeMillis() - (5 * 24 * 60 * 60 * 1000L),
                college = "Government College of Engineering Karad",
                department = "Mechanical Engineering",
                educationLevel = "B.Tech",
                learningActivitySummary = "7 lectures in Fluid Mechanics and Thermodynamics. 5 quizzes with 68% score.",
                teachingActivitySummary = "Studied peer notes. No teaching sessions hosted.",
                totalLecturesAttended = 7,
                totalQuizzesTaken = 5,
                totalSessionsHosted = 0,
                totalStudentsHelped = 1,
                overallProgressPercent = 41,
                teach2LearnScore = 48,
                proFeaturesUsed = listOf("Unlimited Note Downloads"),
                featureUsageStats = mapOf("Unlimited Note Downloads" to 14)
            ),
            ProUserRecord(
                id = "user_meera",
                name = "Meera Iyer",
                email = "meera.i@example.edu",
                role = UserRole.TEACHER,
                accountStatus = "Active",
                proPlan = ProPlanType.CAMPUS_PRO_ANNUAL,
                proStatus = ProSubscriptionStatus.ACTIVE,
                subscriptionStartDate = "18 Jun 2026",
                subscriptionExpiryDate = "18 Jun 2027",
                startTimestamp = 1781740800000L,
                expiryTimestamp = 1813276800000L,
                lastActiveTime = "30 mins ago",
                lastActiveTimestamp = System.currentTimeMillis() - (30 * 60 * 1000L),
                college = "PSG College of Technology",
                department = "CSE",
                educationLevel = "B.Tech",
                learningActivitySummary = "13 lectures completed in Compiler Design and Automata. 10 quizzes passed with 94% score.",
                teachingActivitySummary = "Top peer educator in Algorithms & Graph Theory. Hosted 31 live lectures and helped 64 students.",
                totalLecturesAttended = 13,
                totalQuizzesTaken = 10,
                totalSessionsHosted = 31,
                totalStudentsHelped = 64,
                overallProgressPercent = 84,
                teach2LearnScore = 89,
                proFeaturesUsed = listOf("1080p Lecture Archive", "AI Learning Assistant", "Verified Digital Certificates"),
                featureUsageStats = mapOf("1080p Lecture Archive" to 38, "AI Learning Assistant" to 67, "Verified Digital Certificates" to 4)
            ),
            ProUserRecord(
                id = "user_harsh",
                name = "Harsh Vardhan",
                email = "harsh.v@example.edu",
                role = UserRole.STUDENT,
                accountStatus = "Active",
                proPlan = ProPlanType.CAMPUS_PRO_ANNUAL,
                proStatus = ProSubscriptionStatus.ACTIVE,
                subscriptionStartDate = "15 Aug 2026",
                subscriptionExpiryDate = "15 Aug 2027",
                startTimestamp = 1786752000000L,
                expiryTimestamp = 1818288000000L,
                lastActiveTime = "6 hours ago",
                lastActiveTimestamp = System.currentTimeMillis() - (6 * 60 * 60 * 1000L),
                college = "MIT World Peace University",
                department = "Computer Science",
                educationLevel = "B.Tech",
                learningActivitySummary = "14 lectures in Python Data Science and NumPy. 12 quizzes taken with 89% accuracy.",
                teachingActivitySummary = "Participates in daily challenge discussions. Assisted 8 classmates.",
                totalLecturesAttended = 14,
                totalQuizzesTaken = 12,
                totalSessionsHosted = 2,
                totalStudentsHelped = 8,
                overallProgressPercent = 68,
                teach2LearnScore = 71,
                proFeaturesUsed = listOf("AI Learning Assistant", "Smart Recommendations"),
                featureUsageStats = mapOf("AI Learning Assistant" to 48, "Smart Recommendations" to 26)
            )
        )
    )
    val proUsers: StateFlow<List<ProUserRecord>> = _proUsers.asStateFlow()

    fun getControlCenterOverview(): ControlCenterOverviewMetrics {
        val total = 142
        val active = 118
        val newUsers = 24
        val activeLearners = 98
        val activeTeachers = 32
        val proCount = 127
        return ControlCenterOverviewMetrics(
            totalUsers = total,
            activeUsers = active,
            newUsers = newUsers,
            activeLearners = activeLearners,
            activeTeachers = activeTeachers,
            proUsersCount = proCount
        )
    }

    fun getProHealthMetrics(): ProHealthMetrics {
        val total = 127
        val active = 103
        val expiring = 14
        val expired = 10
        val percentage = (active.toFloat() / total.toFloat()) * 100f
        return ProHealthMetrics(
            totalPro = total,
            activePro = active,
            expiringSoonPro = expiring,
            expiredPro = expired,
            activePercentage = percentage
        )
    }

    fun getProAnalyticsData(): ProAnalyticsData {
        return ProAnalyticsData(
            totalProUsers = 127,
            activeProUsers = 103,
            expiredProUsers = 10,
            newProUsersThisMonth = 28,
            proConversionRatePercent = 18.4f,
            proTrendPoints = listOf(
                "Aug W1" to 76,
                "Aug W2" to 89,
                "Aug W3" to 102,
                "Aug W4" to 114,
                "Sep W1" to 121,
                "Sep W2" to 127
            )
        )
    }

    fun getProFeatureUsageMetrics(): List<FeatureUsageMetric> {
        return listOf(
            FeatureUsageMetric(
                featureName = "AI Learning Assistant",
                usageCount = 104,
                percentage = 82,
                category = "Pro AI",
                description = "Deep doubt explanation, pseudocode generation, and step-by-step debug assistance"
            ),
            FeatureUsageMetric(
                featureName = "Unlimited Note Downloads",
                usageCount = 116,
                percentage = 91,
                category = "Content",
                description = "Offline university exam formula sheets, past year solved papers & verified summaries"
            ),
            FeatureUsageMetric(
                featureName = "Verified Digital Certificates",
                usageCount = 99,
                percentage = 78,
                category = "Credentials",
                description = "Cryptographically signed Academic Council peer teacher and scholar diplomas"
            ),
            FeatureUsageMetric(
                featureName = "1080p Lecture Archive",
                usageCount = 90,
                percentage = 71,
                category = "Streaming",
                description = "Full HD recorded replays with slide sync and timestamped peer bookmarks"
            ),
            FeatureUsageMetric(
                featureName = "Advanced Progress & Radar",
                usageCount = 85,
                percentage = 67,
                category = "Analytics",
                description = "Deep TEACH2LEARN score breakdown, topic accuracy breakdown and weakness detection"
            ),
            FeatureUsageMetric(
                featureName = "Priority Live Teaching Seats",
                usageCount = 74,
                percentage = 58,
                category = "Mentorship",
                description = "Guaranteed interactive 1-on-1 audio/video mic permissions during crowded mentor streams"
            ),
            FeatureUsageMetric(
                featureName = "Smart Recommendations",
                usageCount = 69,
                percentage = 54,
                category = "Personalization",
                description = "Curated study syllabus paths tailored to upcoming semester college exam schedules"
            )
        )
    }

    fun getPlatformInsightsData(): PlatformInsightsData {
        return PlatformInsightsData(
            mostUsedFeature = "AI Learning Assistant & Doubt Solver",
            mostUsedFeatureStats = "3,890 queries solved this month (82% Pro adoption)",
            mostActiveDay = "Thursday & Sunday Evenings",
            mostActiveDayStats = "Peak concurrent users between 7:00 PM - 10:30 PM",
            mostActiveUsers = listOf(
                "Rahul Sharma" to "42 live peer sessions • 86 helped • 2,450 XP",
                "Sneha Rao" to "58 live peer sessions • 112 helped • 2,890 XP",
                "Alok Janrao" to "18 live sessions • 34 helped • 1,240 XP",
                "Meera Iyer" to "31 sessions • 64 helped • 2,120 XP",
                "Vikram Malhotra" to "14 quizzes • 16 lectures attended"
            ),
            fastestGrowingFeature = "Short 60s Tech Reels",
            fastestGrowingStats = "+142% engagement week-over-week across mobile learners",
            averageUserActivity = "4.2 study hours / week • 3.1 quizzes completed • 2.4 doubts explored",
            hasSufficientData = true
        )
    }

    fun extendProSubscription(userId: String, additionalDays: Int = 30) {
        _proUsers.update { list ->
            list.map { user ->
                if (user.id == userId) {
                    val addedMillis = additionalDays * 24 * 60 * 60 * 1000L
                    val newExpiryTs = if (user.expiryTimestamp > System.currentTimeMillis()) {
                        user.expiryTimestamp + addedMillis
                    } else {
                        System.currentTimeMillis() + addedMillis
                    }
                    user.copy(
                        proStatus = ProSubscriptionStatus.ACTIVE,
                        expiryTimestamp = newExpiryTs,
                        subscriptionExpiryDate = "Oct 2027"
                    )
                } else user
            }
        }
    }

    fun updateProStatus(userId: String, newStatus: ProSubscriptionStatus) {
        _proUsers.update { list ->
            list.map { if (it.id == userId) it.copy(proStatus = newStatus) else it }
        }
    }

    fun updateAccountStatus(userId: String, newStatus: String) {
        _proUsers.update { list ->
            list.map { if (it.id == userId) it.copy(accountStatus = newStatus) else it }
        }
    }

    fun grantProPlan(userId: String, plan: ProPlanType) {
        _proUsers.update { list ->
            list.map {
                if (it.id == userId) {
                    it.copy(
                        proPlan = plan,
                        proStatus = ProSubscriptionStatus.ACTIVE,
                        subscriptionStartDate = "Today",
                        subscriptionExpiryDate = "Next Year"
                    )
                } else it
            }
        }
    }

    // ========================================================
    // SHORT LEARNING SYSTEM (Firestore + Cloud Storage)
    // ========================================================

    private val _firestoreShortVideos = MutableStateFlow<List<FirestoreShortVideo>>(
        listOf(
            FirestoreShortVideo(
                videoId = "short_c_ptr_1",
                title = "Pointer Arithmetic: Memory Addresses Demystified",
                description = "Understanding how incrementing ptr changes address by sizeof(type). Avoid memory segfaults!",
                subject = "C Programming",
                teacherId = "user_alok",
                teacherName = "Alok Janrao",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4",
                thumbnailUrl = "💻",
                createdAt = System.currentTimeMillis() - 7200000L,
                views = 1420,
                likes = 184,
                status = "active",
                isLiked = true,
                isSaved = true,
                durationSeconds = 54
            ),
            FirestoreShortVideo(
                videoId = "short_py_comp_2",
                title = "Python List Comprehensions vs Loops: Clean Code",
                description = "Turn 8 lines of nested loop filter into 1 elegant line in Python without sacrificing performance.",
                subject = "Python",
                teacherId = "user_sneha",
                teacherName = "Sneha Rao",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4",
                thumbnailUrl = "🐍",
                createdAt = System.currentTimeMillis() - 14400000L,
                views = 2890,
                likes = 312,
                status = "active",
                isLiked = false,
                isSaved = true,
                durationSeconds = 48
            ),
            FirestoreShortVideo(
                videoId = "short_dsa_quick_3",
                title = "QuickSort Partitioning: Lomuto vs Hoare Scheme",
                description = "Step-by-step visual swap tracing. Why Hoare's scheme makes 3x fewer swaps in practice.",
                subject = "Data Structures",
                teacherId = "user_rahul",
                teacherName = "Rahul Sharma",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4",
                thumbnailUrl = "⚡",
                createdAt = System.currentTimeMillis() - 28800000L,
                views = 3450,
                likes = 428,
                status = "active",
                isLiked = false,
                isSaved = false,
                durationSeconds = 58
            ),
            FirestoreShortVideo(
                videoId = "short_math_eigen_4",
                title = "Eigenvectors: Why Direction Never Changes",
                description = "Visual geometric transformation showing how Av = λv scales space without rotating the vector.",
                subject = "Engineering Mathematics",
                teacherId = "user_meera",
                teacherName = "Meera Iyer",
                videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerEscapes.mp4",
                thumbnailUrl = "📐",
                createdAt = System.currentTimeMillis() - 86400000L,
                views = 980,
                likes = 145,
                status = "active",
                isLiked = false,
                isSaved = false,
                durationSeconds = 42
            )
        )
    )
    val firestoreShortVideos: StateFlow<List<FirestoreShortVideo>> = _firestoreShortVideos.asStateFlow()

    fun publishShortVideo(
        title: String,
        description: String,
        subject: String,
        videoUrl: String,
        thumbnailUrl: String = "⚡"
    ): FirestoreShortVideo {
        val user = _currentUser.value
        val newVideo = FirestoreShortVideo(
            videoId = "short_${UUID.randomUUID().toString().take(8)}",
            title = title.trim(),
            description = description.trim(),
            subject = subject,
            teacherId = user.id,
            teacherName = user.name,
            videoUrl = videoUrl,
            thumbnailUrl = thumbnailUrl,
            createdAt = System.currentTimeMillis(),
            views = 1,
            likes = 0,
            status = "active",
            isLiked = false,
            isSaved = false,
            durationSeconds = 60
        )

        _firestoreShortVideos.update { listOf(newVideo) + it }

        // Award XP for sharing peer educational content
        addXp(50, "Published Short Learning Video: $title")

        // Also add to existing reels list so old screens can see it
        val newReel = ShortVideo(
            id = newVideo.videoId,
            title = newVideo.title,
            subject = newVideo.subject,
            topic = newVideo.title,
            category = newVideo.subject,
            creatorName = user.name,
            creatorAvatar = "",
            creatorRank = RankTier.MASTER,
            videoUrl = newVideo.videoUrl,
            thumbnail = newVideo.thumbnailUrl,
            summary = newVideo.description,
            keyConcept = newVideo.description,
            durationSeconds = newVideo.durationSeconds,
            likesCount = 0,
            commentsCount = 0,
            isLiked = false,
            isSaved = false
        )
        _shortVideos.update { listOf(newReel) + it }

        return newVideo
    }

    fun toggleShortVideoLike(videoId: String) {
        _firestoreShortVideos.update { list ->
            list.map { v ->
                if (v.videoId == videoId) {
                    val willLike = !v.isLiked
                    v.copy(
                        isLiked = willLike,
                        likes = if (willLike) v.likes + 1 else maxOf(0, v.likes - 1)
                    )
                } else v
            }
        }
    }

    fun toggleShortVideoSave(videoId: String) {
        _firestoreShortVideos.update { list ->
            list.map { v ->
                if (v.videoId == videoId) {
                    v.copy(isSaved = !v.isSaved)
                } else v
            }
        }
    }

    fun incrementShortVideoViews(videoId: String) {
        _firestoreShortVideos.update { list ->
            list.map { v ->
                if (v.videoId == videoId) v.copy(views = v.views + 1) else v
            }
        }
    }

    fun removeShortVideo(videoId: String) {
        _firestoreShortVideos.update { list ->
            list.filter { it.videoId != videoId }
        }
        _shortVideos.update { list ->
            list.filter { it.id != videoId }
        }
    }

    fun flagShortVideo(videoId: String) {
        _firestoreShortVideos.update { list ->
            list.map { if (it.videoId == videoId) it.copy(status = "flagged") else it }
        }
    }

    // ========================================================
    // LIVE LEARNING SYSTEM (Real-Time Audio/Video & Classes)
    // ========================================================

    private val _firestoreLiveClasses = MutableStateFlow<List<FirestoreLiveClass>>(
        listOf(
            FirestoreLiveClass(
                classId = "live_c_prog_101",
                hostId = "user_alok",
                hostName = "Alok Janrao",
                title = "Pointer Arithmetic & Memory Layout Lab",
                subject = "C Programming",
                description = "Live peer coding session: solving segment tree and dynamic memory allocation traps in C.",
                scheduledAt = "LIVE NOW",
                status = "live",
                roomId = "room-c-live-101",
                createdAt = System.currentTimeMillis() - 1800000L,
                participantCount = 14,
                teacherAvatar = "🎓"
            ),
            FirestoreLiveClass(
                classId = "live_dsa_202",
                hostId = "user_rahul",
                hostName = "Rahul Sharma",
                title = "Binary Tree Traversals & Recursion Unwound",
                subject = "Data Structures",
                description = "Interactive breakdown of In-Order, Pre-Order, and Level-Order traversals with memory stack trace.",
                scheduledAt = "Today, 8:30 PM",
                status = "scheduled",
                roomId = "room-dsa-202",
                createdAt = System.currentTimeMillis() - 3600000L,
                participantCount = 28,
                teacherAvatar = "⚡"
            ),
            FirestoreLiveClass(
                classId = "live_ai_303",
                hostId = "user_sneha",
                hostName = "Sneha Rao",
                title = "Neural Networks from Scratch in Python",
                subject = "AI & Data Science",
                description = "Writing matrix multiplication forward prop and backpropagation without high-level frameworks.",
                scheduledAt = "Tomorrow, 6:00 PM",
                status = "scheduled",
                roomId = "room-ai-303",
                createdAt = System.currentTimeMillis() - 7200000L,
                participantCount = 42,
                teacherAvatar = "🤖"
            )
        )
    )
    val firestoreLiveClasses: StateFlow<List<FirestoreLiveClass>> = _firestoreLiveClasses.asStateFlow()

    private val _liveParticipants = MutableStateFlow<Map<String, List<FirestoreLiveParticipant>>>(
        mapOf(
            "live_c_prog_101" to listOf(
                FirestoreLiveParticipant("p1", "live_c_prog_101", "user_alok", "Alok Janrao (Host)", "host", isMuted = false, isVideoOn = true, isHandRaised = false),
                FirestoreLiveParticipant("p2", "live_c_prog_101", "user_priya", "Priya Patel", "student", isMuted = true, isVideoOn = true, isHandRaised = true),
                FirestoreLiveParticipant("p3", "live_c_prog_101", "user_rahul", "Rahul Sharma", "student", isMuted = true, isVideoOn = false, isHandRaised = false),
                FirestoreLiveParticipant("p4", "live_c_prog_101", "user_aman", "Aman Verma", "student", isMuted = false, isVideoOn = true, isHandRaised = false),
                FirestoreLiveParticipant("p5", "live_c_prog_101", "user_sneha", "Sneha Rao", "student", isMuted = true, isVideoOn = false, isHandRaised = false)
            )
        )
    )
    val liveParticipants: StateFlow<Map<String, List<FirestoreLiveParticipant>>> = _liveParticipants.asStateFlow()

    private val _liveChatMessages = MutableStateFlow<Map<String, List<LiveClassChatMessage>>>(
        mapOf(
            "live_c_prog_101" to listOf(
                LiveClassChatMessage("m1", "live_c_prog_101", "user_alok", "Alok Janrao", "Welcome to live session! We are reviewing malloc vs calloc memory layout.", "7:00 PM", isTeacher = true),
                LiveClassChatMessage("m2", "live_c_prog_101", "user_priya", "Priya Patel", "Hello! Excited for the live code debugging.", "7:02 PM"),
                LiveClassChatMessage("m3", "live_c_prog_101", "user_aman", "Aman Verma", "Can you show pointer-to-pointer **ptr on screen?", "7:04 PM")
            )
        )
    )
    val liveChatMessages: StateFlow<Map<String, List<LiveClassChatMessage>>> = _liveChatMessages.asStateFlow()

    fun scheduleOrStartLiveClass(
        title: String,
        subject: String,
        description: String,
        scheduledAt: String,
        isStartNow: Boolean
    ): FirestoreLiveClass {
        val user = _currentUser.value
        val roomId = "room-${UUID.randomUUID().toString().take(6)}"
        val newClass = FirestoreLiveClass(
            classId = "live_${UUID.randomUUID().toString().take(8)}",
            hostId = user.id,
            hostName = user.name,
            title = title.trim(),
            subject = subject,
            description = description.trim(),
            scheduledAt = if (isStartNow) "LIVE NOW" else scheduledAt.ifBlank { "Today, 7:00 PM" },
            status = if (isStartNow) "live" else "scheduled",
            roomId = roomId,
            createdAt = System.currentTimeMillis(),
            participantCount = 1,
            teacherAvatar = "🎓"
        )

        _firestoreLiveClasses.update { listOf(newClass) + it }

        // Add host as participant
        val hostParticipant = FirestoreLiveParticipant(
            participantId = "p_${user.id}",
            classId = newClass.classId,
            userId = user.id,
            userName = "${user.name} (Host)",
            role = "host",
            isMuted = false,
            isVideoOn = true,
            isHandRaised = false
        )
        _liveParticipants.update { currentMap ->
            currentMap + (newClass.classId to listOf(hostParticipant))
        }

        // Add welcome message
        val welcomeMsg = LiveClassChatMessage(
            id = "msg_${UUID.randomUUID().toString().take(6)}",
            classId = newClass.classId,
            senderId = user.id,
            senderName = user.name,
            message = "Class started: ${newClass.title}. Welcome students!",
            timestamp = "Just now",
            isTeacher = true
        )
        _liveChatMessages.update { currentMap ->
            currentMap + (newClass.classId to listOf(welcomeMsg))
        }

        addXp(60, "Hosted Live Class: ${newClass.title}")
        return newClass
    }

    fun endLiveClass(classId: String) {
        _firestoreLiveClasses.update { list ->
            list.map { if (it.classId == classId) it.copy(status = "ended") else it }
        }
    }

    fun removeLiveClass(classId: String) {
        _firestoreLiveClasses.update { list ->
            list.filter { it.classId != classId }
        }
    }

    fun joinLiveClass(classId: String) {
        val user = _currentUser.value
        _firestoreLiveClasses.update { list ->
            list.map {
                if (it.classId == classId) it.copy(participantCount = it.participantCount + 1) else it
            }
        }

        val studentParticipant = FirestoreLiveParticipant(
            participantId = "p_${UUID.randomUUID().toString().take(6)}",
            classId = classId,
            userId = user.id,
            userName = user.name,
            role = if (user.id == _firestoreLiveClasses.value.firstOrNull { it.classId == classId }?.hostId) "host" else "student",
            isMuted = true,
            isVideoOn = false,
            isHandRaised = false
        )

        _liveParticipants.update { currentMap ->
            val existing = currentMap[classId] ?: emptyList()
            if (existing.any { it.userId == user.id }) currentMap
            else currentMap + (classId to (existing + studentParticipant))
        }
    }

    fun leaveLiveClass(classId: String) {
        val user = _currentUser.value
        _firestoreLiveClasses.update { list ->
            list.map {
                if (it.classId == classId) it.copy(participantCount = maxOf(1, it.participantCount - 1)) else it
            }
        }
        _liveParticipants.update { currentMap ->
            val existing = currentMap[classId] ?: emptyList()
            currentMap + (classId to existing.filter { it.userId != user.id })
        }
    }

    fun toggleParticipantMic(classId: String, userId: String) {
        _liveParticipants.update { currentMap ->
            val list = currentMap[classId] ?: emptyList()
            currentMap + (classId to list.map {
                if (it.userId == userId) it.copy(isMuted = !it.isMuted) else it
            })
        }
    }

    fun toggleParticipantCamera(classId: String, userId: String) {
        _liveParticipants.update { currentMap ->
            val list = currentMap[classId] ?: emptyList()
            currentMap + (classId to list.map {
                if (it.userId == userId) it.copy(isVideoOn = !it.isVideoOn) else it
            })
        }
    }

    fun toggleHandRaise(classId: String, userId: String) {
        _liveParticipants.update { currentMap ->
            val list = currentMap[classId] ?: emptyList()
            currentMap + (classId to list.map {
                if (it.userId == userId) it.copy(isHandRaised = !it.isHandRaised) else it
            })
        }
    }

    fun sendLiveChatMessage(classId: String, messageText: String) {
        if (messageText.isBlank()) return
        val user = _currentUser.value
        val hostId = _firestoreLiveClasses.value.firstOrNull { it.classId == classId }?.hostId
        val msg = LiveClassChatMessage(
            id = "msg_${UUID.randomUUID().toString().take(6)}",
            classId = classId,
            senderId = user.id,
            senderName = user.name,
            message = messageText.trim(),
            timestamp = "Now",
            isTeacher = user.id == hostId
        )
        _liveChatMessages.update { currentMap ->
            val list = currentMap[classId] ?: emptyList()
            currentMap + (classId to (list + msg))
        }
    }
}


