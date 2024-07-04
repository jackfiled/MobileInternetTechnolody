package top.rrricardo.musicplayer.service

import top.rrricardo.musicplayer.model.PlayState
import top.rrricardo.musicplayer.model.Song

interface MusicController {
    var mediaControllerCallback: (
        (
        playerState: PlayState,
        currentMusic: Song?,
        currentPosition: Long,
        totalDuration: Long,
        isShuffleEnabled: Boolean,
        isRepeatOneEnabled: Boolean
    ) -> Unit
    )?

    fun addMediaItems(songs: List<Song>)

    fun play(mediaItemIndex: Int)

    fun resume()

    fun pause()

    fun getCurrentPosition() : Long

    fun destroy()

    fun skipToNextSong()

    fun skipToPreviousSong()

    fun getCurrentSong() : Song?

    fun seekTo(position: Long)
}