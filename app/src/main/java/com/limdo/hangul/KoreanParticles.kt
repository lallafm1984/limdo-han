package com.limdo.hangul

internal fun objectiveParticleFor(text: String): String {
    val lastCharacter = text.lastOrNull() ?: return "를"
    val syllableOffset = lastCharacter.code - HANGUL_SYLLABLE_START
    val hasFinalConsonant = syllableOffset in 0 until HANGUL_SYLLABLE_COUNT &&
        syllableOffset % HANGUL_FINAL_CONSONANT_COUNT != 0
    return if (hasFinalConsonant) "을" else "를"
}

internal fun writingCanvasDescription(glyph: String, strokeCount: Int): String =
    "큰 쓰기판. 초록 점에서 시작해 $glyph${objectiveParticleFor(glyph)} ${strokeCount}획으로 그려요"

private const val HANGUL_SYLLABLE_START = 0xAC00
private const val HANGUL_SYLLABLE_COUNT = 11_172
private const val HANGUL_FINAL_CONSONANT_COUNT = 28
