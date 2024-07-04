package top.rrricardo.musicplayer.service.impl

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import top.rrricardo.musicplayer.model.Song
import top.rrricardo.musicplayer.service.SongRepository
import kotlin.time.Duration.Companion.seconds

class LocalSongRepository : SongRepository {
    override fun getSongs(): Flow<List<Song>> {
        return flow {
            delay(3.seconds)
            emit(listOf(Song(
                id = 0,
                mediaId = "Liyue",
                title = "璃月",
                subTitle = "璃月",
                songUrl = "https://oss.rrricardo.top/music/01%20Liyue.flac",
                imageUrl = "https://oss.rrricardo.top/oss/Artwork.jpg"
            )))
        }
    }
}