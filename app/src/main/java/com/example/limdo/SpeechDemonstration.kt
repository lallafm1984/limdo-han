package com.example.limdo

internal fun SpokenCue.demonstrationStrokeIndex(characterStart: Int): Int? {
    if (characterStart < 0) return null

    when (this) {
        SpokenCue.RETRY_START,
        SpokenCue.RETRY_DIRECTION,
        SpokenCue.RETRY_FIRST_DOWN_DIRECTION,
        SpokenCue.RETRY_GUIDE,
        SpokenCue.RETRY_FINISH,
        -> return 0
        SpokenCue.RETRY_SECOND_START,
        SpokenCue.RETRY_SECOND_DIRECTION,
        SpokenCue.RETRY_SECOND_RIGHT_DIRECTION,
        SpokenCue.RETRY_SECOND_LEFT_DIRECTION,
        SpokenCue.RETRY_SECOND_GUIDE,
        SpokenCue.RETRY_SECOND_FINISH,
        -> return 1
        SpokenCue.RETRY_THIRD_START,
        SpokenCue.RETRY_THIRD_DIRECTION,
        SpokenCue.RETRY_THIRD_DOWN_DIRECTION,
        SpokenCue.RETRY_THIRD_GUIDE,
        SpokenCue.RETRY_THIRD_FINISH,
        -> return 2
        SpokenCue.RETRY_FOURTH_START,
        SpokenCue.RETRY_FOURTH_RIGHT_DIRECTION,
        SpokenCue.RETRY_FOURTH_GUIDE,
        SpokenCue.RETRY_FOURTH_FINISH,
        -> return 3
        SpokenCue.SUCCESS,
        SpokenCue.SUCCESS_A,
        SpokenCue.SUCCESS_GIEOK,
        SpokenCue.SUCCESS_NIEUN,
        SpokenCue.SUCCESS_DIGEUT,
        SpokenCue.SUCCESS_NA,
        SpokenCue.SUCCESS_DA,
        SpokenCue.SUCCESS_RIEUL,
        SpokenCue.SUCCESS_MIEUM,
        SpokenCue.SUCCESS_BIEUP,
        SpokenCue.SUCCESS_SIOT,
        SpokenCue.SUCCESS_IEUNG,
        SpokenCue.SUCCESS_JIEUT,
        -> return null
        SpokenCue.INITIAL_GIEOK,
        SpokenCue.INITIAL_NIEUN,
        -> return 0
        SpokenCue.INITIAL_A,
        SpokenCue.INITIAL_DIGEUT,
        SpokenCue.INITIAL,
        SpokenCue.INITIAL_NA,
        SpokenCue.INITIAL_DA,
        SpokenCue.INITIAL_RIEUL,
        SpokenCue.INITIAL_MIEUM,
        SpokenCue.INITIAL_BIEUP,
        SpokenCue.INITIAL_SIOT,
        SpokenCue.INITIAL_IEUNG,
        SpokenCue.INITIAL_JIEUT,
        -> Unit
    }

    val strokeStarts = buildList {
        add(0)
        var boundary = utterance.indexOf(". ")
        while (boundary >= 0) {
            add(boundary + 2)
            boundary = utterance.indexOf(". ", boundary + 2)
        }
    }
    return strokeStarts.indexOfLast { characterStart >= it }.coerceAtLeast(0)
}
