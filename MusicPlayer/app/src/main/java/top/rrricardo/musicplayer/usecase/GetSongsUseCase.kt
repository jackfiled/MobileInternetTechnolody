package top.rrricardo.musicplayer.usecase

import top.rrricardo.musicplayer.service.SongRepository
import javax.inject.Inject

class GetSongsUseCase @Inject constructor(private val songRepository: SongRepository) {
    operator fun invoke() = songRepository.getSongs()
}