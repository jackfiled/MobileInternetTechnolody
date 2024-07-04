package top.rrricardo.musicplayer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import top.rrricardo.musicplayer.model.HomeEvent
import top.rrricardo.musicplayer.ui.page.HomeBottomBar
import top.rrricardo.musicplayer.ui.page.HomePage
import top.rrricardo.musicplayer.ui.page.SongScreen
import top.rrricardo.musicplayer.ui.viewmodel.HomeViewModel
import top.rrricardo.musicplayer.ui.viewmodel.SharedViewModel
import top.rrricardo.musicplayer.ui.viewmodel.SongViewModel

@Composable
fun MusicPlayerApp(sharedViewModel: SharedViewModel) {
    val navController = rememberNavController()

    MusicPlayerNavHost(navController = navController, sharedViewModel = sharedViewModel)
}

@Composable
fun MusicPlayerNavHost(navController: NavHostController, sharedViewModel: SharedViewModel) {
    val musicControllerUiState = sharedViewModel.musicControllerUiState

    NavHost(navController = navController, startDestination = Navigation.HOME) {
        composable(route = Navigation.HOME) {
            val homeViewModel: HomeViewModel = hiltViewModel()
            val isInitialized = rememberSaveable(stateSaver = autoSaver()) {
                mutableStateOf(false)
            }

            if (!isInitialized.value) {
                LaunchedEffect(key1 = Unit) {
                    homeViewModel.onEvent(HomeEvent.FetchSong)
                    isInitialized.value = true
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                HomePage(onEvent = homeViewModel::onEvent, uiState = homeViewModel.homeUiState)

                HomeBottomBar(
                    modifier = Modifier
                        .align(Alignment.BottomCenter),
                    onEvent = homeViewModel::onEvent,
                    playState = musicControllerUiState.playerState,
                    song = musicControllerUiState.currentSong,
                    onBarClick = {
                        navController.navigate(Navigation.SONG_SCREEN)
                    }
                )
            }
        }

        composable(route = Navigation.SONG_SCREEN) {
            val songViewModel: SongViewModel = hiltViewModel()

            SongScreen(
                onEvent = songViewModel::onEvent,
                musicControllerUiState = musicControllerUiState,
                onNavigateUp = {
                    navController.navigateUp()
                })
        }
    }
}

object Navigation {
    const val HOME = "Home"
    const val SONG_SCREEN = "songScreen"
}