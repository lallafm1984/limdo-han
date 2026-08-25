package com.example.limdo

internal enum class SpokenCue(val utterance: String) {
    INITIAL("초록점에서 오른쪽, 아래쪽. 다음은 아래쪽, 마지막은 오른쪽으로 그려봐."),
    SUCCESS("잘했어! 가를 완성했어."),
    RETRY_START("초록점에서 다시 시작해보자."),
    RETRY_SECOND_START("두 번째 획 위에서 시작해 아래쪽으로 그려보자."),
    RETRY_THIRD_START("세 번째 획 왼쪽에서 시작해 오른쪽으로 그려보자."),
    RETRY_DIRECTION("오른쪽으로 가고, 아래로 내려가보자."),
    RETRY_SECOND_DIRECTION("두 번째 획은 아래쪽으로 내려가보자."),
    RETRY_THIRD_DIRECTION("세 번째 획은 오른쪽으로 그려보자."),
    RETRY_GUIDE("선을 따라 다시 그려보자."),
    RETRY_SECOND_GUIDE("두 번째 선을 따라 아래쪽으로 그려보자."),
    RETRY_THIRD_GUIDE("세 번째 선을 따라 오른쪽으로 그려보자."),
    RETRY_FINISH("끝까지 이어서 그려보자."),
    RETRY_SECOND_FINISH("두 번째 획을 아래쪽 끝까지 그려보자."),
    RETRY_THIRD_FINISH("세 번째 획을 오른쪽 끝까지 그려보자."),
}

internal object SpokenCueModel {
    fun forResult(result: GieokTraceResult?, strokeIndex: Int = 0): SpokenCue = when (result) {
        null, GieokTraceResult.EMPTY -> SpokenCue.INITIAL
        GieokTraceResult.SUCCESS -> SpokenCue.SUCCESS
        GieokTraceResult.WRONG_START -> forStroke(
            strokeIndex,
            SpokenCue.RETRY_START,
            SpokenCue.RETRY_SECOND_START,
            SpokenCue.RETRY_THIRD_START,
        )
        GieokTraceResult.WRONG_DIRECTION -> when (strokeIndex) {
            1 -> SpokenCue.RETRY_SECOND_DIRECTION
            2 -> SpokenCue.RETRY_THIRD_DIRECTION
            else -> SpokenCue.RETRY_DIRECTION
        }
        GieokTraceResult.OFF_GUIDE -> forStroke(
            strokeIndex,
            SpokenCue.RETRY_GUIDE,
            SpokenCue.RETRY_SECOND_GUIDE,
            SpokenCue.RETRY_THIRD_GUIDE,
        )
        GieokTraceResult.INCOMPLETE -> forStroke(
            strokeIndex,
            SpokenCue.RETRY_FINISH,
            SpokenCue.RETRY_SECOND_FINISH,
            SpokenCue.RETRY_THIRD_FINISH,
        )
    }

    private fun forStroke(
        strokeIndex: Int,
        first: SpokenCue,
        second: SpokenCue,
        third: SpokenCue,
    ): SpokenCue = when (strokeIndex) {
        1 -> second
        2 -> third
        else -> first
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

internal fun shouldResumeInitialCue(
    speechState: SpeechPlaybackState,
    initialSpeechPending: Boolean,
    alreadyHandled: Boolean,
): Boolean = speechState == SpeechPlaybackState.Ready &&
    initialSpeechPending &&
    !alreadyHandled

internal fun shouldStartNextInitialCue(
    moveCompleted: Boolean,
    nextVehiclePending: Boolean,
): Boolean = moveCompleted && nextVehiclePending
