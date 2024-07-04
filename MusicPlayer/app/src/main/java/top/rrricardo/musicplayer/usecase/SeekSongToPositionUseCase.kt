package top.rrricardo.musicplayer.usecase

import top.rrricardo.musicplayer.service.MusicController
import javax.inject.Inject

class SeekSongToPositionUseCase @Inject constructor(private val musicController: MusicController) {
    operator fun invoke(position: Long) = musicController.seekTo(position)
}