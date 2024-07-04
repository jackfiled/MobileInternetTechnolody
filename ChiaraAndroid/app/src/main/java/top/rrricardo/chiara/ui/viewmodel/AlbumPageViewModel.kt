package top.rrricardo.chiara.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import top.rrricardo.chiara.Configuration
import top.rrricardo.chiara.model.AlbumPageState
import top.rrricardo.chiara.openapi.api.AlbumApi
import top.rrricardo.chiara.openapi.client.infrastructure.ApiClient
import top.rrricardo.chiara.openapi.model.SongResponse
import top.rrricardo.chiara.service.MusicController
import javax.inject.Inject

@HiltViewModel
class AlbumPageViewModel @Inject constructor(
    apiClient: ApiClient,
    private val musicController: MusicController
) : ViewModel() {
    var albumPageState by mutableStateOf(AlbumPageState())
        private set

    private val albumApi = apiClient.createService(AlbumApi::class.java)

    fun fetchAlbum(albumId: Int) {
        albumPageState = albumPageState.copy(
            loading = true
        )

        viewModelScope.launch {
            val albumResponse = albumApi.apiAlbumIdGet(albumId)
            var album = albumResponse.body()

            if (albumResponse.isSuccessful && album != null) {
                album = album.copy(
                    coverImageUrl = Configuration.SERVER_ADDRESS + album.coverImageUrl,
                    songs = album.songs.map {
                        it.copy(
                            coverImageUrl = Configuration.SERVER_ADDRESS + it.coverImageUrl,
                            url = Configuration.SERVER_ADDRESS + it.url
                        )
                    }
                )

                albumPageState = albumPageState.copy(
                    loading = false,
                    album = album
                )
            } else {
                albumPageState = albumPageState.copy(
                    loading = false,
                    errorMessage = "网络错误"
                )
            }
        }
    }

    fun selectSong(song: SongResponse) {
        albumPageState = albumPageState.copy(
            selectedSong = song
        )
    }

    fun playSong() {
        albumPageState.album?.let {
            musicController.addSongRange(it.songs)
        }

        albumPageState.selectedSong?.let {
            musicController.play(it.id)
        }
    }
}