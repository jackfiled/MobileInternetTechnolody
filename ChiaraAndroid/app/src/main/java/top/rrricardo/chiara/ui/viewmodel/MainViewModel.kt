package top.rrricardo.chiara.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import top.rrricardo.chiara.model.MusicControllerState
import top.rrricardo.chiara.model.PlayerState
import top.rrricardo.chiara.openapi.model.SongResponse
import top.rrricardo.chiara.service.MusicController
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

@HiltViewModel
class MainViewModel @Inject constructor(
    val musicController: MusicController
) : ViewModel() {
    var musicControllerState by mutableStateOf(MusicControllerState())
        private set

    var playList: List<SongResponse> by mutableStateOf(emptyList())
        private set

    init {
        musicController.mediaControllerCallback = { playerState,
                                                    currentMusic,
                                                    currentPosition,
                                                    totalDuration,
                                                    repeatState ->
            musicControllerState = musicControllerState.copy(
                playerState = playerState,
                currentSong = currentMusic,
                currentPosition = currentPosition,
                totalDuration = totalDuration,
                repeatState = repeatState
            )

            if (playerState == PlayerState.PLAYING) {
                viewModelScope.launch {
                    while (true) {
                        // 每秒钟更新一次播放器的状态
                        delay(1.seconds)
                        musicControllerState = musicControllerState.copy(
                            currentPosition = musicController.getCurrentPosition()
                        )
                    }
                }
            }
        }
    }

    fun destroyMediaController() = musicController.destroy()

    fun refreshPlayList() {
        playList = musicController.getPlayList()
    }
}