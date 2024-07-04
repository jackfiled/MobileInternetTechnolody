package top.rrricardo.chiara.ui.viewmodel

import android.util.Log
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import top.rrricardo.chiara.Configuration
import top.rrricardo.chiara.model.VideoPageState
import top.rrricardo.chiara.openapi.api.HlsApi
import top.rrricardo.chiara.openapi.api.SeasonApi
import top.rrricardo.chiara.openapi.client.infrastructure.ApiClient
import top.rrricardo.chiara.service.MusicController
import javax.inject.Inject

@HiltViewModel
class VideoPageViewModel @Inject constructor(
    val musicController: MusicController,
    apiClient: ApiClient
) : ViewModel() {
    private val hlsApi = apiClient.createService(HlsApi::class.java)
    private val showSeasonApi = apiClient.createService(SeasonApi::class.java)

    var videoPageState by mutableStateOf(VideoPageState())
        private set

    val snackBarHostState = SnackbarHostState()

    fun fetchAndPlay(id: Int) {
        videoPageState = videoPageState.copy(
            loading = true
        )
        musicController.mediaControllerErrorCallback = {
            showPlayerException(it)
        }

        viewModelScope.launch {
            val response = hlsApi.apiHlsStartEpisodeIdPost(id)
            val playList = response.body()

            if (response.isSuccessful && playList != null) {
                videoPageState = videoPageState.copy(
                    loading = false,
                    playing = true
                )

                val mediaItem = MediaItem.Builder()
                    .setMediaId(Configuration.SERVER_ADDRESS + playList.playList)
                    .setUri(Configuration.SERVER_ADDRESS + playList.playList)
                    .build()
                Log.i("Video Player", Configuration.SERVER_ADDRESS + playList.playList)
                musicController.playVideo(mediaItem)
            } else {
                videoPageState = videoPageState.copy(
                    loading = false
                )
            }
        }
    }

    fun fetchShow(showId: Int, videoId: Int) {
        viewModelScope.launch {
            val response = showSeasonApi.apiSeasonIdGet(showId)
            val showSeasonResponse = response.body()

            if (response.isSuccessful && showSeasonResponse != null) {
                videoPageState = videoPageState.copy(
                    showSeasonResponse = showSeasonResponse,
                    episodeResponse = showSeasonResponse.episodes.find {
                        it.id == videoId
                    }
                )
            }
        }
    }

    fun stopTransaction() {
        musicController.mediaControllerErrorCallback = null

        if (videoPageState.playing) {
            musicController.stopVideo()
            viewModelScope.launch {
                hlsApi.apiHlsStopPost()
            }
        }
    }

    fun getPlayer() = musicController.player

    private fun showPlayerException(exception: PlaybackException) {
        viewModelScope.launch {
            snackBarHostState.showSnackbar("设备不支持此类型视频！")
        }
    }
}