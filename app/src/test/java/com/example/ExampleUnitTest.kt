package com.example

import com.example.data.model.TimelineActivityType
import com.example.data.repository.Teach2LearnRepository
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testTeach2LearnScoreBreakdown() {
        val repo = Teach2LearnRepository()
        val scores = repo.getTeach2LearnScoreBreakdown()

        assertTrue("Learn score should be positive", scores.learnScore > 0)
        assertTrue("Teach score should be positive", scores.teachScore > 0)
        assertTrue("Overall score should be between 0 and 100", scores.overallScore in 0..100)
        assertTrue(scores.learnDetails.isNotBlank())
        assertTrue(scores.teachDetails.isNotBlank())
    }

    @Test
    fun testLearningGoalsManagement() {
        val repo = Teach2LearnRepository()
        val initialCount = repo.learningGoals.value.size

        // Add a goal
        repo.addLearningGoal(
            title = "Master Recursion in C",
            category = "Skill Mastery",
            target = 5,
            unit = "Topics",
            deadline = "Next Week"
        )

        assertEquals(initialCount + 1, repo.learningGoals.value.size)
        val addedGoal = repo.learningGoals.value.first { it.title == "Master Recursion in C" }
        assertEquals(0, addedGoal.currentProgress)
        assertFalse(addedGoal.isCompleted)

        // Increment progress
        repo.incrementGoalProgress(addedGoal.id)
        val updatedGoal = repo.learningGoals.value.first { it.id == addedGoal.id }
        assertEquals(1, updatedGoal.currentProgress)

        // Toggle completion
        repo.toggleGoalCompletion(addedGoal.id)
        val completedGoal = repo.learningGoals.value.first { it.id == addedGoal.id }
        assertTrue(completedGoal.isCompleted)

        // Delete goal
        repo.deleteGoal(addedGoal.id)
        assertEquals(initialCount, repo.learningGoals.value.size)
    }

    @Test
    fun testRecordLearningActivity() {
        val repo = Teach2LearnRepository()
        val initialHistoryCount = repo.learningHistory.value.size
        val initialXp = repo.currentUser.value.xp

        repo.recordLearningActivity(
            title = "Binary Tree Traversal Lab",
            subtitle = "Completed in-order and pre-order practice",
            type = TimelineActivityType.PRACTICE_SESSION,
            xp = 40,
            scorePercent = 95
        )

        assertEquals(initialHistoryCount + 1, repo.learningHistory.value.size)
        assertEquals(initialXp + 40, repo.currentUser.value.xp)
        val latest = repo.learningHistory.value.first()
        assertEquals("Binary Tree Traversal Lab", latest.title)
        assertEquals(95, latest.scorePercent)
    }

    @Test
    fun testControlCenterRoleBasedAccessControl() {
        val repo = Teach2LearnRepository()

        // Default user is Alok (ADMIN)
        assertTrue("Default user Alok must have ADMIN privileges", repo.isCurrentAdmin())
        assertTrue(repo.hasAdminPermission("user_alok"))

        // Switch to Rahul (TEACHER) - must NOT have admin access
        repo.switchUser("user_rahul")
        assertFalse("Rahul (TEACHER) must NOT have ADMIN privileges", repo.isCurrentAdmin())
        assertFalse(repo.hasAdminPermission("user_rahul"))

        // Switch to Vikram (STUDENT) - must NOT have admin access
        repo.switchUser("user_vikram")
        assertFalse("Vikram (STUDENT) must NOT have ADMIN privileges", repo.isCurrentAdmin())
        assertFalse(repo.hasAdminPermission("user_vikram"))

        // Switch back to Alok (ADMIN)
        repo.switchUser("user_alok")
        assertTrue(repo.isCurrentAdmin())
    }

    @Test
    fun testControlCenterOverviewAndProHealthMetrics() {
        val repo = Teach2LearnRepository()

        val overview = repo.getControlCenterOverview()
        assertTrue("Total users should be > 0", overview.totalUsers > 0)
        assertTrue("Active users should be > 0", overview.activeUsers > 0)
        assertTrue("Pro users count should match 127", overview.proUsersCount == 127)

        val health = repo.getProHealthMetrics()
        assertEquals(127, health.totalPro)
        assertEquals(103, health.activePro)
        assertEquals(14, health.expiringSoonPro)
        assertEquals(10, health.expiredPro)
        assertTrue("Active percentage should be approx 81%", health.activePercentage in 80.0..82.0)
    }

    @Test
    fun testProUserSubscriptionManagement() {
        val repo = Teach2LearnRepository()
        val userRahul = repo.proUsers.value.first { it.id == "user_rahul" }
        val initialExpiry = userRahul.expiryTimestamp

        // Extend Pro subscription by 30 days
        repo.extendProSubscription("user_rahul", 30)
        val updatedUser = repo.proUsers.value.first { it.id == "user_rahul" }
        assertTrue("Expiry timestamp should be increased by 30 days", updatedUser.expiryTimestamp > initialExpiry)

        // Toggle account status
        repo.updateAccountStatus("user_rahul", "Suspended")
        assertEquals("Suspended", repo.proUsers.value.first { it.id == "user_rahul" }.accountStatus)

        repo.updateAccountStatus("user_rahul", "Active")
        assertEquals("Active", repo.proUsers.value.first { it.id == "user_rahul" }.accountStatus)
    }
}
