package top.rrricardo.musicplayer.ui.viewmodel

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import dagger.hilt.android.lifecycle.HiltViewModel
import top.rrricardo.musicplayer.model.SongEvent
import top.rrricardo.musicplayer.usecase.PauseSongUseCase
import top.rrricardo.musicplayer.usecase.ResumeSongUseCase
import top.rrricardo.musicplayer.usecase.SeekSongToPositionUseCase
import top.rrricardo.musicplayer.usecase.SkipToNextSongUseCase
import top.rrricardo.musicplayer.usecase.SkipToPreviousSongUseCase
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    private val pauseSongUseCase: PauseSongUseCase,
    private val resumeSongUseCase: ResumeSongUseCase,
    private val skipToPreviousSongUseCase: SkipToPreviousSongUseCase,
    private val skipToNextSongUseCase: SkipToNextSongUseCase,
    private val seekSongToPositionUseCase: SeekSongToPositionUseCase
) : ViewModel() {
    fun onEvent(event: SongEvent) {
        when (event) {
            SongEvent.PauseSong -> pauseSongUseCase()
            SongEvent.ResumeSong -> resumeSongUseCase()
            is SongEvent.SeekSongToPosition -> seekSongToPositionUseCase(event.position)
            SongEvent.SkipToNextSong -> skipToNextSongUseCase {}
            SongEvent.SkipToPreviousSong -> skipToPreviousSongUseCase {}
        }
    }

    fun calculateColorPalette(drawable: Bitmap, onFinish: (Color) -> Unit) {
        Palette.from(drawable).generate { palette ->
            palette?.dominantSwatch?.rgb?.let {
                onFinish(Color(it))
            }
        }
    }
}