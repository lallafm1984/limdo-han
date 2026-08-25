package com.example.limdo

internal fun SpokenCue.demonstrationStrokeIndex(characterStart: Int): Int? {
    if (characterStart < 0) return null

    when (this) {
        SpokenCue.RETRY_START,
        SpokenCue.RETRY_DIRECTION,
        SpokenCue.RETRY_GUIDE,
        SpokenCue.RETRY_FINISH,
        -> return 0
        SpokenCue.RETRY_SECOND_START,
        SpokenCue.RETRY_SECOND_DIRECTION,
        SpokenCue.RETRY_SECOND_GUIDE,
        SpokenCue.RETRY_SECOND_FINISH,
        -> return 1
        SpokenCue.RETRY_THIRD_START,
        SpokenCue.RETRY_THIRD_DIRECTION,
        SpokenCue.RETRY_THIRD_GUIDE,
        SpokenCue.RETRY_THIRD_FINISH,
        -> return 2
        SpokenCue.INITIAL -> Unit
        SpokenCue.SUCCESS -> return null
    }

    val secondStrokeStart = utterance.indexOf(". ").let { if (it < 0) it else it + 2 }
    val thirdStrokeStart = utterance.lastIndexOf(", ").let { if (it < 0) it else it + 2 }
    return when {
        thirdStrokeStart >= 0 && characterStart >= thirdStrokeStart -> 2
        secondStrokeStart >= 0 && characterStart >= secondStrokeStart -> 1
        else -> 0
    }
}
