package top.rrricardo.musicplayer.usecase

import top.rrricardo.musicplayer.model.PlayState
import top.rrricardo.musicplayer.model.Song
import top.rrricardo.musicplayer.service.MusicController
import javax.inject.Inject

class SetMediaControllerCallbackUseCase @Inject constructor(
    private val musicController: MusicController
) {
    operator fun invoke(
        callback: (
            playState: PlayState,
            currentSong: Song?,
            currentPosition: Long,
            totalDuration: Long,
            isShuffleEnabled: Boolean,
            isRepeatOneEnabled: Boolean
                )-> Unit
    ) {
        musicController.mediaControllerCallback = callback
    }
}