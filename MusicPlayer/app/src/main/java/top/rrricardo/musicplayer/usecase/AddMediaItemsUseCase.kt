package top.rrricardo.musicplayer.usecase

import top.rrricardo.musicplayer.model.Song
import top.rrricardo.musicplayer.service.MusicController
import javax.inject.Inject

class AddMediaItemsUseCase @Inject constructor(private val musicController: MusicController) {

    operator fun invoke(songs: List<Song>) = musicController.addMediaItems(songs)
}