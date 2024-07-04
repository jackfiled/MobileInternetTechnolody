package top.rrricardo.musicplayer.service

import androidx.room.Database
import androidx.room.RoomDatabase
import top.rrricardo.musicplayer.model.Song

@Database(entities = [Song::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun songRepository() : SongRepository
}