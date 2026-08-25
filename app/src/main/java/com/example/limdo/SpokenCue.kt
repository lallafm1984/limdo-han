package com.example.limdo

internal enum class SpokenCue(val utterance: String) {
    INITIAL_GIEOK("초록점에서 오른쪽, 모서리에서 아래쪽으로 그려봐."),
    SUCCESS_GIEOK("잘했어! 기역을 완성했어."),
    INITIAL_NIEUN("초록점에서 아래쪽, 모서리에서 오른쪽으로 그려봐."),
    SUCCESS_NIEUN("잘했어! 니은을 완성했어."),
    INITIAL_DIGEUT("오른쪽으로 그려봐. 다음은 아래쪽, 모서리에서 오른쪽으로 그려봐."),
    SUCCESS_DIGEUT("잘했어! 디귿을 완성했어."),
    INITIAL_RIEUL("오른쪽, 아래쪽으로 그려봐. 다음은 왼쪽으로 그려봐. 마지막은 아래쪽, 오른쪽으로 그려봐."),
    SUCCESS_RIEUL("잘했어! 리을을 완성했어."),
    INITIAL_MIEUM("아래쪽으로 그려봐. 다음은 오른쪽, 아래쪽으로 그려봐. 마지막은 오른쪽으로 그려봐."),
    SUCCESS_MIEUM("잘했어! 미음을 완성했어."),
    INITIAL_BIEUP("왼쪽 아래. 다음은 오른쪽 아래. 다음은 위를 오른쪽. 마지막은 가운데와 아래를 이어봐."),
    SUCCESS_BIEUP("잘했어! 비읍을 완성했어."),
    INITIAL_A("아래쪽으로 그려봐. 다음은 오른쪽으로 그려봐."),
    SUCCESS_A("잘했어! 아를 완성했어."),
    INITIAL("오른쪽, 모서리에서 아래쪽으로 그려봐. 다음은 아래쪽으로 그려봐. 마지막은 오른쪽으로 그려봐."),
    SUCCESS("잘했어! 가를 완성했어."),
    INITIAL_NA("아래쪽, 모서리에서 오른쪽으로 그려봐. 다음은 아래쪽으로 그려봐. 마지막은 오른쪽으로 그려봐."),
    SUCCESS_NA("잘했어! 나를 완성했어."),
    INITIAL_DA("오른쪽으로 그려봐. 다음은 아래쪽, 오른쪽. 다음은 아래쪽. 마지막은 오른쪽."),
    SUCCESS_DA("잘했어! 다를 완성했어."),
    RETRY_START("초록점에서 다시 시작해보자."),
    RETRY_SECOND_START("두 번째 획 위에서 시작해 아래쪽으로 그려보자."),
    RETRY_THIRD_START("세 번째 획 왼쪽에서 시작해 오른쪽으로 그려보자."),
    RETRY_FOURTH_START("네 번째 획 왼쪽에서 시작해 오른쪽으로 그려보자."),
    RETRY_DIRECTION("오른쪽으로 가고, 아래로 내려가보자."),
    RETRY_FIRST_DOWN_DIRECTION("첫 번째 획은 아래쪽으로 내려가보자."),
    RETRY_SECOND_DIRECTION("두 번째 획은 아래쪽으로 내려가보자."),
    RETRY_SECOND_RIGHT_DIRECTION("두 번째 획은 오른쪽으로 그려보자."),
    RETRY_SECOND_LEFT_DIRECTION("두 번째 획은 왼쪽으로 그려보자."),
    RETRY_THIRD_DIRECTION("세 번째 획은 오른쪽으로 그려보자."),
    RETRY_THIRD_DOWN_DIRECTION("세 번째 획은 아래쪽으로 내려가보자."),
    RETRY_FOURTH_RIGHT_DIRECTION("네 번째 획은 오른쪽으로 그려보자."),
    RETRY_GUIDE("선을 따라 다시 그려보자."),
    RETRY_SECOND_GUIDE("두 번째 선을 따라 아래쪽으로 그려보자."),
    RETRY_THIRD_GUIDE("세 번째 선을 따라 오른쪽으로 그려보자."),
    RETRY_FOURTH_GUIDE("네 번째 선을 따라 오른쪽으로 그려보자."),
    RETRY_FINISH("끝까지 이어서 그려보자."),
    RETRY_SECOND_FINISH("두 번째 획을 아래쪽 끝까지 그려보자."),
    RETRY_THIRD_FINISH("세 번째 획을 오른쪽 끝까지 그려보자."),
    RETRY_FOURTH_FINISH("네 번째 획을 오른쪽 끝까지 그려보자."),
}

internal object SpokenCueModel {
    fun forResult(
        result: GieokTraceResult?,
        strokeIndex: Int = 0,
        lesson: LessonSpec = GaLesson,
    ): SpokenCue = when (result) {
        null, GieokTraceResult.EMPTY -> lesson.initialCue
        GieokTraceResult.SUCCESS -> lesson.successCue
        GieokTraceResult.WRONG_START -> forStroke(
            strokeIndex,
            SpokenCue.RETRY_START,
            SpokenCue.RETRY_SECOND_START,
            SpokenCue.RETRY_THIRD_START,
            SpokenCue.RETRY_FOURTH_START,
        )
        GieokTraceResult.WRONG_DIRECTION -> directionCue(
            strokeIndex = strokeIndex,
            direction = lesson.strokeDirections.getOrElse(strokeIndex) {
                lesson.strokeDirections.last()
            },
        )
        GieokTraceResult.OFF_GUIDE -> forStroke(
            strokeIndex,
            SpokenCue.RETRY_GUIDE,
            SpokenCue.RETRY_SECOND_GUIDE,
            SpokenCue.RETRY_THIRD_GUIDE,
            SpokenCue.RETRY_FOURTH_GUIDE,
        )
        GieokTraceResult.INCOMPLETE -> forStroke(
            strokeIndex,
            SpokenCue.RETRY_FINISH,
            SpokenCue.RETRY_SECOND_FINISH,
            SpokenCue.RETRY_THIRD_FINISH,
            SpokenCue.RETRY_FOURTH_FINISH,
        )
    }

    private fun directionCue(strokeIndex: Int, direction: StrokeDirection): SpokenCue = when {
        strokeIndex <= 0 && direction == StrokeDirection.RIGHT -> SpokenCue.RETRY_DIRECTION
        strokeIndex <= 0 -> SpokenCue.RETRY_FIRST_DOWN_DIRECTION
        strokeIndex == 1 && direction == StrokeDirection.RIGHT -> SpokenCue.RETRY_SECOND_RIGHT_DIRECTION
        strokeIndex == 1 && direction == StrokeDirection.LEFT -> SpokenCue.RETRY_SECOND_LEFT_DIRECTION
        strokeIndex == 1 -> SpokenCue.RETRY_SECOND_DIRECTION
        strokeIndex == 3 -> SpokenCue.RETRY_FOURTH_RIGHT_DIRECTION
        direction == StrokeDirection.RIGHT -> SpokenCue.RETRY_THIRD_DIRECTION
        direction == StrokeDirection.LEFT -> SpokenCue.RETRY_SECOND_LEFT_DIRECTION
        else -> SpokenCue.RETRY_THIRD_DOWN_DIRECTION
    }

    private fun forStroke(
        strokeIndex: Int,
        first: SpokenCue,
        second: SpokenCue,
        third: SpokenCue,
        fourth: SpokenCue,
    ): SpokenCue = when (strokeIndex) {
        1 -> second
        2 -> third
        3 -> fourth
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
