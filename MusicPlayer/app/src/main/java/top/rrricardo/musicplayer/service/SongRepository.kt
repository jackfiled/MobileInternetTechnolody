package top.rrricardo.musicplayer.service

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import top.rrricardo.musicplayer.model.Song

@Dao
interface SongRepository {
    @Query("SELECT * FROM song")
    fun getSongs() : Flow<List<Song>>
}