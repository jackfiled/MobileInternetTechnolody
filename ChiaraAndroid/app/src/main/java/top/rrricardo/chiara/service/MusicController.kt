package top.rrricardo.chiara.service

import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import top.rrricardo.chiara.model.PlayerState
import top.rrricardo.chiara.model.RepeatState
import top.rrricardo.chiara.openapi.model.SongResponse

interface MusicController {
    var mediaControllerCallback: (
        (
        playerState: PlayerState,
        currentMusic: SongResponse?,
        currentPosition: Long,
        totalDuration: Long,
        repeatState: RepeatState
    ) -> Unit
    )?

    var mediaControllerReadyCallback: ((Player) -> Unit)?

    var mediaControllerErrorCallback: ((PlaybackException) -> Unit)?

    var player: Player?

    /**
     * 获得当前播放器的播放列表
     */
    fun getPlayList(): List<SongResponse>

    /**
     * 添加一首曲目到播放列表中
     */
    fun addSong(song: SongResponse)

    /**
     * 添加一系列曲目到播放列表中
     */
    fun addSongRange(songs: Iterable<SongResponse>)

    /**
     * 清空播放列表
     */
    fun clearPlayList()

    /**
     * 删除播放列表中的指定曲目
     */
    fun remove(songId: Int)

    /**
     * 播放播放列表中的指定曲目
     */
    fun play(songId: Int)

    /**
     * 恢复播放
     */
    fun resume()

    /**
     * 暂停
     */
    fun pause()

    /**
     * 获得当前播放歌曲的位置
     */
    fun getCurrentPosition(): Long

    fun destroy()

    /**
     * 跳转到下一首曲目
     */
    fun skipToNextSong()

    /**
     * 跳转到上一首曲目
     */
    fun skipToPreviousSong()

    /**
     * 获得当前正在播放的曲目
     */
    fun getCurrentSong(): SongResponse?

    /**
     * 跳转到指定位置
     */
    fun seekTo(position: Long)

    fun setSequenceRepeat()

    fun setRepeatOne()

    fun setShuffleRepeat()

    fun playVideo(mediaItem: MediaItem)

    fun stopVideo()
}