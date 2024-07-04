package top.rrricardo.chiara.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import top.rrricardo.chiara.model.ShowPageState
import top.rrricardo.chiara.openapi.api.SeasonApi
import top.rrricardo.chiara.openapi.client.infrastructure.ApiClient
import top.rrricardo.chiara.openapi.model.EpisodeResponse
import javax.inject.Inject

@HiltViewModel
class ShowPageViewModel @Inject constructor(
    apiClient: ApiClient
) : ViewModel() {
    private val showSeasonApi = apiClient.createService(SeasonApi::class.java)

    var showPageState by mutableStateOf(ShowPageState())
        private set

    fun fetchShowSeason(id: Int) {
        showPageState = showPageState.copy(
            loading = true
        )

        viewModelScope.launch {
            val response = showSeasonApi.apiSeasonIdGet(id)
            val showSeason = response.body()

            showPageState = if (response.isSuccessful && showSeason != null) {
                showPageState.copy(
                    loading = false,
                    showSeason = showSeason
                )
            } else {
                showPageState.copy(
                    loading = false
                )
            }
        }
    }

    fun selectEpisode(selectedEpisode: EpisodeResponse) {

    }
}