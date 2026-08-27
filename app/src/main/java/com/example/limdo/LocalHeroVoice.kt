package com.example.limdo

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.util.Log
import androidx.annotation.RawRes

internal enum class HeroVoiceCue(
    @RawRes val resourceId: Int,
    val transcript: String,
) {
    MENU_CONSONANTS(R.raw.hero_menu_consonants, "씩씩하게 자음!"),
    MENU_VOWELS(R.raw.hero_menu_vowels, "반짝반짝 모음!"),
    MENU_GANADA(R.raw.hero_menu_ganada, "힘차게 가나다!"),
    WRITING_START(R.raw.hero_writing_start, "초록 점에서 시작! 움직이는 표식을 따라가자!"),
    RETRY(R.raw.hero_retry, "괜찮아! 초록 점에서 다시 출발!"),
    SUCCESS(R.raw.hero_success, "해냈다! 정말 멋진 영웅이야!"),
    ;

    companion object {
        fun forMenu(menu: LearningMenu): HeroVoiceCue = when (menu) {
            LearningMenu.CONSONANTS -> MENU_CONSONANTS
            LearningMenu.VOWELS -> MENU_VOWELS
            LearningMenu.GANADA -> MENU_GANADA
        }

        fun forTraceResult(result: GieokTraceResult?): HeroVoiceCue? = when (result) {
            GieokTraceResult.SUCCESS -> SUCCESS
            GieokTraceResult.WRONG_START,
            GieokTraceResult.WRONG_DIRECTION,
            GieokTraceResult.OFF_GUIDE,
            GieokTraceResult.INCOMPLETE,
            -> RETRY
            GieokTraceResult.EMPTY, null -> null
        }
    }
}

internal class LocalHeroVoice(private val context: Context) {
    private var player: MediaPlayer? = null
    private var requestNumber = 0

    fun play(cue: HeroVoiceCue) {
        stop("replace")
        requestNumber += 1
        val requestId = requestNumber
        val nextPlayer = runCatching {
            MediaPlayer.create(context, cue.resourceId, AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build(), 0)
        }.getOrNull()
        if (nextPlayer == null) {
            Log.w(TAG, "event=unavailable cue=${cue.name} request=$requestId fallback=silent")
            return
        }
        player = nextPlayer
        nextPlayer.setOnCompletionListener { completed ->
            if (player === completed) player = null
            completed.release()
            Log.i(TAG, "event=completed cue=${cue.name} request=$requestId")
        }
        nextPlayer.setOnErrorListener { failed, what, extra ->
            if (player === failed) player = null
            failed.release()
            Log.w(TAG, "event=error cue=${cue.name} request=$requestId what=$what extra=$extra fallback=silent")
            true
        }
        runCatching { nextPlayer.start() }
            .onSuccess { Log.i(TAG, "event=started cue=${cue.name} request=$requestId") }
            .onFailure {
                if (player === nextPlayer) player = null
                nextPlayer.release()
                Log.w(TAG, "event=start_failed cue=${cue.name} request=$requestId fallback=silent", it)
            }
    }

    fun stop(reason: String) {
        val active = player ?: return
        player = null
        runCatching { active.stop() }
        active.release()
        Log.i(TAG, "event=stopped reason=$reason")
    }

    fun release() = stop("release")

    private companion object {
        const val TAG = "LimDoVoice"
    }
}
