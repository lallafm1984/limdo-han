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
    private val onDemonstrationStrokeChanged: (Int?) -> Unit = {},
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

            override fun onRangeStart(utteranceId: String?, start: Int, end: Int, frame: Int) {
                val cue = (tracker.state as? SpeechPlaybackState.Playing)?.cue
                val strokeIndex = cue?.demonstrationStrokeIndex(start)
                Log.i(LOG_TAG, "시범 획:${strokeIndex?.plus(1) ?: 0} 범위:$start-$end")
                onDemonstrationStrokeChanged(strokeIndex)
            }

            override fun onDone(utteranceId: String?) {
                onDemonstrationStrokeChanged(null)
                if (utteranceId != null) update { completed(utteranceId) }
            }

            @Deprecated("Deprecated in Android")
            override fun onError(utteranceId: String?) {
                onDemonstrationStrokeChanged(null)
                update { failed(utteranceId) }
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                onDemonstrationStrokeChanged(null)
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
        onDemonstrationStrokeChanged(null)
        update { stop() }
    }

    fun release() {
        engine?.stop()
        engine?.shutdown()
        engine = null
        onDemonstrationStrokeChanged(null)
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
