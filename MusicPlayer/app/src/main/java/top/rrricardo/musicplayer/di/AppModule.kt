package top.rrricardo.musicplayer.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import top.rrricardo.musicplayer.service.AppDatabase
import top.rrricardo.musicplayer.service.MusicController
import top.rrricardo.musicplayer.service.SongRepository
import top.rrricardo.musicplayer.service.impl.LocalSongRepository
import top.rrricardo.musicplayer.service.impl.MusicControllerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
//    @Singleton
//    @Provides
//    fun provideDatabase(@ApplicationContext context: Context) = Room.databaseBuilder(
//        context,
//        AppDatabase::class.java, "music-store"
//    ).build()
//
//    @Singleton
//    @Provides
//    fun provideSongRepository(database: AppDatabase) = database.songRepository()

    @Singleton
    @Provides
    fun provideSongRepository(): SongRepository = LocalSongRepository()

    @Singleton
    @Provides
    fun provideMusicController(@ApplicationContext context: Context): MusicController =
        MusicControllerImpl(context)
}