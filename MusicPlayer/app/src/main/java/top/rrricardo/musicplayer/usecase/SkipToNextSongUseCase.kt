package top.rrricardo.musicplayer.usecase

import top.rrricardo.musicplayer.model.Song
import top.rrricardo.musicplayer.service.MusicController
import javax.inject.Inject

class SkipToNextSongUseCase @Inject constructor(private val musicController: MusicController) {
    operator fun invoke(updateHomeUi: (Song?) -> Unit) {
        musicController.skipToNextSong()
        updateHomeUi(musicController.getCurrentSong())
    }
}