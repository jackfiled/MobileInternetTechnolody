package top.rrricardo.musicplayer.usecase

import top.rrricardo.musicplayer.service.MusicController
import javax.inject.Inject

class PlaySongUseCase @Inject constructor(private val musicController: MusicController) {
    operator fun invoke(mediaItemIndex: Int) = musicController.play(mediaItemIndex)
}