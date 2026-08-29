package com.limdo.hangul

internal enum class GaAssemblyTarget(
    val lessonId: LessonId,
    val glyph: String,
    val initialName: String = "기역",
    val initialStrokeCount: Int = 1,
) {
    GA(LessonId.GA, "가"),
    GEO(LessonId.GEO, "거"),
    GYEO(LessonId.GYEO, "겨"),
    GO(LessonId.GO, "고"),
    GYO(LessonId.GYO, "교"),
    GU(LessonId.GU, "구"),
    GYU(LessonId.GYU, "규"),
    GEU(LessonId.GEU, "그"),
    GI(LessonId.GI, "기"),
    NA(LessonId.NA, "나", initialName = "니은"),
    NEO(LessonId.NEO, "너", initialName = "니은"),
    NYEO(LessonId.NYEO, "\uB140", initialName = "\uB2C8\uC740"),
    NO(LessonId.NO, "노", initialName = "니은"),
    NYO(LessonId.NYO, "뇨", initialName = "니은"),
    NU(LessonId.NU, "누", initialName = "니은"),
    NYU(LessonId.NYU, "뉴", initialName = "니은"),
    NEU(LessonId.NEU, "느", initialName = "니은"),
    NI(LessonId.NI, "니", initialName = "니은"),
    DA(LessonId.DA, "다", initialName = "디귿", initialStrokeCount = 2),
    DEO(LessonId.DEO, "더", initialName = "디귿", initialStrokeCount = 2),
    DYEO(LessonId.DYEO, "뎌", initialName = "디귿", initialStrokeCount = 2),
    DO(LessonId.DO, "도", initialName = "디귿", initialStrokeCount = 2),
    DYO(LessonId.DYO, "\uB434", initialName = "\uB514\uADE3", initialStrokeCount = 2),
    DU(LessonId.DU, "두", initialName = "디귿", initialStrokeCount = 2),
    DYU(LessonId.DYU, "듀", initialName = "\uB514\uADFF", initialStrokeCount = 2),
    DEU(LessonId.DEU, "드", initialName = "디귿", initialStrokeCount = 2),
    DI(LessonId.DI, "디", initialName = "디귿", initialStrokeCount = 2),
    RA(LessonId.RA, "라", initialName = "리을", initialStrokeCount = 3),
}

internal val GaAssemblyTarget.isHorizontalVowel: Boolean
    get() = this == GaAssemblyTarget.GO || this == GaAssemblyTarget.GYO ||
        this == GaAssemblyTarget.GU || this == GaAssemblyTarget.GYU ||
        this == GaAssemblyTarget.GEU || this == GaAssemblyTarget.NO ||
        this == GaAssemblyTarget.NYO || this == GaAssemblyTarget.NU ||
        this == GaAssemblyTarget.NYU || this == GaAssemblyTarget.NEU ||
        this == GaAssemblyTarget.DO || this == GaAssemblyTarget.DYO ||
        this == GaAssemblyTarget.DU || this == GaAssemblyTarget.DYU ||
        this == GaAssemblyTarget.DEU

internal enum class GaAssemblyPiece { GIEOK, VOWEL }

internal data class GaAssemblyState(
    val gieokPlaced: Boolean = false,
    val vowelPlaced: Boolean = false,
    val retryPiece: GaAssemblyPiece? = null,
) {
    val complete: Boolean get() = gieokPlaced && vowelPlaced

    fun place(piece: GaAssemblyPiece): GaAssemblyState = when (piece) {
        GaAssemblyPiece.GIEOK -> if (gieokPlaced) this else copy(gieokPlaced = true, retryPiece = null)
        GaAssemblyPiece.VOWEL -> if (!gieokPlaced || vowelPlaced) copy(retryPiece = piece)
        else copy(vowelPlaced = true, retryPiece = null)
    }
}
