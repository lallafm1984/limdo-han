package com.example.limdo

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class SpeechPlaybackTrackerTest {
    @Test
    fun `모든 재생 상태는 발화문 없는 안정적인 진단 토큰을 제공한다`() {
        val expected = mapOf(
            SpeechPlaybackState.Initializing to "초기화 중",
            SpeechPlaybackState.Ready to "준비 완료",
            SpeechPlaybackState.Playing(SpokenCue.INITIAL) to "재생 중:INITIAL",
            SpeechPlaybackState.Completed(SpokenCue.SUCCESS) to "재생 완료:SUCCESS",
            SpeechPlaybackState.Error(SpokenCue.RETRY_GUIDE) to "재생 오류:RETRY_GUIDE",
            SpeechPlaybackState.Error(null) to "재생 오류:알 수 없음",
            SpeechPlaybackState.Unavailable to "사용 불가",
            SpeechPlaybackState.Released to "자원 해제",
        )

        expected.forEach { (state, token) ->
            assertEquals(token, state.diagnosticToken())
            SpokenCue.entries.forEach { cue ->
                assertFalse(state.diagnosticToken().contains(cue.utterance))
            }
        }
    }

    @Test
    fun newestRequestReplacesPriorCallbackIdentity() {
        val tracker = SpeechPlaybackTracker()
        tracker.ready()

        val first = tracker.start(SpokenCue.INITIAL)!!
        val latest = tracker.start(SpokenCue.RETRY_START)!!
        assertNotEquals(first, latest)
        assertEquals(SpeechPlaybackState.Playing(SpokenCue.RETRY_START), tracker.state)

        tracker.completed(first)
        assertEquals(SpeechPlaybackState.Playing(SpokenCue.RETRY_START), tracker.state)
        tracker.completed(latest)
        assertEquals(SpeechPlaybackState.Completed(SpokenCue.RETRY_START), tracker.state)
    }

    @Test
    fun unavailableAndReleasedStatesRejectPlayback() {
        val unavailable = SpeechPlaybackTracker().apply { unavailable() }
        assertNull(unavailable.start(SpokenCue.INITIAL))

        val released = SpeechPlaybackTracker().apply {
            ready()
            release()
        }
        assertNull(released.start(SpokenCue.SUCCESS))
        released.ready()
        assertEquals(SpeechPlaybackState.Released, released.state)
    }

    @Test
    fun currentPlaybackErrorExposesFailedCue() {
        val tracker = SpeechPlaybackTracker()
        tracker.ready()
        val request = tracker.start(SpokenCue.RETRY_GUIDE)!!

        tracker.failed(request)

        assertEquals(SpeechPlaybackState.Error(SpokenCue.RETRY_GUIDE), tracker.state)
    }
}
