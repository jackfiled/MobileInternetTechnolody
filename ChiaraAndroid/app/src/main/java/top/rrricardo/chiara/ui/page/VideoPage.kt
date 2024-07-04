package top.rrricardo.chiara.ui.page

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import top.rrricardo.chiara.openapi.model.EpisodeResponse
import top.rrricardo.chiara.openapi.model.ShowSeasonResponse
import top.rrricardo.chiara.ui.component.ChiaraAppBar
import top.rrricardo.chiara.ui.component.EpisodeItem
import top.rrricardo.chiara.ui.viewmodel.VideoPageViewModel

@Composable
fun VideoPage(videoPageViewModel: VideoPageViewModel, navController: NavController) {
    Scaffold(topBar = {
        with(videoPageViewModel.videoPageState) {
            ChiaraAppBar(title = episodeResponse?.title ?: "视频",
                showNavigationIcon = true, onNavigateUp = {
                    videoPageViewModel.stopTransaction()
                    navController.popBackStack()
                }
            )
        }
    }, snackbarHost = {
        SnackbarHost(hostState = videoPageViewModel.snackBarHostState)
    }) { padding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            BackHandler(enabled = true) {
                videoPageViewModel.stopTransaction()
                navController.popBackStack()
            }

            Column(modifier = Modifier.fillMaxSize()) {
                with(videoPageViewModel.videoPageState) {
                    when {
                        loading -> Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp),
                            contentAlignment = Alignment.BottomCenter
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .width(100.dp)
                                    .fillMaxHeight()
                                    .align(Alignment.Center)
                                    .padding(
                                        top = 16.dp,
                                        start = 16.dp,
                                        end = 16.dp,
                                        bottom = 16.dp
                                    )
                            )
                        }

                        !loading -> {
                            PlayerSurface(
                                modifier = Modifier
                                    .height(300.dp)
                                    .fillMaxWidth()
                            ) {
                                it.player = videoPageViewModel.getPlayer()
                            }
                        }
                    }

                    showSeasonResponse?.let { showSeasonResponse ->
                        episodeResponse?.let {
                            NextEpisodeList(
                                showSeasonResponse = showSeasonResponse,
                                episodeResponse = it
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerSurface(modifier: Modifier = Modifier, onPlayerViewReady: (PlayerView) -> Unit) {
    AndroidView(factory = {
        PlayerView(it).apply {
            useController = true
            onPlayerViewReady(this)
        }
    }, modifier = modifier)
}

@Composable
fun NextEpisodeList(
    modifier: Modifier = Modifier,
    showSeasonResponse: ShowSeasonResponse,
    episodeResponse: EpisodeResponse
) {
    Column(modifier.fillMaxSize()) {
        Text(text = "接下来:")

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
        ) {
            items(showSeasonResponse.episodes.filter {
                it.id != episodeResponse.id
            }) {
                EpisodeItem(episode = it) {

                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NextEpisodeListPreview() {
    NextEpisodeList(
        showSeasonResponse = ShowSeasonResponse(
            id = 0,
            title = "TestTestTest",
            episodes = listOf(
                EpisodeResponse(
                    id = 1,
                    title = "E01",
                    episodeNumber = "1",
                    showSeasonId = 0
                ),
                EpisodeResponse(
                    id = 2,
                    title = "E02",
                    episodeNumber = "2",
                    showSeasonId = 0
                )
            )
        ), episodeResponse = EpisodeResponse(
            id = 1,
            title = "E01",
            episodeNumber = "1",
            showSeasonId = 0
        )
    )
}
