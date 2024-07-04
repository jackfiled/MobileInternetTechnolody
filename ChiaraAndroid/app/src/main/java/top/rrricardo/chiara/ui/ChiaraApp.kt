package top.rrricardo.chiara.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.autoSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import top.rrricardo.chiara.ui.page.AlbumPage
import top.rrricardo.chiara.ui.page.HomePage
import top.rrricardo.chiara.ui.page.PlayListPage
import top.rrricardo.chiara.ui.page.ShowPage
import top.rrricardo.chiara.ui.page.SongPage
import top.rrricardo.chiara.ui.page.SplashPage
import top.rrricardo.chiara.ui.page.VideoPage
import top.rrricardo.chiara.ui.viewmodel.AlbumPageViewModel
import top.rrricardo.chiara.ui.viewmodel.HomePageViewModel
import top.rrricardo.chiara.ui.viewmodel.MainViewModel
import top.rrricardo.chiara.ui.viewmodel.ShowPageViewModel
import top.rrricardo.chiara.ui.viewmodel.VideoPageViewModel

@Composable
fun ChiaraApp(mainViewModel: MainViewModel) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Navigation.SPLASH_SCREEN) {
        composable(route = Navigation.SPLASH_SCREEN) {
            SplashPage(mainViewModel = mainViewModel, navController = navController)
        }

        composable(route = Navigation.HOME) {
            val homePageViewModel: HomePageViewModel = hiltViewModel()
            val isInitialized = rememberSaveable(stateSaver = autoSaver()) {
                mutableStateOf(false)
            }

            if (!isInitialized.value) {
                LaunchedEffect(key1 = Unit) {
                    homePageViewModel.fetchRemoteData()
                    isInitialized.value = true
                }
            }

            HomePage(
                mainViewModel = mainViewModel,
                homePageViewModel = homePageViewModel,
                navController = navController
            )
        }

        composable(
            "${Navigation.ALBUM_SCREEN}/{albumId}",
            arguments = listOf(navArgument("albumId") {
                type = NavType.IntType
            })
        ) { backStackEntry ->
            val albumId = backStackEntry.arguments?.getInt("albumId")
            val albumPageViewModel: AlbumPageViewModel = hiltViewModel()
            val isInitialized = rememberSaveable(stateSaver = autoSaver()) {
                mutableStateOf(false)
            }

            if (!isInitialized.value) {
                LaunchedEffect(key1 = Unit) {
                    albumId?.let {
                        albumPageViewModel.fetchAlbum(it)
                    }
                    isInitialized.value = true
                }
            }

            AlbumPage(
                mainViewModel = mainViewModel,
                albumPageViewModel = albumPageViewModel,
                navController = navController
            )
        }

        composable(route = Navigation.SONG_SCREEN) {
            SongPage(mainViewModel = mainViewModel, navController = navController)
        }

        composable(route = Navigation.PLAYLIST_SCREEN) {
            PlayListPage(mainViewModel = mainViewModel, navController = navController)
        }

        composable(
            route = "${Navigation.SHOW_SCREEN}/{showId}", arguments = listOf(
                navArgument("showId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val seasonId = backStackEntry.arguments?.getInt("showId")
            val showPageViewModel: ShowPageViewModel = hiltViewModel()
            val isInitialized = rememberSaveable(stateSaver = autoSaver()) {
                mutableStateOf(false)
            }

            if (!isInitialized.value) {
                LaunchedEffect(key1 = Unit) {
                    seasonId?.let {
                        showPageViewModel.fetchShowSeason(it)
                    }
                    isInitialized.value = true
                }
            }

            ShowPage(showPageViewModel = showPageViewModel, navController = navController)
        }

        composable(route = "${Navigation.VIDEO_SCREEN}?videoId={videoId}&showId={showId}",
            arguments = listOf(
                navArgument("videoId") {
                    type = NavType.IntType
                },
                navArgument("showId") {
                    type = NavType.IntType
                }
            )) { backStackEntry ->
            val videoId = backStackEntry.arguments?.getInt("videoId")
            val showId = backStackEntry.arguments?.getInt("showId")
            val videoPageViewModel: VideoPageViewModel = hiltViewModel()
            val isInitialized = rememberSaveable(stateSaver = autoSaver()) {
                mutableStateOf(false)
            }

            if (!isInitialized.value) {
                LaunchedEffect(key1 = Unit) {
                    videoId?.let {videoId ->
                        videoPageViewModel.fetchAndPlay(videoId)

                        showId?.let {
                            videoPageViewModel.fetchShow(it, videoId)
                        }
                    }

                    isInitialized.value = true
                }
            }

            VideoPage(videoPageViewModel = videoPageViewModel, navController = navController)
        }
    }
}

object Navigation {
    const val SPLASH_SCREEN = "splashScreen"
    const val HOME = "home"
    const val ALBUM_SCREEN = "albumScreen"
    const val SONG_SCREEN = "songScreen"
    const val PLAYLIST_SCREEN = "playlistScreen"
    const val SHOW_SCREEN = "showScreen"
    const val VIDEO_SCREEN = "videoScreen"
}