package com.example.limdo

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import android.util.Log
import java.util.Locale

internal fun selectInstalledOfflineKoreanVoice(voices: Iterable<Voice>?): Voice? = voices
    ?.asSequence()
    ?.filter {
        isInstalledOfflineKoreanVoice(
            language = it.locale.language,
            networkRequired = it.isNetworkConnectionRequired,
            features = it.features,
        )
    }
    ?.sortedByDescending { it.locale == Locale.KOREA }
    ?.firstOrNull()

internal fun isInstalledOfflineKoreanVoice(
    language: String,
    networkRequired: Boolean,
    features: Set<String>,
): Boolean = language == Locale.KOREAN.language &&
    !networkRequired &&
    TextToSpeech.Engine.KEY_FEATURE_NOT_INSTALLED !in features

internal class LocalKoreanSpeech(
    context: Context,
    private val onStateChanged: (SpeechPlaybackState) -> Unit = {},
) : TextToSpeech.OnInitListener {
    private val tracker = SpeechPlaybackTracker()
    private var engine: TextToSpeech? = TextToSpeech(context.applicationContext, this)

    val state: SpeechPlaybackState
        get() = tracker.state

    override fun onInit(status: Int) {
        val currentEngine = engine
        if (status != TextToSpeech.SUCCESS || currentEngine == null) {
            markUnavailable()
            return
        }

        val offlineKoreanVoice = selectInstalledOfflineKoreanVoice(currentEngine.voices)

        if (offlineKoreanVoice == null || currentEngine.setVoice(offlineKoreanVoice) == TextToSpeech.ERROR) {
            markUnavailable()
            return
        }

        currentEngine.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) = Unit

            override fun onDone(utteranceId: String?) {
                if (utteranceId != null) update { completed(utteranceId) }
            }

            @Deprecated("Deprecated in Android")
            override fun onError(utteranceId: String?) {
                update { failed(utteranceId) }
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                update { failed(utteranceId) }
            }
        })
        update { ready() }
    }

    fun speakLatest(cue: SpokenCue): Boolean {
        val currentEngine = engine ?: return false
        if (tracker.state == SpeechPlaybackState.Initializing) return false
        val requestId = tracker.start(cue) ?: return false
        publishState()
        val result = currentEngine.speak(cue.utterance, TextToSpeech.QUEUE_FLUSH, null, requestId)
        if (result == TextToSpeech.ERROR) update { failed(requestId) }
        return result != TextToSpeech.ERROR
    }

    fun stop() {
        engine?.stop()
        update { stop() }
    }

    fun release() {
        engine?.stop()
        engine?.shutdown()
        engine = null
        update { release() }
    }

    private fun markUnavailable() {
        engine?.shutdown()
        engine = null
        update { unavailable() }
    }

    private inline fun update(change: SpeechPlaybackTracker.() -> Unit) {
        tracker.change()
        publishState()
    }

    private fun publishState() {
        Log.i(LOG_TAG, tracker.state.diagnosticToken())
        onStateChanged(tracker.state)
    }

    private companion object {
        const val LOG_TAG = "LimDoSpeech"
    }
}
