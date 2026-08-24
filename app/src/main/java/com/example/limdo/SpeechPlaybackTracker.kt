package com.example.limdo

internal sealed interface SpeechPlaybackState {
    data object Initializing : SpeechPlaybackState
    data object Ready : SpeechPlaybackState
    data class Playing(val cue: SpokenCue) : SpeechPlaybackState
    data class Completed(val cue: SpokenCue) : SpeechPlaybackState
    data class Error(val cue: SpokenCue?) : SpeechPlaybackState
    data object Unavailable : SpeechPlaybackState
    data object Released : SpeechPlaybackState
}

internal fun SpeechPlaybackState.diagnosticToken(): String = when (this) {
    SpeechPlaybackState.Initializing -> "초기화 중"
    SpeechPlaybackState.Ready -> "준비 완료"
    is SpeechPlaybackState.Playing -> "재생 중:${cue.name}"
    is SpeechPlaybackState.Completed -> "재생 완료:${cue.name}"
    is SpeechPlaybackState.Error -> "재생 오류:${cue?.name ?: "알 수 없음"}"
    SpeechPlaybackState.Unavailable -> "사용 불가"
    SpeechPlaybackState.Released -> "자원 해제"
}

internal val SpeechPlaybackState.canReplay: Boolean
    get() = this is SpeechPlaybackState.Ready || this is SpeechPlaybackState.Completed

internal class SpeechPlaybackTracker {
    var state: SpeechPlaybackState = SpeechPlaybackState.Initializing
        private set

    private var nextRequestNumber = 0
    private var activeRequestId: String? = null

    fun ready() {
        if (state != SpeechPlaybackState.Released) state = SpeechPlaybackState.Ready
    }

    fun unavailable() {
        if (state != SpeechPlaybackState.Released) state = SpeechPlaybackState.Unavailable
    }

    fun start(cue: SpokenCue): String? {
        if (state is SpeechPlaybackState.Unavailable || state is SpeechPlaybackState.Released) return null
        nextRequestNumber += 1
        return "${cue.name}-$nextRequestNumber".also {
            activeRequestId = it
            state = SpeechPlaybackState.Playing(cue)
        }
    }

    fun completed(requestId: String) {
        val playing = state as? SpeechPlaybackState.Playing ?: return
        if (requestId == activeRequestId) state = SpeechPlaybackState.Completed(playing.cue)
    }

    fun failed(requestId: String?) {
        val playing = state as? SpeechPlaybackState.Playing
        if (requestId == null || requestId == activeRequestId) {
            state = SpeechPlaybackState.Error(playing?.cue)
        }
    }

    fun stop() {
        if (state is SpeechPlaybackState.Playing || state is SpeechPlaybackState.Completed) {
            activeRequestId = null
            state = SpeechPlaybackState.Ready
        }
    }

    fun release() {
        activeRequestId = null
        state = SpeechPlaybackState.Released
    }
}
