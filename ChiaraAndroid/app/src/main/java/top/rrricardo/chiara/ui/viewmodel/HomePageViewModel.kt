package top.rrricardo.chiara.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import top.rrricardo.chiara.Configuration
import top.rrricardo.chiara.model.HomePageState
import top.rrricardo.chiara.openapi.api.AlbumApi
import top.rrricardo.chiara.openapi.api.SeasonApi
import top.rrricardo.chiara.openapi.client.infrastructure.ApiClient
import top.rrricardo.chiara.openapi.model.AlbumResponse
import javax.inject.Inject

@HiltViewModel
class HomePageViewModel @Inject constructor(
    private val apiClient: ApiClient
) : ViewModel() {

    var homePageState by mutableStateOf(HomePageState())
        private set


    fun changeToMusicPage() {
        homePageState = homePageState.copy(
            isMusicPage = true
        )
    }

    fun changeToVideoPage() {
        homePageState = homePageState.copy(
            isMusicPage = false
        )
    }

    fun fetchRemoteData() {
        if (homePageState.loading) {
            return
        }

        homePageState = homePageState.copy(
            loading = true,
            selectedAlbum = null,
            selectedSeason = null,
        )

        val albumApi = apiClient.createService(AlbumApi::class.java)
        val seasonApi = apiClient.createService(SeasonApi::class.java)
        viewModelScope.launch(CoroutineExceptionHandler { _, _ ->
            run {
                homePageState = homePageState.copy(
                    loading = false,
                    errorMessage = "网络错误"
                )
            }
        }) {
            val albumResponse = albumApi.apiAlbumGet()
            val albums = albumResponse.body()

            homePageState = if (albumResponse.isSuccessful && albums != null) {
                homePageState.copy(
                    loading = false,
                    albums = albums.map {
                        it.copy(
                            coverImageUrl = Configuration.SERVER_ADDRESS + it.coverImageUrl
                        )
                    }
                )
            } else {
                homePageState.copy(
                    loading = false,
                    errorMessage = "网络错误"
                )
            }

            val seasonResponse = seasonApi.apiSeasonGet()
            val seasons = seasonResponse.body()

            homePageState = if (seasonResponse.isSuccessful && seasons != null) {
                homePageState.copy(
                    loading = false,
                    seasons = seasons
                )
            } else {
                homePageState.copy(
                    loading = false,
                    errorMessage = "网络错误"
                )
            }
        }
    }

    fun selectAlbum(selectedAlbum: AlbumResponse) {
        if (selectedAlbum == homePageState.selectedAlbum) {
            return
        }

        homePageState = homePageState.copy(
            selectedAlbum = selectedAlbum
        )
    }
}