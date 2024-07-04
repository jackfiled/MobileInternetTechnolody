package top.rrricardo.musicplayer.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.rrricardo.musicplayer.model.MusicControllerUiState
import top.rrricardo.musicplayer.model.PlayState
import top.rrricardo.musicplayer.usecase.DestroyMediaControllerUseCase
import top.rrricardo.musicplayer.usecase.GetCurrentSongPositionUseCase
import top.rrricardo.musicplayer.usecase.SetMediaControllerCallbackUseCase
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class SharedViewModel @Inject constructor(
    private val setMediaControllerCallbackUseCase: SetMediaControllerCallbackUseCase,
    private val getCurrentSongPositionUseCase: GetCurrentSongPositionUseCase,
    private val destroyMediaControllerUseCase: DestroyMediaControllerUseCase
) : ViewModel() {
    var musicControllerUiState by mutableStateOf(MusicControllerUiState())
        private set

    init {
        setMediaControllerCallback()
    }

    fun destroyMediaController() = destroyMediaControllerUseCase()

    private fun setMediaControllerCallback() {
        setMediaControllerCallbackUseCase {
                                          playState,
                                          currentSong,
                                          currentPosition,
                                          totalDuration,
                                          isShuffleEnabled,
                                          isRepeatOneEnabled ->
            musicControllerUiState = musicControllerUiState.copy(
                playerState = playState,
                currentSong = currentSong,
                currentPosition = currentPosition,
                totalDuration = totalDuration,
                isShuffleEnabled = isShuffleEnabled,
                isRepeatOneEnabled = isRepeatOneEnabled
            )

            if (playState == PlayState.PLAYING) {
                viewModelScope.launch {
                    while (true) {
                        delay(3.seconds)
                        musicControllerUiState = musicControllerUiState.copy(
                            currentPosition = getCurrentSongPositionUseCase()
                        )
                    }
                }
            }
        }
    }
}