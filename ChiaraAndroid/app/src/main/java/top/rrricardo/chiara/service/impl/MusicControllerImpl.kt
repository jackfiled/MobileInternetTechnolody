package top.rrricardo.chiara.service.impl

import android.content.ComponentName
import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import top.rrricardo.chiara.model.PlayerState
import top.rrricardo.chiara.model.RepeatState
import top.rrricardo.chiara.openapi.model.SongResponse
import top.rrricardo.chiara.service.MusicController

class MusicControllerImpl(context: Context) : MusicController {
    private var mediaControllerFuture: ListenableFuture<MediaController>

    private val mediaController: MediaController?
        get() = if (mediaControllerFuture.isDone) mediaControllerFuture.get() else null

    private val playList = HashMap<Int, SongResponse>()
    private val mediaIdMap = HashMap<Int, Int>()
    private val songUrlMap = HashMap<String, SongResponse>()

    init {
        val sessionToken =
            SessionToken(context, ComponentName(context, MusicService::class.java))

        mediaControllerFuture = MediaController.Builder(context, sessionToken).buildAsync()
        mediaControllerFuture.addListener({
            controllerListener()
            mediaController?.let {
                mediaControllerReadyCallback?.invoke(it)
                player = it
            }
        }, MoreExecutors.directExecutor())
    }

    override var mediaControllerCallback: (
        (
        playerState: PlayerState,
        currentMusic: SongResponse?,
        currentPosition: Long,
        totalDuration: Long,
        repeatState: RepeatState
    )
    -> Unit)? = null

    override var mediaControllerReadyCallback: ((Player) -> Unit)? = null

    override var mediaControllerErrorCallback: ((PlaybackException) -> Unit)? = null

    override var player: Player? = mediaController

    override fun clearPlayList() {
        mediaController?.stop()
        playList.clear()
    }

    override fun getPlayList(): List<SongResponse> = playList.values.toList()

    override fun addSong(song: SongResponse) {
        playList[song.id] = song

        refreshMediaItems()
    }

    override fun addSongRange(songs: Iterable<SongResponse>) {
        songs.map {
            playList[it.id] = it
        }

        refreshMediaItems()
    }

    override fun remove(songId: Int) {
        playList.remove(songId)
    }

    override fun play(songId: Int) {
        mediaController?.apply {
            mediaIdMap[songId]?.let {
                seekToDefaultPosition(it)
                prepare()
                play()
            }
        }
    }

    override fun resume() {
        mediaController?.play()
    }

    override fun pause() {
        mediaController?.pause()
    }

    override fun getCurrentSong(): SongResponse? {
        mediaController?.currentMediaItem?.let {
            return songUrlMap[it.mediaId]
        }

        return null
    }

    override fun getCurrentPosition(): Long = mediaController?.currentPosition ?: 0L

    override fun seekTo(position: Long) {
        mediaController?.seekTo(position)
    }

    override fun destroy() {
        MediaController.releaseFuture(mediaControllerFuture)
        mediaControllerCallback = null
    }

    override fun skipToNextSong() {
        mediaController?.seekToNext()
    }

    override fun skipToPreviousSong() {
        mediaController?.seekToPrevious()
    }

    override fun setSequenceRepeat() {
        mediaController?.shuffleModeEnabled = false
        mediaController?.repeatMode = Player.REPEAT_MODE_ALL
    }

    override fun setRepeatOne() {
        mediaController?.shuffleModeEnabled = false
        mediaController?.repeatMode = Player.REPEAT_MODE_ONE
    }

    override fun setShuffleRepeat() {
        mediaController?.repeatMode = Player.REPEAT_MODE_ALL
        mediaController?.shuffleModeEnabled = true
    }

    override fun playVideo(mediaItem: MediaItem) {
        mediaController?.setMediaItem(mediaItem)
        mediaController?.apply {
            prepare()
            play()
        }
    }

    override fun stopVideo() {
       mediaController?.apply {
           stop()
           clearMediaItems()
       }
    }

    private fun controllerListener() {
        mediaController?.addListener(object : Player.Listener {
            override fun onEvents(player: Player, events: Player.Events) {
                super.onEvents(player, events)

                with(player) {
                    val repeatState = if (shuffleModeEnabled) {
                        RepeatState.Shuffle
                    } else {
                        if (repeatMode == Player.REPEAT_MODE_ONE) {
                            RepeatState.RepeatOne
                        } else {
                            RepeatState.Sequence
                        }
                    }

                    mediaControllerCallback?.invoke(
                        playbackState.toPlayerState(isPlaying),
                        currentMediaItem?.let {
                            songUrlMap[it.mediaId]
                        },
                        currentPosition.coerceAtLeast(0L),
                        duration.coerceAtLeast(0L),
                        repeatState
                    )
                }
            }

            override fun onPlayerError(error: PlaybackException) {
                super.onPlayerError(error)
                mediaControllerErrorCallback?.invoke(error)
            }
        })
    }

    private fun Int.toPlayerState(isPlaying: Boolean) =
        when (this) {
            Player.STATE_IDLE -> PlayerState.STOPPED
            Player.STATE_ENDED -> PlayerState.STOPPED
            else -> if (isPlaying) {
                PlayerState.PLAYING
            } else {
                PlayerState.PAUSED
            }
        }

    private fun refreshMediaItems() {
        mediaIdMap.clear()
        songUrlMap.clear()

        val mediaItems = playList.values.mapIndexed { i, song ->
            mediaIdMap[song.id] = i
            songUrlMap[song.url] = song

            MediaItem.Builder()
                .setUri(song.url)
                .setMediaId(song.url)
                .setMediaMetadata(
                    MediaMetadata.Builder()
                        .setTitle(song.title)
                        .setSubtitle(song.arist)
                        .setArtist(song.arist)
                        .setArtworkUri(Uri.parse(song.coverImageUrl))
                        .build()
                )
                .build()
        }

        mediaController?.setMediaItems(mediaItems)
    }
}