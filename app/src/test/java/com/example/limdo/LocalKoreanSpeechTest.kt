package com.example.limdo

import android.speech.tts.TextToSpeech
import java.util.Locale
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalKoreanSpeechTest {
    @Test
    fun acceptsInstalledOfflineKoreanVoice() {
        assertTrue(
            isInstalledOfflineKoreanVoice(
                language = Locale.KOREAN.language,
                networkRequired = false,
                features = emptySet(),
            ),
        )
    }

    @Test
    fun rejectsNetworkMissingAndNonKoreanVoices() {
        assertFalse(
            isInstalledOfflineKoreanVoice(
                language = Locale.KOREAN.language,
                networkRequired = true,
                features = emptySet(),
            ),
        )
        assertFalse(
            isInstalledOfflineKoreanVoice(
                language = Locale.KOREAN.language,
                networkRequired = false,
                features = setOf(TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED),
            ),
        )
        assertFalse(
            isInstalledOfflineKoreanVoice(
                language = Locale.ENGLISH.language,
                networkRequired = false,
                features = emptySet(),
            ),
        )
    }
}
