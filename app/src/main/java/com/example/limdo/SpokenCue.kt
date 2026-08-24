package com.example.limdo

internal enum class SpokenCue(val utterance: String) {
    INITIAL("초록점에서 시작해 오른쪽, 아래로 그려봐."),
    SUCCESS("잘했어! 기역을 완성했어."),
    RETRY_START("초록점에서 다시 시작해보자."),
    RETRY_DIRECTION("오른쪽으로 가고, 아래로 내려가보자."),
    RETRY_GUIDE("선을 따라 다시 그려보자."),
    RETRY_FINISH("끝까지 이어서 그려보자."),
}

internal object SpokenCueModel {
    fun forResult(result: GieokTraceResult?): SpokenCue = when (result) {
        null, GieokTraceResult.EMPTY -> SpokenCue.INITIAL
        GieokTraceResult.SUCCESS -> SpokenCue.SUCCESS
        GieokTraceResult.WRONG_START -> SpokenCue.RETRY_START
        GieokTraceResult.WRONG_DIRECTION -> SpokenCue.RETRY_DIRECTION
        GieokTraceResult.OFF_GUIDE -> SpokenCue.RETRY_GUIDE
        GieokTraceResult.INCOMPLETE -> SpokenCue.RETRY_FINISH
    }
}

internal fun shouldResumeSuccessCue(
    speechState: SpeechPlaybackState,
    traceResult: GieokTraceResult?,
    successSpeechPending: Boolean,
    alreadyHandled: Boolean,
): Boolean = speechState == SpeechPlaybackState.Ready &&
    traceResult == GieokTraceResult.SUCCESS &&
    successSpeechPending &&
    !alreadyHandled
