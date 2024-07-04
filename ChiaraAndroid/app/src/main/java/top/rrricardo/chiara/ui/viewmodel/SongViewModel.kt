package top.rrricardo.chiara.ui.viewmodel

import android.graphics.Bitmap
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.palette.graphics.Palette
import dagger.hilt.android.lifecycle.HiltViewModel
import top.rrricardo.chiara.service.MusicController
import javax.inject.Inject

@HiltViewModel
class SongViewModel @Inject constructor(
    val musicController: MusicController
) : ViewModel() {
    fun calculateColorPalette(drawable: Bitmap, onFinish: (Color) -> Unit) {
        Palette.from(drawable).generate { palette ->
            palette?.dominantSwatch?.rgb?.let {
                onFinish(Color(it))
            }
        }
    }
}