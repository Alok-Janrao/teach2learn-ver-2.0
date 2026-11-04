package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.data.model.Teach2LearnScoreBreakdown
import com.example.ui.screens.Teach2LearnScoreHeroCard
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val sampleScores = Teach2LearnScoreBreakdown(
      learnScore = 84,
      teachScore = 78,
      overallScore = 81,
      learnDetails = "85% subject coverage • 92% quiz accuracy",
      teachDetails = "18 sessions completed • 34 peers helped",
      evaluationSummary = "Exceptional Balanced Peer Scholar & Mentor"
    )

    composeTestRule.setContent {
      MyApplicationTheme {
        Teach2LearnScoreHeroCard(scoreBreakdown = sampleScores)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/progress_score.png")
  }
}
