package top.rrricardo.musicplayer.usecase

import top.rrricardo.musicplayer.service.MusicController
import javax.inject.Inject

class DestroyMediaControllerUseCase @Inject constructor(private val musicController: MusicController) {
    operator fun invoke() = musicController.destroy()
}