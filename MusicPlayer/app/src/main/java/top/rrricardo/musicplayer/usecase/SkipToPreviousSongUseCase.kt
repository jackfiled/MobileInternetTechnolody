package top.rrricardo.musicplayer.usecase

import top.rrricardo.musicplayer.model.Song
import top.rrricardo.musicplayer.service.MusicController
import javax.inject.Inject

class SkipToPreviousSongUseCase @Inject constructor(private val musicController: MusicController) {
    operator fun invoke(updateHomeUi: (Song?) -> Unit) {
        musicController.skipToPreviousSong()
        updateHomeUi(musicController.getCurrentSong())
    }
}