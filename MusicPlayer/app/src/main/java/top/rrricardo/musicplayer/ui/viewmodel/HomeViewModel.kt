package top.rrricardo.musicplayer.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import top.rrricardo.musicplayer.model.HomeEvent
import top.rrricardo.musicplayer.model.HomeUiState
import top.rrricardo.musicplayer.usecase.AddMediaItemsUseCase
import top.rrricardo.musicplayer.usecase.GetSongsUseCase
import top.rrricardo.musicplayer.usecase.PauseSongUseCase
import top.rrricardo.musicplayer.usecase.PlaySongUseCase
import top.rrricardo.musicplayer.usecase.ResumeSongUseCase
import top.rrricardo.musicplayer.usecase.SkipToNextSongUseCase
import top.rrricardo.musicplayer.usecase.SkipToPreviousSongUseCase
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getSongsUseCase: GetSongsUseCase,
    private val addMediaItemsUseCase: AddMediaItemsUseCase,
    private val playSongUseCase: PlaySongUseCase,
    private val pauseSongUseCase: PauseSongUseCase,
    private val resumeSongUseCase: ResumeSongUseCase,
    private val skipToNextSongUseCase: SkipToNextSongUseCase,
    private val skipToPreviousSongUseCase: SkipToPreviousSongUseCase
) : ViewModel() {
    var homeUiState by mutableStateOf(HomeUiState())
        private set

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.PlaySong -> playSong()

            HomeEvent.PauseSong -> pauseSong()

            HomeEvent.ResumeSong -> resumeSong()

            HomeEvent.FetchSong -> getSong()

            is HomeEvent.OnSongSelected -> homeUiState = homeUiState.copy(selectedSong = event.selectedSong)

            is HomeEvent.SkipToNextSong -> skipToNextSong()

            is HomeEvent.SkipToPreviousSong -> skipToPreviousSong()
        }
    }

    private fun getSong() {
        homeUiState = homeUiState.copy(loading = true)

        viewModelScope.launch {
            getSongsUseCase().catch {
                homeUiState = homeUiState.copy(
                    loading = false,
                    errorMessage = it.message
                )
            }.collect {
                homeUiState = homeUiState.copy(
                    loading = false,
                    songs = it
                )

                addMediaItemsUseCase(it)
            }
        }
    }

    private fun playSong() {
        homeUiState.apply {
            songs?.indexOf(selectedSong)?.let { song ->
                playSongUseCase(song)
            }
        }
    }

    private fun pauseSong() = pauseSongUseCase()

    private fun resumeSong() = resumeSongUseCase()

    private fun skipToNextSong() = skipToNextSongUseCase {
        homeUiState = homeUiState.copy(selectedSong = it)
    }

    private fun skipToPreviousSong() = skipToPreviousSongUseCase {
        homeUiState = homeUiState.copy(selectedSong = it)
    }
}