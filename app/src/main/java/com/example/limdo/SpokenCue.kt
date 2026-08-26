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
    INITIAL_BIEUP("왼쪽을 아래로. 다음은 오른쪽을 아래로. 가운데를 오른쪽으로. 마지막은 아래를 오른쪽으로."),
    SUCCESS_BIEUP("잘했어! 비읍을 완성했어."),
    INITIAL_SIOT("가운데에서 왼쪽 아래로. 다음은 오른쪽 아래로 그려봐."),
    SUCCESS_SIOT("잘했어! 시옷을 완성했어."),
    INITIAL_IEUNG("위에서 오른쪽으로 돌아 동그라미를 그려봐."),
    SUCCESS_IEUNG("잘했어! 이응을 완성했어."),
    INITIAL_JIEUT("위를 오른쪽으로. 가운데에서 왼쪽 아래로. 마지막은 오른쪽 아래로."),
    SUCCESS_JIEUT("잘했어! 지읏을 완성했어."),
    INITIAL_CHIEUT("짧게 오른쪽으로. 아래 긴 선도 오른쪽으로. 왼쪽 아래로. 오른쪽 아래로."),
    SUCCESS_CHIEUT("잘했어! 치읓을 완성했어."),
    INITIAL_KIEUK("오른쪽, 모서리에서 아래쪽으로. 다음은 가운데를 오른쪽으로."),
    SUCCESS_KIEUK("잘했어! 키읔을 완성했어."),
    INITIAL_TIEUT("위를 오른쪽으로. 가운데를 오른쪽으로. 마지막은 왼쪽 아래와 바닥을 이어봐."),
    SUCCESS_TIEUT("잘했어! 티읕을 완성했어."),
    INITIAL_PIEUP("위를 오른쪽으로. 아래도 오른쪽으로. 왼쪽 짧은 선을 아래로. 오른쪽 선도 아래로."),
    SUCCESS_PIEUP("잘했어! 피읖을 완성했어."),
    INITIAL_HIEUH("위를 오른쪽으로. 다음은 가운데를 오른쪽으로. 마지막은 위에서 오른쪽으로 돌아 동그라미를 그려봐."),
    SUCCESS_HIEUH("잘했어! 히읗을 완성했어."),
    INITIAL_A("아래쪽으로 그려봐. 다음은 오른쪽으로 그려봐."),
    SUCCESS_A("잘했어! 아를 완성했어."),
    INITIAL_AE("왼쪽을 아래로. 가운데를 오른쪽으로. 마지막은 오른쪽을 아래로."),
    SUCCESS_AE("잘했어! 애를 완성했어."),
    INITIAL_YA("아래쪽으로 그려봐. 다음은 위쪽 짧은 선을 오른쪽으로. 마지막은 아래쪽 짧은 선을 오른쪽으로."),
    SUCCESS_YA("잘했어! 야를 완성했어."),
    INITIAL_EO("아래쪽으로 그려봐. 다음은 가운데에서 왼쪽으로 그려봐."),
    SUCCESS_EO("잘했어! 어를 완성했어."),
    INITIAL_YEO("아래쪽으로 그려봐. 다음은 위쪽 짧은 선을 왼쪽으로. 마지막은 아래쪽 짧은 선을 왼쪽으로."),
    SUCCESS_YEO("잘했어! 여를 완성했어."),
    INITIAL_O("왼쪽에서 오른쪽으로 그려봐. 다음은 가운데에서 위쪽으로 그려봐."),
    SUCCESS_O("잘했어! 오를 완성했어."),
    INITIAL_YO("왼쪽에서 오른쪽으로 그려봐. 다음은 왼쪽 짧은 선을 위쪽으로. 마지막은 오른쪽 짧은 선을 위쪽으로."),
    SUCCESS_YO("잘했어! 요를 완성했어."),
    INITIAL_U("왼쪽에서 오른쪽으로 그려봐. 다음은 가운데에서 아래쪽으로 그려봐."),
    SUCCESS_U("잘했어! 우를 완성했어."),
    INITIAL_YU("긴 선을 오른쪽으로. 다음은 왼쪽 짧은 선을 아래쪽으로. 마지막은 오른쪽 짧은 선을 아래쪽으로."),
    SUCCESS_YU("잘했어! 유를 완성했어."),
    INITIAL_EU("왼쪽에서 오른쪽으로 그려봐."),
    SUCCESS_EU("잘했어! 으를 완성했어."),
    INITIAL_I("위에서 아래쪽으로 그려봐."),
    SUCCESS_I("잘했어! 이를 완성했어."),
    INITIAL("오른쪽, 모서리에서 아래쪽으로 그려봐. 다음은 아래쪽으로 그려봐. 마지막은 오른쪽으로 그려봐."),
    SUCCESS("잘했어! 가를 완성했어."),
    INITIAL_NA("아래쪽, 모서리에서 오른쪽으로 그려봐. 다음은 아래쪽으로 그려봐. 마지막은 오른쪽으로 그려봐."),
    SUCCESS_NA("잘했어! 나를 완성했어."),
    INITIAL_DA("오른쪽으로 그려봐. 다음은 아래쪽, 오른쪽. 다음은 아래쪽. 마지막은 오른쪽."),
    SUCCESS_DA("잘했어! 다를 완성했어."),
    INITIAL_RA("오른쪽 아래. 다음은 왼쪽. 다음은 아래 오른쪽. 다음은 아래. 마지막은 오른쪽."),
    SUCCESS_RA("잘했어! 라를 완성했어."),
    INITIAL_MA("왼쪽 아래. 다음은 오른쪽 아래. 다음은 아래를 오른쪽. 다음은 아래. 마지막은 오른쪽."),
    SUCCESS_MA("잘했어! 마를 완성했어."),
    INITIAL_BA("왼쪽 아래. 오른쪽 아래. 가운데를 오른쪽. 아래를 오른쪽. 다음은 아래. 마지막은 오른쪽."),
    SUCCESS_BA("잘했어! 바를 완성했어."),
    INITIAL_SA("가운데에서 왼쪽 아래. 다음은 오른쪽 아래. 다음은 아래. 마지막은 오른쪽."),
    SUCCESS_SA("잘했어! 사를 완성했어."),
    INITIAL_AH("위에서 오른쪽으로 돌아 동그라미. 다음은 아래. 마지막은 오른쪽."),
    SUCCESS_AH("잘했어! 아를 완성했어."),
    INITIAL_JA("위를 오른쪽. 왼쪽 아래. 오른쪽 아래. 다음은 아래. 마지막은 오른쪽."),
    SUCCESS_JA("잘했어! 자를 완성했어."),
    INITIAL_CHA("짧게 오른쪽. 아래도 오른쪽. 왼쪽 아래. 오른쪽 아래. 다음은 아래. 마지막은 오른쪽."),
    SUCCESS_CHA("잘했어! 차를 완성했어."),
    INITIAL_KA("오른쪽 아래. 다음은 가운데를 오른쪽. 다음은 아래. 마지막은 오른쪽."),
    SUCCESS_KA("잘했어! 카를 완성했어."),
    INITIAL_TA("위를 오른쪽. 가운데도 오른쪽. 왼쪽 아래와 바닥. 다음은 아래. 마지막은 오른쪽."),
    SUCCESS_TA("잘했어! 타를 완성했어."),
    INITIAL_PA("위를 오른쪽. 아래도 오른쪽. 왼쪽을 아래. 오른쪽도 아래. 다음은 아래. 마지막은 오른쪽."),
    SUCCESS_PA("잘했어! 파를 완성했어."),
    INITIAL_HA("위를 오른쪽. 가운데도 오른쪽. 동그라미를 오른쪽으로 돌아봐. 다음은 아래. 마지막은 오른쪽."),
    SUCCESS_HA("잘했어! 하를 완성했어."),
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
