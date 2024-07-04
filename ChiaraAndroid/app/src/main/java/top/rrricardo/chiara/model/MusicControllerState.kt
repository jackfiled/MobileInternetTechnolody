package top.rrricardo.chiara.model

import top.rrricardo.chiara.openapi.model.SongResponse

data class MusicControllerState(
    val playerState: PlayerState= PlayerState.STOPPED,
    val currentSong: SongResponse? = null,
    val currentPosition: Long = 0L,
    val totalDuration: Long = 0L,
    val repeatState : RepeatState = RepeatState.Sequence
)
