package com.limdo.hangul

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DefaultGuardianVoicePlaybackTest {
    private val instrumentation = InstrumentationRegistry.getInstrumentation()

    @Test
    fun allBundledGuardianVoicesPlayToCompletionAndCanBeStopped() {
        val lessonIds = GuardianLessonCatalog.lessons.map { it.id }

        assertEquals(38, lessonIds.size)

        lessonIds.forEach { lessonId ->
            val completed = CountDownLatch(1)
            val controller = GuardianVoiceController(instrumentation.targetContext, lessonId)
            lateinit var idleState: GuardianVoiceState
            instrumentation.runOnMainSync {
                idleState = controller.currentState()
                assertTrue(controller.play(useUserRecording = false) { completed.countDown() })
                assertEquals(GuardianVoiceState.PLAYING, controller.currentState())
            }
            assertTrue("$lessonId playback did not complete", completed.await(3, TimeUnit.SECONDS))
            instrumentation.runOnMainSync {
                assertEquals(idleState, controller.currentState())
            }

            instrumentation.runOnMainSync {
                assertTrue(controller.play(useUserRecording = false))
                assertEquals(GuardianVoiceState.PLAYING, controller.currentState())
                controller.stopPlayback()
                assertEquals(idleState, controller.currentState())
                controller.release()
            }
        }
    }
}
